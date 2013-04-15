
; Animation addon module for bOGL
;=================================


; This module provides functions to load animations from BO3D files, and apply
; them to hierarchical mesh systems.

; This module depends on "MeshLoader.bb" to load the surfaces to combine into
; complete animated meshes ad for some BO3D file functions.

; See also "MD2.bb" for a limited but potentially faster animation system
; (much of the code here is copy/pasted from it).


Include "bOGL\bOGL.bb"
Include "bOGL-Addons\MeshUtils.bb"
Include "bOGL-Addons\MeshLoader.bb"


Type bOGL_Animation
	Field root.bOGL_Ent
	Field keyFrames, nodes
	Field length
	
	Field animMode, animTime#
	Field animSpd#, trans#
	Field fromF, toF
	Field seqS, seqE, nextMode
End Type


Const ANIM_MODE_STOP = 0, ANIM_MODE_LOOP = 1, ANIM_MODE_PING = 2, ANIM_MODE_ONCE = 3, ANIM_MODE_TRANS = 4
Const ANIM_KF_SIZE = 44, ANIM_ALLOC_TICKER = 25

Global ANIM_private_UDSlot_, ANIM_private_LoadErr_, ANIM_private_ALen_, ANIM_private_NewCounter_
Global ANIM_header_.bOGL_Animation, ANIM_buffer_.bOGL_Animation
Dim ANIM_AnimName_$(0)


Function InitAnimationAddon()		;Only call this once per program
	ANIM_private_UDSlot_ = RegisterEntityUserDataSlot()
	ANIM_header_ = New bOGL_Animation
	ANIM_buffer_ = New bOGL_Animation
	MESH_InitMeshUtils_
End Function

; Call this once per loop to update entity positions
Function UpdateAnimations()
	Insert ANIM_header_ After Last bOGL_Animation
	Local m.bOGL_Animation, doClear = False : For m = Each bOGL_Animation
		If m = ANIM_buffer_ Then Exit
		
		If m\root = Null Or m\animMode = ANIM_MODE_STOP
			Local n.bOGL_Animation = m
			m = Before m
			Insert n After Last bOGL_Animation
			If n\root = Null Then doClear = True
		Else
			ANIM_UpdateAnimation_ m
			ANIM_UpdateNodePositions_ m
		EndIf
		
		If m = Null Then m = ANIM_header_ : Insert m Before First bOGL_Animation
	Next
	If doClear Then ANIM_ClearUnused
	Insert ANIM_header_ Before First bOGL_Animation
End Function

Function LoadAnimation(root, file$)
	Local size = FileSize(file), bank = CreateBank(size), f = ReadFile(file)
	ReadBytes bank, f, 0, size
	LoadAnimBank(root, bank, 0, size)
	CloseFile f : FreeBank bank
End Function

Function LoadAnimBank(root, bk, start, size)		;Much of this is copied directly from the mesh loader
	If size < 20 Then Return		;Size of the BO3D header: minimum possible valid file size
	If PeekInt(bk, start + 0) <> $44334f42 Then Return	;Magic number check
	If PeekInt(bk, start + 4) > 100 Then Return		;Version check
	Local eCount = PeekInt(bk, start + 8)
	Local eListSize = PeekInt(bk, start + 12)
	If eCount = 0 Then Return		;No entities... nothing to return
	If eListSize > size + 20 Then Return		;Entity list doesn't fit
	
	Dim ANIM_AnimName_$(eCount)
	Local p[0], tgt = start + size, i, anims = CreateBank(4 * eCount + 4)
	p[0] = start + 20	;Skip the header
	ANIM_private_LoadErr_ = False : ANIM_private_ALen_ = 0
	For i = 0 To eCount - 1
		PokeInt anims, i * 4, ANIM_LoadAnimDef_(bk, p, tgt, i)
		If ANIM_private_LoadErr_ Then Exit
	Next
	
	If ANIM_private_LoadErr_	;Loading a def failed: cleanup all of the loaded anims and return
		For i = 0 To eCount - 1
			If PeekInt(anims, i * 4) Then FreeBank PeekInt(anims, i * 4)
		Next
		FreeBank anims
		Dim ANIM_AnimName_$(0) : Return
	EndIf
	
	If ANIM_private_NewCounter_ = ANIM_ALLOC_TICKER Then ANIM_ClearUnused
	ANIM_private_NewCounter_ = ANIM_private_NewCounter_ + 1
	
	Local a.bOGL_Animation = New bOGL_Animation		;Construct the animation object
	a\root = bOGL_EntList_(root)
	a\keyFrames = anims
	a\nodes = CreateBank(eCount * 4)
	a\length = ANIM_private_ALen_
	a\animMode = ANIM_MODE_STOP
	SetEntityUserData root, ANIM_private_UDSlot_, Handle a
	
	;Attach animations to their respective child nodes
	For i = eCount - 1 To 0 Step -1
		Local node = GetChildByName(root, ANIM_AnimName_(i)), keys = PeekInt(anims, i * 4), nEnt.bOGL_Ent = bOGL_EntList_(node)
		If node = 0 And ANIM_AnimName_(i) = GetEntityName(root) Then node = root
		If node = 0 Or BankSize(keys) = 0
			FreeBank keys
			CopyBank anims, i * 4 + 4, anims, i * 4, BankSize(anims) - (i * 4 + 4)
			ResizeBank anims, BankSize(anims) - 4
			CopyBank a\nodes, i * 4 + 4, a\nodes, i * 4, BankSize(a\nodes) - (i * 4 + 4)
			ResizeBank a\nodes, BankSize(a\nodes) - 4
		Else
			PokeInt a\nodes, i * 4, node
			
			ResizeBank keys, BankSize(keys) + 44	;Add the base position of the mesh at index -1
			CopyBank keys, 0, keys, 44, BankSize(keys) - 44
			PokeInt keys, 0, -1
			PokeFloat keys, 4, nEnt\x
			PokeFloat keys, 8, nEnt\y
			PokeFloat keys, 12, nEnt\z
			PokeFloat keys, 16, nEnt\sx
			PokeFloat keys, 20, nEnt\sy
			PokeFloat keys, 24, nEnt\sz
			PokeFloat keys, 28, nEnt\q[0]
			PokeFloat keys, 32, nEnt\q[1]
			PokeFloat keys, 36, nEnt\q[2]
			PokeFloat keys, 40, nEnt\q[3]
			
			If BankSize(keys) / 44 > a\length Then a\length = BankSize(keys)
		EndIf
	Next
	
	;Reference count
	PokeInt anims, BankSize(anims) - 4, 1
	
	Dim ANIM_AnimName_$(0)
End Function

Function CopyAnimation(root, src)
	Local a.bOGL_Animation = Object.bOGL_Animation GetEntityUserData(src, ANIM_private_UDSlot_)
	
	If ANIM_private_NewCounter_ = ANIM_ALLOC_TICKER Then ANIM_ClearUnused
	ANIM_private_NewCounter_ = ANIM_private_NewCounter_ + 1
	
	Local c.bOGL_Animation = New bOGL_Animation
	c\root = bOGL_EntList_(root)
	c\nodes = CreateBank(BankSize(a\nodes))
	c\length = a\length
	c\animMode = ANIM_MODE_STOP
	
	;Update the node list with new nodes
	Local p, child
	For p = BankSize(c\nodes) - 4 To 0 Step -4
		child = PeekInt(a\nodes, p)
		If child Then PokeInt c\nodes, p, GetChildByName(root, GetEntityName(child)) Else PokeInt c\nodes, p, 0
	Next
	
	;Reference count the keys
	c\keyFrames = a\keyFrames
	PokeInt c\keyFrames, BankSize(c\keyFrames) - 4, PeekInt(c\keyFrames, BankSize(c\keyFrames) - 4) + 1
	
	SetEntityUserData root, ANIM_private_UDSlot_, Handle c
End Function

; Literally copied directly from MD2.bb
; Ideally these common functions should be shared
Function Animate(ent, mode = ANIM_MODE_LOOP, speed# = 1.0, fF = 0, lF = -1, trans = 0)
	Local m.bOGL_Animation = Object.bOGL_Animation GetEntityUserData(ent, ANIM_private_UDSlot_)
	If fF < 0 Then fF = 0 : Else If fF > m\length - 1 Then fF = m\length - 1
	If lF < 0 Or lF > m\length - 1 Then lF = m\length - 1
	
	If speed <= 0.0 Then mode = ANIM_MODE_STOP
	If mode = ANIM_MODE_STOP Then speed = 0.0
	If fF > lF Then speed = -speed
	
	m\animSpd = speed
	m\seqS = fF
	m\seqE = lF
	
	If trans < 1
		m\animMode = mode
		m\nextMode = mode
		m\animTime = fF
		m\fromF = fF : m\toF = fF + 1
	Else
		m\trans = 1.0 / Float trans
		If Abs(m\animTime - fF) < m\trans
			m\nextMode = ANIM_MODE_TRANS
			m\animMode = mode
			m\animTime = fF
			m\fromF = fF : m\toF = fF + 1
		Else
			m\nextMode = mode
			m\animMode = ANIM_MODE_TRANS
			m\fromF = Floor(m\animTime) : m\toF = fF
			m\animTime = m\fromF
		EndIf
	EndIf
	
	Insert m Before First bOGL_Animation
End Function

Function SetAnimTime(ent, t#)
	Local a.bOGL_Animation = Object.bOGL_Animation GetEntityUserData(ent, ANIM_private_UDSlot_)
	a\animTime = t : a\animMode = ANIM_MODE_STOP
End Function

Function GetAnimTime(ent)
	Local a.bOGL_Animation = Object.bOGL_Animation GetEntityUserData(ent, ANIM_private_UDSlot_)
	Return a\animTime
End Function

Function GetNumFrames(ent)
	Local a.bOGL_Animation = Object.bOGL_Animation GetEntityUserData(ent, ANIM_private_UDSlot_)
	Return a\length
End Function

Function GetAnimMode(ent)
	Local a.bOGL_Animation = Object.bOGL_Animation GetEntityUserData(ent, ANIM_private_UDSlot_)
	Return a\animMode
End Function

; Safely check if an entity has an associated animation, so other commands don't crash
Function IsAnimated(ent)
	Local a.bOGL_Animation : For a = Each bOGL_Animation	;Need to do this because GetEntityUserData is unsafe
		If a\root = bOGL_EntList_(ent) Then Return True
	Next
End Function

Function ANIM_ClearUnused()
	Local m.bOGL_Animation
	For m = Each bOGL_Animation
		If m\root = Null
			FreeBank m\nodes
			If PeekInt(m\keyFrames, BankSize(m\keyFrames) - 4) < 2
				Local i, t = BankSize(m\keyFrames) - 8
				For i = 0 To t Step 4
					FreeBank PeekInt(m\keyFrames, i)
				Next
				FreeBank m\keyFrames
			Else
				PokeInt m\keyFrames, BankSize(m\keyFrames) - 4, PeekInt(m\keyFrames, BankSize(m\keyFrames) - 4) - 1
			EndIf
			Delete m
		EndIf
	Next
	ANIM_private_NewCounter_ = 0
End Function


; Internal
;==========

Function ANIM_LoadAnimDef_(bk, p[0], tgt, ID)
	Local st = p[0], maxSz = tgt - st
	If maxSz < 64 Then Return 0	;Minimum entity def header size
	
	Local sz = PeekInt(bk, st)	;Entity def size
	If sz > maxSz Then Return 0	;Check that the whole def fits within the range
	p[0] = p[0] + LOADER_AlignSz_(sz)	;Increment the entity pointer
	
	;Anim length and keyframes are just loaded dumbly since this is not an anim module
	Local aLen = PeekInt(bk, st + 48), kC = PeekInt(bk, st + 52)
	If aLen > ANIM_private_ALen_ Then ANIM_private_ALen_ = aLen
	
	Local nLen = PeekInt(bk, st + 56)		;Byte length of name string
	Local vertC = PeekInt(bk, st + 60)		;Number of vertices
	
	If vertC	;If it's a mesh, also read mesh-specific extended header
		If sz < 92 Then ANIM_private_LoadErr_ = 1 : Return 0		;At a minimum must have space for the header
	EndIf
	
	If kC < 0 Or nLen < 0 Then ANIM_private_LoadErr_ = 1 : Return 0		;Invalid, corrupt sizes
	
	;Last check that the def is large enough to hold all requested data (this is qhy keys are first: ignore the rest)
	sz = sz - (64 + (28 * (vertC <> 0)))
	If sz < kC * 44 + LOADER_AlignSz_(nLen)
		ANIM_private_LoadErr_ = 1 : Return 0
	EndIf
	;After this point, the def can definitely be loaded: everything fits in the required range
	
	st = st + 64 + (28 * (vertC <> 0))	;Inc past the header
	
	;Load keyframes
	Local keyFrames = CreateBank(kC * 44)
	CopyBank bk, st, keyFrames, 0, BankSize(keyFrames)
	st = st + LOADER_AlignSz_(kC * 44)
	
	;Load the name these frames belong to
	ANIM_AnimName_(ID) = LOADER_PeekChars_(bk, st, nLen)
	st = st + LOADER_AlignSz_(nLen)
	
	;Skip the rest (this is not the mesh module)
	Return keyFrames
End Function

Function ANIM_UpdateNodePositions_(m.bOGL_Animation)
	Local eCount = BankSize(m\nodes) / 4, i
	
	For i = 0 To eCount - 1
		Local pol0#, pol1#, node.bOGL_Ent = bOGL_EntList_(PeekInt(m\nodes, i * 4))
		If node <> Null
			Local frames = PeekInt(m\keyFrames, i * 4), fC = BankSize(frames) / ANIM_KF_SIZE, f, fromF, toF, tP, fP
			For f = fC - 1 To 0 Step -1		;Note that these assume the keys are ordered
				fromF = PeekInt(frames, f * ANIM_KF_SIZE)
				If fromF <= m\fromF Then fP = f * ANIM_KF_SIZE : Exit
			Next
			For f = 0 To fC - 1
				toF = PeekInt(frames, f * ANIM_KF_SIZE)
				If toF >= m\toF Then tP = f * ANIM_KF_SIZE : Exit
			Next
			
			If m\animMode <> ANIM_MODE_TRANS
				If toF = fromF Then pol0 = 0. Else pol0 = Abs(m\animTime - fromF) / Float Abs(toF - fromF)
			Else
				pol0 = m\animTime - m\fromF
			EndIf
			pol1 = 1. - pol0
			
			node\x = pol1 * PeekFloat(frames, fP + 4) + pol0 * PeekFloat(frames, tP + 4)
			node\y = pol1 * PeekFloat(frames, fP + 8) + pol0 * PeekFloat(frames, tP + 8)
			node\z = pol1 * PeekFloat(frames, fP + 12) + pol0 * PeekFloat(frames, tP + 12)
			
			node\sx = pol1 * PeekFloat(frames, fP + 16) + pol0 * PeekFloat(frames, tP + 16)
			node\sy = pol1 * PeekFloat(frames, fP + 20) + pol0 * PeekFloat(frames, tP + 20)
			node\sz = pol1 * PeekFloat(frames, fP + 24) + pol0 * PeekFloat(frames, tP + 24)
			
			node\q[0] = pol1 * PeekFloat(frames, fP + 28) + pol0 * PeekFloat(frames, tP + 28)
			node\q[1] = pol1 * PeekFloat(frames, fP + 32) + pol0 * PeekFloat(frames, tP + 32)
			node\q[2] = pol1 * PeekFloat(frames, fP + 36) + pol0 * PeekFloat(frames, tP + 36)
			node\q[3] = pol1 * PeekFloat(frames, fP + 40) + pol0 * PeekFloat(frames, tP + 40)
			bOGL_NormaliseQuat_ node\q : node\Rv = False
			
			bOGL_InvalidateGlobalPosition_ node, True
		EndIf
	Next
End Function

; This is literally identical to MD2_UpdateAnimation_
; A sensible coder would factor out the shared structures to an anim module...
Function ANIM_UpdateAnimation_(m.bOGL_Animation)
	If m\animMode <> ANIM_MODE_TRANS
		m\animTime = m\animTime + m\animSpd
		
		If (m\animSpd > 0 And m\animTime > m\seqE) Or (m\animSpd < 0 And m\animTime < m\seqS)
			Select m\animMode
				Case ANIM_MODE_ONCE
					m\animMode = ANIM_MODE_STOP
					
				Case ANIM_MODE_LOOP
					If m\nextMode = ANIM_MODE_TRANS
						m\animTime = m\seqE
						m\fromF = m\seqE : m\toF = m\seqS
						m\nextMode = ANIM_MODE_LOOP : m\animMode = ANIM_MODE_TRANS
					Else
						m\animTime = m\seqS
						m\fromF = m\seqS : m\toF = m\seqS
					EndIf
					
				Case ANIM_MODE_PING
					If m\animSpd > 0 Then m\animTime = m\seqE : Else m\animTime = m\seqS
					m\fromF = Int m\animTime : m\toF = Int m\animTime
					m\animSpd = -m\animSpd
			End Select
		Else
			If m\animSpd >= 0
				m\fromF = Floor(m\animTime) : m\toF = Ceil(m\animTime)
			Else
				m\toF = Floor(m\animTime) : m\fromF = Ceil(m\animTime)
			EndIf
		EndIf
	Else
		m\animTime = m\animTime + m\trans
		If m\animTime >= m\fromF + 1
			m\animMode = m\nextMode
			m\nextMode = ANIM_MODE_TRANS
			m\fromF = m\seqS : m\toF = m\fromF + (1 * Sgn(m\animSpd))
			m\animTime = Float m\seqS
		EndIf
	EndIf
End Function


;~IDEal Editor Parameters:
;~F#14#28#30#45#4C#96#B2#D6#DB#E0#E5#EB#F1#109#134#160
;~C#BlitzPlus

; MD2 Addon module for bOGL
;===========================


; This module provides functions to load and use MD2 meshes and animations

; MD2s may be loaded directly out of banks, and may be loaded as subsections
; of an existing mesh in order to share surface data
; Separate movement commands are included for manipulating submesh MD2s


Include "bOGL\bOGL.bb"


Type bOGL_MD2Model
	Field mesh.bOGL_Ent, bone.bOGL_Ent
	Field numFrames, frames
	Field numVerts, firstVert
	Field animMode, animTime#
	Field animSpd#, trans#
	Field fromF, toF
	Field seqS, seqE, nextMode
	Field autoMove
End Type


Const MD2_MODE_STOP = 0, MD2_MODE_LOOP = 1, MD2_MODE_PING = 2, MD2_MODE_ONCE = 3, MD2_MODE_TRANS = 4
Const MD2_FRAME_SIZE = 44
Global MD2_private_UDSlot_ = -1, MD2_private_TexName_$
Global MD2_buffer_.bOGL_MD2Model, MD2_header_.bOGL_MD2Model
Dim MD2_VertN_#(0, 0), MD2_VertC_(0)


; Interface
;===========

Function InitMD2Addon()		;Only call this once per program
	MD2_private_UDSlot_ = RegisterEntityUserDataSlot()
	MD2_header_ = New bOGL_MD2Model
	MD2_buffer_ = New bOGL_MD2Model
	MD2_InitVertexNormals_
End Function

Function UpdateMD2Anims()
	Insert MD2_header_ After Last bOGL_MD2Model
	Local m.bOGL_MD2Model, doClear = False : For m = Each bOGL_MD2Model
		If m = MD2_buffer_ Then Exit
		
		If m\animMode = MD2_MODE_STOP Or m\mesh = Null Or m\bone = Null
			Local n.bOGL_MD2Model = m
			m = Before m
			Insert n After Last bOGL_MD2Model
			If n\mesh = Null Or n\bone = Null Then doClear = True
		Else
			MD2_UpdateFramePosition_ m, m\fromF, m\toF
			MD2_UpdateAnimation_ m
		EndIf
		
		If m = Null Then m = MD2_header_ : Insert m Before First bOGL_MD2Model
	Next
	If doClear Then MD2_ClearUnused
	Insert MD2_header_ Before First bOGL_MD2Model
End Function

Function LoadMD2Model(file$, parent = 0)
	If FileType(file) <> 1 Then Return 0
	Local sz = FileSize(file), bk = CreateBank(sz), mesh = CreateMesh(parent)
	Local f = ReadFile(file)
	ReadBytes bk, f, 0, sz
	CloseFile f
	Local bone = LoadMD2SubMesh(bk, 0, sz, mesh, False)
	If Not bone
		FreeEntity mesh : mesh = 0
	Else
		SetEntityUserData mesh, MD2_private_UDSlot_, GetEntityUserData(bone, MD2_private_UDSlot_)
		If MD2_private_TexName_ <> ""
			Local tex = LoadTexture(MD2_private_TexName_, 0)
			If tex Then EntityTexture mesh, tex : FreeTexture tex
		EndIf
	EndIf
	FreeBank bk : Return mesh	;Return 0 on failure
End Function

Function LoadMD2SubMesh(bk, st, sz, targetMesh, doAutoMove = True)
	If sz < 68 Then Return 0	;Size of MD2 header; return 0 for invalid (too short) data
	If st + sz < BankSize(bk) Then Return 0
	If PeekInt(bk, st) <> 844121161 Or PeekInt(bk, st + 4) <> 8 Then Return 0	;Magic number & format version
	If sz < st + PeekInt(bk, st + 64) Then Return 0		;Size of MD2 according to header
	;The data is all there, so we'll assume something valid can be made out of it
	
	Local skinWidth# = Float PeekInt(bk, st + 8), skinHeight# = Float PeekInt(bk, st + 12)
	Local frameSize = PeekInt(bk, st + 16), numSkins = PeekInt(bk, st + 20)
	Local numVerts = PeekInt(bk, st + 24), numTexCoords = PeekInt(bk, st + 28)
	Local numTris = PeekInt(bk, st + 32), numFrames = PeekInt(bk, st + 40)	;Skip GL commands
	Local ofsSkins = st + PeekInt(bk, st + 44), ofsTexCoords = st + PeekInt(bk, st + 48)
	Local ofsTris = st + PeekInt(bk, st + 52), ofsFrames = st + PeekInt(bk, st + 56)
	
	;Apply value clamps
	If skinWidth < 1 Then skinWidth = 1
	If skinHeight < 1 Then skinHeight = 1
	If frameSize < 0 Or numSkins < 0 Or numVerts < 0 Or numTexCoords < 0 Or numFrames < 0 Then Return 0
	
	Local ofsLimit = st + sz
	If ofsSkins + numSkins * 64 > ofsLimit Or ofsSkins < 68 Then Return 0
	If ofsTexCoords + numTexCoords * 4 > ofsLimit Or ofsTexCoords < 68 Then Return 0
	If ofsTris + numTris * 12 > ofsLimit Or ofsTris < 68 Then Return 0
	If ofsFrames + numFrames * frameSize > ofsLimit Or ofsFrames < 68 Then Return 0
	;After this point, the model will get loaded, although errors might cause corruptions
	
	Local bone = CreatePivot(targetMesh), firstVertex = CountVertices(targetMesh)
	Local m.bOGL_MD2Model = MD2_NewModel_(bone, targetMesh, firstVertex, numVerts, numFrames), i
	
	For i = 0 To numFrames - 1
		CopyBank bk, ofsFrames + frameSize * i, m\frames, MD2_FRAME_SIZE * i, MD2_FRAME_SIZE - 4
		Local fr = CreateBank(frameSize - (MD2_FRAME_SIZE - 4))
		CopyBank bk, ofsFrames + frameSize * i + (MD2_FRAME_SIZE - 4), fr, 0, BankSize(fr)
		PokeInt m\frames, MD2_FRAME_SIZE * i + (MD2_FRAME_SIZE - 4), fr
	Next
	
	For i = 0 To numVerts - 1
		AddVertex targetMesh, 0, 0, 0
	Next
	
	Dim MD2_VertC_(numVerts)
	
	For i = 0 To numTris - 1
		Local v0 = PeekShort(bk, ofsTris + i * 12), v1 = PeekShort(bk, ofsTris + i * 12 + 2), v2 = PeekShort(bk, ofsTris + i * 12 + 4)
		
		;MD2_SetVertexUVs_ will create new vertices if necessary and add their frame data to handle welds
		Local u# = PeekShort(bk, ofsTexCoords + PeekShort(bk, ofsTris + i * 12 + 6) * 4) / skinWidth
		Local v# = PeekShort(bk, ofsTexCoords + PeekShort(bk, ofsTris + i * 12 + 6) * 4 + 2) / skinHeight
		v0 = MD2_SetVertexUVs_(m, v0, u, v)
		
		u = PeekShort(bk, ofsTexCoords + PeekShort(bk, ofsTris + i * 12 + 8) * 4) / skinWidth
		v = PeekShort(bk, ofsTexCoords + PeekShort(bk, ofsTris + i * 12 + 8) * 4 + 2) / skinHeight
		v1 = MD2_SetVertexUVs_(m, v1, u, v)
		
		u = PeekShort(bk, ofsTexCoords + PeekShort(bk, ofsTris + i * 12 + 10) * 4) / skinWidth
		v = PeekShort(bk, ofsTexCoords + PeekShort(bk, ofsTris + i * 12 + 10) * 4 + 2) / skinHeight
		v2 = MD2_SetVertexUVs_(m, v2, u, v)
		
		AddTriangle targetMesh, v0, v1, v2
	Next
	
	Dim MD2_VertC_(0)
	
	MD2_private_TexName_ = ""
	If numSkins		;Note that bOGL only supports single-texturing
		For i = 0 To 63
			If Not PeekByte(bk, ofsSkins + i) Then Exit
			MD2_private_TexName_ = MD2_private_TexName_ + Chr(PeekByte(bk, ofsSkins + i))
		Next
	EndIf
	
	MD2_UpdateFramePosition_ m, 0, 0
	Return bone
End Function

Function AnimateMD2(ent, mode = MD2_MODE_LOOP, speed# = 1.0, fF = 0, lF = -1, trans = 0)
	Local m.bOGL_MD2Model = Object.bOGL_MD2Model GetEntityUserData(ent, MD2_private_UDSlot_)
	If fF < 0 Then fF = 0 : Else If fF > m\numFrames - 1 Then fF = m\numFrames - 1
	If lF < 0 Or lF > m\numFrames - 1 Then lF = m\numFrames - 1
	
	If speed <= 0.0 Then mode = MD2_MODE_STOP
	If mode = MD2_MODE_STOP Then speed = 0.0
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
		If Abs(m\animTime - fF) < trans
			m\nextMode = MD2_MODE_TRANS
			m\animMode = mode
			m\animTime = fF
			m\fromF = fF : m\toF = fF + 1
		Else
			m\nextMode = mode
			m\animMode = MD2_MODE_TRANS
			m\fromF = Floor(m\animTime) : m\toF = fF
		EndIf
	EndIf
End Function

Function SetMD2AutoMove(ent, doAutoMove)
	Local m.bOGL_MD2Model = Object.bOGL_MD2Model GetEntityUserData(ent, MD2_private_UDSlot_)
	m\autoMove = doAutoMove
End Function

Function SetMD2AnimTime(ent, time#)
	Local m.bOGL_MD2Model = Object.bOGL_MD2Model GetEntityUserData(ent, MD2_private_UDSlot_)
	If time < 0 Then time = 0 : Else If time > m\numFrames - 1 Then time = m\numFrames - 1
	m\animTime = time : m\animMode = MD2_MODE_STOP
	MD2_UpdateFramePosition_ m, Floor(time), Ceil(time)
End Function

Function GetMD2SeqByName(out[1], ent, name$)
	Local m.bOGL_MD2Model = Object.bOGL_MD2Model GetEntityUserData(ent, MD2_private_UDSlot_)
	Local c, n$, char
	out[0] = -1 : out[1] = -1
	Local i : For i = 0 To m\numFrames - 1
		n = ""
		For c = 0 To 15
			char = PeekByte(m\frames, i * MD2_FRAME_SIZE + 24 + c)
			If char = 0 Then Exit
			n = n + Chr(char)
		Next
		If Left(n, Len(name)) = name Then out[0] = i : Exit
	Next
	If out[0] = -1 Then Return
	For i = out[0] To m\numFrames - 1
		n = ""
		For c = 0 To 15
			char = PeekByte(m\frames, i * MD2_FRAME_SIZE + 24 + c)
			If char = 0 Then Exit
			n = n + Chr(char)
		Next
		If Left(n, Len(name)) <> name Then out[1] = i - 1 : Exit
	Next
End Function

Function MD2_ClearUnused()
	Local m.bOGL_MD2Model
	For m = Each bOGL_MD2Model
		If m\bone = Null Or m\mesh = Null
			If m <> MD2_header_ And m <> MD2_buffer_ Then MD2_FreeModel_ m
		EndIf
	Next
	MD2_private_NewCounter_ = 0
End Function


; Internal
;==========

Const MD2_ALLOC_TICKER = 25
Global MD2_private_NewCounter_

; Allocate a new MD2 instance. This also checks for MD2s attached to dead entities and removes them
Function MD2_NewModel_.bOGL_MD2Model(bone, mesh, fv, vc, fc)
	If MD2_private_NewCounter_ = MD2_ALLOC_TICKER Then MD2_ClearUnused
	MD2_private_NewCounter_ = MD2_private_NewCounter_ + 1
	Local m.bOGL_MD2Model = New bOGL_MD2Model
	Insert m Before First bOGL_MD2Model
	m\bone = bOGL_EntList_(bone) : m\mesh = bOGL_EntList_(mesh)
	m\numFrames = fc : m\frames = CreateBank(fc * MD2_FRAME_SIZE)
	m\numVerts = vc : m\firstVert = fv
	SetEntityUserData mesh, MD2_private_UDSlot_, 0
	SetEntityUserData bone, MD2_private_UDSlot_, Handle m
	Return m
End Function

Function MD2_FreeModel_(m.bOGL_MD2Model)
	Local i : For i = 0 To m\numFrames - 1
		FreeBank PeekInt(m\frames, (i + 1) * MD2_FRAME_SIZE - 4)
	Next
	FreeBank m\frames
	SetEntityUserData m\mesh\handler, MD2_private_UDSlot_, 0
	SetEntityUserData m\bone\handler, MD2_private_UDSlot_, 0
	Delete m
End Function

; Since MD2s store XYZ and UV data separately we may have to selectively unweld some vertices for correct results
Function MD2_SetVertexUVs_(m.bOGL_MD2Model, vi, u#, v#)
	Local vn = vi, i, fr
	
	If MD2_VertC_(vi - m\firstVert)		;If the vertex is being reused with *different* UVs...
		If VertexU(m\mesh\handler, vi) <> u Or VertexV(m\mesh\handler, vi) <> v
			
			vn = AddVertex(m\mesh\handler, 0, 0, 0)		;Create a sub for the new UVs
			m\numVerts = m\numVerts + 1
			
			For i = 1 To m\numFrames		;Add it to the end of each frame block with copied xyzn
				fr = PeekInt(m\frames, i * MD2_FRAME_SIZE - 4)
				ResizeBank fr, m\numVerts * 4
				PokeInt fr, (m\numVerts - 1) * 4, PeekInt(fr, (vi - m\firstVert) * 4)
			Next
		EndIf
	Else
		MD2_VertC_(vi - m\firstVert) = 1	;Mark vert as having been used
	EndIf
	
	VertexTexCoords m\mesh\handler, vn, u, v
	Return vn		;Return the new vertex index for use forming the triangle
End Function

Function MD2_UpdateFramePosition_(m.bOGL_MD2Model, pTime, nTime)
	Local tw1# = m\animTime - pTime, tw0# = 1 - tw1
	
	Local sx# = PeekFloat(m\frames, pTime * MD2_FRAME_SIZE) * tw0 + PeekFloat(m\frames, nTime * MD2_FRAME_SIZE) * tw1
	Local sy# = PeekFloat(m\frames, pTime * MD2_FRAME_SIZE + 4) * tw0 + PeekFloat(m\frames, nTime * MD2_FRAME_SIZE + 4) * tw1
	Local sz# = PeekFloat(m\frames, pTime * MD2_FRAME_SIZE + 8) * tw0 + PeekFloat(m\frames, nTime * MD2_FRAME_SIZE + 8) * tw1
	Local tx# = PeekFloat(m\frames, pTime * MD2_FRAME_SIZE + 12) * tw0 + PeekFloat(m\frames, nTime * MD2_FRAME_SIZE + 12) * tw1
	Local ty# = PeekFloat(m\frames, pTime * MD2_FRAME_SIZE + 16) * tw0 + PeekFloat(m\frames, nTime * MD2_FRAME_SIZE + 16) * tw1
	Local tz# = PeekFloat(m\frames, pTime * MD2_FRAME_SIZE + 20) * tw0 + PeekFloat(m\frames, nTime * MD2_FRAME_SIZE + 20) * tw1
	
	Local pFr = PeekInt(m\frames, pTime * MD2_FRAME_SIZE + 40), nFr = PeekInt(m\frames, nTime * MD2_FRAME_SIZE + 40)
	
	Local i, vt = m\numVerts - 1, pVal, nVal, vx#, vy#, vz#, vnx#, vny#, vnz#
	Local varr = m\mesh\m\vp, vptr, voff = m\firstVert, tfv#[2], bone = m\bone\handler, mesh = m\mesh\handler
	
	If m\autoMove
		For i = 0 To vt		;Loop is copied from below (to inline), tForm inserted
			pVal = PeekInt(pFr, i * 4) : nVal = PeekInt(nFr, i * 4)
			vx = ((pVal And $FF) * tw0 + (nVal And $FF) * tw1) * sx + tx
			vy = (((pVal And $FF00) Shr 8) * tw0 + ((nVal And $FF00) Shr 8) * tw1) * sy + ty
			vz = (((pVal And $FF0000) Shr 16) * tw0 + ((nVal And $FF0000) Shr 16) * tw1) * sz + tz
			
			vnx = MD2_VertN_((pVal And $FF000000) Shr 24, 0) * tw0 + MD2_VertN_((nVal And $FF000000) Shr 24, 0) * tw1
			vny = MD2_VertN_((pVal And $FF000000) Shr 24, 1) * tw0 + MD2_VertN_((nVal And $FF000000) Shr 24, 1) * tw1
			vnz = MD2_VertN_((pVal And $FF000000) Shr 24, 2) * tw0 + MD2_VertN_((nVal And $FF000000) Shr 24, 2) * tw1
			
			TFormPoint vx, vy, vz, bone, mesh, tfv
			
			vptr = (i + voff) * BOGL_VERT_STRIDE
			PokeFloat varr, vptr + 20, tfv[0] : PokeFloat varr, vptr + 24, tfv[1] : PokeFloat varr, vptr + 28, tfv[2]
			PokeFloat varr, vptr + 8, vnx : PokeFloat varr, vptr + 12, vny : PokeFloat varr, vptr + 16, vnz
		Next
	Else
		For i = 0 To vt
			pVal = PeekInt(pFr, i * 4) : nVal = PeekInt(nFr, i * 4)
			vx = ((pVal And $FF) * tw0 + (nVal And $FF) * tw1) * sx + tx
			vy = (((pVal And $FF00) Shr 8) * tw0 + ((nVal And $FF00) Shr 8) * tw1) * sy + ty
			vz = (((pVal And $FF0000) Shr 16) * tw0 + ((nVal And $FF0000) Shr 16) * tw1) * sz + tz
			
			vnx = MD2_VertN_((pVal And $FF000000) Shr 24, 0) * tw0 + MD2_VertN_((nVal And $FF000000) Shr 24, 0) * tw1
			vny = MD2_VertN_((pVal And $FF000000) Shr 24, 1) * tw0 + MD2_VertN_((nVal And $FF000000) Shr 24, 1) * tw1
			vnz = MD2_VertN_((pVal And $FF000000) Shr 24, 2) * tw0 + MD2_VertN_((nVal And $FF000000) Shr 24, 2) * tw1
			
			vptr = (i + voff) * BOGL_VERT_STRIDE
			PokeFloat varr, vptr + 20, vx : PokeFloat varr, vptr + 24, vy : PokeFloat varr, vptr + 28, vz
			PokeFloat varr, vptr + 8, vnx : PokeFloat varr, vptr + 12, vny : PokeFloat varr, vptr + 16, vnz
		Next
	EndIf
End Function

Function MD2_UpdateAnimation_(m.bOGL_MD2Model)
	If m\animMode <> MD2_MODE_TRANS
		m\animTime = m\animTime + m\animSpd
		
		If (m\animSpd > 0 And m\animTime > m\seqE) Or (m\animSpd < 0 And m\animTime < m\seqS)
			Select m\animMode
				Case MD2_MODE_ONCE
					m\animMode = MD2_MODE_STOP
					
				Case MD2_MODE_LOOP
					If m\nextMode = MD2_MODE_TRANS
						m\animTime = m\seqE
						m\fromF = m\seqE : m\toF = m\seqS
						m\nextMode = MD2_MODE_LOOP : m\animMode = MD2_MODE_TRANS
					Else
						m\animTime = m\seqS
						m\fromF = m\seqS : m\toF = m\seqS
					EndIf
					
				Case MD2_MODE_PING
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
			m\nextMode = MD2_MODE_TRANS
			m\fromF = m\seqS : m\toF = m\fromF + (1 * Sgn(m\animSpd))
			m\animTime = Float m\seqS
		EndIf
	EndIf
End Function

; Fill the vertex normals table (data taken from http://tfc.duke.free.fr/coding/src/anorms.h )
Function MD2_InitVertexNormals_()
	Dim MD2_VertN_#(161, 2)
	MD2_VertN_(  0, 0) = -0.525731 : MD2_VertN_(  0, 1) =   0.000000 : MD2_VertN_(  0, 2) =   0.850651
	MD2_VertN_(  1, 0) = -0.442863 : MD2_VertN_(  1, 1) =   0.238856 : MD2_VertN_(  1, 2) =   0.864188
	MD2_VertN_(  2, 0) = -0.295242 : MD2_VertN_(  2, 1) =   0.000000 : MD2_VertN_(  2, 2) =   0.955423
	MD2_VertN_(  3, 0) = -0.309017 : MD2_VertN_(  3, 1) =   0.500000 : MD2_VertN_(  3, 2) =   0.809017
	MD2_VertN_(  4, 0) = -0.162460 : MD2_VertN_(  4, 1) =   0.262866 : MD2_VertN_(  4, 2) =   0.951056
	MD2_VertN_(  5, 0) =  0.000000 : MD2_VertN_(  5, 1) =   0.000000 : MD2_VertN_(  5, 2) =   1.000000
	MD2_VertN_(  6, 0) =  0.000000 : MD2_VertN_(  6, 1) =   0.850651 : MD2_VertN_(  6, 2) =   0.525731
	MD2_VertN_(  7, 0) = -0.147621 : MD2_VertN_(  7, 1) =   0.716567 : MD2_VertN_(  7, 2) =   0.681718
	MD2_VertN_(  8, 0) =  0.147621 : MD2_VertN_(  8, 1) =   0.716567 : MD2_VertN_(  8, 2) =   0.681718
	MD2_VertN_(  9, 0) =  0.000000 : MD2_VertN_(  9, 1) =   0.525731 : MD2_VertN_(  9, 2) =   0.850651
	MD2_VertN_( 10, 0) =  0.309017 : MD2_VertN_( 10, 1) =   0.500000 : MD2_VertN_( 10, 2) =   0.809017
	MD2_VertN_( 11, 0) =  0.525731 : MD2_VertN_( 11, 1) =   0.000000 : MD2_VertN_( 11, 2) =   0.850651
	MD2_VertN_( 12, 0) =  0.295242 : MD2_VertN_( 12, 1) =   0.000000 : MD2_VertN_( 12, 2) =   0.955423
	MD2_VertN_( 13, 0) =  0.442863 : MD2_VertN_( 13, 1) =   0.238856 : MD2_VertN_( 13, 2) =   0.864188
	MD2_VertN_( 14, 0) =  0.162460 : MD2_VertN_( 14, 1) =   0.262866 : MD2_VertN_( 14, 2) =   0.951056
	MD2_VertN_( 15, 0) = -0.681718 : MD2_VertN_( 15, 1) =   0.147621 : MD2_VertN_( 15, 2) =   0.716567
	MD2_VertN_( 16, 0) = -0.809017 : MD2_VertN_( 16, 1) =   0.309017 : MD2_VertN_( 16, 2) =   0.500000
	MD2_VertN_( 17, 0) = -0.587785 : MD2_VertN_( 17, 1) =   0.425325 : MD2_VertN_( 17, 2) =   0.688191
	MD2_VertN_( 18, 0) = -0.850651 : MD2_VertN_( 18, 1) =   0.525731 : MD2_VertN_( 18, 2) =   0.000000
	MD2_VertN_( 19, 0) = -0.864188 : MD2_VertN_( 19, 1) =   0.442863 : MD2_VertN_( 19, 2) =   0.238856
	MD2_VertN_( 20, 0) = -0.716567 : MD2_VertN_( 20, 1) =   0.681718 : MD2_VertN_( 20, 2) =   0.147621
	MD2_VertN_( 21, 0) = -0.688191 : MD2_VertN_( 21, 1) =   0.587785 : MD2_VertN_( 21, 2) =   0.425325
	MD2_VertN_( 22, 0) = -0.500000 : MD2_VertN_( 22, 1) =   0.809017 : MD2_VertN_( 22, 2) =   0.309017
	MD2_VertN_( 23, 0) = -0.238856 : MD2_VertN_( 23, 1) =   0.864188 : MD2_VertN_( 23, 2) =   0.442863
	MD2_VertN_( 24, 0) = -0.425325 : MD2_VertN_( 24, 1) =   0.688191 : MD2_VertN_( 24, 2) =   0.587785
	MD2_VertN_( 25, 0) = -0.716567 : MD2_VertN_( 25, 1) =   0.681718 : MD2_VertN_( 25, 2) =  -0.147621
	MD2_VertN_( 26, 0) = -0.500000 : MD2_VertN_( 26, 1) =   0.809017 : MD2_VertN_( 26, 2) =  -0.309017
	MD2_VertN_( 27, 0) = -0.525731 : MD2_VertN_( 27, 1) =   0.850651 : MD2_VertN_( 27, 2) =   0.000000
	MD2_VertN_( 28, 0) =  0.000000 : MD2_VertN_( 28, 1) =   0.850651 : MD2_VertN_( 28, 2) =  -0.525731
	MD2_VertN_( 29, 0) = -0.238856 : MD2_VertN_( 29, 1) =   0.864188 : MD2_VertN_( 29, 2) =  -0.442863
	MD2_VertN_( 30, 0) =  0.000000 : MD2_VertN_( 30, 1) =   0.955423 : MD2_VertN_( 30, 2) =  -0.295242
	MD2_VertN_( 31, 0) = -0.262866 : MD2_VertN_( 31, 1) =   0.951056 : MD2_VertN_( 31, 2) =  -0.162460
	MD2_VertN_( 32, 0) =  0.000000 : MD2_VertN_( 32, 1) =   1.000000 : MD2_VertN_( 32, 2) =   0.000000
	MD2_VertN_( 33, 0) =  0.000000 : MD2_VertN_( 33, 1) =   0.955423 : MD2_VertN_( 33, 2) =   0.295242
	MD2_VertN_( 34, 0) = -0.262866 : MD2_VertN_( 34, 1) =   0.951056 : MD2_VertN_( 34, 2) =   0.162460
	MD2_VertN_( 35, 0) =  0.238856 : MD2_VertN_( 35, 1) =   0.864188 : MD2_VertN_( 35, 2) =   0.442863
	MD2_VertN_( 36, 0) =  0.262866 : MD2_VertN_( 36, 1) =   0.951056 : MD2_VertN_( 36, 2) =   0.162460
	MD2_VertN_( 37, 0) =  0.500000 : MD2_VertN_( 37, 1) =   0.809017 : MD2_VertN_( 37, 2) =   0.309017
	MD2_VertN_( 38, 0) =  0.238856 : MD2_VertN_( 38, 1) =   0.864188 : MD2_VertN_( 38, 2) =  -0.442863
	MD2_VertN_( 39, 0) =  0.262866 : MD2_VertN_( 39, 1) =   0.951056 : MD2_VertN_( 39, 2) =  -0.162460
	MD2_VertN_( 40, 0) =  0.500000 : MD2_VertN_( 40, 1) =   0.809017 : MD2_VertN_( 40, 2) =  -0.309017
	MD2_VertN_( 41, 0) =  0.850651 : MD2_VertN_( 41, 1) =   0.525731 : MD2_VertN_( 41, 2) =   0.000000
	MD2_VertN_( 42, 0) =  0.716567 : MD2_VertN_( 42, 1) =   0.681718 : MD2_VertN_( 42, 2) =   0.147621
	MD2_VertN_( 43, 0) =  0.716567 : MD2_VertN_( 43, 1) =   0.681718 : MD2_VertN_( 43, 2) =  -0.147621
	MD2_VertN_( 44, 0) =  0.525731 : MD2_VertN_( 44, 1) =   0.850651 : MD2_VertN_( 44, 2) =   0.000000
	MD2_VertN_( 45, 0) =  0.425325 : MD2_VertN_( 45, 1) =   0.688191 : MD2_VertN_( 45, 2) =   0.587785
	MD2_VertN_( 46, 0) =  0.864188 : MD2_VertN_( 46, 1) =   0.442863 : MD2_VertN_( 46, 2) =   0.238856
	MD2_VertN_( 47, 0) =  0.688191 : MD2_VertN_( 47, 1) =   0.587785 : MD2_VertN_( 47, 2) =   0.425325
	MD2_VertN_( 48, 0) =  0.809017 : MD2_VertN_( 48, 1) =   0.309017 : MD2_VertN_( 48, 2) =   0.500000
	MD2_VertN_( 49, 0) =  0.681718 : MD2_VertN_( 49, 1) =   0.147621 : MD2_VertN_( 49, 2) =   0.716567
	MD2_VertN_( 50, 0) =  0.587785 : MD2_VertN_( 50, 1) =   0.425325 : MD2_VertN_( 50, 2) =   0.688191
	MD2_VertN_( 51, 0) =  0.955423 : MD2_VertN_( 51, 1) =   0.295242 : MD2_VertN_( 51, 2) =   0.000000
	MD2_VertN_( 52, 0) =  1.000000 : MD2_VertN_( 52, 1) =   0.000000 : MD2_VertN_( 52, 2) =   0.000000
	MD2_VertN_( 53, 0) =  0.951056 : MD2_VertN_( 53, 1) =   0.162460 : MD2_VertN_( 53, 2) =   0.262866
	MD2_VertN_( 54, 0) =  0.850651 : MD2_VertN_( 54, 1) =  -0.525731 : MD2_VertN_( 54, 2) =   0.000000
	MD2_VertN_( 55, 0) =  0.955423 : MD2_VertN_( 55, 1) =  -0.295242 : MD2_VertN_( 55, 2) =   0.000000
	MD2_VertN_( 56, 0) =  0.864188 : MD2_VertN_( 56, 1) =  -0.442863 : MD2_VertN_( 56, 2) =   0.238856
	MD2_VertN_( 57, 0) =  0.951056 : MD2_VertN_( 57, 1) =  -0.162460 : MD2_VertN_( 57, 2) =   0.262866
	MD2_VertN_( 58, 0) =  0.809017 : MD2_VertN_( 58, 1) =  -0.309017 : MD2_VertN_( 58, 2) =   0.500000
	MD2_VertN_( 59, 0) =  0.681718 : MD2_VertN_( 59, 1) =  -0.147621 : MD2_VertN_( 59, 2) =   0.716567
	MD2_VertN_( 60, 0) =  0.850651 : MD2_VertN_( 60, 1) =   0.000000 : MD2_VertN_( 60, 2) =   0.525731
	MD2_VertN_( 61, 0) =  0.864188 : MD2_VertN_( 61, 1) =   0.442863 : MD2_VertN_( 61, 2) =  -0.238856
	MD2_VertN_( 62, 0) =  0.809017 : MD2_VertN_( 62, 1) =   0.309017 : MD2_VertN_( 62, 2) =  -0.500000
	MD2_VertN_( 63, 0) =  0.951056 : MD2_VertN_( 63, 1) =   0.162460 : MD2_VertN_( 63, 2) =  -0.262866
	MD2_VertN_( 64, 0) =  0.525731 : MD2_VertN_( 64, 1) =   0.000000 : MD2_VertN_( 64, 2) =  -0.850651
	MD2_VertN_( 65, 0) =  0.681718 : MD2_VertN_( 65, 1) =   0.147621 : MD2_VertN_( 65, 2) =  -0.716567
	MD2_VertN_( 66, 0) =  0.681718 : MD2_VertN_( 66, 1) =  -0.147621 : MD2_VertN_( 66, 2) =  -0.716567
	MD2_VertN_( 67, 0) =  0.850651 : MD2_VertN_( 67, 1) =   0.000000 : MD2_VertN_( 67, 2) =  -0.525731
	MD2_VertN_( 68, 0) =  0.809017 : MD2_VertN_( 68, 1) =  -0.309017 : MD2_VertN_( 68, 2) =  -0.500000
	MD2_VertN_( 69, 0) =  0.864188 : MD2_VertN_( 69, 1) =  -0.442863 : MD2_VertN_( 69, 2) =  -0.238856
	MD2_VertN_( 70, 0) =  0.951056 : MD2_VertN_( 70, 1) =  -0.162460 : MD2_VertN_( 70, 2) =  -0.262866
	MD2_VertN_( 71, 0) =  0.147621 : MD2_VertN_( 71, 1) =   0.716567 : MD2_VertN_( 71, 2) =  -0.681718
	MD2_VertN_( 72, 0) =  0.309017 : MD2_VertN_( 72, 1) =   0.500000 : MD2_VertN_( 72, 2) =  -0.809017
	MD2_VertN_( 73, 0) =  0.425325 : MD2_VertN_( 73, 1) =   0.688191 : MD2_VertN_( 73, 2) =  -0.587785
	MD2_VertN_( 74, 0) =  0.442863 : MD2_VertN_( 74, 1) =   0.238856 : MD2_VertN_( 74, 2) =  -0.864188
	MD2_VertN_( 75, 0) =  0.587785 : MD2_VertN_( 75, 1) =   0.425325 : MD2_VertN_( 75, 2) =  -0.688191
	MD2_VertN_( 76, 0) =  0.688191 : MD2_VertN_( 76, 1) =   0.587785 : MD2_VertN_( 76, 2) =  -0.425325
	MD2_VertN_( 77, 0) = -0.147621 : MD2_VertN_( 77, 1) =   0.716567 : MD2_VertN_( 77, 2) =  -0.681718
	MD2_VertN_( 78, 0) = -0.309017 : MD2_VertN_( 78, 1) =   0.500000 : MD2_VertN_( 78, 2) =  -0.809017
	MD2_VertN_( 79, 0) =  0.000000 : MD2_VertN_( 79, 1) =   0.525731 : MD2_VertN_( 79, 2) =  -0.850651
	MD2_VertN_( 80, 0) = -0.525731 : MD2_VertN_( 80, 1) =   0.000000 : MD2_VertN_( 80, 2) =  -0.850651
	MD2_VertN_( 81, 0) = -0.442863 : MD2_VertN_( 81, 1) =   0.238856 : MD2_VertN_( 81, 2) =  -0.864188
	MD2_VertN_( 82, 0) = -0.295242 : MD2_VertN_( 82, 1) =   0.000000 : MD2_VertN_( 82, 2) =  -0.955423
	MD2_VertN_( 83, 0) = -0.162460 : MD2_VertN_( 83, 1) =   0.262866 : MD2_VertN_( 83, 2) =  -0.951056
	MD2_VertN_( 84, 0) =  0.000000 : MD2_VertN_( 84, 1) =   0.000000 : MD2_VertN_( 84, 2) =  -1.000000
	MD2_VertN_( 85, 0) =  0.295242 : MD2_VertN_( 85, 1) =   0.000000 : MD2_VertN_( 85, 2) =  -0.955423
	MD2_VertN_( 86, 0) =  0.162460 : MD2_VertN_( 86, 1) =   0.262866 : MD2_VertN_( 86, 2) =  -0.951056
	MD2_VertN_( 87, 0) = -0.442863 : MD2_VertN_( 87, 1) =  -0.238856 : MD2_VertN_( 87, 2) =  -0.864188
	MD2_VertN_( 88, 0) = -0.309017 : MD2_VertN_( 88, 1) =  -0.500000 : MD2_VertN_( 88, 2) =  -0.809017
	MD2_VertN_( 89, 0) = -0.162460 : MD2_VertN_( 89, 1) =  -0.262866 : MD2_VertN_( 89, 2) =  -0.951056
	MD2_VertN_( 90, 0) =  0.000000 : MD2_VertN_( 90, 1) =  -0.850651 : MD2_VertN_( 90, 2) =  -0.525731
	MD2_VertN_( 91, 0) = -0.147621 : MD2_VertN_( 91, 1) =  -0.716567 : MD2_VertN_( 91, 2) =  -0.681718
	MD2_VertN_( 92, 0) =  0.147621 : MD2_VertN_( 92, 1) =  -0.716567 : MD2_VertN_( 92, 2) =  -0.681718
	MD2_VertN_( 93, 0) =  0.000000 : MD2_VertN_( 93, 1) =  -0.525731 : MD2_VertN_( 93, 2) =  -0.850651
	MD2_VertN_( 94, 0) =  0.309017 : MD2_VertN_( 94, 1) =  -0.500000 : MD2_VertN_( 94, 2) =  -0.809017
	MD2_VertN_( 95, 0) =  0.442863 : MD2_VertN_( 95, 1) =  -0.238856 : MD2_VertN_( 95, 2) =  -0.864188
	MD2_VertN_( 96, 0) =  0.162460 : MD2_VertN_( 96, 1) =  -0.262866 : MD2_VertN_( 96, 2) =  -0.951056
	MD2_VertN_( 97, 0) =  0.238856 : MD2_VertN_( 97, 1) =  -0.864188 : MD2_VertN_( 97, 2) =  -0.442863
	MD2_VertN_( 98, 0) =  0.500000 : MD2_VertN_( 98, 1) =  -0.809017 : MD2_VertN_( 98, 2) =  -0.309017
	MD2_VertN_( 99, 0) =  0.425325 : MD2_VertN_( 99, 1) =  -0.688191 : MD2_VertN_( 99, 2) =  -0.587785
	MD2_VertN_(100, 0) =  0.716567 : MD2_VertN_(100, 1) =  -0.681718 : MD2_VertN_(100, 2) =  -0.147621
	MD2_VertN_(101, 0) =  0.688191 : MD2_VertN_(101, 1) =  -0.587785 : MD2_VertN_(101, 2) =  -0.425325
	MD2_VertN_(102, 0) =  0.587785 : MD2_VertN_(102, 1) =  -0.425325 : MD2_VertN_(102, 2) =  -0.688191
	MD2_VertN_(103, 0) =  0.000000 : MD2_VertN_(103, 1) =  -0.955423 : MD2_VertN_(103, 2) =  -0.295242
	MD2_VertN_(104, 0) =  0.000000 : MD2_VertN_(104, 1) =  -1.000000 : MD2_VertN_(104, 2) =   0.000000
	MD2_VertN_(105, 0) =  0.262866 : MD2_VertN_(105, 1) =  -0.951056 : MD2_VertN_(105, 2) =  -0.162460
	MD2_VertN_(106, 0) =  0.000000 : MD2_VertN_(106, 1) =  -0.850651 : MD2_VertN_(106, 2) =   0.525731
	MD2_VertN_(107, 0) =  0.000000 : MD2_VertN_(107, 1) =  -0.955423 : MD2_VertN_(107, 2) =   0.295242
	MD2_VertN_(108, 0) =  0.238856 : MD2_VertN_(108, 1) =  -0.864188 : MD2_VertN_(108, 2) =   0.442863
	MD2_VertN_(109, 0) =  0.262866 : MD2_VertN_(109, 1) =  -0.951056 : MD2_VertN_(109, 2) =   0.162460
	MD2_VertN_(110, 0) =  0.500000 : MD2_VertN_(110, 1) =  -0.809017 : MD2_VertN_(110, 2) =   0.309017
	MD2_VertN_(111, 0) =  0.716567 : MD2_VertN_(111, 1) =  -0.681718 : MD2_VertN_(111, 2) =   0.147621
	MD2_VertN_(112, 0) =  0.525731 : MD2_VertN_(112, 1) =  -0.850651 : MD2_VertN_(112, 2) =   0.000000
	MD2_VertN_(113, 0) = -0.238856 : MD2_VertN_(113, 1) =  -0.864188 : MD2_VertN_(113, 2) =  -0.442863
	MD2_VertN_(114, 0) = -0.500000 : MD2_VertN_(114, 1) =  -0.809017 : MD2_VertN_(114, 2) =  -0.309017
	MD2_VertN_(115, 0) = -0.262866 : MD2_VertN_(115, 1) =  -0.951056 : MD2_VertN_(115, 2) =  -0.162460
	MD2_VertN_(116, 0) = -0.850651 : MD2_VertN_(116, 1) =  -0.525731 : MD2_VertN_(116, 2) =   0.000000
	MD2_VertN_(117, 0) = -0.716567 : MD2_VertN_(117, 1) =  -0.681718 : MD2_VertN_(117, 2) =  -0.147621
	MD2_VertN_(118, 0) = -0.716567 : MD2_VertN_(118, 1) =  -0.681718 : MD2_VertN_(118, 2) =   0.147621
	MD2_VertN_(119, 0) = -0.525731 : MD2_VertN_(119, 1) =  -0.850651 : MD2_VertN_(119, 2) =   0.000000
	MD2_VertN_(120, 0) = -0.500000 : MD2_VertN_(120, 1) =  -0.809017 : MD2_VertN_(120, 2) =   0.309017
	MD2_VertN_(121, 0) = -0.238856 : MD2_VertN_(121, 1) =  -0.864188 : MD2_VertN_(121, 2) =   0.442863
	MD2_VertN_(122, 0) = -0.262866 : MD2_VertN_(122, 1) =  -0.951056 : MD2_VertN_(122, 2) =   0.162460
	MD2_VertN_(123, 0) = -0.864188 : MD2_VertN_(123, 1) =  -0.442863 : MD2_VertN_(123, 2) =   0.238856
	MD2_VertN_(124, 0) = -0.809017 : MD2_VertN_(124, 1) =  -0.309017 : MD2_VertN_(124, 2) =   0.500000
	MD2_VertN_(125, 0) = -0.688191 : MD2_VertN_(125, 1) =  -0.587785 : MD2_VertN_(125, 2) =   0.425325
	MD2_VertN_(126, 0) = -0.681718 : MD2_VertN_(126, 1) =  -0.147621 : MD2_VertN_(126, 2) =   0.716567
	MD2_VertN_(127, 0) = -0.442863 : MD2_VertN_(127, 1) =  -0.238856 : MD2_VertN_(127, 2) =   0.864188
	MD2_VertN_(128, 0) = -0.587785 : MD2_VertN_(128, 1) =  -0.425325 : MD2_VertN_(128, 2) =   0.688191
	MD2_VertN_(129, 0) = -0.309017 : MD2_VertN_(129, 1) =  -0.500000 : MD2_VertN_(129, 2) =   0.809017
	MD2_VertN_(130, 0) = -0.147621 : MD2_VertN_(130, 1) =  -0.716567 : MD2_VertN_(130, 2) =   0.681718
	MD2_VertN_(131, 0) = -0.425325 : MD2_VertN_(131, 1) =  -0.688191 : MD2_VertN_(131, 2) =   0.587785
	MD2_VertN_(132, 0) = -0.162460 : MD2_VertN_(132, 1) =  -0.262866 : MD2_VertN_(132, 2) =   0.951056
	MD2_VertN_(133, 0) =  0.442863 : MD2_VertN_(133, 1) =  -0.238856 : MD2_VertN_(133, 2) =   0.864188
	MD2_VertN_(134, 0) =  0.162460 : MD2_VertN_(134, 1) =  -0.262866 : MD2_VertN_(134, 2) =   0.951056
	MD2_VertN_(135, 0) =  0.309017 : MD2_VertN_(135, 1) =  -0.500000 : MD2_VertN_(135, 2) =   0.809017
	MD2_VertN_(136, 0) =  0.147621 : MD2_VertN_(136, 1) =  -0.716567 : MD2_VertN_(136, 2) =   0.681718
	MD2_VertN_(137, 0) =  0.000000 : MD2_VertN_(137, 1) =  -0.525731 : MD2_VertN_(137, 2) =   0.850651
	MD2_VertN_(138, 0) =  0.425325 : MD2_VertN_(138, 1) =  -0.688191 : MD2_VertN_(138, 2) =   0.587785
	MD2_VertN_(139, 0) =  0.587785 : MD2_VertN_(139, 1) =  -0.425325 : MD2_VertN_(139, 2) =   0.688191
	MD2_VertN_(140, 0) =  0.688191 : MD2_VertN_(140, 1) =  -0.587785 : MD2_VertN_(140, 2) =   0.425325
	MD2_VertN_(141, 0) = -0.955423 : MD2_VertN_(141, 1) =   0.295242 : MD2_VertN_(141, 2) =   0.000000
	MD2_VertN_(142, 0) = -0.951056 : MD2_VertN_(142, 1) =   0.162460 : MD2_VertN_(142, 2) =   0.262866
	MD2_VertN_(143, 0) = -1.000000 : MD2_VertN_(143, 1) =   0.000000 : MD2_VertN_(143, 2) =   0.000000
	MD2_VertN_(144, 0) = -0.850651 : MD2_VertN_(144, 1) =   0.000000 : MD2_VertN_(144, 2) =   0.525731
	MD2_VertN_(145, 0) = -0.955423 : MD2_VertN_(145, 1) =  -0.295242 : MD2_VertN_(145, 2) =   0.000000
	MD2_VertN_(146, 0) = -0.951056 : MD2_VertN_(146, 1) =  -0.162460 : MD2_VertN_(146, 2) =   0.262866
	MD2_VertN_(147, 0) = -0.864188 : MD2_VertN_(147, 1) =   0.442863 : MD2_VertN_(147, 2) =  -0.238856
	MD2_VertN_(148, 0) = -0.951056 : MD2_VertN_(148, 1) =   0.162460 : MD2_VertN_(148, 2) =  -0.262866
	MD2_VertN_(149, 0) = -0.809017 : MD2_VertN_(149, 1) =   0.309017 : MD2_VertN_(149, 2) =  -0.500000
	MD2_VertN_(150, 0) = -0.864188 : MD2_VertN_(150, 1) =  -0.442863 : MD2_VertN_(150, 2) =  -0.238856
	MD2_VertN_(151, 0) = -0.951056 : MD2_VertN_(151, 1) =  -0.162460 : MD2_VertN_(151, 2) =  -0.262866
	MD2_VertN_(152, 0) = -0.809017 : MD2_VertN_(152, 1) =  -0.309017 : MD2_VertN_(152, 2) =  -0.500000
	MD2_VertN_(153, 0) = -0.681718 : MD2_VertN_(153, 1) =   0.147621 : MD2_VertN_(153, 2) =  -0.716567
	MD2_VertN_(154, 0) = -0.681718 : MD2_VertN_(154, 1) =  -0.147621 : MD2_VertN_(154, 2) =  -0.716567
	MD2_VertN_(155, 0) = -0.850651 : MD2_VertN_(155, 1) =   0.000000 : MD2_VertN_(155, 2) =  -0.525731
	MD2_VertN_(156, 0) = -0.688191 : MD2_VertN_(156, 1) =   0.587785 : MD2_VertN_(156, 2) =  -0.425325
	MD2_VertN_(157, 0) = -0.587785 : MD2_VertN_(157, 1) =   0.425325 : MD2_VertN_(157, 2) =  -0.688191
	MD2_VertN_(158, 0) = -0.425325 : MD2_VertN_(158, 1) =   0.688191 : MD2_VertN_(158, 2) =  -0.587785
	MD2_VertN_(159, 0) = -0.425325 : MD2_VertN_(159, 1) =  -0.688191 : MD2_VertN_(159, 2) =  -0.587785
	MD2_VertN_(160, 0) = -0.587785 : MD2_VertN_(160, 1) =  -0.425325 : MD2_VertN_(160, 2) =  -0.688191
	MD2_VertN_(161, 0) = -0.688191 : MD2_VertN_(161, 1) =  -0.587785 : MD2_VertN_(161, 2) =  -0.425325
End Function


;~IDEal Editor Parameters:
;~F#F#25#2C#41#54#9F#C0#C5#CC#E5#F7#104#10F#126#158#183
;~C#BlitzPlus
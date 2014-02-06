
; MeshLoader addon module for bOGL
;==================================


; This module provides functions to load static meshes from .bo3d amd .obj files
; and to save static meshes to .obj format
; BO3D meshes are loaded as entity hierarchies where appropriate
; BO3D meshes may also be loaded directly out of banks

; This module does not provide any animation functionality: see "Animation.bb"
; for procedures to also load animation key data, or "MD2.bb" for a different
; animation system altogether

; (BO3D is a custom format optimised for bOGL, described in Docs/bo3d_spec.txt)


Include "bOGL\bOGL.bb"
Include "bOGL-Addons\MeshUtils.bb"


Type bOGL_BoneEntData_
	Field ent.bOGL_Ent, isActive
	Field bones, verts
End Type


Const LOADER_OBJ_UV_STRIDE = 8, LOADER_OBJ_NORM_STRIDE = 12
Global LOADER_private_SplitCt_ : Dim LOADER_private_SplitRes_$(0)
Global LOADER_private_FBank_, LOADER_VFSize_ : Dim LOADER_Ents_(0)
Global LOADER_private_UDSlot_, LOADER_private_CopyStk_, LOADER_private_FreeStk_
Global LOADER_header_.bOGL_BoneEntData_, LOADER_buffer_.bOGL_BoneEntData_


; Interface
;===========

Function InitMeshLoaderAddon()		;Only call this once per program
	LOADER_private_UDSlot_ = RegisterEntityUserDataSlot()
	LOADER_private_CopyStk_ = CreateBank(0)
	LOADER_private_FreeStk_ = CreateBank(0)
	LOADER_header_ = New bOGL_BoneEntData_
	LOADER_buffer_ = New bOGL_BoneEntData_
	LOADER_private_FBank_ = CreateBank(4)
	MESH_InitMeshUtils_
End Function

Function LoadMesh(file$, parent = 0)
	If FileType(file) <> 1 Then Return 0
	
	Local mesh, size = FileSize(file), bank, f
	
	Select LOADER_Ext_(file)
		Case "obj" : mesh = LoadOBJMesh(file)
			
		Case "bo3d"
			bank = CreateBank(size) : f = ReadFile(file)
			ReadBytes bank, f, 0, size
			mesh = LoadBO3D(bank, 0, size)
			CloseFile f : FreeBank bank
			
		Default
			DebugLog "Unsupported file type for LoadMesh: '" + LOADER_Ext_(file) + "'"
	End Select
	
	If mesh Then SetEntityParent mesh, parent
	Return mesh
End Function

Function LoadBO3D(bk, start, size)
	If Not LOADER_private_FBank_ Then RuntimeError "Loader addon was not initialised!"
	
	If size < 20 Then Return 0		;Size of the BO3D header: minimum possible valid file size
	If PeekInt(bk, start + 0) <> $44334f42 Then Return 0	;Magic number check
	If PeekInt(bk, start + 4) > 100 Then Return 0		;Version check
	Local eCount = PeekInt(bk, start + 8)
	Local eListSize = PeekInt(bk, start + 12)
	LOADER_VFSize_ = PeekInt(bk, start + 16)
	If LOADER_VFSize_ <> 16 And LOADER_VFSize_ <> 32 Then Return 0	;Invalid VFloat size
	If eCount = 0 Then Return 0		;No entities... nothing to return
	If eListSize > size + 20 Then Return 0		;Entity list doesn't fit
	
	Dim LOADER_Ents_(eCount)
	Local p[0], tgt = start + size, doFail = False, i
	p[0] = start + 20	;Skip the header
	For i = 0 To eCount - 1
		LOADER_Ents_(i) = LOADER_LoadEntityDef_(bk, p, tgt, i)
		If Not LOADER_Ents_(i) Then doFail = True : Exit
	Next
	
	If doFail	;Loading an entity def failed: cleanup all of the loaded entities and return 0
		For i = 0 To eCount - 1
			If LOADER_Ents_(i) Then SetEntityParent LOADER_Ents_(i), 0
		Next
		For i = 0 To eCount - 1
			If LOADER_Ents_(i) Then FreeEntity LOADER_Ents_(i)
		Next
		Dim LOADER_Ents_(0) : Return 0
	EndIf
	
	For i = 1 To eCount - 1	;Ensure all entities are attached to the root node and have updated tforms
		If Not GetEntityParent(LOADER_Ents_(i)) Then SetEntityParent LOADER_Ents_(i), LOADER_Ents_(0)
		Local ent.bOGL_Ent = bOGL_EntList_(LOADER_Ents_(i))
		bOGL_UpdateGlobalPosition_ ent
		bOGL_UpdateAxisAngle_ ent\g_r, ent\g_q : ent\g_Rv = True
	Next
	
	For i = 0 To eCount - 1	;Update bone banks with correct entity handles
		Local boneData.bOGL_BoneEntData_ = Object.bOGL_BoneEntData_ GetEntityUserData(LOADER_Ents_(i), LOADER_private_UDSlot_), bi
		If boneData <> Null
			For bi = 0 To BankSize(boneData\bones) / 12 - 1
				Local bone = LOADER_Ents_(PeekInt(boneData\bones, bi * 12))
				PokeInt boneData\bones, bi * 12, bone
				
				Local verts = PeekInt(boneData\verts, (bi + 1) * 4)
				If verts
					Local vp, vt = BankSize(verts) - 24, tfv#[2]
					For vp = 0 To vt Step 24
						TFormPoint PeekFloat(verts, vp + 12), PeekFloat(verts, vp + 16), PeekFloat(verts, vp + 20), boneData\ent\handler, bone, tfv
						PokeFloat verts, vp + 12, tfv[0]
						PokeFloat verts, vp + 16, tfv[1]
						PokeFloat verts, vp + 20, tfv[2]
					Next
				EndIf
				
			Next
			Insert boneData Before First bOGL_BoneEntData_
		EndIf
	Next
	
	Local ret = LOADER_Ents_(0)
	Dim LOADER_Ents_(0)
	Return ret
End Function

Function LoadOBJMesh(file$)
	Local sz = FileSize(file), f = ReadFile(file), mesh = CreateMesh(), root = mesh
	Local norms = CreateBank(sz), uvs = CreateBank(sz), np = 0, up = 0, vc = 0
	SetEntityName root, ""
	
	While Not Eof(f)
		Local def$ = ReadLine(f) : LOADER_Split_ def
		If LOADER_private_SplitCt_
			Select LOADER_private_SplitRes_(0)
				Case "v"
					If LOADER_private_SplitCt_ >= 4		;Need x, y, z
						Local x# = Float LOADER_private_SplitRes_(1)
						Local y# = Float LOADER_private_SplitRes_(2)
						Local z# = Float LOADER_private_SplitRes_(3)
						AddVertex mesh, x, y, z
						vc = vc + 1
					EndIf
					
				Case "vt"
					If LOADER_private_SplitCt_ >= 2		;Need u
						PokeFloat uvs, up, Float LOADER_private_SplitRes_(1)
						If LOADER_private_SplitCt_ >= 3		;Want v
							PokeFloat uvs, up + 4, Float LOADER_private_SplitRes_(2)
						Else
							PokeFloat uvs, up + 4, 0.0		;But technically v is optional
						EndIf
						up = up + LOADER_OBJ_UV_STRIDE
					EndIf
					
				Case "vn"
					If LOADER_private_SplitCt_ >= 4		;Need i, j, k
						PokeFloat norms, np, Float LOADER_private_SplitRes_(1)
						PokeFloat norms, np + 4, Float LOADER_private_SplitRes_(2)
						PokeFloat norms, np + 8, Float LOADER_private_SplitRes_(3)
						np = np + LOADER_OBJ_NORM_STRIDE
					EndIf
					
				Case "f"
					If LOADER_private_SplitCt_ >= 4		;Need v0, v1, v2 (v3+ are ignored!)
						Local vs$[2], vi[2], v, err = False
						
						vs[0] = LOADER_private_SplitRes_(1)
						vs[1] = LOADER_private_SplitRes_(2)
						vs[2] = LOADER_private_SplitRes_(3)
						
						For v = 0 To 2
							LOADER_Split_ vs[v], "/"
							
							vi[v] = (Int LOADER_private_SplitRes_(0))	;Must be at least an index present
							
							If Abs(vi[v]) > vc Then err = True
							If vi[v] < 0 Then vi[v] = vc - vi[v] : Else vi[v] = vi[v] - 1
							
							If LOADER_private_SplitCt_ >= 2
								Local vt = Int LOADER_private_SplitRes_(1)
								If (Abs vt <= up / LOADER_OBJ_UV_STRIDE) And (vt <> 0)	;If it's 0 the index was skipped
									If vt < 0 Then vt = (up / LOADER_OBJ_UV_STRIDE) - vt : Else vt = vt - 1
									Local vu# = PeekFloat(uvs, LOADER_OBJ_UV_STRIDE * vt)
									Local vv# = PeekFloat(uvs, LOADER_OBJ_UV_STRIDE * vt + 4)
									VertexTexCoords mesh, vi[v], vu, vv
								EndIf
							EndIf
							
							If LOADER_private_SplitCt_ >= 3
								Local vn = Int LOADER_private_SplitRes_(2)
								If Abs vn <= np / LOADER_OBJ_NORM_STRIDE
									If vn < 0 Then vn = (np / LOADER_OBJ_NORM_STRIDE) - vn : Else vn = vn - 1
									Local nx# = PeekFloat(norms, LOADER_OBJ_NORM_STRIDE * vn)
									Local ny# = PeekFloat(norms, LOADER_OBJ_NORM_STRIDE * vn + 4)
									Local nz# = PeekFloat(norms, LOADER_OBJ_NORM_STRIDE * vn + 8)
									VertexNormal mesh, vi[v], nx, ny, nz
								EndIf
							EndIf
						Next
						
						If Not err Then AddTriangle mesh, vi[0], vi[1], vi[2]
					EndIf
					
				Case "o"
					mesh = CreateMesh(root)
					If LOADER_private_SplitCt_ > 1		;Need a name
						SetEntityName mesh, LOADER_private_SplitRes_(1)
					Else
						SetEntityName mesh, ""
					EndIf
					
				Default
					;Do nothing. Other commands are not supported
			End Select
		EndIf
	Wend
	
	If CountVertices(root) = 0 And root <> mesh		;Cleanup potential single-object meshes
		If CountChildren(root) = 1 And GetEntityName(root) = ""		;No content, no name, one child = redundant
			SetEntityParent mesh, 0
			FreeEntity root : root = mesh
		EndIf
	EndIf
	
	FreeBank norms : FreeBank uvs
	Return root
End Function

Function SaveOBJMesh(mesh, file$)
	Local f = WriteFile(file) : If Not f Then Return
	WriteLine f, "# Exported from bOGL"
	LOADER_WriteObj_ f, mesh, mesh	;Recurse down the children (will lose structure)
	CloseFile f
End Function

; Call this once per loop to deform parent meshes, update copies, and clear garbage
Function UpdateBonedMeshes()
	Local c, doClear = False
	
	;Something has been deleted
	If BankSize(LOADER_private_FreeStk_) Then ResizeBank LOADER_private_FreeStk_, 0 : doClear = True
	If BankSize(LOADER_private_CopyStk_)	;Something has been copied
		For c = 0 To BankSize(LOADER_private_CopyStk_) - 4 Step 4
			LOADER_FinishCopy_ PeekInt(LOADER_private_CopyStk_, c)
		Next
		ResizeBank LOADER_private_CopyStk_, 0
	EndIf
	
	Insert LOADER_header_ After Last bOGL_BoneEntData_
	Local m.bOGL_BoneEntData_ : For m = Each bOGL_BoneEntData_
		If m = LOADER_buffer_ Then Exit
		
		If m\ent = Null Or m\isActive = False
			Local n.bOGL_BoneEntData_ = m
			m = Before m
			Insert n After Last bOGL_BoneEntData_
			If n\ent = Null Then doClear = True
		Else
			LOADER_UpdateBones_ m
		EndIf
		
		If m = Null Then m = LOADER_header_ : Insert m Before First bOGL_BoneEntData_
	Next
	If doClear Then LOADER_ClearUnused_
	Insert LOADER_header_ Before First bOGL_BoneEntData_
End Function

; Cause a mesh to resume being deformed by its bones
Function ActivateMeshBones(ent)
	Local boneData.bOGL_BoneEntData_ = Object.bOGL_BoneEntData_ GetEntityUserData(ent, LOADER_private_UDSlot_)
	boneData\isActive = True : Insert boneData Before First bOGL_BoneEntData_
End Function

; Stop a mesh from being deformed by any bones
Function DeactivateMeshBones(ent)
	Local boneData.bOGL_BoneEntData_ = Object.bOGL_BoneEntData_ GetEntityUserData(ent, LOADER_private_UDSlot_)
	boneData\isActive = False
End Function


; Internal
;==========

Const LOADER_ALLOC_TICKER = 25
Global LOADER_private_NewCounter_

Function LOADER_LoadEntityDef_(bk, p[0], tgt, ID)
	If LOADER_private_NewCounter_ >= LOADER_ALLOC_TICKER Then LOADER_ClearUnused_
	LOADER_private_NewCounter_ = LOADER_private_NewCounter_ + 1
	
	Local st = p[0], maxSz = tgt - st
	If maxSz < 64 Then Return 0	;Minimum entity def header size
	
	Local sz = PeekInt(bk, st)	;Entity def size
	If sz > maxSz Then Return 0	;Check that the whole def fits within the range
	p[0] = p[0] + LOADER_AlignSz_(sz)	;Increment the entity pointer
	
	;Create the entity base - if it has vertices, it's a mesh; if not, it's a pivot
	Local entH : If PeekInt(bk, st + 60) Then entH = CreateMesh() Else entH = CreatePivot()
	Local ent.bOGL_Ent = bOGL_EntList_(entH)
	bOGL_InvalidateGlobalPosition_ ent, True
	
	;Read base entity (pivot) data
	Local pID = PeekInt(bk, st + 4)		;Parent ID (within file)
	If pID >= 0 And pID < ID Then SetEntityParent entH, LOADER_Ents_(pID)	;No circular parent loops
	
	;Local position vector
	ent\x = PeekFloat(bk, st + 8) : ent\y = PeekFloat(bk, st + 12) : ent\z = PeekFloat(bk, st + 16)
	;Local scale vector
	ent\sx = PeekFloat(bk, st + 20) : ent\sy = PeekFloat(bk, st + 24) : ent\sz = PeekFloat(bk, st + 28)
	;Local rotation quaternion
	ent\q[0] = PeekFloat(bk, st + 32) : ent\q[1] = PeekFloat(bk, st + 36)
	ent\q[2] = PeekFloat(bk, st + 40) : ent\q[3] = PeekFloat(bk, st + 44)
	ent\Qv = True : ent\Rv = False
	
	;Anim length and keyframes are just loaded dumbly since this is not an anim module
	Local aLen = PeekInt(bk, st + 48), kC = PeekInt(bk, st + 52)
	
	Local nLen = PeekInt(bk, st + 56)		;Byte length of name string
	Local vertC = PeekInt(bk, st + 60)		;Number of vertices
	
	Local vColC, triC, tnLen, boneC
	
	If vertC	;If it's a mesh, also read mesh-specific extended header
		If sz < 92 Then FreeEntity entH : Return 0		;At a minimum must have space for the header
		
		vColC = PeekInt(bk, st + 64)
		triC = PeekInt(bk, st + 68)
		tnLen = PeekInt(bk, st + 72)
		ent\m\argb = $FFFFFFFF;PeekInt(bk, st + 76)
		ent\m\alpha = PeekFloat(bk, st + 80)
		ent\m\FX = PeekInt(bk, st + 84)
		boneC = PeekInt(bk, st + 88)
	EndIf
	
	If kC < 0 Or vertC < 0 Or nLen < 0 Or vColC < 0 Or triC < 0 Or tnLen < 0 Or boneC < 0		;Invalid, corrupt sizes
		FreeEntity entH : Return 0
	EndIf
	
	;Last check that the def is large enough to hold all requested data
	sz = sz - (64 + (28 * (vertC <> 0)))
	If sz < kC * 44 + LOADER_AlignSz_(nLen) + vertC * 8 * (LOADER_VFSize_ / 8) + vColC * 3 + triC * 6 + LOADER_AlignSz_(tnLen) + boneC * 12
		FreeEntity entH : Return 0
	EndIf
	;After this point, the entity def will definitely be loaded: everything fits in the required range
	
	st = st + 64 + (28 * (vertC <> 0))	;Inc past the header
	
	;Skip keyframes (this is not the animation module)
	st = st + LOADER_AlignSz_(kC * 44)
	
	ent\name = LOADER_PeekChars_(bk, st, nLen)
	st = st + LOADER_AlignSz_(nLen)
	
	SetEntityUserData entH, LOADER_private_UDSlot_, 0
	If vertC	;Remaining properties are all mesh properties
		ResizeBank ent\m\vp, vertC * BOGL_VERT_STRIDE
		CopyBank bk, st, ent\m\vp, 0, vertC * 8 * (LOADER_VFSize_ / 8)	;This line will become dangerous if VFSize is ever more than 32
		If LOADER_VFSize_ = 16 Then LOADER_ExpandVertexData_ ent\m\vp
		st = st + LOADER_AlignSz_(vertC * 8 * (LOADER_VFSize_ / 8))
		
		If vColC = vertC
			ent\m\vc = CreateBank(vColC * BOGL_COL_STRIDE)
			CopyBank bk, st, ent\m\vc, 0, vColC * BOGL_COL_STRIDE
		EndIf
		st = st + LOADER_AlignSz_(vColC * 3)
		
		ResizeBank ent\m\poly, triC * BOGL_TRIS_STRIDE
		CopyBank bk, st, ent\m\poly, 0, triC * 6
		st = st + LOADER_AlignSz_(triC * 6)
		
		If tnLen
			Local tName$ = LOADER_PeekChars_(bk, st, tnLen)
			Local tex = LoadTexture(tName)
			If tex Then EntityTexture entH, tex : FreeTexture tex
		EndIf
		st = st + LOADER_AlignSz_(tnLen)
		
		If boneC	;Bones
			Local boneData.bOGL_BoneEntData_ = New bOGL_BoneEntData_, i
			boneData\ent = ent : boneData\isActive = True
			boneData\bones = CreateBank(12 * boneC)	;Array of bone handles
			boneData\verts = CreateBank(4 * boneC + 4)	;Refcounted array of vertex banks
			For i = 0 To boneC - 1
				PokeInt boneData\bones, i * 12, PeekInt(bk, st + i * 12)	;Poke def indices for now
				Local fV = PeekInt(bk, st + i * 12 + 4), lV = PeekInt(bk, st + i * 12 + 8)
				PokeInt boneData\bones, i * 12 + 4, fV
				PokeInt boneData\bones, i * 12 + 8, lV
				If fV <= lV
					Local vBank = CreateBank((lV - fV + 1) * 24), v
					For v = fV To lV
						CopyBank ent\m\vp, v * BOGL_VERT_STRIDE + 8, vBank, (v - fV) * 24, 24
					Next
					PokeInt boneData\verts, (i + 1) * 4, vBank
				Else
					PokeInt boneData\verts, (i + 1) * 4, 0
				EndIf
			Next
			PokeInt boneData\verts, 0, 1
			SetEntityUserData entH, LOADER_private_UDSlot_, Handle boneData
			SetEntityUserData entH, LOADER_private_UDSlot_, LOADER_private_CopyStk_, 1
			SetEntityUserData entH, LOADER_private_UDSlot_, LOADER_private_FreeStk_, 2
		EndIf
	EndIf
	
	Return entH
End Function

Function LOADER_WriteObj_(f, root, ent)
	Local this.bOGL_Ent = bOGL_EntList_(ent), m.bOGL_Mesh = this\m, ch
	For ch = 0 To CountChildren(ent) - 1
		LOADER_WriteObj_ f, root, GetChildEntity(ent, ch)
	Next
	If this\eClass = BOGL_CLASS_MESH
		WriteLine f, "o " + this\name
		Local v, vtmax = CountVertices(ent) - 1, t, l$, out#[2]
		For v = 0 To vtmax
			TFormPoint VertexX(ent, v), VertexY(ent, v), VertexZ(ent, v), ent, root, out
			WriteLine f, "v " + out[0] + " " + out[1] + " " + out[2]
		Next
		For v = 0 To vtmax
			l = "vt " + PeekFloat(m\vp, v * BOGL_VERT_STRIDE)
			WriteLine f, l + " " + PeekFloat(m\vp, v * BOGL_VERT_STRIDE + 4)
		Next
		For v = 0 To vtmax
			l = "vn " + PeekFloat(m\vp, v * BOGL_VERT_STRIDE + 8)
			l = l + " " + PeekFloat(m\vp, v * BOGL_VERT_STRIDE + 12)
			WriteLine f, l + " " + PeekFloat(m\vp, v * BOGL_VERT_STRIDE + 16)
		Next
		vtmax = CountTriangles(ent) - 1
		For t = 0 To vtmax
			v = PeekShort(this\m\poly, t * BOGL_TRIS_STRIDE) + 1
			l = v + "/" + v + "/" + v + " "
			v = PeekShort(this\m\poly, t * BOGL_TRIS_STRIDE + 2) + 1
			l = l + v + "/" + v + "/" + v + " "
			v = PeekShort(this\m\poly, t * BOGL_TRIS_STRIDE + 4) + 1
			l = l + v + "/" + v + "/" + v
			WriteLine f, "f " + l
		Next
	EndIf
End Function

Function LOADER_FinishCopy_(copy)
	Local boneData.bOGL_BoneEntData_ = Object.bOGL_BoneEntData_ GetEntityUserData(copy, LOADER_private_UDSlot_)
	Local copyData.bOGL_BoneEntData_ = New bOGL_BoneEntData_
	SetEntityUserData copy, LOADER_private_UDSlot_, Handle copyData
	copyData\ent = bOGL_EntList_(copy)
	copyData\isActive = boneData\isActive
	If copyData\isActive Then Insert copyData Before First bOGL_BoneEntData_
	copyData\bones = CreateBank(BankSize(boneData\bones))
	CopyBank boneData\bones, 0, copyData\bones, 0, BankSize(boneData\bones)
	Local bi : For bi = 0 To BankSize(copyData\bones) / 12 - 1
		PokeInt copyData\bones, bi * 12, GetChildByName(copy, GetEntityName(PeekInt(copyData\bones, bi * 12)))
	Next
	copyData\verts = boneData\verts
	PokeInt copyData\verts, 0, PeekInt(copyData\verts, 0) + 1
End Function

Function LOADER_ClearUnused_()
	Local m.bOGL_BoneEntData_
	For m = Each bOGL_BoneEntData_
		If m\ent = Null
			If m <> LOADER_header_ And m <> LOADER_buffer_
				FreeBank m\bones
				If PeekInt(m\verts, 0) < 2
					Local v : For v = 4 To BankSize(m\verts) - 4 Step 4
						FreeBank PeekInt(m\verts, v)
					Next
					FreeBank m\verts
				Else
					PokeInt m\verts, 0, PeekInt(m\verts, 0) - 1
				EndIf
				Delete m
			EndIf
		EndIf
	Next
	LOADER_private_NewCounter_ = 0
End Function

Function LOADER_ExpandVertexData_(vp)
	Local f, fc = BankSize(vp) / 4
	For f = fc - 1 To 0 Step -1
		PokeFloat vp, f * 4, LOADER_HalfToFloat_(PeekShort(vp, f * 2))
	Next
End Function

Function LOADER_UpdateBones_(m.bOGL_BoneEntData_)
	Local ent.bOGL_Ent = m\ent, msh.bOGL_Mesh = ent\m, mshvp = msh\vp
	If Not ent\Gv Then bOGL_UpdateGlobalPosition_ ent
	If Not ent\g_Rv Then bOGL_UpdateAxisAngle_ ent\g_r, ent\g_q
	
	Local boneC = BankSize(m\bones) / 12, bi
	Local tmp_ro = Int(-ent\g_r[0] * Float MESH_SIN_ACC) Mod MESH_SLT_SIZE	;Pre-calc lookup for faster tforms
	If tmp_ro < 0 Then tmp_ro = tmp_ro + MESH_SLT_SIZE
	
	For bi = 0 To boneC - 1
		Local bone.bOGL_Ent = bOGL_EntList_(PeekInt(m\bones, bi * 12)), vBank = PeekInt(m\verts, (bi + 1) * 4)
		Local fV = PeekInt(m\bones, bi * 12 + 4), lV = PeekInt(m\bones, bi * 12 + 8), v, tfv#[2]
		If Not bone\Gv
			bOGL_UpdateGlobalPosition_ bone
			bOGL_UpdateAxisAngle_ bone\g_r, bone\g_q : bone\g_Rv = True
		EndIf
		
		Local gx# = Abs(bone\g_sx * bone\sx), gy# = Abs(bone\g_sy * bone\sy), gz# = Abs(bone\g_sz * bone\sz)
		
		If vBank
			For v = fV To lV
				Local vi = v - fV, ptr = v * BOGL_VERT_STRIDE
				
				Local lpx# = PeekFloat(vBank, vi * 24 + 12), lpy# = PeekFloat(vBank, vi * 24 + 16), lpz# = PeekFloat(vBank, vi * 24 + 20)
				MESH_TFormFast2_ lpx, lpy, lpz, bone, ent, tfv, tmp_ro
				PokeFloat mshvp, ptr + 20, tfv[0] : PokeFloat mshvp, ptr + 24, tfv[1] : PokeFloat mshvp, ptr + 28, tfv[2]
				
				Local lnx# = PeekFloat(vBank, vi * 24), lny# = PeekFloat(vBank, vi * 24 + 4), lnz# = PeekFloat(vBank, vi * 24 + 8), tfn#[2]
				MESH_TFormFast2_ lnx + lpx, lny + lpy, lnz + lpz, bone, ent, tfn, tmp_ro
				
				tfn[0] = tfn[0] - tfv[0]
				tfn[1] = tfn[1] - tfv[1]
				tfn[2] = tfn[2] - tfv[2]
				Local l# = Sqr(tfn[0] * tfn[0] + tfn[1] * tfn[1] + tfn[2] * tfn[2])
				PokeFloat mshvp, ptr + 8, (tfn[0] / l) * gx
				PokeFloat mshvp, ptr + 12, (tfn[1] / l) * gy
				PokeFloat mshvp, ptr + 16, (tfn[2] / l) * gz
			Next
		EndIf
	Next
	
	msh\Nv = True
End Function

;Get the file extension off a name
Function LOADER_Ext_$(name$)
	While Instr(name, ".")
		name = Mid(name, Instr(name, ".") + 1)
	Wend
	Return Lower(name)
End Function

; This is actually just a string split function (results go in SplitRes)
Function LOADER_Split_(s$, on$ = " ", compact = True)
	If Len(s) = 0 Then LOADER_private_SplitCt_ = 0 : Return
	
	If on = " " And compact = True
		Local t$ = Replace(Trim(s), Chr(9), " ")
		Repeat
			s = Replace(t, "  ", " ")
			If Len(s) = Len(t) Then Exit : Else t = s
		Forever
	EndIf
	
	Local c, sCount : For c = 1 To Len(s)
		If Mid(s, c, 1) = on Then sCount = sCount + 1
	Next
	Dim LOADER_private_SplitRes_$(sCount) : LOADER_private_SplitCt_ = sCount + 1
	
	If sCount = 0
		LOADER_private_SplitRes_(0) = s
	Else
		For c = 0 To sCount
			If c < sCount
				Local p = Instr(s, on)
				LOADER_private_SplitRes_(c) = Left(s, p - 1)
				s = Mid(s, p + 1)
			Else
				LOADER_private_SplitRes_(c) = s
			EndIf
		Next
	EndIf
End Function

Function LOADER_HalfToFloat_#(h)
	Local signBit, exponent, fraction, fBits
	
	signBit = (h And 32768) <> 0
	exponent = (h And %0111110000000000) Shr 10
	fraction = (h And %0000001111111111)
	
	If exponent = $1F Then exponent = $FF : ElseIf exponent Then exponent = (exponent - 15) + 127
	fBits = (signBit Shl 31) Or (exponent Shl 23) Or (fraction Shl 13)
	
	PokeInt LOADER_private_FBank_, 0, fBits
	Return PeekFloat(LOADER_private_FBank_, 0)
End Function

Function LOADER_FloatToHalf_(f#)
	Local signBit, exponent, fraction, fBits
	
	PokeFloat LOADER_private_FBank_, 0, f
	fBits = PeekInt(LOADER_private_FBank_, 0)
	
	signBit = (fBits And (1 Shl 31)) <> 0
	exponent = (fBits And $7F800000) Shr 23
	fraction = fBits And $007FFFFF
	
	If exponent
		exponent = exponent - 127
		If Abs(exponent) > $1F
			If exponent <> ($FF - 127) Then fraction = 0
			exponent = $1F * Sgn(exponent)
		Else
			exponent = exponent + 15
		EndIf
		exponent = exponent And %11111
	EndIf
	fraction = fraction Shr 13
	
	Return (signBit Shl 15) Or (exponent Shl 10) Or fraction
End Function

Function LOADER_AlignSz_(s)
	If s Mod 4 Then Return s + (4 - s Mod 4) Else Return s
End Function

Function LOADER_PeekChars_$(bk, st, sLen)
	Local s$, c
	For c = 0 To sLen - 1
		s = s + Chr(PeekByte(bk, st + c))
	Next
	Return s
End Function


;~IDEal Editor Parameters:
;~F#15#25#2F#45#87#EE#F6#116#11C#128#1A2#1C4#1D4#1E9#1F0#21D#225#244#252#26B
;~F#26F
;~C#BlitzPlus
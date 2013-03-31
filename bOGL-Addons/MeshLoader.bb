
; MeshLoader addon module for bOGL
;==================================


; This module provides functions to load static meshes from .b3d amd .obj files
; and to save static meshes to .obj format
; B3D meshes are loaded as entity hierarchies where appropriate
; B3D meshes may also be loaded directly out of banks


Include "bOGL\bOGL.bb"


Const LOADER_OBJ_UV_STRIDE = 8, LOADER_OBJ_NORM_STRIDE = 12
Global LOADER_private_SplitCt_ : Dim LOADER_private_SplitRes_$(0)


; Interface
;===========

Function LoadMesh(file$, parent = 0)
	If FileType(file) <> 1 Then Return 0
	
	Local mesh, size = FileSize(file), bank
	
	Select LOADER_Ext_(file)
		Case "obj" : mesh = LoadOBJMesh(file)
			
		Case "b3d"
			bank = CreateBank(size) : ReadBytes bank, file, 0, size
			mesh = LoadB3DMesh(bank, 0, size)
			FreeBank bank
			
		Default
			DebugLog "Unsupported file type for LoadMesh: '" + LOADER_Ext_(file) + "'"
	End Select
	
	EntityParent mesh, parent
	Return mesh
End Function

Function LoadB3DMesh(bk, start, size)
	
End Function

Function LoadOBJMesh(file$)
	Local sz = FileSize(file), f = ReadFile(file)
	Local norms = CreateBank(sz), uvs = CreateBank(sz), np = 0, up = 0, vc = 0
	Local name$ = "", mesh = CreateMesh()
	
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
	
	FreeBank norms : FreeBank uvs
	Return mesh
End Function

Function SaveOBJMesh(mesh, file$)
	Local f = WriteFile(file) : If Not f Then Return
	Local this.bOGL_Ent = bOGL_EntList_(mesh), m.bOGL_Mesh = this\m
	WriteLine f, "# Exported from bOGL"
	WriteLine f, "o " + this\name
	Local v, vtmax = CountVertices(mesh) - 1, t, l$
	For v = 0 To vtmax
		l = "v " + PeekFloat(m\vp, v * BOGL_VERT_STRIDE + 20)
		l = l + " " + PeekFloat(m\vp, v * BOGL_VERT_STRIDE + 24)
		WriteLine f, l + " " + PeekFloat(m\vp, v * BOGL_VERT_STRIDE + 28)
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
	vtmax = CountTriangles(mesh) - 1
	For t = 0 To vtmax
		v = PeekShort(this\m\poly, t * BOGL_TRIS_STRIDE) + 1
		l = v + "/" + v + "/" + v + " "
		v = PeekShort(this\m\poly, t * BOGL_TRIS_STRIDE + 2) + 1
		l = l + v + "/" + v + "/" + v + " "
		v = PeekShort(this\m\poly, t * BOGL_TRIS_STRIDE + 4) + 1
		l = l + v + "/" + v + "/" + v
		WriteLine f, "f " + l
	Next
End Function


; Internal
;==========

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


;~IDEal Editor Parameters:
;~F#2A#2E#8D
;~C#BlitzPlus
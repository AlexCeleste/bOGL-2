
; This is a quick and dirty conversion utility from .b3d (Blitz3D, miniB3D) to .bo3d (bOGL)
; Based on the B3D sample code by Mark Sibly

; To use, pass a list of files to convert as the command line arguments
; use -fsize=X to change the vfloat size for the following files
; 16 and 32 are the supported sizes

; bo3d format version number is 100


Global SplitCount : Dim SplitRes$(0)	;(Needed by Split below, must be declared above use)
Dim b3d_stack(100)		;Needed by b3dFuncs, must be declared and sized above use


Global VFloatSize = 32		;Width of floating point numbers (also support IEEE half-precision)


Split CommandLine()
Local i : For i = 0 To SplitCount - 1
	Local arg$ = SplitRes(i)
	If Instr(arg, "=")		;Option
		Select Left(arg, Instr(arg, "=") - 1)
			Case "-fsize"
				If Int(Mid(arg, Instr(arg, "=") + 1)) = 16 Then VFloatSize = 16 : Else VFloatSize = 32
		End Select
	Else
		If FileType(arg) = 1 And GetExt(arg) = "b3d" Then Convert arg
	EndIf
Next
End



; Texture and brush lists are loaded up before the entities, so store b3d data
Type Tex
	Field name$
End Type

Type Brush
	Field tex.Tex
	Field r, g, b, a#
	Field bl, fx
End Type

; Entities are bo3d entitiy def structures
Type Ent
	Field index, parent.Ent
	
	Field size, pid
	Field px#, py#, pz#
	Field sx#, sy#, sz#
	Field rw#, rx#, ry#, rz#
	Field fr, kc, name$, vertc, vcolc, tric
	Field tname$, col, alpha#, fx, bc
	
	Field kfs, verts, vcols, tris, bones
End Type


Global fileName$, eCounter, totalSize


; Convert a b3d file to a bo3d file (adapted from dumpb3d.bb)
Function Convert(file$)
	Local f = ReadFile(file) : If Not f Then RuntimeError "Unable to open '" + file + "'"
	b3dSetFile f
	If b3dReadChunk() <> "BB3D" Then RuntimeError "Invalid b3d file '" + file + "'"
	If b3dReadInt() / 100 > 0 Then RuntimeError "Invalid b3d file version '" + file + "'"
	
	fileName = file : eCounter = 0 : totalSize = 0
	
	ReadChunks Null
	CloseFile f
	
	If VFloatSize <> 32 Then ConvertVFloats
	AddEntSizes		;Add up sizes
	
	f = WriteFile(StripExt(file) + ".bo3d")		;Write data
	
	WriteInt f, $44334f42
	WriteInt f, 100
	WriteInt f, eCounter
	WriteInt f, totalSize
	WriteInt f, VFloatSize
	
	Local e.Ent : For e = Each Ent
		If e\bc Then ReorderVerts e		;Rearrange vertices to match bone structure
		WriteEnt f, e
	Next
	
	CloseFile f
	
	Delete Each Tex : Delete Each Brush
	For e = Each Ent
		If e\kfs Then FreeBank e\kfs
		If e\verts Then FreeBank e\verts
		If e\vcols Then FreeBank e\vcols
		If e\tris Then FreeBank e\tris
		If e\bones Then FreeBank e\bones
		Delete e
	Next
End Function

Function ConvertVFloats()
	Local e.Ent : For e = Each Ent
		If e\vertc
			Local f : For f = 0 To BankSize(e\verts) - 4 Step 4
				PokeShort e\verts, f / 2, FloatToHalf(PeekFloat(e\verts, f))
			Next
			ResizeBank e\verts, BankSize(e\verts) / 2
		EndIf
	Next
End Function

Dim VertexMap(0)	;Not used outside of ReorderVerts

; Rearrange the vertices of a mesh to form bone groups, also reduce bone sizes
Function ReorderVerts(e.Ent)
	Local vc = 0, v, bp, bone.Ent, verts, vertSize = 8 * (VFloatSize / 8)
	Dim VertexMap(e\vertc)
	
	For v = 0 To e\vertc - 1
		VertexMap(v) = -1		;Fill the array with null data (some verts might be unboned)
	Next
	
	For bp = 0 To BankSize(e\bones) - 12 Step 12
		bone = Object.Ent PeekInt(e\bones, bp + 8)
		verts = PeekInt(e\bones, bp + 4)
		
		PokeInt e\bones, bp + 4, vc		;Start vertex of bone range
		
		For v = 0 To BankSize(verts) / 4 - 1		;Build the list of remapped vert indices
			If VertexMap(PeekInt(verts, v * 4)) = -1	;No shared vertices
				VertexMap(PeekInt(verts, v * 4)) = vc
				vc = vc + 1
			EndIf
		Next
		
		PokeInt e\bones, bp + 8, vc - 1		;End vertex of bone range
		FreeBank verts
	Next
	
	Local vertsNew = CreateBank(BankSize(e\verts))		;New vertex array
	For v = 0 To e\vertc - 1
		If VertexMap(v) = -1	;Not boned, not remapped yet
			VertexMap(v) = vc	;Put it at the end...
			vc = vc + 1		;...and move the end up one
		EndIf
		CopyBank e\verts, v * vertSize, vertsNew, VertexMap(v) * vertSize, vertSize
	Next
	FreeBank e\verts : e\verts = vertsNew
	
	For v = 0 To e\tric * 3 - 1		;Update triangle indices (vert by vert)
		PokeShort e\tris, v * 2, VertexMap(PeekShort(e\tris, v * 2))
	Next
	
	Dim VertexMap(0)
End Function

; Recursively navigate the B3D tree structure, dump data in global lists
; Heavily modified version of Mark's DumpChunks
Function ReadChunks(p.Ent)
	Local flags, sz, i, e.Ent = p, b.Brush
	
	While b3dChunkSize() > 0
		Local chunk$ = b3dReadChunk()
		
		Select chunk
			Case "ANIM"
				b3dReadInt
				e\fr = b3dReadInt()
				SkipChunk
				
			Case "KEYS"
				flags = b3dReadInt()
				sz = 4
				If flags And 1 Then sz = sz + 12
				If flags And 2 Then sz = sz + 12
				If flags And 4 Then sz = sz + 16
				If Not e\kfs
					e\kfs = CreateBank((b3dChunkSize() / sz) * 44)
				Else
					ResizeBank e\kfs, BankSize(e\kfs) + ((b3dChunkSize() / sz) * 44)
				EndIf
				;read all keys in chunk
				While b3dChunkSize() > 0
					PokeInt e\kfs, e\kc * 44, b3dReadInt()
					If flags And 1
						PokeFloat e\kfs, e\kc * 44 + 4, b3dReadFloat()
						PokeFloat e\kfs, e\kc * 44 + 8, b3dReadFloat()
						PokeFloat e\kfs, e\kc * 44 + 12, b3dReadFloat()
					EndIf
					If flags And 2
						PokeFloat e\kfs, e\kc * 44 + 16, b3dReadFloat()
						PokeFloat e\kfs, e\kc * 44 + 20, b3dReadFloat()
						PokeFloat e\kfs, e\kc * 44 + 24, b3dReadFloat()
					Else
						PokeFloat e\kfs, e\kc * 44 + 16, 1
						PokeFloat e\kfs, e\kc * 44 + 20, 1
						PokeFloat e\kfs, e\kc * 44 + 24, 1
					EndIf
					If flags And 4
						PokeFloat e\kfs, e\kc * 44 + 28, b3dReadFloat()
						PokeFloat e\kfs, e\kc * 44 + 32, b3dReadFloat()
						PokeFloat e\kfs, e\kc * 44 + 36, b3dReadFloat()
						PokeFloat e\kfs, e\kc * 44 + 40, b3dReadFloat()
					Else
						PokeFloat e\kfs, e\kc * 44 + 28, 1
					EndIf
					e\kc = e\kc + 1
				Wend
				
			Case "TEXS"
				While b3dChunkSize() > 0
					Local t.Tex = New Tex
					t\name = b3dReadString$()
					SkipBytes 28
				Wend
				
			Case "BRUS"
				Local n_texs = b3dReadInt()
				;read all brushes in chunk...
				While b3dChunkSize() > 0
					b3dReadString
					b = New Brush
					b\r = b3dReadFloat() * 255
					b\g = b3dReadFloat() * 255
					b\b = b3dReadFloat() * 255
					b\a = b3dReadFloat()
					b3dReadFloat
					b\bl = b3dReadInt()
					b\fx = b3dReadInt()
					If n_texs Then b\tex = TexByIndex(b3dReadInt()) : n_texs = n_texs - 1
					For i = 1 To n_texs
						b3dReadInt
					Next
				Wend
				
			Case "NODE"
				e = New Ent
				e\index = eCounter : eCounter = eCounter + 1
				If p <> Null Then e\pid = p\index : e\parent = p Else e\pid = -1
				e\size = 64
				e\name = b3dReadString()
				e\px = b3dReadFloat()
				e\py = b3dReadFloat()
				e\pz = b3dReadFloat()
				e\sx = b3dReadFloat()
				e\sy = b3dReadFloat()
				e\sz = b3dReadFloat()
				e\rw = b3dReadFloat()
				e\rx = b3dReadFloat()
				e\ry = b3dReadFloat()
				e\rz = b3dReadFloat()
				
			Case "VRTS"
				flags = b3dReadInt()
				Local tc_sets = b3dReadInt()
				Local tc_size = b3dReadInt()
				sz = 12 + tc_sets * tc_size * 4
				If flags And 1 Then sz = sz + 12
				If flags And 2 Then sz = sz + 16 : e\vcols = CreateBank((b3dChunkSize() / sz) * 3)
				e\verts = CreateBank((b3dChunkSize() / sz) * 32)
				;read all verts in chunk
				While b3dChunkSize() > 0
					PokeFloat e\verts, e\vertc * 32 + 20, b3dReadFloat()
					PokeFloat e\verts, e\vertc * 32 + 24, b3dReadFloat()
					PokeFloat e\verts, e\vertc * 32 + 28, b3dReadFloat()
					If flags And 1
						PokeFloat e\verts, e\vertc * 32 + 8, b3dReadFloat()
						PokeFloat e\verts, e\vertc * 32 + 12, b3dReadFloat()
						PokeFloat e\verts, e\vertc * 32 + 16, b3dReadFloat()
					EndIf
					If flags And 2
						PokeByte e\vcols, e\vcolc * 3, b3dReadFloat() * 255
						PokeByte e\vcols, e\vcolc * 3 + 1, b3dReadFloat() * 255
						PokeByte e\vcols, e\vcolc * 3 + 2, b3dReadFloat() * 255
						b3dReadFloat()
						e\vcolc = e\vcolc + 1
					EndIf
					;read tex coords...
					If tc_sets > 0 And tc_size >= 2
						PokeFloat e\verts, e\vertc * 32, b3dReadFloat()
						PokeFloat e\verts, e\vertc * 32 + 4, b3dReadFloat()
					EndIf
					If tc_size > 2 Then SkipBytes((tc_size - 2) * 4)
					For i = 1 To (tc_sets - 1) * tc_size
						b3dReadFloat()
					Next
					e\vertc = e\vertc + 1
				Wend
				
			Case "TRIS"
				b = BrushByIndex(b3dReadInt())
				If e\tname = "" And b <> Null Then If b\tex <> Null Then e\tname = b\tex\name
				sz = 12
				If Not e\vertc Then RuntimeError "Cannot define tris without verts ('" + fileName + "')"
				If Not e\tris
					e\tris = CreateBank((b3dChunkSize() / sz) * 6)
				Else
					ResizeBank e\tris, BankSize(e\tris) + ((b3dChunkSize() / sz) * 6)
				EndIf
				;read all tris in chunk
				While b3dChunkSize() > 0
					PokeShort e\tris, e\tric * 6, b3dReadInt()
					PokeShort e\tris, e\tric * 6 + 2, b3dReadInt()
					PokeShort e\tris, e\tric * 6 + 4, b3dReadInt()
					e\tric = e\tric + 1
				Wend
				
			Case "MESH"
				b = BrushByIndex(b3dReadInt())
				e\size = 92
				If b <> Null
					e\col = b\r Or (b\g Shl 8) Or (b\b Shl 16)
					e\alpha = b\a
					e\tname = b\tex\name
					e\fx = b\fx And 1			;Fullbright
					e\fx = e\fx Or ((b\fx And 12) Shr 1)		;Flatshaded and nofog
					e\fx = e\fx Or ((b\fx And 16) Shl 1)		;Nocull
					If b\bl = 3 Then e\fx = e\fx Or 8		;Additive blend
					If b\bl = 2 Then e\fx = e\fx Or 16		;Multiply blend
				Else
					e\col = $FFFFFF : e\alpha = 1
				EndIf
				
			Case "BONE"
				sz = 8
				Repeat
					p = p\parent
					If p = Null Then RuntimeError "Cannot apply bone weights without parent mesh ('" + fileName + "')"
				Until p\vertc
				Local nw = 0, verts = CreateBank((b3dChunkSize() / sz) * 4)
				;read all weights
				While b3dChunkSize() > 0
					PokeInt verts, nw * 4, b3dReadInt()
					b3dReadFloat	;Weighting not supported
					nw = nw + 1
				Wend
				p\bc = p\bc + 1
				If p\bones Then ResizeBank p\bones, p\bc * 12 Else p\bones = CreateBank(p\bc * 12)
				PokeInt p\bones, (p\bc - 1) * 12, e\index
				PokeInt p\bones, (p\bc - 1) * 12 + 4, verts
				PokeInt p\bones, (p\bc - 1) * 12 + 8, Handle e
				
			Default
				SkipChunk
		End Select
		
		ReadChunks(e)	;Read any subchunks
		b3dExitChunk()		;exit this chunk
	Wend
End Function

Function AddEntSizes()
	Local e.Ent : For e = Each Ent
		If e\kfs Then e\size = e\size + AlignedSize(BankSize(e\kfs))
		If e\verts Then e\size = e\size + AlignedSize(BankSize(e\verts))
		If e\vcols Then e\size = e\size + AlignedSize(BankSize(e\vcols))
		If e\tris Then e\size = e\size + AlignedSize(BankSize(e\tris))
		If e\bones Then e\size = e\size + AlignedSize(BankSize(e\bones))
		e\size = e\size + AlignedSize(Len(e\name))
		e\size = e\size + AlignedSize(Len(e\tname))
		totalSize = totalSize + e\size
	Next
End Function

Function WriteEnt(f, e.Ent)
	WriteInt f, e\size
	WriteInt f, e\pid
	WriteFloat f, e\px
	WriteFloat f, e\py
	WriteFloat f, e\pz
	WriteFloat f, e\sx
	WriteFloat f, e\sy
	WriteFloat f, e\sz
	WriteFloat f, e\rw
	WriteFloat f, e\rx
	WriteFloat f, e\ry
	WriteFloat f, e\rz
	WriteInt f, e\fr
	WriteInt f, e\kc
	WriteInt f, Len(e\name)
	WriteInt f, e\vertc
	If e\vertc
		WriteInt f, e\vcolc
		WriteInt f, e\tric
		WriteInt f, Len(e\tname)
		WriteInt f, e\col
		WriteFloat f, e\alpha
		WriteInt f, e\fx
		WriteInt f, e\bc
	EndIf
	If e\kfs Then WriteAlignedBytes e\kfs, f, 0, BankSize(e\kfs)
	WriteAlignedString f, e\name
	If e\vertc
		WriteAlignedBytes e\verts, f, 0, BankSize(e\verts)
		If e\vcolc Then WriteAlignedBytes e\vcols, f, 0, BankSize(e\vcols)
		WriteAlignedBytes e\tris, f, 0, BankSize(e\tris)
		WriteAlignedString f, e\tname
		If e\bones Then WriteAlignedBytes e\bones, f, 0, BankSize(e\bones)
	EndIf
End Function

Function AlignedSize(s)
	If s Mod 4 Then Return s + (4 - s Mod 4) Else Return s
End Function

Function WriteAlignedString(f, s$)
	Local c
	For c = 1 To Len(s)
		WriteByte f, Asc(Mid(s, c, 1))
	Next
	Local tail = AlignedSize(Len(s)) - Len(s)
	If tail
		For c = 1 To tail
			WriteByte f, 0
		Next
	EndIf
End Function

Function WriteAlignedBytes(bk, f, ofs, sz)
	WriteBytes bk, f, ofs, sz
	Local c, tail = AlignedSize(sz) - sz
	If tail
		For c = 1 To tail
			WriteByte f, 0
		Next
	EndIf
End Function

; Skip over and ignore an unknown or irrelevant chunk
Function SkipChunk()
	While b3dChunkSize() > 0
		b3dReadByte
	Wend
End Function

Function SkipBytes(n)
	Local i : For i = 1 To n
		b3dReadByte
	Next
End Function

Function TexByIndex.Tex(i)
	Local n, t.Tex : For t = Each Tex
		If n = i Then Return t Else n = n + 1
	Next
End Function

Function BrushByIndex.Brush(i)
	Local n, b.Brush : For b = Each Brush
		If n = i Then Return b Else n = n + 1
	Next
End Function

; Get the file extension off a name
Function GetExt$(name$)
	name = Replace(name, "\", "/")
	While Instr(name, "/")
		name = Mid(name, Instr(name, "/") + 1)
	Wend
	While Instr(name, ".")
		name = Mid(name, Instr(name, ".") + 1)
	Wend
	Return Lower(name)
End Function

; Get the name off a file
Function StripExt$(name$)
	Local ext$ = GetExt(name)
	If Len(ext) Then Return Left(name, Len(name) - (Len(ext) + 1)) Else Return name
End Function

; String split function (results go in SplitRes)
Function Split(s$, on$ = " ", compact = True)
	If Len(s) = 0 Then SplitCount = 0 : Return
	
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
	Dim SplitRes$(sCount) : SplitCount = sCount + 1
	
	If sCount = 0
		SplitRes(0) = s
	Else
		For c = 0 To sCount
			If c < sCount
				Local p = Instr(s, on)
				SplitRes(c) = Left(s, p - 1)
				s = Mid(s, p + 1)
			Else
				SplitRes(c) = s
			EndIf
		Next
	EndIf
End Function

Global Half_CBank_

; Convert 16-bit to 32-bit float
Function HalfToFloat#(h)
	Local signBit, exponent, fraction, fBits
	
	signBit = (h And 32768) <> 0
	exponent = (h And %0111110000000000) Shr 10
	fraction = (h And %0000001111111111)
	
	If exponent = $1F Then exponent = $FF : ElseIf exponent Then exponent = (exponent - 15) + 127
	fBits = (signBit Shl 31) Or (exponent Shl 23) Or (fraction Shl 13)
	
	If Half_CBank_ = 0 Then Half_CBank_ = CreateBank(4)
	PokeInt Half_CBank_, 0, fBits
	Return PeekFloat(Half_CBank_, 0)
End Function

; Convert 32-bit to 16-bit float (returned as short int)
Function FloatToHalf(f#)
	Local signBit, exponent, fraction, fBits
	
	If Half_CBank_ = 0 Then Half_CBank_ = CreateBank(4)
	PokeFloat Half_CBank_, 0, f
	fBits = PeekInt(Half_CBank_, 0)
	
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

Function FloatToBits(f#)
	If Half_CBank_ = 0 Then Half_CBank_ = CreateBank(4)
	PokeFloat Half_CBank_, 0, f
	Return PeekInt(Half_CBank_, 0)
End Function



;==============================================================================
; The remaining functions taken from Mark's "b3dfile.bb"
; http://www.blitzbasic.com/sdkspecs/sdkspecs/b3dfile_utils.zip

;
;b3d file utils to be included
;

Dim b3d_stack(100)
Global b3d_file,b3d_tos

Function b3dSetFile( file )
	b3d_tos=0
	b3d_file=file
End Function

;***** functions for reading from B3D files *****

Function b3dReadByte()
	Return ReadByte( b3d_file )
End Function

Function b3dReadInt()
	Return ReadInt( b3d_file )
End Function

Function b3dReadFloat#()
	Return ReadFloat( b3d_file )
End Function

Function b3dReadString$()
	Repeat
		ch=b3dReadByte()
		If ch=0 Then Return t$
		t$=t$+Chr$(ch)
	Forever
End Function

Function b3dReadChunk$()
	For k=1 To 4
		tag$=tag$+Chr$(b3dReadByte())
	Next
	sz=ReadInt( b3d_file )
	b3d_tos=b3d_tos+1
	b3d_stack(b3d_tos)=FilePos( b3d_file )+sz
	Return tag$
End Function

Function b3dExitChunk()
	SeekFile b3d_file,b3d_stack(b3d_tos)
	b3d_tos=b3d_tos-1
End Function

Function b3dChunkSize()
	Return b3d_stack(b3d_tos)-FilePos( b3d_file )
End Function


;~IDEal Editor Parameters:
;~F#23#27#2E#40#68#76#A2#163#170#195#199#1A6#1B1#1B7#1BD#1C3#1CA#1D6#1DC#1FE
;~F#20E#228#23B#242#246#24A#24E#256#260#265
;~L#-fsize=16 robot.b3d
;~C#Blitz3D
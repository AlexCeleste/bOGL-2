;----------------------------------------------------------------------------
; Filename ogld_utils.bb
; Rev 0.51 2012.09.06
;
;----------------------------------------------------------------------------
;
; OpenGL Direct for Blitz ( http://www.blitzbasic.com )
; Using OpenGl from Blitz without a wrapper DLL
;
; by Peter Scheutz
;
;----------------------------------------------------------------------------
;
; This file contains core setup and utility functionsfor use with
; with OpenGl direct. There are also some types to ease the use of the lib.
; You can write your own versions of these, but these should help you 
; quickly get started with putting OpenGL in a Blitz Window
;
;----------------------------------------------------------------------------


Global BlitzVersion
Const VER_B3D = 2, VER_BPLUS = 3


Type ogld_PixelFormat
	Field index
	Field Size
	Field Version
	Field Flags
	Field PixelType
	Field ColorBits
	Field RedBits
	Field RedShift
	Field GreenBits
	Field GreenShift
	Field BlueBits
	Field BlueShift
	Field AlphaBits
	Field AlphaShift
	Field AccumBits
	Field AccumRedBits
	Field AccumGreenBits
	Field AccumBlueBits
	Field AccumAlphaBits
	Field DepthBits
	Field StencilBits
	Field AuxBuffers
	Field LayerType
	Field Reserved
	Field wLayerMask
	Field wVisibleMask
	Field wDamageMask
End Type

Type ogld_Integer
	Field value
End Type

Type ogld_value1f
	Field value#
End Type

Type ogld_pos3f
	Field x#, y#, z#
End Type

Type ogld_pos4f
	Field x#, y#, z#, w#
End Type

Type ogld_color4f
	Field r#, g#, b#, a#
End Type


; Create maxpf number of types filled with availeble pixelformats.
; the extra index field holds the nu
Function ogld_EnumeratePixelformats(hDC)
	Local lpPixelFormat = CreateBank(40)	
 	Local maxpf = DescribePixelFormat(hDC, 0, 0, 0), n, pf.ogld_PixelFormat
	
	DebugLog ""
	DebugLog "Number of Pixelformats: " + maxpf
	DebugLog ""
	
	For n = 1 To maxpf
		DescribePixelFormat hDC, n, BankSize(lpPixelFormat), lpPixelFormat
		pf = New ogld_PixelFormat
		ogld_PF_BankToType lpPixelFormat, pf
		pf\index=n
	Next 
	FreeBank lpPixelFormat
	
	For pf = Each ogld_PixelFormat
		If pf\index <> 0 ; if index>0 it one of those returned from DescribePixelFormat
			DebugLog "Color: " + pf\ColorBits +  " Depth: " + pf\DepthBits + " Alpha: " + pf\AlphaBits + " Stencil: " + pf\StencilBits  
		EndIf
	Next	
End Function


Function ogld_GetWindow(title$ = "", x = 30, y = 30)
	Local childhWnd, parenthWnd, classname$, hWnd
	
	; This is only if a specific Blitz+ Window is seached for:
	If Len(title$)
		hWnd = FindWindow("GX_WIN32_CLASS", title)
	Else
		;find this thread's active window:
		hWnd=GetActiveWindow()
		; get the top parent window, but alas not in Win95
		;hWnd=GetAncestor(activehWnd,3)
		;therefore:
		parenthWnd = GetParenthWnd(hWnd)
		
		;Loop while there are parents:
		While parenthWnd
			hWnd = parenthWnd
			parenthWnd = GetParenthWnd(hWnd)
		Wend
	EndIf
	
	Local n, classnamebank = CreateBank(256), captionlen = GetClassName(hWnd, classnamebank, BankSize(classnamebank) - 1)
	For n = 0 To captionlen - 1
		classname = classname + Chr$(PeekByte(classnamebank, n))
	Next
	FreeBank classnamebank
	DebugLog "classname " + classname
	
	; First look for Blitz 3D:
	If classname$="Blitz Runtime Class"
		;Set Global variable for later use 
		BlitzVersion = VER_B3D
		;Restore title
		AppTitle title	;findtitle$
		Return hWnd
	EndIf
	
	; If Blitz 3D is not found it's Blitz+:
	If classname$ = "GX_WIN32_CLASS"
		;look for child windows. in main window client coordinates.
		childhWnd = ChildWindowFromPoint(hWnd, x, y)
		;Loop while there are child windows at that point:
		While childhWnd <> hWnd And childhWnd <> 0
			hWnd = childhWnd
			;DebugLog "child "+ childhWnd  
			childhWnd = ChildWindowFromPoint(hWnd, x, y)
		Wend
	EndIf
	
	;Set Global variable for later use 
	BlitzVersion=VER_BPLUS
	Return hWnd
End Function


Function ogld_MakeDefaultPixelFormat.ogld_PixelFormat()
	Local pf.ogld_PixelFormat = New ogld_PixelFormat
	
	pf\Size = 40
	pf\Version = 1
	pf\Flags = PFD_SUPPORT_OPENGL Or PFD_DRAW_TO_WINDOW Or PFD_DOUBLEBUFFER
	pf\PixelType = PFD_TYPE_RGBA 
	pf\ColorBits = 32
	;pf\RedBits =
	;pf\RedShift =
	;pf\GreenBits =
	;pf\GreenShift =
	;pf\BlueBits =
	;pf\BlueShift =
	pf\AlphaBits = 16
	;pf\AlphaShift =
	;pf\AccumBits =
	;pf\AccumRedBits =
	;pf\AccumGreenBits =
	;pf\AccumBlueBits =
	pf\AccumAlphaBits = 16
	pf\DepthBits = 16
	pf\StencilBits = 8
	;pf\AuxBuffers =
	;pf\LayerType =
	;pf\Reserved =
	pf\wLayerMask = PFD_MAIN_PLANE 
	;pf\wVisibleMask =
	;pf\wDamageMask =
	
	Return pf
End Function


Function ogld_PF_BankToType(lpPixelFormat, pf.ogld_PixelFormat)
	pf\Size = PeekShort(lpPixelFormat, 0)
	pf\Version = PeekShort(lpPixelFormat, 2)
	pf\Flags = PeekInt(lpPixelFormat, 4)
	pf\PixelType = PeekByte(lpPixelFormat, 8)
	pf\ColorBits = PeekByte(lpPixelFormat, 9)
	pf\RedBits = PeekByte(lpPixelFormat, 10)
	pf\RedShift = PeekByte(lpPixelFormat, 11)
	pf\GreenBits = PeekByte(lpPixelFormat, 12)
	pf\GreenShift = PeekByte(lpPixelFormat, 13)
	pf\BlueBits = PeekByte(lpPixelFormat, 14)
	pf\BlueShift = PeekByte(lpPixelFormat, 15)
	pf\AlphaBits = PeekByte(lpPixelFormat, 16)
	pf\AlphaShift = PeekByte(lpPixelFormat, 17)
	pf\AccumBits = PeekByte(lpPixelFormat, 18)
	pf\AccumRedBits = PeekByte(lpPixelFormat, 19)
	pf\AccumGreenBits = PeekByte(lpPixelFormat, 20)
	pf\AccumBlueBits = PeekByte(lpPixelFormat, 21)
	pf\AccumAlphaBits = PeekByte(lpPixelFormat, 22)
	pf\DepthBits = PeekByte(lpPixelFormat, 23)
	pf\StencilBits = PeekByte(lpPixelFormat, 24)
	pf\AuxBuffers = PeekByte(lpPixelFormat, 25)
	pf\LayerType = PeekByte(lpPixelFormat, 26)
	pf\Reserved = PeekByte(lpPixelFormat, 27)
	pf\wLayerMask = PeekInt(lpPixelFormat, 28)
	pf\wVisibleMask = PeekInt(lpPixelFormat, 32)
	pf\wDamageMask = PeekInt(lpPixelFormat, 36)
End Function


Function ogld_PF_TypeToBank(pf.ogld_PixelFormat, lpPixelFormat)
	PokeShort lpPixelFormat, 0,pf\Size
	PokeShort lpPixelFormat, 2,pf\Version
	PokeInt lpPixelFormat, 4, pf\Flags
	PokeByte lpPixelFormat, 8, pf\PixelType
	PokeByte lpPixelFormat, 9, pf\ColorBits
	PokeByte lpPixelFormat, 10, pf\RedBits
	PokeByte lpPixelFormat, 11, pf\RedShift
	PokeByte lpPixelFormat, 12, pf\GreenBits
	PokeByte lpPixelFormat, 13, pf\GreenShift
	PokeByte lpPixelFormat, 14, pf\BlueBits
	PokeByte lpPixelFormat, 15, pf\BlueShift
	PokeByte lpPixelFormat, 16, pf\AlphaBits
	PokeByte lpPixelFormat, 17, pf\AlphaShift
	PokeByte lpPixelFormat, 18, pf\AccumBits
	PokeByte lpPixelFormat, 19, pf\AccumRedBits
	PokeByte lpPixelFormat, 20, pf\AccumGreenBits
	PokeByte lpPixelFormat, 21, pf\AccumBlueBits
	PokeByte lpPixelFormat, 22, pf\AccumAlphaBits
	PokeByte lpPixelFormat, 23, pf\DepthBits
	PokeByte lpPixelFormat, 24, pf\StencilBits
	PokeByte lpPixelFormat, 25, pf\AuxBuffers
	PokeByte lpPixelFormat, 26, pf\LayerType
	PokeByte lpPixelFormat, 27, pf\Reserved
	PokeInt lpPixelFormat, 28, pf\wLayerMask
	PokeInt lpPixelFormat, 32, pf\wVisibleMask
	PokeInt lpPixelFormat, 36, pf\wDamageMask	
End Function


Function ogld_SetUp_OpenGL(hWnd, pf.ogld_PixelFormat)
	Local hDC = GetDC(hWnd)
	If Not hDC Then RuntimeError "Can't get DC"
	
	Local lpPixelFormat = ogld_SetupPixelFormat(hDC, pf), hRC = wglCreateContext(hDC)
	If Not hRC Then RuntimeError  "wglCreateContext() failed"
	
	wglMakeCurrent hDC, hRC : Return hDC
End Function


Function ogld_SetupPixelFormat(hDC, pf.ogld_PixelFormat)
	Local lPixelFormat, lpPixelFormat=CreateBank(40)	
	ogld_PF_TypeToBank pf, lpPixelFormat
	
	lPixelFormat = ChoosePixelFormat(hDC, lpPixelFormat)
	If Not lPixelFormat Then RuntimeError  "ChoosePixelFormat() failed"
	
	DescribePixelFormat hDC, lPixelFormat, BankSize(lpPixelFormat), lpPixelFormat
	ogld_PF_BankToType lpPixelFormat,pf.ogld_PixelFormat
	
	SetPixelFormat hDC, lPixelFormat, lpPixelFormat
	FreeBank lpPixelFormat
	
	; return pixel format index
    Return lPixelFormat
End Function 


Function ogld_ShowInfo$()
	Local CRLF$ = Chr(13) + Chr(10)
	
	Local glInfo$ = glGetString(GL_VENDOR) 
	glInfo = glInfo + CRLF + CRLF + glGetString(GL_RENDERER) 
	glInfo = glInfo + CRLF + CRLF + glGetString(GL_VERSION) 
	
	Local glExtentions$ = Replace(glGetString(GL_EXTENSIONS), " ", CRLF)
	
	glInfo = glInfo + CRLF + CRLF + glExtentions
	DebugLog glInfo
End Function


Function ogld_SelcectDepth#(buffer, offset)
	Local v = PeekInt(buffer,offset) Shr 1
	Return Float(v)/2147483647.0
	
	;To get the actual depth, do something like
	; d#= (znear+(Float(v)/2147483647.0)*(zfar-znear))
End Function


;~IDEal Editor Parameters:
;~F#19#37#3B#3F#43#47#4E#66#9D#BF#DD#FB#106#118#126
;~C#BlitzPlus
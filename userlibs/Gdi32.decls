;----------------------------------------------------------------------------
; Filename Gdi32.decls
; Rev 0.5 2003.03.06
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
; This file contains the Blitz UserLib for Gdi32.dll, 
; It must be copied to you Userlib directory.
;
;----------------------------------------------------


.lib "Gdi32.dll"

ChoosePixelFormat%(hDC%, pPixelFormatDescriptor*):"ChoosePixelFormat"
Blitz_DescribePixelFormat% (hDC%,iPixelFormat%, nBytes%, ppfd*):"DescribePixelFormat"
Blitz_NuberOfPixelFormats% (hDC%,iPixelFormat%, nBytes%, ppfd%):"DescribePixelFormat"
SetPixelFormat%(hDC%,n%,pcPixelFormatDescriptor*):"SetPixelFormat"
SwapBuffers%(hDC%):"SwapBuffers"

CreateFont%(nHeight,nWidth,nEscapement,nOrientation,fnWeight,fdwItalic,fdwUnderline,fdwStrikeOut,fdwCharSet,fdwOutputPrecision,fdwClipPrecision,fdwQuality,fdwPitchAndFamily,lpszFace$):"CreateFontA"

SelectObject%(hDc%,hgdiobj%):"SelectObject"
DeleteObject%(hObject%):"DeleteObject"
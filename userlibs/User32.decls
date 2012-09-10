;----------------------------------------------------------------------------
; Filename User32.decls
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
; This file contains the Blitz UserLib for User32.dll, 
; It must be copied to you Userlib directory.
;
;----------------------------------------------------------------------------


.lib "user32.dll"
 
FindWindow%( class$,Text$ ):"FindWindowA"
GetDC%(hWnd% ):"GetDC"
ReleaseDC (hWnd%,hDC%):"ReleaseDC"
ChildWindowFromPoint%(hWndParent%,x%,y%):"ChildWindowFromPoint"
;SendMessage%(hWnd%,Msg%,wParam%,lParam%):"SendMessageA"
;PostMessage%(hWnd%,Msg%,wParam%,lParam%):"PostMessageA"
GetWindowText%(hwnd%, lpString*, cch%):"GetWindowTextA"
GetWindowTextLength%(hwnd%):"GetWindowTextLengthA" 
GetActiveWindow%():"GetActiveWindow"
SetWindowText(hwnd%,lptext$):"SetWindowTextA"
;GetAncestor%(hwnd%,gaFlags%):"GetAncestor"
GetClassName%(hWnd%,lpClassName*,nMaxCount%):"GetClassNameA"
GetParenthWnd%(hwnd%):"GetParent"


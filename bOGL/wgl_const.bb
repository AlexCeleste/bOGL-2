;----------------------------------------------------
; Filename: wgl_const.bb
; Rev 0.1 2003.02.21
;
;----------------------------------------------------
;
; OpenGL Direct for Blitz
; Using OpenGl from Blitz without a wrapper DLL
;
; by Peter Scheutz,... (your name here) 
;
; Based on Sublevel6's PowerBasic headers
; Link: root.tty0.org/sublevel6/?page=files 
;
;----------------------------------------------------
;
; This file contains Windows constants for OpenGl.
;
;----------------------------------------------------


Const PFD_TYPE_RGBA             = 0
Const PFD_TYPE_COLORINDEX       = 1

Const PFD_MAIN_PLANE            = 0
Const PFD_OVERLAY_PLANE         = 1
Const PFD_UNDERLAY_PLANE        = -1

Const PFD_DOUBLEBUFFER          = $00000001
Const PFD_STEREO                = $00000002
Const PFD_DRAW_TO_WINDOW        = $00000004
Const PFD_DRAW_TO_BITMAP        = $00000008
Const PFD_SUPPORT_GDI           = $00000010
Const PFD_SUPPORT_OPENGL        = $00000020
Const PFD_GENERIC_FORMAT        = $00000040
Const PFD_NEED_PALETTE          = $00000080
Const PFD_NEED_SYSTEM_PALETTE   = $00000100
Const PFD_SWAP_EXCHANGE         = $00000200
Const PFD_SWAP_COPY             = $00000400
Const PFD_SWAP_LAYER_BUFFERS    = $00000800
Const PFD_GENERIC_ACCELERATED   = $00001000
Const PFD_SUPPORT_DIRECTDRAW    = $00002000

Const PFD_DEPTH_DONTCARE        = $20000000
Const PFD_DOUBLEBUFFER_DONTCARE = $40000000
Const PFD_STEREO_DONTCARE       = $80000000

Const WGL_FONT_LINES            = 0
Const WGL_FONT_POLYGONS         = 1

Const LPD_DOUBLEBUFFER          = $00000001
Const LPD_STEREO                = $00000002
Const LPD_SUPPORT_GDI           = $00000010
Const LPD_SUPPORT_OPENGL        = $00000020
Const LPD_SHARE_DEPTH           = $00000040
Const LPD_SHARE_STENCIL         = $00000080
Const LPD_SHARE_ACCUM           = $00000100
Const LPD_SWAP_EXCHANGE         = $00000200
Const LPD_SWAP_COPY             = $00000400
Const LPD_TRANSPARENT           = $00001000

Const LPD_TYPE_RGBA             = 0
Const LPD_TYPE_COLORINDEX       = 1

Const WGL_SWAP_MAIN_PLANE       = $00000001
Const WGL_SWAP_OVERLAY1         = $00000002
Const WGL_SWAP_OVERLAY2         = $00000004
Const WGL_SWAP_OVERLAY3         = $00000008
Const WGL_SWAP_OVERLAY4         = $00000010
Const WGL_SWAP_OVERLAY5         = $00000020
Const WGL_SWAP_OVERLAY6         = $00000040
Const WGL_SWAP_OVERLAY7         = $00000080
Const WGL_SWAP_OVERLAY8         = $00000100
Const WGL_SWAP_OVERLAY9         = $00000200
Const WGL_SWAP_OVERLAY10        = $00000400
Const WGL_SWAP_OVERLAY11        = $00000800
Const WGL_SWAP_OVERLAY12        = $00001000
Const WGL_SWAP_OVERLAY13        = $00002000
Const WGL_SWAP_OVERLAY14        = $00004000
Const WGL_SWAP_OVERLAY15        = $00008000
Const WGL_SWAP_UNDERLAY1        = $00010000
Const WGL_SWAP_UNDERLAY2        = $00020000
Const WGL_SWAP_UNDERLAY3        = $00040000
Const WGL_SWAP_UNDERLAY4        = $00080000
Const WGL_SWAP_UNDERLAY5        = $00100000
Const WGL_SWAP_UNDERLAY6        = $00200000
Const WGL_SWAP_UNDERLAY7        = $00400000
Const WGL_SWAP_UNDERLAY8        = $00800000
Const WGL_SWAP_UNDERLAY9        = $01000000
Const WGL_SWAP_UNDERLAY10       = $02000000
Const WGL_SWAP_UNDERLAY11       = $04000000
Const WGL_SWAP_UNDERLAY12       = $08000000
Const WGL_SWAP_UNDERLAY13       = $10000000
Const WGL_SWAP_UNDERLAY14       = $20000000
Const WGL_SWAP_UNDERLAY15       = $40000000

Const WGL_SWAPMULTIPLE_MAX      = 16    

;~IDEal Editor Parameters:
;~C#BlitzPlus
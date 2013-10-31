
; Optional function declarations for the Animation bOGL addon
; This file is only really to provide syntax highlighting

; Array parameters (e.g. 'arr[3]') are represented with underscores (e.g. 'arr__3__')

.lib " "

InitAnimationAddon()
UpdateAnimations()
LoadAnimation(root, file$)
LoadAnimBank(root, bk, start, size)
CopyAnimation(root, src)
Animate(ent, mode, speed#, fF, lF, trans)
SetAnimTime(ent, time#)
GetAnimTime#(ent)
GetNumFrames%(ent)
GetAnimMode%(ent)
IsAnimated%(ent)
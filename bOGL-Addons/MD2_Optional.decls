
; Optional function declarations for the MD2 bOGL addon
; This file is only really to provide syntax highlighting

; Array parameters (e.g. 'arr[3]') are represented with underscores (e.g. 'arr__3__')

.lib " "

InitMD2Addon()
UpdateMD2Anims()
LoadMD2Model(file$, parent, numInstances)
LoadMD2SubMesh(bk, st, sz, targetMesh, numInstances, doAutoMove)
CopyMD2Mesh(rootMesh, parent)
AnimateMD2(ent, mode, speed#, fF, lF, trans)
SetMD2AutoMove(ent, doAutoMove)
SetMD2AnimTime(ent, time#)
GetMD2AnimTime(ent)
GetMD2NumFrames(ent)
GetMD2AnimMode(ent)
GetMD2SeqByName(out__1__, ent, name$)
MD2_ClearUnused()


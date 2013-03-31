
; Optional function declarations for the MD2 bOGL addon

.lib " "

InitMD2Addon()
UpdateMD2Anims()
LoadMD2Model(file$, parent = 0)
LoadMD2SubMesh(bk, st, sz, targetMesh, doAutoMove = True)
AnimateMD2(ent, mode = MD2_MODE_LOOP, speed# = 1.0, fF = 0, lF = -1, trans = 0)
SetMD2AutoMove(ent, doAutoMove)
SetMD2AnimTime(ent, time#)
GetMD2SeqByName(out[1], ent, name$)
MD2_ClearUnused()



; Optional function declarations for the 3DAction bOGL addon
; This file is only really to provide syntax highlighting

; Object parameters (e.g. 'o.MyType') are represented with underscores (e.g. 'o__MyType__')

.lib " "

Init3DAction()
Update3DActions(rate#)
Stop3DActions()
RunAction(ent, act$)
StopActionsFor(ent)

LoopAction$(act$, n)
ComposeActions$(a0$, a1$, a2$, a3$, a4$, a5$, a6$, a7$, a8$, a9$)
SequenceActions$(a0$, a1$, a2$, a3$, a4$, a5$, a6$, a7$, a8$, a9$)

MoveBy$(time, x#, y#, z#, rate)
TurnBy$(time, x#, y#, z#, rate)
ScaleBy$(time, x#, y#, z#, rate)
FadeBy$(time, alpha#, rate)
TintBy$(time, r, g, b, rate)

MoveTo$(time, x#, y#, z#, rate)
TurnTo$(time, x#, y#, z#, rate)
ScaleTo$(time, x#, y#, z#, rate)
FadeTo$(time, alpha#, rate)
TintTo$(time, r, g, b, rate)

WaitFor$(time)
SendAction$(target__ActionListener__, msg)

TrackByPoint$(target, x#, y#, z#, strength#)
TrackByDistance$(target, dist#, strength#)
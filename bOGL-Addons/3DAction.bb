
; 3DAction: event-based 3D movement addon for bOGL
;==================================================


; This module provides Cocos3D-style "actions", for triggering and updating
; automatic entity movement

; Actions fire once and drive themselves, much like animations - this makes it
; easy to remove boring movement update code from your main loop and instead
; attach behaviours directly to the source of events
; You can also pass behaviours around as values, so an entity can decide what
; exactly it wants to do at runtime

; Actions control entity movement, rotation, scale, alpha, and colour
; They can also be used to "track" another entity, like a follow camera
; Multiple actions can be composed into a single loop or sequence

; Actions are represented as strings


Include "bOGL\bOGL.bb"


Type ActionListener
	Field msg
End Type

Type ACT3_Action
	Field ent, state, act$, parent.ACT3_Action
	Field aType, aLen, aRate, aPos#, e, s#, t#, u#, v#
End Type


Const ACT3_RATE_LINEAR = 0, ACT3_RATE_EASEIN = 1, ACT3_RATE_EASEOUT = 2, ACT3_RATE_EASEBOTH = 3

Const ACT3_TYPE_MB = 0, ACT3_TYPE_TB = 1, ACT3_TYPE_SB = 2, ACT3_TYPE_CB = 3, ACT3_TYPE_FB = 4
Const ACT3_TYPE_M2 = 5, ACT3_TYPE_T2 = 6, ACT3_TYPE_S2 = 7, ACT3_TYPE_C2 = 8, ACT3_TYPE_F2 = 9
Const ACT3_TYPE_TRP = 9, ACT3_TYPE_TRD = 10, ACT3_TYPE_WAIT = 11, ACT3_TYPE_SEND = 12
Const ACT3_TYPE_LOOP = 32, ACT3_TYPE_SEQ = 33, ACT3_TYPE_COMP = 34
Const ACT3_TYPE_UNDF = -1
Const ACT3_SIZE_CELL = 4, ACT3_SIZE_SIMP = 9, ACT3_EPSILON# = 0.001

Global ACT3_private_UDSlot_ = -1, ACT3_private_Temp_, ACT3_private_Idx_
Global ACT3_buffer_.ACT3_Action, ACT3_header_.ACT3_Action


; Interface
;===========

; Initialization

Function Init3DAction()		;Only call this once per program
	If ACT3_private_UDSlot_ < 0 Then ACT3_private_UDSlot_ = RegisterEntityUserDataSlot()
	If ACT3_private_Temp_ = 0 Then ACT3_private_Temp_ = CreateBank(ACT3_SIZE_CELL * ACT3_SIZE_SIMP)
	If ACT3_private_Idx_ = 0 Then ACT3_private_Idx_ = CreateBank(11 * 8)
	If ACT3_buffer_ = Null Then ACT3_buffer_ = New ACT3_Action
	If ACT3_header_ = Null Then ACT3_header_ = New ACT3_Action
End Function


; Updates

Function Update3DActions(rate# = 1.0)
	Insert ACT3_header_ After Last ACT3_Action
	Local a.ACT3_Action, b.ACT3_Action : For a = Each ACT3_Action
		If a = ACT3_buffer_ Then Exit
		
		If a\aType = ACT3_TYPE_UNDF Then ACT3_ExtractAction_ a	;Give it a defined type
		
		If a\aType <= ACT3_TYPE_SEND
			ACT3_ExecuteAction_ a, rate
			If a\aPos >= a\aLen - ACT3_EPSILON
				b = a : a = Before a
				ACT3_FinalizeAction_ b
			EndIf
		Else	;Move compounds over to the non-running area
			b = a : a = Before a
			Insert b After ACT3_buffer_
		EndIf
		
		If a = Null Then a = ACT3_header_ : Insert a Before First ACT3_Action
	Next
	Insert ACT3_header_ Before First ACT3_Action
End Function

Function Stop3DActions()
	Delete Each ACT3_Action
	ACT3_buffer_ = New ACT3_Action
	ACT3_header_ = New ACT3_Action
End Function

Function RunAction(ent, act$)
	ACT3_RunAction_ ent, act$, Null, True
End Function

Function StopActionsFor(ent)
	If ent = 0 Then Return
	Local a.ACT3_Action : For a = Each ACT3_Action
		If a\ent = ent Then Delete a
	Next
End Function


; Creation and composition

Function LoopAction$(act$, n = 0)
	PokeInt ACT3_private_Temp_, 0, n
	Local i, ret$ : For i = 0 To 3
		ret = ret + Chr(PeekByte(ACT3_private_Temp_, i))
	Next
	Return Chr(ACT3_TYPE_LOOP) + String(Chr(0), 3) + ret + act
End Function

Function ComposeActions$(a0$, a1$, a2$ = "", a3$ = "", a4$ = "", a5$ = "", a6$ = "", a7$ = "", a8$ = "", a9$ = "")
	Return ACT3_CompoundAction_(ACT3_TYPE_COMP, a0$, a1$, a2$, a3$, a4$, a5$, a6$, a7$, a8$, a9$)
End Function

Function SequenceActions$(a0$, a1$, a2$ = "", a3$ = "", a4$ = "", a5$ = "", a6$ = "", a7$ = "", a8$ = "", a9$ = "")
	Return ACT3_CompoundAction_(ACT3_TYPE_SEQ, a0$, a1$, a2$, a3$, a4$, a5$, a6$, a7$, a8$, a9$)
End Function

Function MoveBy$(time, x#, y#, z#, rate = ACT3_RATE_LINEAR)
	Return ACT3_SimpleAction_(ACT3_TYPE_MB, time, rate, 0, x, y, z, 0)
End Function

Function TurnBy$(time, x#, y#, z#, rate = ACT3_RATE_LINEAR)
	Local q#[3] : bOGL_QuatFromEuler_ q, x, y, z
	Return ACT3_SimpleAction_(ACT3_TYPE_TB, time, rate, 0, q[0], q[1], q[2], q[3])
End Function

Function ScaleBy$(time, x#, y#, z#, rate = ACT3_RATE_LINEAR)
	Return ACT3_SimpleAction_(ACT3_TYPE_SB, time, rate, 0, x, y, z, 0)
End Function

Function FadeBy$(time, alpha#, rate = ACT3_RATE_LINEAR)
	Return ACT3_SimpleAction_(ACT3_TYPE_FB, time, rate, 0, 0, 0, 0, alpha)
End Function

Function TintBy$(time, r, g, b, rate = ACT3_RATE_LINEAR)
	Return ACT3_SimpleAction_(ACT3_TYPE_CB, time, rate, 0, r, g, b, 0)
End Function

Function MoveTo$(time, x#, y#, z#, rate = ACT3_RATE_LINEAR)
	Return ACT3_SimpleAction_(ACT3_TYPE_M2, time, rate, 0, x, y, z, 0)
End Function

Function TurnTo$(time, x#, y#, z#, rate = ACT3_RATE_LINEAR)
	Local q#[3] : bOGL_QuatFromEuler_ q, x, y, z
	Return ACT3_SimpleAction_(ACT3_TYPE_T2, time, rate, 0, q[0], q[1], q[2], q[3])
End Function

Function ScaleTo$(time, x#, y#, z#, rate = ACT3_RATE_LINEAR)
	Return ACT3_SimpleAction_(ACT3_TYPE_S2, time, rate, 0, x, y, z, 0)
End Function

Function FadeTo$(time, alpha#, rate = ACT3_RATE_LINEAR)
	Return ACT3_SimpleAction_(ACT3_TYPE_F2, time, rate, 0, 0, 0, 0, alpha)
End Function

Function TintTo$(time, r, g, b, rate = ACT3_RATE_LINEAR)
	Return ACT3_SimpleAction_(ACT3_TYPE_C2, time, rate, 0, r, g, b, 0)
End Function

Function WaitFor$(time)
	Return ACT3_SimpleAction_(ACT3_TYPE_WAIT, time, 0, 0, 0, 0, 0, 0)
End Function

Function SendAction$(target.ActionListener, msg)
	Return ACT3_SimpleAction_(ACT3_TYPE_SEND, 0, msg, Handle target, 0, 0, 0, 0)
End Function

Function TrackByPoint$(target, time, x#, y#, z#, rate = ACT3_RATE_LINEAR)
	Return ACT3_SimpleAction_(ACT3_TYPE_TRP, time, rate, target, x, y, z, 0)
End Function

Function TrackByDistance$(target, time, dist#, rate = ACT3_RATE_LINEAR)
	Return ACT3_SimpleAction_(ACT3_TYPE_TRD, time, rate, target, dist, 0, 0, 0)
End Function


; Internal
;==========

Function ACT3_ExtractAction_(a.ACT3_Action)
	a\aType = Asc(a\act)
	Local b = ACT3_private_Temp_, i
	
	If a\aType <= ACT3_TYPE_SEND	;Simple - expand in-place
		For i = 4 To ACT3_SIZE_CELL * ACT3_SIZE_SIMP - 1
			PokeByte b, i, Asc(Mid(a\act, i + 1, 1))
		Next
		a\aLen = PeekInt(b, 4) : a\aRate = PeekInt(b, 8) : a\aPos = PeekFloat(b, 12)
		a\e = PeekInt(b, 16) : a\s = PeekFloat(b, 20) : a\t = PeekFloat(b, 24) : a\u = PeekFloat(b, 28) : a\v = PeekFloat(b, 32)
		ACT3_Convert2ToB_ a	;Convert "to" actions to "by" actions where appropriate
		
	ElseIf a\aType = ACT3_TYPE_LOOP
		ACT3_RunAction_ a\ent, Mid(a\act, 9), a, True	;Enqueue, will be extracted in its turn
		For i = 0 To 3
			PokeByte b, i, Asc(Mid(a\act, i + 5, 1))
		Next
		a\state = PeekInt(b, 0)
		
	Else
		For i = 0 To 3
			PokeByte b, i, Asc(Mid(a\act, i + 5, 1))
		Next
		Local idx = ACT3_private_Idx_, bLen = PeekInt(b, 0) * 8
		For i = 0 To bLen - 1
			PokeByte idx, i, Asc(Mid(a\act, i + 9, 1))
		Next
		a\state = bLen / 8
		
		Select a\aType
			Case ACT3_TYPE_SEQ
				ACT3_RunAction_ a\ent, Mid(a\act, PeekInt(idx, 0), PeekInt(idx, 4)), a, True
				
			Case ACT3_TYPE_COMP
				For i = 0 To bLen - 8 Step 8
					ACT3_RunAction_ a\ent, Mid(a\act, PeekInt(idx, i), PeekInt(idx, i + 4)), a, True
				Next
		End Select
	EndIf
End Function

Function ACT3_ExecuteAction_(a.ACT3_Action, s#)
	a\aPos = a\aPos + s
	Select a\aType
		Case ACT3_TYPE_MB
			MoveEntity a\ent, ACT3_Mov_(a,s, a\s), ACT3_Mov_(a,s, a\t), ACT3_Mov_(a,s, a\u)
			
		Case ACT3_TYPE_TB
		Case ACT3_TYPE_SB
		Case ACT3_TYPE_CB
		Case ACT3_TYPE_FB
		Case ACT3_TYPE_TRP
		Case ACT3_TYPE_TRD
		Case ACT3_TYPE_WAIT
			; Do nothing!
			
		Case ACT3_TYPE_SEND
			Local l.ActionListener = Object.ActionListener a\e
			If l <> Null Then l\msg = a\aRate
	End Select
End Function

Function ACT3_Mov_#(a.ACT3_Action, s#, d#)
	Select a\aRate
		Case ACT3_RATE_LINEAR : Return (s / a\aLen) * d
		Case ACT3_RATE_EASEIN
		Case ACT3_RATE_EASEOUT
		Case ACT3_RATE_EASEBOTH
	End Select
End Function

Function ACT3_FinalizeAction_(f.ACT3_Action)
	Local p.ACT3_Action = f\parent
	Delete f : If p = Null Then Return
	
	p\state = p\state - 1
	If p\state = 0
		ACT3_FinalizeAction_ p
		
	ElseIf p\aType = ACT3_TYPE_LOOP
		ACT3_RunAction_ p\ent, Mid(p\act, 9), p, False
		If p\state < 0 Then p\state = 0
		
	ElseIf p\aType = ACT3_TYPE_SEQ
		Local b = ACT3_private_Temp_, i
		For i = 0 To 3
			PokeByte b, i, Asc(Mid(p\act, i + 5, 1))
		Next
		Local idx = ACT3_private_Idx_, bLen = PeekInt(b, 0) * 8
		For i = 0 To bLen - 1
			PokeByte idx, i, Asc(Mid(p\act, i + 9, 1))
		Next
		
		i = (bLen / 8) - p\state
		ACT3_RunAction_ p\ent, Mid(p\act, PeekInt(idx, i * 8), PeekInt(idx, i * 8 + 4)), p, False
	EndIf
End Function

Function ACT3_RunAction_(ent, act$, p.ACT3_Action, thisFrame)
	Local a.ACT3_Action = New ACT3_Action
	a\ent = ent : a\act = act : a\aType = ACT3_TYPE_UNDF : a\parent = p
	If thisFrame Then Insert a Before ACT3_buffer_ Else Insert a Before First ACT3_Action
End Function

Function ACT3_Convert2ToB_(a.ACT3_Action)
	Local e.bOGL_Ent = bOGL_EntList_(a\ent)
	Select a\aType
		Case ACT3_TYPE_M2
			a\aType = ACT3_TYPE_MB
			a\s = a\s - e\x : a\t = a\t - e\y : a\u = a\u - e\z
			
		Case ACT3_TYPE_T2
			If Not e\Qv Then bOGL_UpdateQuat_ e\q, e\r
			Local qo#[3], qi#[3] : qi[0] = a\s : qi[1] = a\t : qi[2] = a\u : qi[3] = a\v
			e\q[0] = -e\q[0] : bOGL_QuatMul_ qo, qi, e\q : e\q[0] = -e\q[0]
			a\aType = ACT3_TYPE_TB
			a\s = qo[0] : a\t = qo[1] : a\u = qo[2] : a\v = qo[3]
			
		Case ACT3_TYPE_S2
			a\aType = ACT3_TYPE_SB
			a\s = a\s - e\sx : a\t = a\t - e\sy : a\u = a\u - e\sz
			
		Case ACT3_TYPE_C2
			a\aType = ACT3_TYPE_CB
			If e\m <> Null
				a\s = a\s - ((e\m\argb And $FF0000) Shr 16)
				a\t = a\t - ((e\m\argb And $FF00) Shr 8) : a\u = a\u - (e\m\argb And $FF)
			EndIf
			
		Case ACT3_TYPE_F2
			a\aType = ACT3_TYPE_FB
			If e\m <> Null Then a\v = a\v - e\m\alpha
	End Select
End Function

Function ACT3_SimpleAction_$(aType, aLen, aRate, e, s#, t#, u#, v#)
	Local b = ACT3_private_Temp_, i
	
	PokeInt b, 0, aType : PokeInt b, 4, aLen : PokeInt b, 8, aRate : PokeFloat b, 12, 0
	PokeInt b, 16, e : PokeFloat b, 20, s : PokeFloat b, 24, t : PokeFloat b, 28, u : PokeFloat b, 32, v
	
	Local ret$
	For i = 0 To (ACT3_SIZE_CELL * ACT3_SIZE_SIMP) - 1
		ret = ret + Chr(PeekByte(b, i))
	Next
	Return ret
End Function

Function ACT3_CompoundAction_$(aType, a0$, a1$, a2$, a3$, a4$, a5$, a6$, a7$, a8$, a9$)
	Local count = 2 + (a2 <> "") + (a3 <> "") + (a4 <> "") + (a5 <> "") + (a6 <> "") + (a7 <> "") + (a8 <> "") + (a9 <> "")
	Local ret$ = Chr(aType) + String(Chr(0), 3)
	Local b = ACT3_private_Idx_, bLen = count * 8 + 4, ofs = 4, p1 = bLen + 5, i
	PokeInt b, 0, count
	PokeInt b, ofs, p1 : PokeInt b, ofs + 4, Len(a0) : ofs = ofs + 8 : p1 = p1 + Len(a0)
	PokeInt b, ofs, p1 : PokeInt b, ofs + 4, Len(a1) : ofs = ofs + 8 : p1 = p1 + Len(a1)
	If a2 <> "" Then PokeInt b, ofs, p1 : PokeInt b, ofs + 4, Len(a2) : ofs = ofs + 8 : p1 = p1 + Len(a2)
	If a3 <> "" Then PokeInt b, ofs, p1 : PokeInt b, ofs + 4, Len(a3) : ofs = ofs + 8 : p1 = p1 + Len(a3)
	If a4 <> "" Then PokeInt b, ofs, p1 : PokeInt b, ofs + 4, Len(a4) : ofs = ofs + 8 : p1 = p1 + Len(a4)
	If a5 <> "" Then PokeInt b, ofs, p1 : PokeInt b, ofs + 4, Len(a5) : ofs = ofs + 8 : p1 = p1 + Len(a5)
	If a6 <> "" Then PokeInt b, ofs, p1 : PokeInt b, ofs + 4, Len(a6) : ofs = ofs + 8 : p1 = p1 + Len(a6)
	If a7 <> "" Then PokeInt b, ofs, p1 : PokeInt b, ofs + 4, Len(a7) : ofs = ofs + 8 : p1 = p1 + Len(a7)
	If a8 <> "" Then PokeInt b, ofs, p1 : PokeInt b, ofs + 4, Len(a8) : ofs = ofs + 8 : p1 = p1 + Len(a8)
	If a9 <> "" Then PokeInt b, ofs, p1 : PokeInt b, ofs + 4, Len(a9)
	For i = 0 To bLen - 1
		ret = ret + Chr(PeekByte(b, i))
	Next
	Return ret + a0 + a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9
End Function


;~IDEal Editor Parameters:
;~F#18#1C#34#56#5C#60#6A#72#76#7A#7E#83#87#8B#8F#93#98#9C#A0#A4
;~F#A8#AC#B0
;~C#BlitzPlus
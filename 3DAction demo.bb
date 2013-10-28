;Include bOGL
Include "bOGL\bOGL.bb"
;Inlcude action addon
Include "bOGL-Addons\3DAction.bb"

Init3DAction

Graphics3D "bOGL", 1024, 768, 32, 2
AmbientLight 128, 128, 128

Local camera = CreateCamera()
CameraClsColor camera, 32, 32, 64
Local light = CreateLight(255, 255, 255, BOGL_LIGHT_DIR)
PositionEntity light, 2, 6, -6
PointEntity light, 0, 0, -6


; Cubes are going to have some actions applied to them
Local cube1 = CreateCube()
PaintEntity cube1, 220, 0, 0
PositionEntity cube1, -9, -5, -20

Local cube2 = CreateCube()
PaintEntity cube2, 0, 220, 0
PositionEntity cube2, -3, -5, -20

Local cube3 = CreateCube()
PaintEntity cube3, 0, 0, 220
PositionEntity cube3, 3, -5, -20

Local cube4 = CreateCube()
PaintEntity cube4, 220, 220, 220
PositionEntity cube4, 9, -5, -20


; Actions can be composed together into loops, sequences, simultaneuos activation, etc.
; This makes for a very natural way to string together movements

; Since they're represented as strings, you don't need to worry about ownership or freeing

; Explode!
Local upAndOut$ = ComposeActions(ScaleTo(100, 3, 3, 1), FadeTo(100, 0))		;Expand and fade away
Local inAndDown$ = ComposeActions(ScaleTo(100, 0.5, 0.5, 1), FadeTo(100, 1))	;Contract and reappear
RunAction cube1, LoopAction(SequenceActions(upAndOut, inAndDown))	;Inflate and deflate

; Bounce and spin
Local bounce$ = SequenceActions(MoveBy(50, 0, 5, 0, ACT3_RATE_EASEOUT), MoveBy(50, 0, -5, 0, ACT3_RATE_EASEIN))
RunAction cube2, ComposeActions(LoopAction(TurnBy(30, 0, 45, 0)), LoopAction(bounce))	;Different durations... doesn't matter

Local l.ActionListener = New ActionListener
; This one sends a message to l to let us know when it's moving
Local goUp$ = MoveBy(50, 0, 6, 0), goDown$ = MoveBy(50, 0, -6, 0), stopped$ = ComposeActions(SendAction(l, 0), WaitFor(50))
RunAction cube3, LoopAction(SequenceActions(SendAction(l, 1), goUp, stopped, SendAction(l, -1), goDown, stopped))

; Absolute movement with trackers
Local topRight$ = MoveTo(50, 9, 5, -20)
Local orbit$ = SequenceActions(topRight, MoveTo(50, -9, 5, -20), topRight, MoveTo(50, 9, -5, -20))
RunAction cube4, LoopAction(orbit)

Local moon1 = CreateCube(), moon2 = CreateCube()	;These will follow it
ScaleEntity moon1, 0.25, 0.25, 0.25 : ScaleEntity moon2, 0.25, 0.25, 0.25
PaintEntity moon1, 128, 128, 128 : PaintEntity moon2, 64, 64, 64

RunAction moon1, TrackByDistance(cube4, 3.2)	;Just follow it arbitrarily
RunAction moon2, TrackByPoint(cube4, 0.5, 3, 0)		;Stay above and to the right
; Note that tracker actions have no timer and never expire on their own


Include "bOGL-Addons\Draw2D.bb"
InitDraw2D : Local f = LoadFont2D("Media\Blitz.png") : SetFont2D f

Const SC_FPS = 60 : Local rTime = Floor(1000.0 / SC_FPS)

; Mainloop
While Not KeyHit(1)
	Local cTime = MilliSecs()
	
	; Look - NO USER UPDATE CODE IN THE MAIN LOOP!
	
	Update3DActions	;This handles action updates
	RenderWorld
	
	; Onscreen text
	BeginDraw2D
	Text2D 50, 670, "Exploder!"
	Text2D 330, 670, "Bouncer"
	
	Local elevatorState$ : Select l\msg		;We can poll a listener to see what actions are doing and react accordingly
		Case 0 : elevatorState = "stopped"
		Case 1 : elevatorState = "going up"
		Case -1 : elevatorState = "going down"
	End Select
	Text2D 580, 670, "Elevator is " + elevatorState
	
	Text2D 800, 670, "Orbiter with two followers"
	EndDraw2D
	
	SwapBuffers(bOGL_hMainDC)
	Delay rTime - (MilliSecs() - cTime) - 1
Wend

EndGraphics3D
End


;~IDEal Editor Parameters:
;~C#BlitzPlus
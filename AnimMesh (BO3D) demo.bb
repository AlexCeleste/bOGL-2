;Include bOGL
Include "bOGL\bOGL.bb"
;Inlcude mesh addon
Include "bOGL-Addons/MeshLoader.bb"
;Include animation addon
Include "bOGL-Addons/Animation.bb"

; Initialize graphics
Graphics3D "bOGL", 1024, 768, 32, 2

; Initialize libraries
InitMeshLoaderAddon
InitAnimationAddon


AmbientLight 128, 128, 128

Local yPiv = CreatePivot(), xPiv = CreatePivot(yPiv)
Local camera = CreateCamera(xPiv)
CameraClsColor camera, 32, 32, 64
PositionEntity camera, 0, 0, 20 : PositionEntity yPiv, 0, 5, 0
RotateEntity xPiv, -15, 0, 0 : RotateEntity yPiv, 0, 20, 0

Local light = CreateLight(255, 255, 255, BOGL_LIGHT_DIR)
PositionEntity light, 200, 600, -200
PointEntity light, 0, 0, 0

Local cube = CreateCube() : FlipPolygons cube
ScaleEntity cube, 30, 30, 30 : PositionEntity cube, 0, 30, 0
Local tex = LoadTexture("Media\chorme-2.png")
EntityTexture cube, tex : FreeTexture tex


; Start by loading up a mesh: manipulate it like any other entity
; This model is Psionic's free ninja.b3d, converted with Tools\bo3d_conv.bb, using 16-bit accuracy
Local ninja = LoadMesh("Media\ninja.bo3d")
tex = LoadTexture("Media\nskinbr.jpg")	;Texture it normally
EntityTexture ninja, tex : FreeTexture tex
RotateEntity ninja, 0, 180, 0

; Next we need to load and apply an animation (there is no LoadAnimMesh - make one if you want)
; Animations are loaded "onto" an existing entity; it should have the same bone names
; Obviously in this case it will match perfectly
LoadAnimation ninja, "Media\ninja.bo3d"
; The ninja can now be animated with Animate()


; Build a list of animation sequences
; (this data needs to be supplied by the artist, it's not in the file - for best results use consistent ranges)
Dim seq(20, 1), seqName$(20)
seq(0, 0) = 1    : seq(0, 1) = 14   : seqName(0) = "Walk"
seq(1, 0) = 15   : seq(1, 1) = 30   : seqName(1) = "Stealth Walk"
seq(2, 0) = 32   : seq(2, 1) = 44   : seqName(2) = "Punch and swipe sword"
seq(3, 0) = 45   : seq(3, 1) = 59   : seqName(3) = "Swipe and spin sword"
seq(4, 0) = 60   : seq(4, 1) = 68   : seqName(4) = "Overhead twohanded downswipe"
seq(5, 0) = 69   : seq(5, 1) = 72   : seqName(5) = "Up to block position (play backwards to lower sword if you want)"
seq(6, 0) = 73   : seq(6, 1) = 83   : seqName(6) = "Forward kick"
seq(7, 0) = 84   : seq(7, 1) = 93   : seqName(7) = "Pick up from floor (or down to crouch at frame 87)"
seq(8, 0) = 94   : seq(8, 1) = 102  : seqName(8) = "Jump"
seq(9, 0) = 103  : seq(9, 1) = 111  : seqName(9) = "Jump without height (for programmer controlled jumps)"
seq(10, 0) = 112 : seq(10, 1) = 125 : seqName(10) = "High jump to Sword Kill (Finish em off move??)"
seq(11, 0) = 126 : seq(11, 1) = 133 : seqName(11) = "Side Kick"
seq(12, 0) = 134 : seq(12, 1) = 145 : seqName(12) = "Spinning Sword attack (might wanna speed this up in game)"
seq(13, 0) = 146 : seq(13, 1) = 158 : seqName(13) = "Backflip"
seq(14, 0) = 159 : seq(14, 1) = 165 : seqName(14) = "Climb wall"
seq(15, 0) = 166 : seq(15, 1) = 173 : seqName(15) = "Death 1 - Fall back onto ground"
seq(16, 0) = 174 : seq(16, 1) = 182 : seqName(16) = "Death 2 - Fall forward onto ground"
seq(17, 0) = 184 : seq(17, 1) = 205 : seqName(17) = "Idle 1 - Breathe heavily"
seq(18, 0) = 206 : seq(18, 1) = 250 : seqName(18) = "Idle 2"
seq(19, 0) = 251 : seq(19, 1) = 300 : seqName(19) = "Idle 3"


; Start the model off animating in the first sequence
; This model works best without transitions
Local currentSeq = 0
Animate ninja, ANIM_MODE_LOOP, .2, seq(currentSeq, 0), seq(currentSeq, 1), 0


Include "bogl-Addons\Draw2D.bb"
InitDraw2D : LoadFont2D("Media\Blitz.png")

Const SC_FPS = 60 : Local rTime = Floor(1000.0 / SC_FPS)

; Mainloop
While Not KeyHit(1)
	Local cTime = MilliSecs()
	
	TurnEntity yPiv, 0, (KeyDown(205) - KeyDown(203)) * 0.5, 0
	TurnEntity xPiv, (KeyDown(208) - KeyDown(200)) * 0.5, 0, 0
	MoveEntity camera, 0, 0, (KeyDown(44) - KeyDown(30)) * 0.1
	If EntityXAngle(xPiv) > 0 Then RotateEntity xPiv, 0, 0, 0 : ElseIf EntityXAngle(xPiv) < -89 Then RotateEntity xPiv, -89, 0, 0
	If EntityZ(camera) < 8 Then PositionEntity camera, 0, 0, 8 : ElseIf EntityZ(camera) > 29 Then PositionEntity camera, 0, 0, 29
	
	UpdateAnimations
	UpdateBonedMeshes
	
	RenderWorld
	
	Local change = KeyHit(27) - KeyHit(26)
	If change
		currentSeq = currentSeq + change
		If currentSeq < 0 Then currentSeq = 19 : ElseIf currentSeq > 19 Then currentSeq = 0
		Animate ninja, ANIM_MODE_LOOP, 0.2, seq(currentSeq, 0), seq(currentSeq, 1), 0
	EndIf
	
	BeginDraw2D
	Text2D 5, 5, "Skinned model loaded from compressed BO3D"
	Text2D 5, 25, "Arrow keys to rotate camera, A and Z to zoom"
	Text2D 5, 40, "Use [ and ] to change animation sequence"
	Text2D 5, 80, "Current animation: " + "(#" + currentSeq + ") " + seqName(currentSeq)
	EndDraw2D
	
	; Swap the back buffer with the front buffer
	SwapBuffers(bOGL_hMainDC)
	
	Delay rTime - (MilliSecs() - cTime) - 1
Wend

EndGraphics3D
End


;~IDEal Editor Parameters:
;~C#BlitzPlus
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


Local cube1 = CreateCube()
PaintEntity cube1, 255, 255, 255
PositionEntity cube1, -2, 0, -10
Local cube2 = CreateCube()
;PaintEntity cube1, 255, 255, 255
PositionEntity cube2, 2, 0, -10


Include "bOGL-Addons\Draw2D.bb"
InitDraw2D
Local f = LoadFont2D("Media\Blitz.png") : SetFont2D f


Local a$, b$, l.ActionListener = New ActionListener
a = SequenceActions(MoveBy(25, 0, 1, 0), MoveBy(25, 0, -1, 0), SendAction(l, True))
b = ComposeActions(MoveBy(100, 0, 1, 0), MoveBy(50, 1, 0, 0))
RunAction cube1, LoopAction(a)
RunAction cube2, b


Function countactionobjects()
	Local a.ACT3_Action, c : For a = Each ACT3_Action
		c=c+1
	Next
	Return c
End Function


Const SC_FPS = 60 : Local rTime = Floor(1000.0 / SC_FPS)

; Mainloop
While Not KeyHit(1)
	Local cTime = MilliSecs()
	
	Update3DActions
	RenderWorld
	
	BeginDraw2D
	Text2D 10, 0, "A done: " + l\msg
	EndDraw2D
	
	; Swap the back buffer with the front buffer
	SwapBuffers(bOGL_hMainDC)
	
	Delay rTime - (MilliSecs() - cTime) - 1
Wend

EndGraphics3D
End


;~IDEal Editor Parameters:
;~C#BlitzPlus
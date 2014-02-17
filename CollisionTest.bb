;Include bOGL
Include "bOGL\bOGL.bb"
;Inlcude action addon
Include "bOGL-Addons\Collision.bb"

InitCollisionAddon

Graphics3D "bOGL", 1024, 600, 32, 2
AmbientLight 128, 128, 128

Local camera = CreateCamera()
CameraClsColor camera, 32, 32, 64
Local light = CreateLight(255, 255, 255, BOGL_LIGHT_DIR)
PositionEntity light, 2, 6, -6
PointEntity light, 0, 0, -6


Local piv = CreatePivot()
;PositionEntity piv, 0, 0, -10
;ScaleEntity piv, 2, 0.25, 2

Local cube = CreateCube(piv)
PaintEntity cube, 255, 0, 0
PositionEntity cube, 0, 0, -10
ScaleEntity cube, 2, 0.25, 2
TurnEntity cube, 0, 0, 45

Local c2 = CreateCube()
PaintEntity c2, 0, 0, 255
ScaleEntity c2, 0.5, 0.5, 0.5
PositionEntity c2, 3, -2, -10


Local listener = CreateBank()
SetCollisionListener listener

MakeBlocker cube, 4, 0.5, 4, COLL_RESPONSE_POST + COLL_RESPONSE_STOP
MakeCollider c2, 0.5


Include "bOGL-Addons\Draw2D.bb"
InitDraw2D : Local f = LoadFont2D("Media\Blitz.png") : SetFont2D f

Const SC_FPS = 60, SPD# = 0.04 : Local rTime = Floor(1000.0 / SC_FPS)

While Not KeyHit(1)
	Local cTime = MilliSecs()
	
	MoveEntity c2, (KeyDown(205) - KeyDown(203)) * SPD, (KeyDown(200) - KeyDown(208)) * SPD, 0
	
	UpdateCollisions
	RenderWorld
	
	BeginDraw2D
	If BankSize(listener)
		Text2D 10, 10, "Collided"
	Else
		Text2D 10, 10, "No collision"
	EndIf
	EndDraw2D
	
	SwapBuffers(bOGL_hMainDC)
	Delay rTime - (MilliSecs() - cTime) - 1
Wend

EndGraphics3D
End


;~IDEal Editor Parameters:
;~C#BlitzPlus
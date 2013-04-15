;Include bOGL
Include "bOGL\bOGL.bb"
;Inlcude mesh addon
Include "bOGL-Addons/MeshLoader.bb"

; Initialize graphics
Graphics3D "bOGL", 1024, 768, 32, 2

; Initialize mesh loader library: #### DO NOT SKIP THIS ####
InitMeshLoaderAddon

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

Local bone = GetChildByName(ninja, "Joint11")


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
	
	TurnEntity bone, 1, 0, 0
	UpdateBonedMeshes
	
	RenderWorld
	
	BeginDraw2D
	Text2D 5, 5, "Skinned model loaded from compressed BO3D"
	Text2D 5, 25, "Arrow keys to rotate camera, A and Z to zoom"
	EndDraw2D
	
	; Swap the back buffer with the front buffer
	SwapBuffers(bOGL_hMainDC)
	
	Delay rTime - (MilliSecs() - cTime) - 1
Wend

EndGraphics3D
End


;~IDEal Editor Parameters:
;~C#BlitzPlus
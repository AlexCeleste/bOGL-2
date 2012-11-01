;Include bOGL
Include "bOGL\bOGL.bb"

; Initialize graphics
Graphics3D "bOGL", 1024, 768, 32, 2

AmbientLight 128, 128, 128

; Create a camera
Local camera = CreateCamera()
;CameraClsColor camera, 255, 0, 255

; Load a texture
Local texture = LoadTexture("bOGL.png")

; Create the cube
Local cube = CreateCube()
PaintEntity cube, 255, 255, 255

Local light = CreateLight(255, 255, 255, BOGL_LIGHT_DIR)
PositionEntity light, 2, 6, -6
PointEntity light, 0, 0, -6

; Position and texture the cube
PositionEntity cube, 0, 0, -10
EntityTexture cube, texture
FreeTexture texture

;RotateSubMesh cube, 0, 3, 25, 0, 0, 0, 1, 0
;TranslateSubMesh cube, 0, 3, 0, 0.5, 0
;ScaleSubMesh cube, 0, 3, 1.5, 1, 1.5, 0, 1, 0

Local child = CreateCube(cube)
PositionEntity child, 3, 0, 0
ScaleEntity child, 0.6, 0.6, 0.6
EntityFX child, BOGL_FX_ADDBLEND : PaintEntity child, 0, 255, 128


Local sCube = CreateCube()
RotateEntity sCube, 0, 0, 45
PositionEntity sCube, 0, 0, -7
EntityFX sCube, BOGL_FX_STENCIL_INCR

Local sCamera = CreateCamera(camera)
CameraDrawMode sCamera, BOGL_CAM_STENCIL


Const SC_FPS = 60 : Local rTime = Floor(1000.0 / SC_FPS)

; Mainloop
While Not KeyHit(1)
	Local cTime = MilliSecs()
	
	TurnEntity cube, 0.4, 0.6, 0.8
	TurnEntity child, 0, 0.5, 0
	
	Local scl# = 1.0 + Sin(MilliSecs() / 1500.0 * 180) / 4.0
	ScaleEntity cube, scl, scl, scl
	
	; Render stencil buffer
;	ShowEntity sCube, True
;	RenderStencil
	ShowEntity sCube, False
	
	; Render world
	RenderWorld BOGL_STENCIL_OFF
	
	; Swap the buffer the front buffer
	SwapBuffers(bOGL_hMainDC)
	
	Delay rTime - (MilliSecs() - cTime) - 1
;	DebugLog MilliSecs() - cTime
Wend
End

;~IDEal Editor Parameters:
;~C#BlitzPlus
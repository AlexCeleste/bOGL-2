;Include bOGL
Include "bOGL\bOGL.bb"

; Initialize graphics
Graphics3D "bOGL", 1024, 768, 32, 2

AmbientLight 128, 128, 128

; Create a camera
Local camera = CreateCamera()

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


; Mainloop
While Not KeyHit(1)
	TurnEntity cube, 0.4, 0.6, 0.8
	TurnEntity child, 0, 0.5, 0
	
	Local scl# = 1.0 + Sin(MilliSecs() / 1000.0 * 180) / 4.0
	ScaleEntity cube, scl, scl, scl
	
	; Render world
	RenderWorld()
	
	; Swap the buffer the front buffer
	SwapBuffers(bOGL_hMainDC)
	glFinish
	
	Delay 10
Wend
End

;~IDEal Editor Parameters:
;~C#BlitzPlus
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
;cube2 = CreateCube()
PaintEntity cube, 255, 255, 255

Local light = CreateLight(255, 255, 255, BOGL_LIGHT_DIR)
PositionEntity light, 2, 6, -6
PointEntity light, 0, 0, -6

; Position and texture the cube
PositionEntity cube, 0, 0, -6
EntityTexture cube, texture
FreeTexture texture

;RotateSubMesh cube, 0, 3, 25, 0, 0, 0, 1, 0
;TranslateSubMesh cube, 0, 3, 0, 0.5, 0
;ScaleSubMesh cube, 0, 3, 1.5, 1, 1.5, 0, 1, 0

; Mainloop
While Not KeyHit(1)
	TurnEntity cube, 0.4, 0.6, 0.8
	
;	TurnEntity cube, 0, 2.5, 0
;	MoveEntity cube, 0, 0, -0.1
	
	; Render world
	RenderWorld()
	
	; Swap the buffer the front buffer
	SwapBuffers(bOGL_hMainDC)
	glFinish
	
	Delay 8
Wend
End

;~IDEal Editor Parameters:
;~C#BlitzPlus
;Include bOGL
Include "bOGL\bOGL.bb"

; Initialize graphics
Graphics3D "bOGL", 1024, 768, 32, 2

AmbientLight 128, 128, 128

; Create a camera
Local camera = CreateCamera()
CameraClsColor camera, 32, 32, 64

; Load a texture
;Local texture = LoadTexture("bOGL.png")

; Create the cube
;Local cube = CreateCube()
;PaintEntity cube, 255, 255, 255

Local light = CreateLight(255, 255, 255, BOGL_LIGHT_DIR)
PositionEntity light, 2, 6, -6
PointEntity light, 0, 0, -6

; Position and texture the cube
;PositionEntity cube, 0, 0, -10
;EntityTexture cube, texture
;FreeTexture texture

;RotateSubMesh cube, 0, 3, 25, 0, 0, 0, 1, 0
;TranslateSubMesh cube, 0, 3, 0, 0.5, 0
;ScaleSubMesh cube, 0, 3, 1.5, 1, 1.5, 0, 1, 0

;Local child = CreateCube(cube)
;PositionEntity child, 3, 0, 0
;ScaleEntity child, 0.6, 0.6, 0.6
;EntityFX child, BOGL_FX_ADDBLEND : PaintEntity child, 0, 255, 128
;
;
;Local sCube = CreateCube()
;RotateEntity sCube, 0, 0, 45
;PositionEntity sCube, 0, 0, -7
;EntityFX sCube, BOGL_FX_STENCIL_INCR
;EntityAlpha sCube, 0	;This will hide it from the world render, but stencilling will still work


;Local sCamera = CreateCamera(camera)
;CameraDrawMode sCamera, BOGL_CAM_STENCIL

;ShowEntity cube, 0

Const SC_FPS = 60 : Local rTime = Floor(1000.0 / SC_FPS)

Include "bOGL-Addons/MD2.bb"
InitMD2Addon
Local dr = LoadMD2Model("dragon.md2")
PositionEntity dr, 0, 0, -10
ScaleEntity dr, 0.1, 0.1, -0.1
Local tex = LoadTexture("dragon.png")
EntityTexture dr, tex
RotateEntity dr, 90, -90, 0

Local seq[1] : GetMD2SeqByName seq, dr, "wave"
AnimateMD2 dr, MD2_MODE_LOOP, .1, seq[0], seq[1], 12


; Mainloop
While Not KeyHit(1)
	Local cTime = MilliSecs()
	
;	TurnEntity cube, 0.4, 0.6, 0.8
;	TurnEntity child, 0, 0.5, 0
;	TurnEntity cube, 0, 2, 0
;	MoveEntity cube, 0, 0, -0.1
	
;	TurnEntity camera, 0, (KeyDown(205) - KeyDown(203)) * 0.5, 0
;	MoveEntity camera, 0, 0, (KeyDown(200) - KeyDown(208)) * -0.1
	
;	TurnEntity dr, 0, 0, 0.2
	UpdateMD2Anims
	
;	Local scl# = 1.0 + Sin(MilliSecs() / 1500.0 * 180) / 4.0
;	ScaleEntity cube, scl, scl, scl
	
	; Render stencil buffer
;	RenderStencil
	
	; Render world (try BOGL_STENCIL_TRUE and BOGL_STENCIL_FALSE too)
	RenderWorld BOGL_STENCIL_OFF
	
	; Swap the back buffer with the front buffer
	SwapBuffers(bOGL_hMainDC)
	
	Delay rTime - (MilliSecs() - cTime) - 1
Wend

EndGraphics3D
End


;~IDEal Editor Parameters:
;~C#BlitzPlus
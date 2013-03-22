;Include bOGL
Include "bOGL\bOGL.bb"
;Include 2D drawing addon
Include "bOGL\Addons\Draw2D.bb"

; Initialize graphics; Draw2D needs this too
Graphics3D "bOGL", 1024, 768, 32, 2
AmbientLight 128, 128, 128

Local camera = CreateCamera()
CameraClsColor camera, 32, 32, 64
CameraViewport camera, 0, 0, 256, 192

Local texture = LoadTexture("bOGL.png")

Local cube = CreateCube()
PaintEntity cube, 255, 255, 255

Local light = CreateLight(255, 255, 255, BOGL_LIGHT_DIR)
PositionEntity light, 2, 6, -6
PointEntity light, 0, 0, -6

PositionEntity cube, 0, 0, -10
EntityTexture cube, texture
FreeTexture texture

Local child = CreateCube(cube)
PositionEntity child, 3, 0, 0
ScaleEntity child, 0.6, 0.6, 0.6
EntityFX child, BOGL_FX_ADDBLEND : PaintEntity child, 0, 255, 128


Local sCamera = CreateCamera(camera)
CameraDrawMode sCamera, BOGL_CAM_STENCIL

Const SC_FPS = 60 : Local rTime = Floor(1000.0 / SC_FPS)

; Must initialise the 2D drawing library before use
InitDraw2D

Local mat1 = LoadTexture("Media\Gauss.png")	;Images and materials are loaded using bOGL's LoadTexture
Local mat2 = LoadTexture("Media\Native4.png")
Local img = LoadTexture("Media\Blitz3D.png")
ApplyMaskColor img, $00000000	;Textures are loaded without alpha, so it needs to be applied afterward

Local f = LoadFont2D("Media\Blitz.png") : SetFont2D f

Local bbPix = CreateBank(256 * 192 * 4)
Local bgTex = CreateTexture(256, 192, 0)


; Mainloop
While Not KeyHit(1)
	Local cTime = MilliSecs()
	
	TurnEntity cube, 0.4, 0.6, 0.8
	TurnEntity child, 0, 0.5, 0
	Local scl# = 1.0 + Sin(MilliSecs() / 1500.0 * 180) / 4.0
	ScaleEntity cube, scl, scl, scl
	RenderWorld
	
	GrabBackBuffer 0, 0, 256, 192, bbPix, False		;Copy the backbuffer to a texture
	UpdateTexture bgTex, 0, 0, 256, 192, bbPix, False
	
	;2D operations must take place within a BeginDraw2D/EndDraw2D block
	BeginDraw2D
	
	SetBlend2D B2D_BLEND_NONE	;Draw the low-res 3D scene in the background
	SetScale2D 4.0, 4.0
	SetRotation2D 0.0
	DrawImage2D bgTex, 512, 384
	SetScale2D 1.0, 1.0
	
	SetMaterial2D 0
	SetColor2D 255, 0, 0
	Rect2D 1, 1, 2, 100		;Pixel-accurate
	SetColor2D 0, 0, 255
	Rect2D 3, 1, 1, 100
	SetColor2D 255, 0, 0
	Rect2D GraphicsWidth() - 4, GraphicsHeight() - 101, 2, 100
	SetColor2D 0, 0, 255
	Rect2D GraphicsWidth() - 2, GraphicsHeight() - 101, 1, 100
	
	SetBlend2D B2D_BLEND_ADD
	SetMaterial2D mat1
	SetColor2D 255, 255, 255
	
	Rect2D 800, 300, 100, 100, False, 10
	Rect2D 800, 450, 100, 100, True, 5
	Plot2D 850, 350, 5
	
	Line2D 100, 500, 400, 300, 3
	
	SetRotation2D (MilliSecs() / 40) Mod 360	;Rotation only affects ovals, text, and images
	Oval2D 600, 400, 50, 100, False, 2.5
	
	SetBlend2D B2D_BLEND_ALPHA	;Blend affects everything
	DrawImage2D img, 750, 150
	;DrawSubRect2D img, 750, 350, 100, 100, 100, 100
	;DrawImageLine2D img2, 512, 384, MouseX(), MouseY(), 32, False
	
	SetRotation2D 0
	Text2D 10, 0, "DRAW2D!"
	Text2D 10, 20, "Hi-res 2D graphics over a low virtual-resolution 3D scene"
	
	EndDraw2D	;Done with the 2D stuff
	
	; Swap the back buffer with the front buffer
	SwapBuffers(bOGL_hMainDC)
	
	Delay rTime - (MilliSecs() - cTime) - 1
Wend

EndGraphics3D
End


;~IDEal Editor Parameters:
;~C#BlitzPlus

; If you haven't already, take a look at the MD2 demo first to get an idea of what the other code here is for

Include "bOGL\bOGL.bb"
Include "bOGL-Addons/MD2.bb"
Include "bOGL-Addons/Particle.bb"
Include "bOGL-Addons/Draw2D.bb"

Graphics3D "bOGL", 1024, 768, 32, 2

; Initialize libraries: #### DO NOT SKIP THIS ####
InitMD2Addon
InitParticleAddon
InitDraw2D : LoadFont2D("Media\Blitz.png")

AmbientLight 128, 128, 128

Local yPiv = CreatePivot(), xPiv = CreatePivot(yPiv)
Local camera = CreateCamera(xPiv)
CameraClsColor camera, 32, 32, 64
PositionEntity camera, 0, 0, 20 : PositionEntity yPiv, 0, 3, 0
RotateEntity xPiv, -25, 0, 0 : RotateEntity yPiv, 0, 20, 0

Local light = CreateLight(255, 255, 255, BOGL_LIGHT_DIR)
PositionEntity light, 200, 600, -200
PointEntity light, 0, 0, 0

Local cube = CreateCube() : FlipPolygons cube
ScaleEntity cube, 30, 30, 30 : PositionEntity cube, 0, 30, 0
Local tex = LoadTexture("Media\chorme-2.png")
EntityTexture cube, tex : FreeTexture tex


Local emitter = CreateEmitter(0, 30)
SetParticleDirection emitter, 0, -0.2, 1, 0.2
SetParticleSpeed emitter, 0.04, 0.1
SetParticleRGB emitter, 200, 20, 20, 0.2
SetParticleSize emitter, 0.25, 0.1
tex = LoadTexture("Media\Gauss.png") : SetParticleTexture emitter, tex : FreeTexture tex
SetParticleFX emitter, BOGL_FX_ADDBLEND


Local dragon = LoadMD2Model("Media\dragon.md2")
ScaleEntity dragon, 0.1, 0.1, -0.1		;(Has negative scale)
tex = LoadTexture("Media\dragon.png") : EntityTexture dragon, tex : FreeTexture tex
RotateEntity dragon, 90, -90, 0			;(...and is the wrong way up - blame DirectX)
PositionEntity dragon, 0, 2.44, 0

Local frames[1];, vert = 137
GetMD2SeqByName frames, dragon, "stand"
AnimateMD2 dragon, MD2_MODE_LOOP, 0.15, frames[0], frames[1], 8


Const SC_FPS = 60 : Local rTime = Floor(1000.0 / SC_FPS)

; Mainloop
While Not KeyHit(1)
	Local cTime = MilliSecs()
	
	TurnEntity yPiv, 0, (KeyDown(205) - KeyDown(203)) * 0.5, 0
	TurnEntity xPiv, (KeyDown(208) - KeyDown(200)) * 0.5, 0, 0
	MoveEntity camera, 0, 0, (KeyDown(44) - KeyDown(30)) * 0.1
	If EntityXAngle(xPiv) > -5 Then RotateEntity xPiv, -5, 0, 0 : ElseIf EntityXAngle(xPiv) < -89 Then RotateEntity xPiv, -89, 0, 0
	If EntityZ(camera) < 8 Then PositionEntity camera, 0, 0, 8 : ElseIf EntityZ(camera) > 29 Then PositionEntity camera, 0, 0, 29
	
	UpdateMD2Anims
	
	Local tfv#[2]	;Just a really dumb way to bind to a vertex's (#137) position
	TFormPoint VertexX(dragon, 137), VertexY(dragon, 137), VertexZ(dragon, 137), dragon, 0, tfv
	PositionEntity emitter, tfv[0], tfv[1], tfv[2]
	If Not ((MilliSecs() / 100) Mod 30) Then FireEmitter emitter, 2000
	
	UpdateParticles camera
	
	RenderWorld
	
	BeginDraw2D
	Text2D 5, 10, "Arrow keys to rotate camera, A and Z to zoom"
	EndDraw2D
	
	SwapBuffers(bOGL_hMainDC)
	Delay rTime - (MilliSecs() - cTime) - 1
Wend

EndGraphics3D
End


;~IDEal Editor Parameters:
;~C#BlitzPlus
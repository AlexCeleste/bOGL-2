;Include bOGL
Include "bOGL\bOGL.bb"
;Inlcude particle addon
Include "bOGL-Addons\Particle.bb"

InitParticleAddon

Graphics3D "bOGL", 1024, 768, 32, 2
AmbientLight 128, 128, 128

Local camera = CreateCamera()
CameraClsColor camera, 32, 32, 64
Local light = CreateLight(255, 255, 255, BOGL_LIGHT_DIR)
PositionEntity light, 2, 6, -6
PointEntity light, 0, 0, -6


; Add some objects
Local base = CreateCube()
PositionEntity base, 0, 0, -9
PaintEntity base, 255, 0, 0


; Add some particle stuff
Local emit = CreateEmitter(0, 10)
PositionEntity emit, 0, 0, -7
FireEmitter emit, 10000
Local tex = LoadTexture("Media\Gauss.png")
SetParticleTexture emit, tex
SetParticleFX emit, BOGL_FX_ADDBLEND

Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)


Include "bOGL-Addons\Draw2D.bb"
InitDraw2D : Local f = LoadFont2D("Media\Blitz.png") : SetFont2D f

Const SC_FPS = 60 : Local rTime = Floor(1000.0 / SC_FPS)

; Mainloop
While Not KeyHit(1)
	Local cTime = MilliSecs()
	
	UpdateParticles camera
	RenderWorld
	
	; Onscreen text
	BeginDraw2D
	Text2D 10, 10, e\pCount
	Text2D 10, 25, BankSize(e\pData)
	Text2D 10, 40, CountVertices(e\mesh)
	EndDraw2D
	
	SwapBuffers(bOGL_hMainDC)
	Delay rTime - (MilliSecs() - cTime) - 1
Wend

EndGraphics3D
End


;~IDEal Editor Parameters:
;~C#BlitzPlus
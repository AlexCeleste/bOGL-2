;Include bOGL
Include "bOGL\bOGL.bb"
;Inlcude MD2 addon
Include "bOGL-Addons/MD2.bb"

; Initialize graphics
Graphics3D "bOGL", 1024, 768, 32, 2

; Initialize MD2 library: DO NOT SKIP THIS
InitMD2Addon

AmbientLight 128, 128, 128

Local yPiv = CreatePivot(), xPiv = CreatePivot(yPiv)
Local camera = CreateCamera(xPiv)
CameraClsColor camera, 32, 32, 64
PositionEntity camera, 0, 0, 10

Local light = CreateLight(255, 255, 255, BOGL_LIGHT_DIR)
PositionEntity light, 2, 6, -6
PointEntity light, 0, 0, -6


Local dragon = LoadMD2Model("dragon.md2")	;Load our dragon model (from B3D)
ScaleEntity dragon, 0.1, 0.1, -0.1		;(Has negative scale)
Local tex = LoadTexture("dragon.png")	;Texture it normally
EntityTexture dragon, tex
RotateEntity dragon, 90, -90, 0			;(...and is the wrong way up - blame DirectX)

;Search for the anim sequence named "wave", putting its start and end in the seq[] array
Local seq[1] : GetMD2SeqByName seq, dragon, "wave"

;Set the dragon to loop through the "wave" sequence at .1 speed, with 12 frames of transition
;AnimateMD2 dragon, MD2_MODE_LOOP, .1, seq[0], seq[1], 12


Const SC_FPS = 60 : Local rTime = Floor(1000.0 / SC_FPS)

; Mainloop
While Not KeyHit(1)
	Local cTime = MilliSecs()
	
	TurnEntity yPiv, 0, (KeyDown(205) - KeyDown(203)) * 0.5, 0
	TurnEntity xPiv, (KeyDown(208) - KeyDown(200)) * 0.5, 0, 0
	MoveEntity camera, 0, 0, (KeyDown(44) - KeyDown(30)) * 0.1
	
	UpdateMD2Anims
	
	RenderWorld
	
	; Swap the back buffer with the front buffer
	SwapBuffers(bOGL_hMainDC)
	
	Delay rTime - (MilliSecs() - cTime) - 1
Wend

EndGraphics3D
End


;~IDEal Editor Parameters:
;~C#BlitzPlus
;Include bOGL
Include "bOGL\bOGL.bb"
;Inlcude MD2 addon
Include "bOGL-Addons/MD2.bb"

; Initialize graphics
Graphics3D "bOGL", 1024, 768, 32, 2

; Initialize MD2 library: #### DO NOT SKIP THIS ####
InitMD2Addon

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


; Start by loading up an MD2 mesh: manipulate it like any other entity
Local dragon = LoadMD2Model("Media\dragon.md2")	;Load our dragon model (from B3D)
ScaleEntity dragon, 0.1, 0.1, -0.1		;(Has negative scale)
tex = LoadTexture("Media\dragon.png")	;Texture it normally
EntityTexture dragon, tex
FreeTexture tex
RotateEntity dragon, 90, -90, 0			;(...and is the wrong way up - blame DirectX)
PositionEntity dragon, 0, 2.44, 0


; Build a list of the animation sequences in the model (these are slightly different from then Quake 2 default names)
Dim seq(20, 1) : Local seqName$[20]
seqName[0] = "stand"
seqName[1] = "run"
seqName[2] = "attack"
seqName[3] = "paina"
seqName[4] = "painb"
seqName[5] = "painc"
seqName[6] = "jump"
seqName[7] = "flip"
seqName[8] = "salute"
seqName[9] = "taunt"
seqName[10] = "wave"
seqName[11] = "point"
seqName[12] = "crstand"
seqName[13] = "crwalk"
seqName[14] = "crattack"
seqName[15] = "crpain"
seqName[16] = "crdeath"
seqName[17] = "deatha"
seqName[18] = "deathb"
seqName[19] = "deathc"
seqName[20] = "~~ ALL ~~"		;Using an invalid name returns the whole sequence

Local i, frames[1]
For i = 0 To 20
	GetMD2SeqByName frames, dragon, seqName[i]
	seq(i, 0) = frames[0] : seq(i, 1) = frames[1]	;Put the frame numbers in an array for easy access later
Next

; Start the model off animating in the first "idle" sequence. 8 is a good transition for this speed
Local currentSeq = 0
AnimateMD2 dragon, MD2_MODE_LOOP, 0.15, seq(currentSeq, 0), seq(currentSeq, 1), 8


Include "bogl-Addons\Draw2D.bb"
InitDraw2D : LoadFont2D("Media\Blitz.png")

Const SC_FPS = 60 : Local rTime = Floor(1000.0 / SC_FPS)

; Mainloop
While Not KeyHit(1)
	Local cTime = MilliSecs()
	
	TurnEntity yPiv, 0, (KeyDown(205) - KeyDown(203)) * 0.5, 0
	TurnEntity xPiv, (KeyDown(208) - KeyDown(200)) * 0.5, 0, 0
	MoveEntity camera, 0, 0, (KeyDown(44) - KeyDown(30)) * 0.1
	If EntityXAngle(xPiv) > -5 Then RotateEntity xPiv, -5, 0, 0 : ElseIf EntityXAngle(xPiv) < -89 Then RotateEntity xPiv, -89, 0, 0
	If EntityZ(camera) < 8 Then PositionEntity camera, 0, 0, 8 : ElseIf EntityZ(camera) > 29 Then PositionEntity camera, 0, 0, 29
	
	UpdateMD2Anims	;Necessary to move the MD2 system on a step and update the renderable meshes
	
	RenderWorld
	
	Local change = KeyHit(27) - KeyHit(26)
	If change
		currentSeq = currentSeq + change
		If currentSeq < 0 Then currentSeq = 20 : ElseIf currentSeq > 20 Then currentSeq = 0
		AnimateMD2 dragon, MD2_MODE_LOOP, 0.15, seq(currentSeq, 0), seq(currentSeq, 1), 8
	EndIf
	
	BeginDraw2D
	Text2D 5, 10, "Arrow keys to rotate camera, A and Z to zoom"
	Text2D 5, 30, "Use [ and ] to change animation sequence"
	Text2D 5, 70, "Current animation: " + seqName[currentSeq] + " (#" + currentSeq + ")"
	EndDraw2D
	
	; Swap the back buffer with the front buffer
	SwapBuffers(bOGL_hMainDC)
	
	Delay rTime - (MilliSecs() - cTime) - 1
Wend

EndGraphics3D
End


;~IDEal Editor Parameters:
;~C#BlitzPlus
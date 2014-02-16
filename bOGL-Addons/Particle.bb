
; Particle system addon module for bOGL
;=======================================


; This module provides a very simple particle system

; Particles are produced by "emitters", which are bOGL entities
; All particles belonging to one emitter share a single surface; different
;  emitters use separate surfaces
; Individual particles are lightweight and are not full entities

; Particles can have a randomly variable {direction/speed/colour/size},
;  and may be free-floating, or have their position parented to the emitter
;  that produced them
; Particles may also be textured


Include "bOGL\bOGL.bb"


Type PART_Emitter
	Field mesh.bOGL_Ent
	;These are the starting attributes for emitted particles. Variance is a float multiplier
	Field dirX, dirY, dirZ, dir_var#		;Starting direction vector (0-127 instead of 0.0-1.0)
	Field colour, col_var#		;Colour (ARGB)
	Field speed#, speed_var#	;Starting speed to move at
	Field life, life_var#		;Lifespan in milliseconds
	Field size#, size_var#		;Radius of particle
	
	Field rate, accum#		;Particles per second, accumulated emission debt
	Field parented	;True if particles should move with the emitter (default false)
	Field pData, pCount, pCap, pNext	;Particle data bank, number of active particles, max particles, first freelist slot
End Type

;Type Particle (data layout)
;	(0)  int: remaining lifespan
;	(4)  float[3]: x, y, z position
;	(16) byte[3]: r, g, b colour
;	(20) float: speed
;	(24) byte[3]: x, y, z direction
;	(28) size: float
;End(32)


Const PART_PSTEP = 32, PART_MAX_PARTICLES = 1024, PART_START_CAP = 32, PART_TDIFF_MAX = 500
Global PART_private_UDSlot_ = -1, PART_private_CopyStk_, PART_private_FreeStk_
Global PART_buffer_.PART_Emitter, PART_header_.PART_Emitter, PART_private_LastUpdate_


; Interface
;===========

Function InitParticleAddon()		;Only call this once per program
	PART_private_UDSlot_ = RegisterEntityUserDataSlot()
	PART_private_CopyStk_ = CreateBank(0)
	PART_private_FreeStk_ = CreateBank(0)
	PART_header_ = New PART_Emitter
	PART_buffer_ = New PART_Emitter
End Function

Function UpdateParticles(camera, rate#)
	Local c, doClear = False, tDiff = MilliSecs()
	If tDiff > PART_TDIFF_MAX Then tDiff = 0
	
	;Something has been deleted
	If BankSize(PART_private_FreeStk_) Then ResizeBank PART_private_FreeStk_, 0 : doClear = True
	If BankSize(PART_private_CopyStk_)	;Something has been copied
		For c = 0 To BankSize(PART_private_CopyStk_) - 4 Step 4
			PART_FinishCopy_ PeekInt(PART_private_CopyStk_, c)
		Next
		ResizeBank PART_private_CopyStk_, 0
	EndIf
	
	Insert PART_header_ After Last PART_Emitter
	Local e.PART_Emitter : For e = Each PART_Emitter
		If e = PART_buffer_ Then Exit
		PART_UpdateEmitter_ e, camera, rate, tDiff
	Next
	If doClear Then PART_ClearUnused_
	Insert PART_header_ Before First PART_Emitter
	
	PART_private_LastUpdate_ = tDiff
End Function

Function CreateEmitter(parent = 0, rate = 0, texture = 0)
	Local emit = CreateMesh(parent)
	If texture Then EntityTexture emit, texture
	EntityFX emit, BOGL_FX_FULLBRIGHT
	SetEntityUserData emit, PART_private_UDSlot_, Handle PART_NewEmitter_(rate)
	Return emit
End Function

Function StartEmitter(emit)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	Insert e Before First PART_Emitter
End Function

Function PauseEmitter(emit)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	Insert e After Last PART_Emitter
End Function

Function ClearEmitter(emit)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	Local i : For i = 0 To e\pCap - 1
		VertexCoords emit, i * 4, 0, 0, 0	;Move the vertices for every particle quad to 0,0,0
		VertexCoords emit, i * 4 + 1, 0, 0, 0
		VertexCoords emit, i * 4 + 2, 0, 0, 0
		VertexCoords emit, i * 4 + 3, 0, 0, 0
		PokeInt e\pData, i * PART_PSTEP, (i + 1) + PART_PSTEP	;Stick every slot on the free list
	Next
	PokeInt e\pData, (e\pCap - 1) * PART_PSTEP, -1
	e\pCount = 0 : e\pNext = 0
End Function

Function SetEmitterRate(emit, rate)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	e\rate = rate
End Function

Function SetParticleTexture(emit, tex)
;	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	EntityTexture emit, tex
End Function

Function SetParicleDirection(emit, dx#, dy#, dz#, var# = 0.0)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	Local scl# = Sqr(dx * dx + dy * dy + dz * dz) / 100.0	;sic
	e\dirX = dx * scl : e\dirY = dy * scl : e\dirZ = dz * scl : e\dir_var = var
End Function

Function SetParticleRGB(emit, r, g, b, var# = 0.0)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	e\colour = (r Shl 16) Or (g Shl 8) Or b : e\col_var = var
End Function

Function SetParticleSpeed(emit, spd#, var# = 0.0)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	e\speed = spd : e\speed_var = var
End Function

Function SetParticleLifetime(emit, life, var# = 0.0)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	e\life = life : e\life_var = var
End Function

Function SetParticleSize(emit, size#, var# = 0.0)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	e\size = size : e\size_var = var
End Function

Function SetParticleParentMode(emit, particlesParented)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	e\parented = particlesParented
End Function


; Internal
;==========

Const PART_ALLOC_TICKER = 25
Global PART_private_NewCounter_

Function PART_NewEmitter_.PART_Emitter(rate#)
	If PART_private_NewCounter_ = PART_ALLOC_TICKER Then PART_ClearUnused_
	PART_private_NewCounter_ = PART_private_NewCounter_ + 1
	
	Local e.PART_Emitter = New PART_Emitter
	e\rate = rate : e\accum = 0 : e\parented = 0
	e\pData = CreateBank(PART_START_CAP * PART_PSTEP)
	e\pCount = 0 : e\pCap = PART_START_CAP : e\pNext = 0
	
	e\dirX = 0 : e\dirY = 0 : e\dirZ = 0 : e\dir_var = 25		;Default values (not very meaningful)
	e\colour = $808080 : e\col_var = 0.1
	e\speed = 0.2 : e\speed_var = 0.1
	e\life = 1000 : e\life_var = 0.1
	e\size = 0.05 : e\size_var = 0.1
	
	Insert e After Last PART_Emitter
	Return e
End Function

Function PART_CopyEmitter_.PART_Emitter(o.PART_Emitter)
	Local e.PART_Emitter = PART_NewEmitter_(o\rate)
	
	e\accum = o\accum : e\parented = o\parented
	ResizeBank e\pData, BankSize(o\pData) : CopyBank o\pData, 0, e\pData, 0, BankSize(o\pData)
	e\pCount = o\pCount : e\pCap = o\pCap : e\pNext = o\pNext
	
	e\dirX = o\dirX : e\dirY = o\dirY : e\dirZ = o\dirZ : e\dir_var = o\dir_var
	e\colour = o\colour : e\col_var = o\col_var
	e\speed = o\speed : e\speed_var = o\speed_var
	e\life = o\life : e\life_var = o\life_var
	e\size = o\size : e\size_var = o\size_var
	
	Return e
End Function

Function PART_UpdateEmitter_(e.PART_Emitter, camera, rate#, tDiff)
	Local vp = e\mesh\m\vp, p
	For p = 0 To e\pCount - 1		;Update existing particles
		
	Next
	
	e\accum = e\accum + ((tDiff * rate) / (1000.0 / e\rate))
	While e\accum > 1.0		;Add new particles
		e\accum = e\accum - 1
		;Add a new particle
	Wend
	
	For p = 0 To e\pCount - 1	;Update vertex positions
		Local sz# = PeekFloat(e\pData, p * PART_PSTEP + 28), tf#[2]
		If sz > 0
			
		EndIf
	Next
End Function

Function PART_AddParticle_(e.PART_Emitter)
	If e\pNext = -1	;No slots available
		If e\pCap >= PART_MAX_PARTICLES Then Return		;Nothing to be done!
		ResizeBank e\pData, BankSize e\pData * 2
		Local i : For i = e\pCap To e\pCap * 2 - 1
			PokeInt e\pData, i * PART_PSTEP, (i + 1) * PART_PSTEP
			PokeFloat e\pData, i * PART_PSTEP + 28, 0
			AddVertex e\mesh\handler, 0, 0, 0
			AddVertex e\mesh\handler, 0, 0, 0
			AddVertex e\mesh\handler, 0, 0, 0
			AddVertex e\mesh\handler, 0, 0, 0
		Next
		PokeInt e\pData, e\pCap * 2 - 1, -1
		e\pNext = e\pCap : e\pCap = e\pCap * 2
	EndIf
	
	Local p = e\pNext
	e\pNext = PeekInt(e\pData, p * PART_PSTEP)
	e\pCount = e\pCount + 1
	
;	(0)  int: remaining lifespan
;	(4)  float[3]: x, y, z position
;	(16) byte[3]: r, g, b colour
;	(20) float: speed
;	(24) byte[3]: x, y, z direction
;	(28) size: float
	PokeInt e\pData, p * PART_PSTEP, e\life * (1 + Rnd(-e\life_var, e\life_var))
	PokeFloat e\pData, p * PART_PSTEP + 4, EntityX(e\mesh\handler, True) * (Not e\parented)
	PokeFloat e\pData, p * PART_PSTEP + 8, EntityY(e\mesh\handler, True) * (Not e\parented)
	PokeFloat e\pData, p * PART_PSTEP + 12, EntityZ(e\mesh\handler, True) * (Not e\parented)
	PokeByte e\pData, p * PART_PSTEP + 16, ((e\colour And $FF0000) Shr 16) * (1 + Rnd(-e\dir_var, e\dir_var))
	PokeByte e\pData, p * PART_PSTEP + 17, ((e\colour And $FF00) Shr 8) * (1 + Rnd(-e\dir_var, e\dir_var))
	PokeByte e\pData, p * PART_PSTEP + 18, (e\colour And $FF) * (1 + Rnd(-e\dir_var, e\dir_var))
	PokeFloat e\pData, p * PART_PSTEP + 20, e\speed * (1 + Rnd(-e\speed_var, e\speed_var))
	PokeByte e\pData, p * PART_PSTEP + 24, PART_Clamp_(128 + e\dirX * (1 + Rnd(-e\dir_var, e\dir_var)))
	PokeByte e\pData, p * PART_PSTEP + 25, PART_Clamp_(128 + e\dirY * (1 + Rnd(-e\dir_var, e\dir_var)))
	PokeByte e\pData, p * PART_PSTEP + 26, PART_Clamp_(128 + e\dirZ * (1 + Rnd(-e\dir_var, e\dir_var)))
	PokeFloat e\pData, p * PART_PSTEP + 28, e\size * (1 + Rnd(-e\size_var, e\size_var))
End Function

Function PART_RemoveParticle_(e.PART_Emitter, p)
	PokeInt e\pData, p * PART_PSTEP, e\pNext
	PokeFloat e\pData, p * PART_PSTEP + 28, 0.0
	e\pNext = p
	VertexCoords e\mesh\handler, p, 0, 0, 0
	VertexCoords e\mesh\handler, p + 1, 0, 0, 0
	VertexCoords e\mesh\handler, p + 2, 0, 0, 0
	VertexCoords e\mesh\handler, p + 3, 0, 0, 0
End Function

Function PART_Clamp_(val)
	If val < 0 Then Return 0 : ElseIf val > 255 Then Return 255 : Else Return val
End Function

Function PART_ClearUnused_()
	Local e.PART_Emitter : For e = Each PART_Emitter
		If e\mesh = Null
			If e <> PART_header_ And e <> PART_buffer_
				FreeBank e\pData : Delete e
			EndIf
		EndIf
	Next
	PART_private_NewCounter_ = 0
End Function

Function PART_FinishCopy_(mesh)
	Local ci, e.PART_Emitter = Object.PART_Emitter GetEntityUserData(mesh, PART_private_UDSlot_)
	SetEntityUserData mesh, PART_private_UDSlot_, Handle PART_CopyEmitter_(e)
End Function


;~IDEal Editor Parameters:
;~F#35#3D#55#5D#62#67#74#79#7E#84#89#8E#93#98#A4#B7#103#10D
;~C#BlitzPlus
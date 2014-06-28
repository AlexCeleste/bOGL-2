
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
; Particles may also be textured and FX-ed

; In the absence of vertex alpha this system is kinda ugly, but it does the job


Include "bOGL\bOGL.bb"


Type PART_Emitter
	Field piv.bOGL_Ent, mesh
	;These are the starting attributes for emitted particles. Variance is a float multiplier
	Field dirX#, dirY#, dirZ#, dir_var#		;Starting direction vector, normalized
	Field colour, col_var#		;Colour (ARGB)
	Field speed#, speed_var#	;Starting speed to move at
	Field life, life_var#		;Lifespan in milliseconds
	Field size#, size_var#		;Radius of particle
	
	Field rate, accum#, dur		;Particles per second, accumulated emission debt, time to continue firing for
	Field parented	;True if particles should move with the emitter (default false)
	Field pData, pCount, pCap, pNext	;Particle data bank, number of active particles, max particles, first freelist slot
End Type

;Type Particle (data layout)
;	(0)  int: remaining lifespan
;	(4)  float[3]: x, y, z position
;	(16) byte[4]: a, r, g, b colour
;	(20) float: speed
;	(24) float[3]: x, y, z direction
;	(36) float: size(/live)
;End(40)


Const PART_PSTEP = 40, PART_MAX_PARTICLES = 1024, PART_START_CAP = 32, PART_TDIFF_MAX = 500, PART_MIN_SIZE# = 0.001
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

Function UpdateParticles(camera, rate# = 1.0)
	Local c, cTime = MilliSecs(), tDiff = cTime - PART_private_LastUpdate_
	If tDiff > PART_TDIFF_MAX Then tDiff = 0
	
	;Something has been deleted
	If BankSize(PART_private_FreeStk_) Then ResizeBank PART_private_FreeStk_, 0 : PART_ClearUnused_
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
		If e\dur <= 0 And e\pCount <= 0
			Local n.PART_Emitter = e
			e = Before e : Insert n After PART_buffer_
			If e = Null Then e = PART_header_ : Insert e Before First PART_Emitter
		EndIf
	Next
	Insert PART_header_ Before First PART_Emitter
	
	PART_private_LastUpdate_ = cTime
End Function

Function CreateEmitter(parent = 0, rate = 0, texture = 0)
	Local emit = CreatePivot(parent)
	SetEntityUserData emit, PART_private_UDSlot_, Handle PART_NewEmitter_(emit, rate)
	ClearEmitter emit	;This builds the freelist
	If texture Then SetParticleTexture emit, texture
	Return emit
End Function

Function FireEmitter(emit, duration)	;OK to call this before an emitter stops - it will just reset its lifespan
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	Insert e Before First PART_Emitter
	e\dur = duration
End Function

Function PauseEmitter(emit)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	Insert e After Last PART_Emitter
End Function

Function ResumeEmitter(emit)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	Insert e Before PART_buffer_
End Function

Function ClearEmitter(emit)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	Local i : For i = 0 To e\pCap - 1
		VertexCoords e\mesh, i * 4, 0, 0, 0	;Move the vertices for every particle quad to 0,0,0
		VertexCoords e\mesh, i * 4 + 1, 0, 0, 0
		VertexCoords e\mesh, i * 4 + 2, 0, 0, 0
		VertexCoords e\mesh, i * 4 + 3, 0, 0, 0
		PokeInt e\pData, i * PART_PSTEP, i + 1	;Stick every slot on the free list
	Next
	PokeInt e\pData, (e\pCap - 1) * PART_PSTEP, -1
	e\pCount = 0 : e\pNext = 0
	Insert e After Last PART_Emitter
End Function

Function SetEmitterRate(emit, rate)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	e\rate = rate
End Function

Function SetParticleTexture(emit, tex)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	EntityTexture e\mesh, tex
End Function

Function SetParticleFX(emit, FXflags)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	EntityFX e\mesh, FXflags
End Function

Function SetParticleDirection(emit, dx#, dy#, dz#, var# = 0.0)
	Local e.PART_Emitter = Object.PART_Emitter GetEntityUserData(emit, PART_private_UDSlot_)
	If Not e\parented	;Globalize direction
		Local tf#[2] : TFormPoint dx, dy, dz, emit, 0, tf
		dx = dx - EntityX(emit, 1) : dy = dy - EntityY(emit, 1) : dz = dz - EntityZ(emit, 1)
	EndIf
	Local scl# = Sqr(dx * dx + dy * dy + dz * dz)
	e\dirX = dx / scl : e\dirY = dy / scl : e\dirZ = dz / scl : e\dir_var = var
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
	If size < PART_MIN_SIZE Then size = PART_MIN_SIZE
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

Function PART_NewEmitter_.PART_Emitter(emit, rate#)
	If PART_private_NewCounter_ = PART_ALLOC_TICKER Then PART_ClearUnused_
	PART_private_NewCounter_ = PART_private_NewCounter_ + 1
	
	Local e.PART_Emitter = New PART_Emitter
	e\piv = bOGL_EntList_(emit)
	e\mesh = CreateMesh() : EntityFX e\mesh, BOGL_FX_FULLBRIGHT
	e\rate = rate : e\accum = 0 : e\parented = 0
	e\pData = CreateBank(PART_START_CAP * PART_PSTEP)
	e\pCount = 0 : e\pCap = PART_START_CAP : e\pNext = 0
	
	Local i, v0, v1, v2, v3, msh = e\mesh : For i = 1 To e\pCap
		v0 = AddVertex(msh, 0, 0, 0, 0, 0)
		v1 = AddVertex(msh, 0, 0, 0, 1, 0)
		v2 = AddVertex(msh, 0, 0, 0, 1, 1)
		v3 = AddVertex(msh, 0, 0, 0, 0, 1)
		AddTriangle msh, v0, v1, v2
		AddTriangle msh, v0, v2, v3
	Next
	
	e\dirX = 0 : e\dirY = 0 : e\dirZ = 0 : e\dir_var = 0.1		;Default values (not very meaningful)
	e\colour = $808080 : e\col_var = 0.6
	e\speed = 0.2 : e\speed_var = 0.1
	e\life = 1000 : e\life_var = 0.1
	e\size = 0.05 : e\size_var = 0.1
	
	Insert e After Last PART_Emitter
	Return e
End Function

Function PART_CopyEmitter_.PART_Emitter(o.PART_Emitter, piv)
	Local e.PART_Emitter = PART_NewEmitter_(piv, o\rate)
	
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
	Local p, tf#[2]
	Local life, px#, py#, pz#, col, spd#, dx#, dy#, dz#, sz#
	
	For p = 0 To e\pCap - 1		;Update existing particles (note we iterate over the whole list)
		sz = PeekFloat(e\pData, p * PART_PSTEP + 36)	;(we use the size to skip over the holes in the list)
		If sz > 0
			life = PeekInt(e\pData, p * PART_PSTEP)
			px = PeekFloat(e\pData, p * PART_PSTEP + 4)
			py = PeekFloat(e\pData, p * PART_PSTEP + 8)
			pz = PeekFloat(e\pData, p * PART_PSTEP + 12)
			spd = PeekFloat(e\pData, p * PART_PSTEP + 20)
			dx = PeekFloat(e\pData, p * PART_PSTEP + 24)
			dy = PeekFloat(e\pData, p * PART_PSTEP + 28)
			dz = PeekFloat(e\pData, p * PART_PSTEP + 32)
			
			life = life - tDiff * rate
			If life <= 0
				PART_RemoveParticle_ e, p
			Else
				PokeInt e\pData, p * PART_PSTEP, life
				PokeFloat e\pData, p * PART_PSTEP + 4, px + dx * spd * rate
				PokeFloat e\pData, p * PART_PSTEP + 8, py + dy * spd * rate
				PokeFloat e\pData, p * PART_PSTEP + 12, pz + dz * spd * rate
			EndIf
		EndIf
	Next
	
	If e\dur > 0
		e\accum = e\accum + ((tDiff * rate) / (1000.0 / e\rate))
		While e\accum > 1.0		;Add new particles
			e\accum = e\accum - 1
			PART_AddParticle_ e
		Wend
		e\dur = e\dur - tDiff
	EndIf
	
	SetEntityParent e\mesh, camera	;This may cause confusion later... document well
	For p = 0 To e\pCap - 1	;Update vertex positions
		sz = PeekFloat(e\pData, p * PART_PSTEP + 36)
		If sz > 0
			px = PeekFloat(e\pData, p * PART_PSTEP + 4)
			py = PeekFloat(e\pData, p * PART_PSTEP + 8)
			pz = PeekFloat(e\pData, p * PART_PSTEP + 12)
			TFormPoint px, py, pz, e\piv\handler * e\parented, camera, tf
			VertexCoords e\mesh, p * 4, px - sz, py - sz, pz
			VertexCoords e\mesh, p * 4 + 1, px + sz, py - sz, pz
			VertexCoords e\mesh, p * 4 + 2, px + sz, py + sz, pz
			VertexCoords e\mesh, p * 4 + 3, px - sz, py + sz, pz
			col = PeekInt(e\pData, p * PART_PSTEP + 16)
			Local r = (col And $FF0000) Shr 16, g = (col And $FF00) Shr 8, b = col And $FF
			VertexColor e\mesh, p * 4, r, g, b
			VertexColor e\mesh, p * 4 + 1, r, g, b
			VertexColor e\mesh, p * 4 + 2, r, g, b
			VertexColor e\mesh, p * 4 + 3, r, g, b
		EndIf
	Next
End Function

Function PART_AddParticle_(e.PART_Emitter)
	If e\pNext = -1	;No slots available
		If e\pCap >= PART_MAX_PARTICLES Then Return		;Nothing to be done!
		ResizeBank e\pData, BankSize(e\pData * 2)
		Local i, msh = e\mesh, v0, v1, v2, v3 : For i = e\pCap To e\pCap * 2 - 1
			PokeInt e\pData, i * PART_PSTEP, i + 1
			PokeFloat e\pData, i * PART_PSTEP + 28, 0
			v0 = AddVertex(msh, 0, 0, 0, 0, 0)
			v1 = AddVertex(msh, 0, 0, 0, 1, 0)
			v2 = AddVertex(msh, 0, 0, 0, 1, 1)
			v3 = AddVertex(msh, 0, 0, 0, 0, 1)
			AddTriangle msh, v0, v1, v2
			AddTriangle msh, v0, v2, v3
		Next
		PokeInt e\pData, e\pCap * 2 - 1, -1
		e\pNext = e\pCap : e\pCap = e\pCap * 2
	EndIf
	
	Local p = e\pNext
	e\pNext = PeekInt(e\pData, p * PART_PSTEP)
	e\pCount = e\pCount + 1
	
;	(0)  int: remaining lifespan
;	(4)  float[3]: x, y, z position
;	(16) byte[4]: a, r, g, b colour
;	(20) float: speed
;	(24) float[3]: x, y, z direction
;	(36) float: size(/live)
	PokeInt e\pData, p * PART_PSTEP, e\life * (1 + Rnd(-e\life_var, e\life_var))
	PokeFloat e\pData, p * PART_PSTEP + 4, EntityX(e\piv\handler, True) * (Not e\parented)
	PokeFloat e\pData, p * PART_PSTEP + 8, EntityY(e\piv\handler, True) * (Not e\parented)
	PokeFloat e\pData, p * PART_PSTEP + 12, EntityZ(e\piv\handler, True) * (Not e\parented)
	PokeByte e\pData, p * PART_PSTEP + 16, PART_Clamp_((e\colour And $FF) * (1 + Rnd(-e\col_var, e\col_var)))
	PokeByte e\pData, p * PART_PSTEP + 17, PART_Clamp_(((e\colour And $FF00) Shr 8) * (1 + Rnd(-e\col_var, e\col_var)))
	PokeByte e\pData, p * PART_PSTEP + 18, PART_Clamp_(((e\colour And $FF0000) Shr 16) * (1 + Rnd(-e\col_var, e\col_var)))
	PokeByte e\pData, p * PART_PSTEP + 19, $FF
	PokeFloat e\pData, p * PART_PSTEP + 20, e\speed * (1 + Rnd(-e\speed_var, e\speed_var))
	PokeFloat e\pData, p * PART_PSTEP + 24, e\dirX + Rnd(-e\dir_var, e\dir_var)
	PokeFloat e\pData, p * PART_PSTEP + 28, e\dirY + Rnd(-e\dir_var, e\dir_var)
	PokeFloat e\pData, p * PART_PSTEP + 32, e\dirZ + Rnd(-e\dir_var, e\dir_var)
	PokeFloat e\pData, p * PART_PSTEP + 36, e\size * (1 + Rnd(-e\size_var, e\size_var))
End Function

Function PART_RemoveParticle_(e.PART_Emitter, p)
	PokeInt e\pData, p * PART_PSTEP, e\pNext
	PokeFloat e\pData, p * PART_PSTEP + 36, 0.0
	e\pNext = p : e\pCount = e\pCount - 1
	VertexCoords e\mesh, p * 4, 0, 0, 0
	VertexCoords e\mesh, p * 4 + 1, 0, 0, 0
	VertexCoords e\mesh, p * 4 + 2, 0, 0, 0
	VertexCoords e\mesh, p * 4 + 3, 0, 0, 0
End Function

Function PART_Clamp_(val)
	If val < 0 Then val = 0 : ElseIf val > 255 Then val = 255
	Return val
End Function

Function PART_ClearUnused_()
	Local e.PART_Emitter : For e = Each PART_Emitter
		If e\piv = Null
			If e <> PART_header_ And e <> PART_buffer_
				FreeBank e\pData : FreeEntity e\mesh : Delete e
			EndIf
		EndIf
	Next
	PART_private_NewCounter_ = 0
End Function

Function PART_FinishCopy_(mesh)
	Local ci, e.PART_Emitter = Object.PART_Emitter GetEntityUserData(mesh, PART_private_UDSlot_)
	SetEntityUserData mesh, PART_private_UDSlot_, Handle PART_CopyEmitter_(e, mesh)
End Function


;~IDEal Editor Parameters:
;~F#17#37#3F#5B#63#69#6E#73#81#86#8B#90#9A#9F#A4#A9#AF#BB#D9#E9
;~F#124#14F#159#15E#169
;~C#BlitzPlus

; Collision: very simple collision detection addon for bOGL
;===========================================================


; This module provides highly simplified collision detection in the style of
; the original Blitz3D collision system

; Main difference from B3D is that there are two kinds of object: instead of
; just collision meshes, this has colliders (spherical) and blockers (cuboid).
; Collisions are between one sphere and one cuboid; the sphere is the one that
; gets stopped by the cuboid. A moving cuboid can potentially push a sphere.


Include "bOGL/bOGL.bb"


Type COLL_Collider
	Field e.bOGL_Ent, rad#
End Type

Type COLL_Blocker
	Field e.bOGL_Ent
	Field xs#, ys#, zs#, rad#, resp
End Type


Const COLL_RESPONSE_NONE = 0, COLL_RESPONSE_STOP = 1, COLL_RESPONSE_POST = 2;, COLL_RESPONSE_MOVE = 5
Const COLL_HASH_STARTSZ = 4096, COLL_BUF_STARTSZ = 1024, COLL_HASH_MINSZ = 256;no. cells, not abs size!
Global COLL_private_UDSlot_ = -1, COLL_private_CopyStk_, COLL_private_FreeStk_, COLL_HTSize_, COLL_CBSize_
Dim COLL_HashTbl_(0, 0), COLL_Cons_(0, 0) : Global COLL_ConsFree_
Global COLL_Cbuff_.COLL_Collider, COLL_Bbuff_.COLL_Blocker, COLL_private_CollListener_
Global COLL_private_MaxRadius_#, COLL_MinX_#, COLL_MaxX_#, COLL_MinY_#, COLL_MaxY_#, COLL_MinZ_#, COLL_MaxZ_#


; Interface
;===========

Function InitCollisionAddon()	;Only call this once per program
	COLL_private_UDSlot_ = RegisterEntityUserDataSlot()
	COLL_private_CopyStk_ = CreateBank(0)
	COLL_private_FreeStk_ = CreateBank(0)
	COLL_Cbuff_ = New COLL_Collider : COLL_Bbuff_ = New COLL_Blocker
	COLL_private_MaxRadius_ = 0
	COLL_HTSize_ = COLL_HASH_STARTSZ : COLL_CBSize_ = COLL_BUF_STARTSZ
	Dim COLL_HashTbl_(COLL_HTSize_, 1), COLL_Cons_(COLL_CBSize_, 1)
	SetCollisionSpaceBounds -5000, +5000, -1000, +1000, -5000, +5000
End Function

Function UpdateCollisions()
	If BankSize(COLL_private_FreeStk_)	;bOGL bookkeeping: something has been deleted
		ResizeBank COLL_private_FreeStk_, 0 : COLL_ClearUnused_
	EndIf
	If BankSize(COLL_private_CopyStk_)	;Something has been copied
		Local cp : For cp = 0 To BankSize(COLL_private_CopyStk_) - 4 Step 4
			COLL_FinishCopy_ PeekInt(COLL_private_CopyStk_, cp)
		Next
		ResizeBank COLL_private_CopyStk_, 0
	EndIf
	
	Local i : For i = 0 To COLL_HTSize_ - 1	;Clear the bucket list
		COLL_HashTbl_(i, 0) = -1 : COLL_HashTbl_(i, 1) = -1
	Next
	For i = 0 To COLL_CBSize_ - 1	;Clear the cons freelist
		COLL_Cons_(i, 1) = i + 1
	Next
	COLL_Cons_(COLL_CBSize_ - 1, 1) = -1 : COLL_ConsFree_ = 0
	
	COLL_CellSz_ = COLL_private_MaxRadius_ * 2.5
	COLL_XCells_ = (COLL_MaxX_ - COLL_MinX_) / COLL_CellSz_
	COLL_YCells_ = (COLL_MaxY_ - COLL_MinY_) / COLL_CellSz_
	COLL_ZCells_ = (COLL_MaxZ_ - COLL_MinZ_) / COLL_CellSz_
	Local c.COLL_Collider : For c = Each COLL_Collider	;Place each collider in one or more buckets
		If c = COLL_Cbuff_ Then Exit
		COLL_Bucketize_ c\e\handler, Handle c, c\rad, 0
	Next
	Local b.COLL_Blocker : For b = Each COLL_Blocker	;Place each blocker in one or more buckets
		If b = COLL_Bbuff_ Then Exit
		COLL_Bucketize_ b\e\handler, Handle b, b\rad, 1
	Next
	
	If COLL_private_CollListener_ Then ResizeBank COLL_private_CollListener_, 0
	
	For i = 0 To COLL_HTSize_ - 1	;For each bucket
		If COLL_HashTbl_(i, 0) <> -1 And COLL_HashTbl_(i, 1) <> -1
			COLL_CheckCell_ i	;Test for and act on collisions
		EndIf
	Next
End Function

Function SetCollisionSpaceBounds(minX#, maxX#, minY#, maxY#, minZ#, maxZ#)
	COLL_MinX_ = minX : COLL_MaxX_ = maxX
	COLL_MinY_ = minY : COLL_MaxY_ = maxY
	COLL_MinZ_ = minZ : COLL_MaxZ_ = maxZ
End Function

Function SetCollisionListener(bank)
	COLL_private_CollListener_ = bank
End Function

Function MakeCollider(ent, radius#)
	COLL_AllocTick_
	Local c.COLL_Collider = New COLL_Collider
	c\rad = radius : c\e = bOGL_EntList_(ent)
	If radius > COLL_private_MaxRadius_ Then COLL_private_MaxRadius_ = radius
	SetEntityUserData ent, COLL_private_UDSlot_, Handle c
	Insert c Before COLL_Cbuff_
End Function

Function MakeBlocker(ent, xSize#, ySize#, zSize#, response)
	COLL_AllocTick_
	Local b.COLL_Blocker = New COLL_Blocker
	b\e = bOGL_EntList_(ent)
	b\xs = xSize : b\ys = ySize : b\zs = zSize : b\resp = response
	b\rad = Sqr(xSize * xSize + ySize * ySize + zSize * zSize) / 2
	If b\rad > COLL_private_MaxRadius_ Then COLL_private_MaxRadius_ = b\rad
	SetEntityUserData ent, COLL_private_UDSlot_, Handle b
	Insert b Before COLL_Bbuff_
End Function

Function SetCollisionState(ent, active)
	Local h = GetEntityUserData(ent, COLL_private_UDSlot_)
	Local c.COLL_Collider = Object.COLL_Collider h, b.COLL_Blocker
	If c <> Null
		If active Then Insert c Before COLL_Cbuff_ : Else Insert c After COLL_Cbuff_
	Else
		b = Object.COLL_Blocker h
		If active Then Insert b Before COLL_Bbuff_ : Else Insert b After COLL_Bbuff_
	EndIf
End Function


; Internal
;==========

Const COLL_ALLOC_TICKER = 100
Global COLL_private_NewCounter_

Function COLL_AllocTick_()
	If COLL_private_NewCounter_ = COLL_ALLOC_TICKER Then COLL_ClearUnused_
	COLL_private_NewCounter_ = COLL_private_NewCounter_ + 1
End Function

Function COLL_CopyCollider_(ent, c.COLL_Collider)
	MakeCollider ent, c\rad
End Function

Function COLL_CopyBlocker_(ent, b.COLL_Blocker)
	MakeBlocker ent, b\xs, b\ys, b\zs, b\resp
End Function

Function COLL_CheckCell_(slot)
	Local c = COLL_HashTbl_(slot, 0), tf#[2]
	While c <> -1	;Test each collider against each blocker
		Local b = COLL_HashTbl_(slot, 1), col.COLL_Collider = Object.COLL_Collider COLL_Cons_(c, 0), ce = col\e\handler
		While b <> -1
			Local blk.COLL_Blocker = Object.COLL_Blocker COLL_Cons_(b, 0), be = blk\e\handler
			TFormPoint EntityX(ce, 1), EntityY(ce, 1), EntityZ(ce, 1), 0, be, tf
			If Abs(tf[0]) < blk\xs / 2 + col\rad
				If Abs(tf[1]) < blk\ys / 2 + col\rad
					If Abs(tf[2]) < blk\zs / 2 + col\rad	;Collision detected
						;Act on any detected collisions
						If blk\resp And COLL_RESPONSE_POST		;POST - simply add to an externally-read list
							Local lis = COLL_private_CollListener_
							If lis <> 0
								ResizeBank lis, BankSize(lis) + 8
								PokeInt lis, BankSize(lis) - 8, COLL_Cons_(c, 0)
								PokeInt lis, BankSize(lis) - 4, COLL_Cons_(b, 0)
							EndIf
						EndIf
						If blk\resp And COLL_RESPONSE_STOP		;STOP - move the offending collider out of the block
							
						EndIf
					EndIf
				EndIf
			EndIf
			b = COLL_Cons_(b, 1)
		Wend
		c = COLL_Cons_(c, 1)
	Wend
End Function

Global COLL_CellSz_#, COLL_XCells_#, COLL_YCells_#, COLL_ZCells_#

Function COLL_Bucketize_(ent, h, radius#, ch)
	Local ex# = EntityX(ent, 1) - COLL_MinX_, ey# = EntityY(ent, 1) - COLL_MinY_, ez# = EntityZ(ent, 1) - COLL_MinZ_
	
	Local xc = Floor(ex / COLL_CellSz_), xprop# = ex - (xc * COLL_CellSz_)
	Local yc = Floor(ey / COLL_CellSz_), yprop# = ey - (yc * COLL_CellSz_)
	Local zc = Floor(ez / COLL_CellSz_), zprop# = ez - (zc * COLL_CellSz_)
	
	Local xm = 0, ym = 0, zm = 0
	If xprop < radius Then xm = -1 ElseIf xprop > COLL_CellSz_ - radius Then xm = +1
	If yprop < radius Then ym = -1 ElseIf yprop > COLL_CellSz_ - radius Then ym = +1
	If zprop < radius Then zm = -1 ElseIf zprop > COLL_CellSz_ - radius Then zm = +1
	
	:                        COLL_AddToBucket_ h, COLL_SpatialHash_(     xc,      yc,      zc), ch
	:             If zm Then COLL_AddToBucket_ h, COLL_SpatialHash_(     xc,      yc, zm + zc), ch
	:             If ym Then COLL_AddToBucket_ h, COLL_SpatialHash_(     xc, ym + yc,      zc), ch
	:      If ym And zm Then COLL_AddToBucket_ h, COLL_SpatialHash_(     xc, ym + yc, zm + zc), ch
	:             If xm Then COLL_AddToBucket_ h, COLL_SpatialHash_(xm + xc,      yc,      zc), ch
	:      If xm And zm Then COLL_AddToBucket_ h, COLL_SpatialHash_(xm + xc,      yc, zm + zc), ch
	:      If xm And ym Then COLL_AddToBucket_ h, COLL_SpatialHash_(xm + xc, ym + yc,      zc), ch
	If xm And ym And zm Then COLL_AddToBucket_ h, COLL_SpatialHash_(xm + xc, ym + yc, zm + zc), ch
End Function

Function COLL_AddToBucket_(h, slot, chan)
	Local c = COLL_ConsFree_ : If c = -1 Then c = COLL_ExtendConsList_()
	COLL_ConsFree_ = COLL_Cons_(c, 1)
	COLL_Cons_(c, 0) = h
	COLL_Cons_(c, 1) = COLL_HashTbl_(slot, chan)
	COLL_HashTbl_(slot, chan) = c
End Function

;! hash(x,y,z) = (x p1 xor y p2 xor z p3) mod n
;!   where
;!     p1 = 73856093, p2 = 19349663, p3 = 83492791, n = tablesize
Function COLL_SpatialHash_(xc, yc, zc)
	Return ((xc * 73856093) Xor (yc * 19349663) Xor (zc * 83492791)) Mod COLL_HTSize_
End Function

Function COLL_ExtendConsList_()
	Local tmp = CreateBank(COLL_CBSize_ * 8), i
	For i = 0 To COLL_CBSize_ - 1
		PokeInt tmp, i * 8, COLL_Cons_(i, 0)
		PokeInt tmp, i * 8 + 4, COLL_Cons_(i, 1)
	Next
	Dim COLL_Cons_(COLL_CBSize_ * 2, 1)
	For i = 0 To COLL_CBSize_ - 1
		COLL_Cons_(i, 0) = PeekInt(tmp, i * 8)
		COLL_Cons_(i, 1) = PeekInt(tmp, i * 8 + 4)
	Next
	FreeBank tmp
	For i = COLL_CBSize_ To COLL_CBSize_ * 2 - 1
		COLL_Cons_(i, 1) = i + 1
	Next
	COLL_ConsFree_ = COLL_CBSize_
	COLL_CBSize_ = COLL_CBSize_ * 2
	COLL_Cons_(COLL_CBSize_ - 1, 1) = -1
	Return COLL_ConsFree_
End Function

Function COLL_ClearUnused_()
	Local c.COLL_Collider : For c = Each COLL_Collider
		If c\e = Null
			If c <> COLL_Cbuff_ Then Delete c
		EndIf
	Next
	Local b.COLL_Blocker : For b = Each COLL_Blocker
		If b\e = Null
			If b <> COLL_Bbuff_ Then Delete b
		EndIf
	Next
	COLL_private_NewCounter_ = 0
End Function

Function COLL_FinishCopy_(ent)
	Local h = GetEntityUserData(ent, COLL_private_UDSlot_)
	Local c.COLL_Collider = Object.COLL_Collider h
	If c <> Null Then COLL_CopyCollider_ ent, c : Else COLL_CopyBlocker_ ent, Object.COLL_Blocker h
End Function


;~IDEal Editor Parameters:
;~C#BlitzPlus
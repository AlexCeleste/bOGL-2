
; Draw2D: 2D-in-3D graphics addon for bOGL
;==========================================


; This provides 2D-in-3D drawing primitives, based on Draw3D 3.2 for Blitz3D

; Features:
; - Drawing primitives (points, lines, tris, quads)
; - Images
; - Text
; - Batched operations
; - Realtime rotation, scale, colour, alpha and blending effects
; - Scaled virtual resolution

; Commands:
; InitDraw2D()
; SetViewport2D(x, y, w, h), SetVirtualResolution2D(x, y)
; BeginDraw2D(), EndDraw2D()
; SetColor2D(r, g, b[, a#]), SetBlend2D(mode)
; SetClsColor2D(r, g, b), Cls2D()
; LoadFont2D(fontname$), FreeFont2D(font)
; SetFont2D(font[, size, height#, spacing#, italic#])
; Text2D(x#, y#, s$[, align])
; SetMaterial2D(tex)
; Plot2D(x#, y#[, radius#])
; Line2D(x1#, y1#, x2#, y2#[, width#])
; Rect2D(x#, y#, w#, h#[, fill, border#])
; Oval2D(xc#, yc#, xr#, yr#[, fill, border#])
; Poly2D(x1#, y1#, x2#, y2#, x3#, y3#)
; SetScale2D(xscale#, yscale#), SetRotation2D(angle#)
; DrawImage2D(img(img, x, y, width, height), x, y)
; GrabImage2D(img, x, y, width, height)
; DrawSubRect2D(img, x#, y#, fromx, fromy, width, height)
; DrawImageLine2D(img, x1#, y1#, x2#, y2#, width#, stretch = False)
; DrawImageQuad2D(img, x1#, y1#, x2#, y2#, x3#, y3#, x4#, y4#)
; ApplyAlphaMap(img, amap), ApplyMaskColor(img, mask)
; GetBuffer2D(tex[, asBRGA]), CommitBuffer2D(buf)
; GetPixel2D(buf, x, y), SetPixel2D(buf, x, y, pixel)
; StringWidth2D(s$)


; Images and materials are loaded with LoadTexture from the main bOGL library


Include "bOGL\bOGL.bb"


Type B2D_Font
	Field tex.bOGL_Tex
	Field width#[255], lo#[255], ro#[255]
End Type

Const B2D_BLEND_ADD = 1, B2D_BLEND_MUL = 2, B2D_BLEND_ALPHA = 4, B2D_BLEND_NONE = $FF
Const B2D_BUF_HEADER = 16, B2D_ROT_EPSILON# = 0.01, B2D_FONT_MASK = $000000
Const B2D_TEXT_CENTX = 1, B2D_TEXT_CENTY = 2

Global B2D_ViewportX_, B2D_ViewportY_, B2D_ViewportW_, B2D_ViewportH_, B2D_VResX_, B2D_VResY_
Global B2D_ColR_#, B2D_ColG_#, B2D_ColB_#, B2D_Alpha_#, B2D_CTex_, B2D_BlendMode_
Global B2D_ClsColR_#, B2D_ClsColG_#, B2D_ClsColB_#
Global B2D_XScale_#, B2D_YScale_#, B2D_Rotation_#
Global B2D_CFont_.B2D_Font, B2D_FSize_#, B2D_FHeight_#, B2D_FSpace_#, B2D_FItalic_#, B2D_TBank_


Function InitDraw2D()
	SetColor2D 255, 255, 255 : SetBlend2D B2D_BLEND_ALPHA
	SetViewport2D 0, 0, bOGL_bbHwndW, bOGL_bbHwndH
	B2D_XScale_ = 1.0 : B2D_YScale_ = 1.0 : B2D_Rotation_ = 0.0
	B2D_ColR_# = 255 : B2D_ColG_# = 255 : B2D_ColB_# = 255 : B2D_Alpha_# = 1.0
	B2D_CTex_ = 0 : B2D_BlendMode_ = B2D_BLEND_ALPHA
	SetClsColor2D 255, 255, 255
End Function

Function SetViewport2D(x, y, w, h)
	B2D_ViewportX_ = x : B2D_ViewportY_ = y : B2D_ViewportW_ = w : B2D_ViewportH_ = h
	SetVirtualResolution2D w, h
End Function

Function SetVirtualResolution2D(x, y)
	B2D_VResX_ = x : B2D_VResY_ = y
End Function

Function BeginDraw2D()
	glDisable GL_LIGHTING : glDisable GL_FOG
	glDisable GL_DEPTH_TEST : glDisable GL_STENCIL_TEST
	glViewport B2D_ViewportX_, B2D_ViewportY_, B2D_ViewportW_, B2D_ViewportH_
	glScissor B2D_ViewportX_, B2D_ViewportY_, B2D_ViewportW_, B2D_ViewportH_
	
	glMatrixMode GL_PROJECTION : glLoadIdentity()
	gluOrtho2D 0, B2D_VResX_, 0, B2D_VResY_
	glMatrixMode GL_MODELVIEW : glLoadIdentity
	
	glColor4f B2D_ColR_, B2D_ColG_, B2D_ColB_, B2D_Alpha_
	SetBlend2D B2D_BlendMode_
	B2D_BindMat_ B2D_CTex_
	glClearColor B2D_ClsColR_, B2D_ClsColG_, B2D_ClsColB_, 1.0
End Function

Function EndDraw2D()
	glEnable GL_LIGHTING : glEnable GL_FOG
	glEnable GL_DEPTH_TEST : glEnable GL_STENCIL_TEST
	glViewport 0, 0, bOGL_bbHwndW, bOGL_bbHwndH
	glScissor 0, 0, bOGL_bbHwndW, bOGL_bbHwndH
End Function

Function SetColor2D(r, g, b, a# = 1.0)
	B2D_ColR_ = r / 255.0 : B2D_ColG_ = g / 255.0 : B2D_ColB_ = b / 255.0 : B2D_Alpha_ = a
	glColor4f B2D_ColR_, B2D_ColG_, B2D_ColB_, B2D_Alpha_
End Function

Function SetBlend2D(mode)
	Select mode
		Case B2D_BLEND_ADD
			glBlendFunc GL_ONE, GL_ONE : glEnable GL_BLEND
		Case B2D_BLEND_MUL
			glBlendFunc GL_DST_COLOR, GL_ZERO : glEnable GL_BLEND
		Case B2D_BLEND_ALPHA
			glEnable GL_BLEND : glBlendFunc GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA
		Default : glDisable GL_BLEND
	End Select
	B2D_BlendMode_ = mode
End Function

Function SetClsColor2D(r, g, b)
	B2D_ClsColR_ = r / 255.0 : B2D_ClsColG_ = g / 255.0 : B2D_ClsColB_ = b / 255.0
	glClearColor B2D_ClsColR_, B2D_ClsColG_, B2D_ClsColB_, 1.0
End Function

Function Cls2D()
	glClear GL_COLOR_BUFFER_BIT
End Function

Function LoadFont2D(font$)
	Local tex = LoadTexture(font) : If Not tex Then Return 0
	ApplyMaskColor tex, B2D_FONT_MASK
	Local f.B2D_Font = New B2D_Font
	f\tex = Object.bOGL_Tex tex
	Local cw = f\tex\width / 16, ch = f\tex\height / 16
	Local pix = GetTextureData(tex, False), cx, cy
	For cy = 0 To 15
		For cx = 0 To 15
			B2D_SetCharWidth_ f, pix, f\tex\width, f\tex\height, cx, cy
		Next
	Next
	FreeBank pix
	Local c : For c = 0 To 255
		f\lo[c] = f\lo[c] * (1.0 / Float f\tex\width)
		f\ro[c] = f\ro[c] * (1.0 / Float f\tex\width)
	Next
	If B2D_CFont_ = Null Then SetFont2D Handle f
	Return Handle f
End Function

Function SetFont2D(font, size = 1.0, height# = 1.0, spacing# = 2.0, italic# = 0.0)
	B2D_CFont_ = Object.B2D_Font font
	B2D_FSize_ = size
	B2D_FHeight_ = height
	B2D_FSpace_ = spacing
	B2D_FItalic_ = italic
End Function

Function FreeFont2D(font)
	Local f.B2D_Font = Object.B2D_Font font
	FreeTexture Handle f\tex
	Delete f
End Function

Function Text2D(x#, y#, s$, align = 0)
	Local slen = Len(s), i
	y = B2D_VResY_ - y
	glBindTexture GL_TEXTURE_2D, B2D_CFont_\tex\glName : glEnable GL_TEXTURE_2D
	glPushMatrix
	glTranslatef x, y, 0
	glRotatef -B2D_Rotation_, 0, 0, 1
	
	Local ch# = B2D_CFont_\tex\height / 16.0 * B2D_FHeight_ * B2D_FSize_
	Local cy0# = (ch / 2.0) * ((align And B2D_TEXT_CENTY) <> 0), cy1# = -ch + cy0
	Local w# = 0.0, xoff# = 0.0 : If (align And B2D_TEXT_CENTX) <> 0 Then xoff = (StringWidth2D(s) / 2.0) * B2D_FSize_
	
	glBegin GL_QUADS
	For i = 0 To slen - 1
		Local c = Asc(Mid(s, i + 1))
		
		Local cu0# = (1.0 / 16.0) * (c Mod 16) + B2D_CFont_\lo[c]
		Local cu1# = (1.0 / 16.0) * (c Mod 16 + 1) - B2D_CFont_\ro[c]
		Local cv0# = (1.0 / 16.0) * (c / 16), cv1# = cv0 + (1.0 / 16.0)
		
		glTexCoord2f cu0, cv0 : glVertex2f w + B2D_FItalic_ - xoff, cy0
		glTexCoord2f cu0, cv1 : glVertex2f w - xoff, cy1
		w = w + (B2D_CFont_\width[c] * B2D_FSize_)
		glTexCoord2f cu1, cv1 : glVertex2f w - xoff, cy1
		glTexCoord2f cu1, cv0 : glVertex2f w + B2D_FItalic_ - xoff, cy0
		w = w + (B2D_FSpace_ * B2D_FSize_)
	Next
	glEnd
	glPopMatrix
	B2D_BindMat_ B2D_CTex_
End Function

Function SetMaterial2D(mat)
	If mat
		Local tex.bOGL_Tex = Object.bOGL_Tex mat
		B2D_CTex_ = tex\glName
	Else
		B2D_CTex_ = 0
	EndIf
	B2D_BindMat_ B2D_CTex_
End Function

Function Plot2D(x#, y#, radius# = 1.0)
	y = B2D_VResY_ - y : B2D_BindMat_ B2D_CTex_
	glBegin GL_QUADS
	glTexCoord2f 0.0, 0.0 : glVertex2f x - radius, y + radius
	glTexCoord2f 0.0, 1.0 : glVertex2f x - radius, y - radius
	glTexCoord2f 1.0, 1.0 : glVertex2f x + radius, y - radius
	glTexCoord2f 1.0, 0.0 : glVertex2f x + radius, y + radius
	glEnd
End Function

Function Line2D(x1#, y1#, x2#, y2, width# = 2.0)
	y1 = B2D_VResY_ - y1 : y2 = B2D_VResY_ - y2
	B2D_BindMat_ B2D_CTex_
	Local ang# = ATan2(y2 - y1, x2 - x1), rad# = width / 2.0
	Local rs# = rad * Sin(ang), rc# = rad * Cos(ang)
	glBegin GL_QUADS
	glTexCoord2f 0.5, 0.0 : glVertex2f x1 - rs, y1 + rc
	glTexCoord2f 0.5, 1.0 : glVertex2f x1 + rs, y1 - rc
	glTexCoord2f 0.5, 1.0 : glVertex2f x2 + rs, y2 - rc
	glTexCoord2f 0.5, 0.0 : glVertex2f x2 - rs, y2 + rc
	glEnd
End Function

Function Rect2D(x#, y#, w#, h#, fill = True, border# = 0.0)
	y = B2D_VResY_ - y : B2D_BindMat_ B2D_CTex_
	glBegin GL_QUADS
	If fill
		glTexCoord2f 0.5, 0.5	;Inner block
		glVertex2f x, y
		glVertex2f x, y - h
		glVertex2f x + w, y - h
		glVertex2f x + w, y
	Else
		If border > 0.0	;Inner border
			glTexCoord2f 0.5, 0.5 : glVertex2f x, y							;Top
			glTexCoord2f 0.5, 1.0 : glVertex2f x + border, y - border
			glTexCoord2f 0.5, 1.0 : glVertex2f x + w - border, y - border
			glTexCoord2f 0.5, 0.5 : glVertex2f x + w, y
			glTexCoord2f 0.0, 0.5 : glVertex2f x + w - border, y - border	;Right
			glTexCoord2f 0.0, 0.5 : glVertex2f x + w - border, y - h + border
			glTexCoord2f 0.5, 0.5 : glVertex2f x + w, y - h
			glTexCoord2f 0.5, 0.5 : glVertex2f x + w, y
			glTexCoord2f 0.5, 0.0 : glVertex2f x + border, y - h + border	;Bottom
			glTexCoord2f 0.5, 0.5 : glVertex2f x, y - h
			glTexCoord2f 0.5, 0.5 : glVertex2f x + w, y - h
			glTexCoord2f 0.5, 0.0 : glVertex2f x + w - border, y - h + border
			glTexCoord2f 0.5, 0.5 : glVertex2f x, y							;Left
			glTexCoord2f 0.5, 0.5 : glVertex2f x, y - h
			glTexCoord2f 1.0, 0.5 : glVertex2f x + border, y - h + border
			glTexCoord2f 1.0, 0.5 : glVertex2f x + border, y - border
		EndIf
	EndIf
	If border > 0.0	;Outer border
		glTexCoord2f 0.5, 0.0 : glVertex2f x - border, y + border		;Top
		glTexCoord2f 0.5, 0.5 : glVertex2f x, y
		glTexCoord2f 0.5, 0.5 : glVertex2f x + w, y
		glTexCoord2f 0.5, 0.0 : glVertex2f x + w + border, y + border
		glTexCoord2f 0.5, 0.5 : glVertex2f x + w, y						;Right
		glTexCoord2f 0.5, 0.5 : glVertex2f x + w, y - h
		glTexCoord2f 1.0, 0.5 : glVertex2f x + w + border, y - h - border
		glTexCoord2f 1.0, 0.5 : glVertex2f x + w + border, y + border
		glTexCoord2f 0.5, 0.5 : glVertex2f x, y - h						;Bottom
		glTexCoord2f 0.5, 1.0 : glVertex2f x - border, y - h - border
		glTexCoord2f 0.5, 1.0 : glVertex2f x + w + border, y - h - border
		glTexCoord2f 0.5, 0.5 : glVertex2f x + w, y - h
		glTexCoord2f 0.0, 0.5 : glVertex2f x - border, y + border		;Left
		glTexCoord2f 0.0, 0.5 : glVertex2f x - border, y - h - border
		glTexCoord2f 0.5, 0.5 : glVertex2f x, y - h
		glTexCoord2f 0.5, 0.5 : glVertex2f x, y
	End If
	glEnd
End Function

Function Oval2D(xc#, yc#, xr#, yr#, fill = True, border# = 0.0)
	yc = B2D_VResY_ - yc : B2D_BindMat_ B2D_CTex_
	Local segs = 8 + Int((xr + yr) / 5), ang# = 360.0 / Float segs, s
	glPushMatrix
	glTranslatef xc, yc, 0
	glRotatef -B2D_Rotation_, 0, 0, 1
	If fill
		glBegin GL_TRIANGLE_FAN
		glTexCoord2f 0.5, 0.5
		glVertex2f 0, 0
		For s = 0 To segs
			glVertex2f -Sin(s * ang) * xr, Cos(s * ang) * yr
		Next
		glEnd
	ElseIf border > 0.0		;Inner border
		glBegin GL_QUAD_STRIP
		Local xri# = xr - border, yri# = yr - border
		For s = 0 To segs
			glTexCoord2f 0.5, 0.5
			glVertex2f Sin(s * ang) * xr, Cos(s * ang) * yr
			glTexCoord2f 0.5, 1.0
			glVertex2f Sin(s * ang) * xri, Cos(s * ang) * yri
		Next
		glEnd
	EndIf
	If border > 0.0		;Outer border
		glBegin GL_QUAD_STRIP
		Local xro# = xr + border, yro# = yr + border
		For s = 0 To segs
			glTexCoord2f 0.5, 0.0
			glVertex2f Sin(s * ang) * xro, Cos(s * ang) * yro
			glTexCoord2f 0.5, 0.5
			glVertex2f Sin(s * ang) * xr, Cos(s * ang) * yr
		Next
		glEnd
	EndIf
	glPopMatrix
End Function

Function Poly2D(x1#, y1#, x2#, y2#, x3#, y3#)
	glDisable GL_TEXTURE_2D
	glBegin GL_TRIANGLES
	glTexCoord2f 0.5, 0.5
	glVertex2f x1, B2D_VResY_ - y1
	glVertex2f x1, B2D_VResY_ - y2
	glVertex2f x1, B2D_VResY_ - y3
	glEnd
End Function

Function SetScale2D(xscale#, yscale#)
	B2D_XScale_ = xscale : B2D_YScale_ = yscale
End Function

Function SetRotation2D(angle#)
	B2D_Rotation_ = angle
End Function

Function DrawImage2D(img, x#, y#)
	Local tex.bOGL_Tex = Object.bOGL_Tex img
	B2D_DrawImageRect_ tex\glName, x, y, tex\width / 2.0, tex\height / 2.0, 0.0, 0.0, 1.0, 1.0
End Function

Function GrabImage2D(img, x, y, width, height)
	Local out = CreateTexture(width, height)
	Local srcData = GetTextureData(img, False), src.bOGL_Tex = Object.bOGL_Tex img
	Local i, ptr = (y * src\width + x) * 4 : For i = 0 To height - 1
		CopyBank srcData, ptr, srcData, i * width, width
		ptr = ptr + src\width * 4
	Next
	UpdateTexture out, 0, 0, width, height, srcData, False
	FreeBank srcData
	Return out
End Function

Function DrawSubRect2D(img, x#, y#, fromx, fromy, width, height)
	Local tex.bOGL_Tex = Object.bOGL_Tex img
	Local u0# = Float fromx / tex\width, v0# = Float fromy / tex\height
	Local u1# = Float (fromx + width) / tex\width, v1# = Float (fromy + height) / tex\height
	B2D_DrawImageRect_ tex\glName, x, y, width / 2.0, height / 2.0, u0, v0, u1, v1
End Function

Function DrawImageLine2D(img, x1#, y1#, x2#, y2#, width#, stretch = False)
	y1 = B2D_VResY_ - y1 : y2 = B2D_VResY_ - y2
	Local ang# = ATan2(y2 - y1, x2 - x1), rad# = width / 2.0
	Local rs# = rad * Sin(ang), rc# = rad * Cos(ang)
	Local tex.bOGL_Tex = Object.bOGL_Tex img
	glBindTexture GL_TEXTURE_2D, tex\glName : glEnable GL_TEXTURE_2D
	glBegin GL_QUADS
	glTexCoord2f 0.0, 0.0 : glVertex2f x1 - rs, y1 + rc
	glTexCoord2f 0.0, 1.0 : glVertex2f x1 + rs, y1 - rc
	Local u2# : If stretch
		u2 = 1.0
	Else
		u2 = Sqr((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) / tex\width
	EndIf
	glTexCoord2f u2, 1.0 : glVertex2f x2 + rs, y2 - rc
	glTexCoord2f u2, 0.0 : glVertex2f x2 - rs, y2 + rc
	glEnd
	B2D_BindMat_ B2D_CTex_
End Function

Function DrawImageQuad2D(img, x1#, y1#, x2#, y2#, x3#, y3#, x4#, y4#)
	y1 = B2D_VResY_ - y1 : y2 = B2D_VResY_ - y2 : y3 = B2D_VResY_ - y3 : y4 = B2D_VResY_ - y4
	Local tex.bOGL_Tex = Object.bOGL_Tex img
	glBindTexture GL_TEXTURE_2D, tex\glName : glEnable GL_TEXTURE_2D
	glBegin GL_QUADS
	glTexCoord2f 0.0, 0.0 : glVertex2f x1, y1
	glTexCoord2f 0.0, 1.0 : glVertex2f x2, y2
	glTexCoord2f 1.0, 1.0 : glVertex2f x3, y3
	glTexCoord2f 1.0, 0.0 : glVertex2f x4, y4
	glEnd
	B2D_BindMat_ B2D_CTex_
End Function

Function ApplyAlphaMap(img, amap)
	Local pix = GetTextureData(img, False), p, sz = BankSize(pix)
	For p = 0 To sz - 4 Step 4
		Local val = PeekInt(pix, p) And $00FFFFFF, alpha = PeekByte(amap, p / 4)
		PokeInt pix, p, val Or (alpha Shl 24)
	Next
	UpdateTexture img, 0, 0, TextureWidth(img), TextureHeight(img), pix, False
	FreeBank pix
End Function

Function ApplyMaskColor(img, mask)
	Local pix = GetTextureData(img, False), p, sz = BankSize(pix)
	mask = mask And $00FFFFFF
	For p = 0 To sz - 4 Step 4
		Local val = PeekInt(pix, p) And $00FFFFFF
		If val = mask Then PokeInt pix, p, val : Else PokeInt pix, p, val Or $FF000000
	Next
	UpdateTexture img, 0, 0, TextureWidth(img), TextureHeight(img), pix, False
	FreeBank pix
End Function

Function GetBuffer2D(tex, asRGBA = True)
	Local buffer = GetTextureData(tex, asRGBA), this.bOGL_Tex = Object.bOGL_Tex tex
	ResizeBank buffer, BankSize(buffer) + B2D_BUF_HEADER
	CopyBank buffer, 0, buffer, B2D_BUF_HEADER, BankSize(buffer) - B2D_BUF_HEADER
	PokeInt buffer, 0, tex : PokeInt buffer, 4, TextureWidth(tex)
	PokeInt buffer, 8, TextureHeight(tex) : PokeInt buffer, 12, asRGBA
	Return buffer
End Function

Function CommitBuffer2D(buffer)
	Local tex = PeekInt(buffer, 0), w = PeekInt(buffer, 4), h = PeekInt(buffer, 8), asRGBA = PeekInt(buffer, 12)
	CopyBank buffer, B2D_BUF_HEADER, buffer, 0, BankSize(buffer) - B2D_BUF_HEADER
	If asRGBA
		ResizeBank buffer, BankSize(buffer) - B2D_BUF_HEADER
		UpdateTexture tex, 0, 0, w, h, buffer, True
	Else
		UpdateTexture tex, 0, 0, w, h, buffer, False
	EndIf
	FreeBank buffer
End Function

Function GetPixel2D(pix, x, y)
	Return PeekInt(pix, (y - 1) * PeekInt(pix, 4) + x + B2D_BUF_HEADER)
End Function

Function SetPixel2D(pix, x, y, pixel)
	PokeInt pix, (y - 1) * PeekInt(pix, 4) + x + B2D_BUF_HEADER, pixel
End Function

Function StringWidth2D(s$)
	Local c, w, slen = Len(s)
	For c = 1 To slen
		w = w + B2D_CFont_\width[Asc(Mid(s, c))]
	Next
	Return w + B2D_FSpace_ * slen
End Function

; Internal use only
Function B2D_BindMat_(mat)
	If mat
		glBindTexture GL_TEXTURE_2D, mat : glEnable GL_TEXTURE_2D
	Else
		glDisable GL_TEXTURE_2D
	EndIf
End Function

Function B2D_DrawImageRect_(glTex, x#, y#, x2#, y2#, us#, vs#, ut#, vt#)
	y = (B2D_VResY_ - y)
	x2 = x2 * B2D_XScale_ : y2 = y2 * B2D_YScale_
	glEnable GL_TEXTURE_2D
	glBindTexture GL_TEXTURE_2D, glTex
	
	glBegin GL_QUADS
	If Abs(B2D_Rotation_) < B2D_ROT_EPSILON
		glTexCoord2f us, vs : glVertex2f x - x2, y + y2
		glTexCoord2f us, vt : glVertex2f x - x2, y - y2
		glTexCoord2f ut, vt : glVertex2f x + x2, y - y2
		glTexCoord2f ut, vs : glVertex2f x + x2, y + y2
	Else
		Local ang# = ATan2(y2, x2)
		Local rad# = Sqr(x2 * x2 + y2 * y2)
		Local xr1# = Cos(ang + B2D_Rotation_) * rad, yr1# = Sin(ang + B2D_Rotation_) * rad
		Local xr2# = Cos(ang - B2D_Rotation_) * rad, yr2# = Sin(ang - B2D_Rotation_) * rad
		glTexCoord2f us, vs : glVertex2f x - xr1, y + yr1
		glTexCoord2f us, vt : glVertex2f x - xr2, y - yr2
		glTexCoord2f ut, vt : glVertex2f x + xr1, y - yr1
		glTexCoord2f ut, vs : glVertex2f x + xr2, y + yr2
	EndIf
	glEnd
	
	B2D_BindMat_ B2D_CTex_
End Function

Function B2D_SetCharWidth_(f.B2D_Font, pix, w, h, cx, cy)
	Local x, y, cw = w / 16, ch = h / 16, ptr = (cy * 16 * w + cx * cw) * 4
	For x = 0 To cw - 1
		For y = 0 To ch - 1
			Local p = ptr + (w * y + x) * 4, bk = False
			If (PeekInt(pix, p) And $00FFFFFF) <> B2D_FONT_MASK Then f\lo[cx + cy * 16] = x : bk = True : Exit
		Next
		If bk Then Exit
	Next
	If Not bk Then Return 0
	For x = cw - 1 To 0 Step -1
		For y = 0 To ch - 1
			p = ptr + (w * y + x) * 4
			If (PeekInt(pix, p) And $00FFFFFF) <> B2D_FONT_MASK
				f\ro[cx + cy * 16] = (cw - 1) - x
				f\width[cx + cy * 16] = (x + 1) - f\lo[cx + cy * 16]
				Return
			EndIf
		Next
	Next
End Function


;~IDEal Editor Parameters:
;~F#30#40#49#4E#52#62#69#6E#7B#80#84#99#A1#A7#C7#D1#DB#E8#11A#141
;~F#14B#14F#153#158#164#16B#17F#18C#196#1A1#1AA#1B6#1BA#1BE#1C7#1CF#1EA
;~C#BlitzPlus
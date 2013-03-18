;----------------------------------------------------
; Filename blitz_gl.bb
; Rev 0.3 2012.09.06
;
;----------------------------------------------------
;
; OpenGL Direct for Blitz
; Using OpenGl from Blitz without a wrapper DLL
;
; by Peter Scheutz, Alex G, ... (your name here) 
;
; Based on Sublevel6's PowerBasic headers
; Link: root.tty0.org/sublevel6/?page=files 
;
;----------------------------------------------------
;
; This file contains OpenGl and Glu functions, that Blitz can not call through
; their standard declares. By delaring the OpenGl function in another way
; in the OpenGl.decls and Glu32.decls file, and making a wrapper function 
; the unkonventional calling can be hidden.
; This makes it easy to port existing code, and prevents the need for new
; calling conventions for the user.

; BlitzGL_Init must be called before any of the other functions.

; Example gluPerspective: 
; 
; should be somthing like:
; gluPerspective(fovy as Double,aspect as Double,zNear as Double,zFar as Double)  
;
; Blitz does not have an 8 byte data type that you can pass by value, so this:
;
; blitz_gluPerspective(fovy_hi%,fovy_low%,aspect_hi%,aspect_low%,zNear_hi%,zNear_low%,zFar_hi%,zFar_low%):"gluPerspective"
;
; is used instead. Then a blitz wrapper function is made:
;
; Function gluPerspective(fovy#,aspect#,zNear#,zFar#)
;
; and the only difference is that you call it with Blitz floats instead of Doubles.
;
;
;----------------------------------------------------


Global BlitzGL_DblBank


Function BlitzGL_Init()
	If Not BlitzGL_DblBank Then BlitzGL_DblBank = CreateBank(8)
End Function


Function glDepthRange(zNear#, zFar#)
	SngToDbl zNear,BlitzGL_DblBank
	SngToDbl zFar,BlitzGL_DblBank
	blitz_glDepthRange PeekInt(BlitzGL_DblBank, 0), PeekInt(BlitzGL_DblBank, 4), PeekInt(BlitzGL_DblBank, 0), PeekInt(BlitzGL_DblBank, 4)
End Function


Function gluPerspective(fovy#, aspect#, zNear#, zFar#)
	SngToDbl fovy,BlitzGL_DblBank
	Local fovy_l = PeekInt(BlitzGL_DblBank, 0), fovy_r = PeekInt(BlitzGL_DblBank, 4)
	
	SngToDbl aspect,BlitzGL_DblBank
	Local aspect_l = PeekInt(BlitzGL_DblBank, 0), aspect_r = PeekInt(BlitzGL_DblBank, 4)

	SngToDbl zNear,BlitzGL_DblBank
	Local zNear_l = PeekInt(BlitzGL_DblBank, 0), zNear_r = PeekInt(BlitzGL_DblBank, 4)

	SngToDbl zFar,BlitzGL_DblBank
	Local zFar_l = PeekInt(BlitzGL_DblBank, 0), zFar_r = PeekInt(BlitzGL_DblBank, 4)

	blitz_gluPerspective fovy_l, fovy_r, aspect_l, aspect_r, zNear_l, zNear_r, zFar_l, zFar_r
End Function


Function gluPickMatrix(x#, y#, nwidth#, nheight#, pViewport)
	SngToDbl x,BlitzGL_DblBank
	Local x_l = PeekInt(BlitzGL_DblBank, 0), x_r = PeekInt(BlitzGL_DblBank, 4)
	
	SngToDbl y,BlitzGL_DblBank
	Local y_l = PeekInt(BlitzGL_DblBank, 0), y_r = PeekInt(BlitzGL_DblBank, 4)

	SngToDbl nwidth,BlitzGL_DblBank
	Local nwidth_l = PeekInt(BlitzGL_DblBank, 0), nwidth_r = PeekInt(BlitzGL_DblBank, 4)

	SngToDbl nheight,BlitzGL_DblBank
	Local nheight_l = PeekInt(BlitzGL_DblBank, 0), nheight_r = PeekInt(BlitzGL_DblBank, 4)

	blitz_gluPickMatrix x_l, x_r, y_l, y_r, nwidth_l, nwidth_r, nheight_l, nheight_r, pViewport
End Function


Function gluOrtho2D(nleft#, nright#, nbottom#, ntop#)
	SngToDbl nleft,BlitzGL_DblBank
	Local nleft_l = PeekInt(BlitzGL_DblBank, 0), nleft_r = PeekInt(BlitzGL_DblBank, 4)
	
	SngToDbl nright,BlitzGL_DblBank
	Local nright_l = PeekInt(BlitzGL_DblBank, 0), nright_r = PeekInt(BlitzGL_DblBank, 4)

	SngToDbl nbottom,BlitzGL_DblBank
	Local nbottom_l = PeekInt(BlitzGL_DblBank, 0), nbottom_r = PeekInt(BlitzGL_DblBank, 4)

	SngToDbl ntop,BlitzGL_DblBank
	Local ntop_l = PeekInt(BlitzGL_DblBank, 0), ntop_r = PeekInt(BlitzGL_DblBank, 4)

	blitz_gluOrtho2D nleft_l, nleft_r, nright_l, nright_r, nbottom_l, nbottom_r, ntop_l, ntop_r
End Function


Function glClearDepth(depth#)
	SngToDbl depth,BlitzGL_DblBank
	Blitz_glClearDepth  PeekInt(BlitzGL_DblBank, 0), PeekInt(BlitzGL_DblBank, 4)
End Function


Function gluLookAt(eyex#, eyey#, eyez#, centerx#, centery#, centerz#, upx#, upy#, upz#)
	SngToDbl eyex,BlitzGL_DblBank
	Local eyex_l = PeekInt(BlitzGL_DblBank, 0), eyex_r = PeekInt(BlitzGL_DblBank, 4)
	
	SngToDbl eyey,BlitzGL_DblBank
	Local eyey_l = PeekInt(BlitzGL_DblBank, 0), eyey_r = PeekInt(BlitzGL_DblBank, 4)

	SngToDbl eyez,BlitzGL_DblBank
	Local eyez_l = PeekInt(BlitzGL_DblBank, 0), eyez_r = PeekInt(BlitzGL_DblBank, 4)

	SngToDbl centerx,BlitzGL_DblBank
	Local centerx_l = PeekInt(BlitzGL_DblBank, 0), centerx_r = PeekInt(BlitzGL_DblBank, 4)

	SngToDbl centery,BlitzGL_DblBank
	Local centery_l = PeekInt(BlitzGL_DblBank, 0), centery_r = PeekInt(BlitzGL_DblBank, 4)

	SngToDbl centerz,BlitzGL_DblBank
	Local centerz_l = PeekInt(BlitzGL_DblBank, 0), centerz_r = PeekInt(BlitzGL_DblBank, 4)

	SngToDbl upx,BlitzGL_DblBank
	Local upx_l = PeekInt(BlitzGL_DblBank, 0), upx_r = PeekInt(BlitzGL_DblBank, 4)

	SngToDbl upy,BlitzGL_DblBank
	Local upy_l = PeekInt(BlitzGL_DblBank, 0), upy_r = PeekInt(BlitzGL_DblBank, 4)

	SngToDbl upz,BlitzGL_DblBank
	Local upz_l = PeekInt(BlitzGL_DblBank, 0), upz_r = PeekInt(BlitzGL_DblBank, 4)

	blitz_gluLookAt eyex_l, eyex_r, eyey_l, eyey_r, eyez_l, eyez_r, centerx_l, centerx_r, centery_l, centery_r, centerz_l, centerz_r, upx_l, upx_r, upy_l, upy_r, upz_l, upz_r
End Function


Function gluCylinder(qobj, baseRadius#, topRadius#, nheight#, slices, stacks)
	SngToDbl baseRadius,BlitzGL_DblBank
	Local baseRadius_l = PeekInt(BlitzGL_DblBank, 0), baseRadius_r = PeekInt(BlitzGL_DblBank, 4)
	
	SngToDbl topRadius,BlitzGL_DblBank
	Local topRadius_l = PeekInt(BlitzGL_DblBank, 0), topRadius_r = PeekInt(BlitzGL_DblBank, 4)
	
	SngToDbl nheight,BlitzGL_DblBank
	Local nheight_l = PeekInt(BlitzGL_DblBank, 0), nheight_r = PeekInt(BlitzGL_DblBank, 4)	
	
	blitz_gluCylinder qobj, baseRadius_l, baseRadius_r, topRadius_l, topRadius_r, nheight_l, nheight_r, slices, stacks
End Function


Function gluDisk(qobj, innerRadius#, outerRadius#, slices, loops)
	SngToDbl innerRadius,BlitzGL_DblBank
	Local innerRadius_l = PeekInt(BlitzGL_DblBank, 0), innerRadius_r = PeekInt(BlitzGL_DblBank, 4)
	
	SngToDbl outerRadius,BlitzGL_DblBank
	Local outerRadius_l = PeekInt(BlitzGL_DblBank, 0), outerRadius_r = PeekInt(BlitzGL_DblBank, 4)
	
	blitz_gluDisk qobj, innerRadius_l, innerRadius_r, outerRadius_l, outerRadius_r, slices, loops
End Function


Function gluPartialDisk(qobj, innerRadius#, outerRadius#, slices, loops, startAngle#, sweepAngle#)
	SngToDbl innerRadius,BlitzGL_DblBank
	Local innerRadius_l = PeekInt(BlitzGL_DblBank, 0), innerRadius_r = PeekInt(BlitzGL_DblBank, 4)
	
	SngToDbl outerRadius,BlitzGL_DblBank
	Local outerRadius_l = PeekInt(BlitzGL_DblBank, 0), outerRadius_r = PeekInt(BlitzGL_DblBank, 4)
	
	SngToDbl startAngle,BlitzGL_DblBank
	Local startAngle_l = PeekInt(BlitzGL_DblBank, 0), startAngle_r = PeekInt(BlitzGL_DblBank, 4)
	
	SngToDbl sweepAngle,BlitzGL_DblBank
	Local sweepAngle_l = PeekInt(BlitzGL_DblBank, 0), sweepAngle_r = PeekInt(BlitzGL_DblBank, 4)		
	
	blitz_gluPartialDisk qobj, innerRadius_l, innerRadius_r, outerRadius_l, outerRadius_r, slices, loops, startAngle_l, startAngle_r, sweepAngle_l, sweepAngle_r
End Function


Function gluSphere(qobj, radius#, slices, stacks)
	SngToDbl radius, BlitzGL_DblBank
	blitz_gluSphere qobj, PeekInt(BlitzGL_DblBank, 0), PeekInt(BlitzGL_DblBank, 4), slices, stacks
End Function


Function glOrtho(nleft#, nright#, bottom#, top#, zNear#, zFar#)
	SngToDbl nleft, BlitzGL_DblBank
	Local nleft_l = PeekInt(BlitzGL_DblBank, 0), nleft_r = PeekInt(BlitzGL_DblBank, 4)
	
	SngToDbl nright, BlitzGL_DblBank
	Local nright_l = PeekInt(BlitzGL_DblBank, 0), nright_r = PeekInt(BlitzGL_DblBank, 4)
	
	SngToDbl bottom, BlitzGL_DblBank
	Local bottom_l = PeekInt(BlitzGL_DblBank, 0), bottom_r = PeekInt(BlitzGL_DblBank, 4)
	
	SngToDbl top, BlitzGL_DblBank
	Local top_l = PeekInt(BlitzGL_DblBank, 0), top_r = PeekInt(BlitzGL_DblBank, 4)
	
	SngToDbl zNear, BlitzGL_DblBank
	Local zNear_l = PeekInt(BlitzGL_DblBank, 0), zNear_r = PeekInt(BlitzGL_DblBank, 4)
	
	SngToDbl zFar, BlitzGL_DblBank
	Local zFar_l = PeekInt(BlitzGL_DblBank, 0), zFar_r = PeekInt(BlitzGL_DblBank, 4)

	blitz_glOrtho  nleft_l, nleft_r, nright_l,nright_r, bottom_l,bottom_r, top_l,top_r, zNear_l,zNear_r, zFar_l, zFar_r
End Function


Function DescribePixelFormat(hDC,iPixelFormat, nBytes, ppfd)
	If ppfd=0 Then
		Return Blitz_NuberOfPixelFormats(hDC,iPixelFormat, nBytes, ppfd)
	Else
		Return Blitz_DescribePixelFormat(hDC,iPixelFormat, nBytes, ppfd)
	EndIf
End Function


; Thanks to Floyd for this one. His comments:
; This should convert all ordinary floats correctly. 
; The extreme cases +Infinity, -Infinity, NaN would require special handling. 
Function SngToDbl(x#, bank)
	Local s, e, m, Lo, Hi, n
	
	PokeFloat bank, 0, x
	n = PeekInt( bank, 0 )  ; raw bits of x
	
	s = n And %10000000000000000000000000000000  ; sign bit
	e = n And %01111111100000000000000000000000  ; 8-bit exponent
	e = (e Shr 3) + %00111000000000000000000000000000  ; 11-bit exponent
	m = n And %00000000011111111111111111111111  ; 23-bit mantissa
	Lo = m Shl 29  ; final three bits of mantissa
	Hi = s Or e Or (m Shr 3 ) ; sign, exponent, first twenty bits of m
	
	PokeInt bank, 0, Lo
	PokeInt bank, 4, Hi
End Function


;~IDEal Editor Parameters:
;~F#2F#34#3B#4C#5D#6E#74#94#A2#AD#BE#C4#DB#E7
;~C#BlitzPlus
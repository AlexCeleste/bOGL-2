
; This tool quickly generates a bitmap font for Draw2D from a system font
; To produce a different bitmap font, change the constants defined below


Const OUTPUT_SIZE = 256
Const FONT_NAME$ = "Blitz"
Const FONT_HEIGHT = 20
Const FONT_BORDER = 1		;Thickness in pixels


Graphics 400, 300, 32, 2

Local img = CreateImage(OUTPUT_SIZE, OUTPUT_SIZE)
SetBuffer ImageBuffer(img)

ClsColor 0, 0, 0
Cls

Local f = LoadFont(FONT_NAME, 20)
SetFont f

Local CW = OUTPUT_SIZE / 16, CPOS = CW / 2 - FONT_BORDER

Local x, y
For x = 0 To 15
	For y = 0 To 15
		Color 1, 1, 1
		
		Local x1, y1 : For x1 = 0 To FONT_BORDER * 2
			For y1 = 0 To FONT_BORDER * 2
				Text x * CW + CPOS + x1, y * CW + CPOS + y1, Chr(16 * y + x), True, True
			Next
		Next
		
		Color 255, 255, 255
		Text x * CW + CPOS + FONT_BORDER, y * CW + CPOS + FONT_BORDER, Chr(16 * y + x), True, True
	Next
Next

SaveBuffer ImageBuffer(img), FONT_NAME + ".bmp"

End


;~IDEal Editor Parameters:
;~C#BlitzPlus
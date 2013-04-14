
; Fast mesh transform functions
;===============================

; These are faster, less accurate/safe versions of bOGL math operations
; for use for tasks needing higher performance and less accuracy,
; e.g. vertex deformations which only have to "look" correct


Const MESH_SIN_ACC = 16
Global MESH_SineLT_#[360 * MESH_SIN_ACC + 1], MESH_DidInit_


Function MESH_InitMeshUtils_()
	If MESH_DidInit_ Then Return	;More than one lib may try to init this
	Local i : For i = 0 To 360 * MESH_SIN_ACC + 1
		MESH_SineLT_[i] = Sin(Float i / Float MESH_SIN_ACC)
	Next
End Function

;Minor optimisation to TFormPoint (src and dst never null, always valid, fast rotation)
Function MESH_TFormFast_(x#, y#, z#, s.bOGL_Ent, d.bOGL_Ent, out#[2])
	MESH_RotateVectorFast_ out, x, y, z, s\g_r
	x = (s\g_x + out[0] * s\sx * s\g_sx - d\g_x) / d\g_sx
	y = (s\g_y + out[1] * s\sy * s\g_sy - d\g_y) / d\g_sy
	z = (s\g_z + out[2] * s\sz * s\g_sz - d\g_z) / d\g_sz
	MESH_RotateVectorFast_ out, x, y, z, d\g_r
End Function

; Rotate a vector x,y,z by normalised axis-angle r (Rodrigues' rotation)
; This uses a lookup table instead of "real" sin/cos, so is not accurate
; On most machines it seems to be faster though
Function MESH_RotateVectorFast_(out#[2], x#, y#, z#, r#[3])
	Local cth# = MESH_SineLT_[Int((r[0] + 90.) * Float MESH_SIN_ACC) Mod (360 * MESH_SIN_ACC + 1)]
	Local sth# = MESH_SineLT_[r[0] * Float MESH_SIN_ACC]	; vrot = v cos(theta) + (k cross v) sin(theta) + k(k dot v)(1 - cos(theta))
	Local kdv# = (r[1] * x + r[2] * y + r[3] * z) * (1. - cth)	;(k dot v)(1 - cos(theta))
	out[0] = cth * x + sth * (r[2] * z - r[3] * y) + r[1] * kdv
	out[1] = cth * y + sth * (r[3] * x - r[1] * z) + r[2] * kdv
	out[2] = cth * z + sth * (r[1] * y - r[2] * x) + r[3] * kdv
End Function


;~IDEal Editor Parameters:
;~C#BlitzPlus
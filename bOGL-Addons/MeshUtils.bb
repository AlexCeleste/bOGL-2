
; Fast mesh transform functions
;===============================

; These are faster, less accurate/safe versions of bOGL math operations
; for use for tasks needing higher performance and less accuracy,
; e.g. vertex deformations which only have to "look" correct


Const MESH_SIN_ACC = 16, MESH_SLT_SIZE = 360 * MESH_SIN_ACC + 1
Global MESH_SineLT_#[MESH_SLT_SIZE], MESH_CosLT_#[MESH_SLT_SIZE], MESH_DidInit_


Function MESH_InitMeshUtils_()
	If MESH_DidInit_ Then Return	;More than one lib may try to init this
	Local i : For i = 0 To MESH_SLT_SIZE
		MESH_SineLT_[i] = Sin(Float i / Float MESH_SIN_ACC)
		MESH_CosLT_[i] = Cos(Float i / Float MESH_SIN_ACC)
	Next
End Function

;Minor optimisation to TFormPoint (src and dst never null, always valid, fast rotation)
Function MESH_TFormFast_(x#, y#, z#, s.bOGL_Ent, d.bOGL_Ent, out#[2])
	MESH_RotateVectorFast_ out, x * s\g_sx, y * s\g_sy, z * s\g_sz, s\g_r
	x = (s\g_x + out[0] - d\g_x) : y = (s\g_y + out[1] - d\g_y) : z = (s\g_z + out[2] - d\g_z)
	d\g_r[0] = -d\g_r[0] : MESH_RotateVectorFast_ out, x, y, z, d\g_r : d\g_r[0] = -d\g_r[0]
	out[0] = out[0] / d\g_sx : out[1] = out[1] / d\g_sy : out[2] = out[2] / d\g_sz
End Function

Function MESH_TFormFast2_(x#, y#, z#, s.bOGL_Ent, d.bOGL_Ent, out#[2], ro)	;Precalculated lookup
	MESH_RotateVectorFast_ out, x * s\g_sx, y * s\g_sy, z * s\g_sz, s\g_r
	x = (s\g_x + out[0] - d\g_x) : y = (s\g_y + out[1] - d\g_y) : z = (s\g_z + out[2] - d\g_z)
	MESH_RotateVectorFast2_ out, x, y, z, d\g_r, ro
	out[0] = out[0] / d\g_sx : out[1] = out[1] / d\g_sy : out[2] = out[2] / d\g_sz
End Function


; Rotate a vector x,y,z by normalised axis-angle r (Rodrigues' rotation)
; This uses a lookup table instead of "real" sin/cos, so is not accurate
; On most machines it seems to be faster though
Function MESH_RotateVectorFast_(out#[2], x#, y#, z#, r#[3])
	Local cth# = MESH_CosLT_[Int(r[0] * Float MESH_SIN_ACC) Mod MESH_SLT_SIZE]
	Local sth# = MESH_SineLT_[r[0] * Float MESH_SIN_ACC]	; vrot = v cos(theta) + (k cross v) sin(theta) + k(k dot v)(1 - cos(theta))
	Local kdv# = (r[1] * x + r[2] * y + r[3] * z) * (1. - cth)	;(k dot v)(1 - cos(theta))
	out[0] = cth * x + sth * (r[2] * z - r[3] * y) + r[1] * kdv
	out[1] = cth * y + sth * (r[3] * x - r[1] * z) + r[2] * kdv
	out[2] = cth * z + sth * (r[1] * y - r[2] * x) + r[3] * kdv
End Function

; This version accepts a precalc'ed index for the lookup too
Function MESH_RotateVectorFast2_(out#[2], x#, y#, z#, r#[3], ro)
	Local cth# = MESH_CosLT_[ro], sth# = MESH_SineLT_[ro]
	Local kdv# = (r[1] * x + r[2] * y + r[3] * z) * (1. - cth)
	out[0] = cth * x + sth * (r[2] * z - r[3] * y) + r[1] * kdv
	out[1] = cth * y + sth * (r[3] * x - r[1] * z) + r[2] * kdv
	out[2] = cth * z + sth * (r[1] * y - r[2] * x) + r[3] * kdv
End Function


; Quaternion normalised linear interpolation
; Faster than a SLERP and produces adequate results for most uses (smaller rotations)
Function MESH_QuatNLERP_(out#[3], f#[3], t#[3], pol#)
	out[0] = (1. - pol) * f[0] + pol * t[0]
	out[1] = (1. - pol) * f[1] + pol * t[1]
	out[2] = (1. - pol) * f[2] + pol * t[2]
	out[3] = (1. - pol) * f[3] + pol * t[3]
	bOGL_NormaliseQuat_ out
End Function


;~IDEal Editor Parameters:
;~F#D#16#1D#28#32#3D
;~C#BlitzPlus
;----------------------------------------------------
; Filename: Glu_const.bb
; Rev 0.1 2003.02.21
;
;----------------------------------------------------
;
; OpenGL Direct for Blitz
; Using OpenGl from Blitz without a wrapper DLL
;
; by Peter Scheutz,... (your name here) 
;
; Based on Sublevel6's PowerBasic headers
; Link: root.tty0.org/sublevel6/?page=files 
;
;----------------------------------------------------
;
; This file contains Glu constants.
;
;----------------------------------------------------


Const GLU_INC                         = 1

Const GLU_VERSION_1_1                 = 1
Const GLU_VERSION_1_2                 = 1

Const GLU_INVALID_ENUM                = 100900
Const GLU_INVALID_VALUE               = 100901
Const GLU_OUT_OF_MEMORY               = 100902
Const GLU_INCOMPATIBLE_GL_VERSION     = 100903

Const GLU_VERSION                     = 100800
Const GLU_EXTENSIONS                  = 100801

Const GLU_TRUE                        = GL_TRUE
Const GLU_FALSE                       = GL_FALSE

Const GLU_SMOOTH                      = 100000
Const GLU_FLAT                        = 100001
Const GLU_NONE                        = 100002

Const GLU_POINT                       = 100010
Const GLU_LINE                        = 100011
Const GLU_FILL                        = 100012
Const GLU_SILHOUETTE                  = 100013

Const GLU_OUTSIDE                     = 100020
Const GLU_INSIDE                      = 100021

;Global GLU_TESS_MAX_COORD AS DOUBLE
;GLU_TESS_MAX_COORD              = 1.0e150

Const GLU_TESS_WINDING_RULE           = 100140
Const GLU_TESS_BOUNDARY_ONLY          = 100141
Const GLU_TESS_TOLERANCE              = 100142

Const GLU_TESS_WINDING_ODD            = 100130
Const GLU_TESS_WINDING_NONZERO        = 100131
Const GLU_TESS_WINDING_POSITIVE       = 100132
Const GLU_TESS_WINDING_NEGATIVE       = 100133
Const GLU_TESS_WINDING_ABS_GEQ_TWO    = 100134

Const GLU_TESS_BEGIN                  = 100100
Const GLU_TESS_VERTEX                 = 100101
Const GLU_TESS_END                    = 100102
Const GLU_TESS_ERROR                  = 100103
Const GLU_TESS_EDGE_FLAG              = 100104
Const GLU_TESS_COMBINE                = 100105

Const GLU_TESS_BEGIN_DATA             = 100106

Const GLU_TESS_VERTEX_DATA            = 100107

Const GLU_TESS_END_DATA               = 100108
Const GLU_TESS_ERROR_DATA             = 100109

Const GLU_TESS_EDGE_FLAG_DATA         = 100110

Const GLU_TESS_COMBINE_DATA           = 100111

Const GLU_TESS_ERROR1                 = 100151
Const GLU_TESS_ERROR2                 = 100152
Const GLU_TESS_ERROR3                 = 100153
Const GLU_TESS_ERROR4                 = 100154
Const GLU_TESS_ERROR5                 = 100155
Const GLU_TESS_ERROR6                 = 100156
Const GLU_TESS_ERROR7                 = 100157
Const GLU_TESS_ERROR8                 = 100158

Const GLU_TESS_MISSING_BEGIN_POLYGON  = GLU_TESS_ERROR1
Const GLU_TESS_MISSING_BEGIN_CONTOUR  = GLU_TESS_ERROR2
Const GLU_TESS_MISSING_END_POLYGON    = GLU_TESS_ERROR3
Const GLU_TESS_MISSING_END_CONTOUR    = GLU_TESS_ERROR4
Const GLU_TESS_COORD_TOO_LARGE        = GLU_TESS_ERROR5
Const GLU_TESS_NEED_COMBINE_CALLBACK  = GLU_TESS_ERROR6

Const GLU_AUTO_LOAD_MATRIX            = 100200
Const GLU_CULLING                     = 100201
Const GLU_SAMPLING_TOLERANCE          = 100203
Const GLU_DISPLAY_MODE                = 100204
Const GLU_PARAMETRIC_TOLERANCE        = 100202
Const GLU_SAMPLING_METHOD             = 100205
Const GLU_U_STEP                      = 100206
Const GLU_V_STEP                      = 100207

Const GLU_PATH_LENGTH                 = 100215
Const GLU_PARAMETRIC_ERROR            = 100216
Const GLU_DOMAIN_DISTANCE             = 100217

Const GLU_MAP1_TRIM_2                 = 100210
Const GLU_MAP1_TRIM_3                 = 100211

Const GLU_OUTLINE_POLYGON             = 100240
Const GLU_OUTLINE_PATCH               = 100241

Const GLU_NURBS_ERROR1                = 100251
Const GLU_NURBS_ERROR2                = 100252
Const GLU_NURBS_ERROR3                = 100253
Const GLU_NURBS_ERROR4                = 100254
Const GLU_NURBS_ERROR5                = 100255
Const GLU_NURBS_ERROR6                = 100256
Const GLU_NURBS_ERROR7                = 100257
Const GLU_NURBS_ERROR8                = 100258
Const GLU_NURBS_ERROR9                = 100259
Const GLU_NURBS_ERROR10               = 100260
Const GLU_NURBS_ERROR11               = 100261
Const GLU_NURBS_ERROR12               = 100262
Const GLU_NURBS_ERROR13               = 100263
Const GLU_NURBS_ERROR14               = 100264
Const GLU_NURBS_ERROR15               = 100265
Const GLU_NURBS_ERROR16               = 100266
Const GLU_NURBS_ERROR17               = 100267
Const GLU_NURBS_ERROR18               = 100268
Const GLU_NURBS_ERROR19               = 100269
Const GLU_NURBS_ERROR20               = 100270
Const GLU_NURBS_ERROR21               = 100271
Const GLU_NURBS_ERROR22               = 100272
Const GLU_NURBS_ERROR23               = 100273
Const GLU_NURBS_ERROR24               = 100274
Const GLU_NURBS_ERROR25               = 100275
Const GLU_NURBS_ERROR26               = 100276
Const GLU_NURBS_ERROR27               = 100277
Const GLU_NURBS_ERROR28               = 100278
Const GLU_NURBS_ERROR29               = 100279
Const GLU_NURBS_ERROR30               = 100280
Const GLU_NURBS_ERROR31               = 100281
Const GLU_NURBS_ERROR32               = 100282
Const GLU_NURBS_ERROR33               = 100283
Const GLU_NURBS_ERROR34               = 100284
Const GLU_NURBS_ERROR35               = 100285
Const GLU_NURBS_ERROR36               = 100286
Const GLU_NURBS_ERROR37               = 100287


Const GLU_CW                          = 100120
Const GLU_CCW                         = 100121
Const GLU_INTERIOR                    = 100122
Const GLU_EXTERIOR                    = 100123
Const GLU_UNKNOWN                     = 100124

Const GLU_BEGIN                       = GLU_TESS_BEGIN
Const GLU_VERTEX                      = GLU_TESS_VERTEX
Const GLU_END                         = GLU_TESS_END
Const GLU_ERROR                       = GLU_TESS_ERROR
Const GLU_EDGE_FLAG                   = GLU_TESS_EDGE_FLAG

;~IDEal Editor Parameters:
;~C#BlitzPlus
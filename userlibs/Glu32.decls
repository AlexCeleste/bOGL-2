;----------------------------------------------------------------------------
; Filename Glu32.decls
; Rev 0.5 2003.03.06
;
;----------------------------------------------------------------------------
;
; OpenGL Direct for Blitz ( http://www.blitzbasic.com )
; Using OpenGl from Blitz without a wrapper DLL
;
; by Peter Scheutz
;
;----------------------------------------------------------------------------
;
; This file contains the Blitz UserLib for Glu32.dll, 
; It must be copied to you Userlib directory.
;
;----------------------------------------------------------------------------
;
; Copyright 1996 Silicon Graphics, Inc.
; All Rights Reserved.
;
; This is UNPUBLISHED PROPRIETARY SOURCE CODE of Silicon Graphics, Inc.;
; the contents of this file may Not be disclosed To third parties, copied Or
; duplicated in any form, in whole Or in part, without the prior written
; permission of Silicon Graphics, Inc.
;
; RESTRICTED RIGHTS LEGEND:
; Use, duplication Or disclosure by the Government is subject To restrictions
; as set forth in subdivision (c)(1)(ii) of the Rights in Technical Data
; And Computer Software clause at DFARS 252.227-7013, And/Or in similar Or
; successor clauses in the FAR, DOD Or NASA FAR Supplement. Unpublished -
; rights reserved under the Copyright Laws of the United States.
;
;----------------------------------------------------------------------------



.lib "Glu32.dll"


gluErrorString%(errCode%):"gluErrorString"
gluGetString%(nname%):"gluGetString"
blitz_gluOrtho2D(nleft_l%,nleft_r%, nright_l%,nright_r%,nbottom_l%,nbottom_r%,ntop_l%,ntop_r%):"gluOrtho2D"
blitz_gluPerspective(fovy_l%,fovy_r%,aspect_l%,aspect_r%,zNear_l%,zNear_r%,zFar_l%,zFar_r%):"gluPerspective"
blitz_gluPickMatrix(x_l%,x_r%,y_l%,y_r%,nwidth_l%,nwidth_r%,nheight_l%,nheight_r%, Viewport*):"gluPickMatrix"
blitz_gluLookAt(eyex_l%,eyex_r%, eyey_l%,eyey_r%, eyez_l%, eyez_r%, centerx_l%, centerx_r%, centery_l%, centery_r%, centerz_l%, centerz_r%, upx_l%, upx_r%, upy_l%, upy_r%, upz_l%, upz_r%):"gluLookAt"

;Blitz_gluProject#(ByVal objx As Double, ByVal objy As Double, ByVal objz As Double, ByVal modelMatrix As Double, ByVal projMatrix As Double, ByVal Viewport As Long, winx As Double, winy As Double, winz As Double):"gluProject"

gluBuild1DMipmaps%(ntarget%,components%,nwidth%,nformat%,ntype%, ndata*):"gluBuild1DMipmaps"
gluBuild2DMipmaps%(ntarget%,components%,nwidth%,nheight%,nformat%,ntype%, ndata*):"gluBuild2DMipmaps"

gluNewQuadric%():"gluNewQuadric" 
gluDeleteQuadric(State%):"gluDeleteQuadric"
gluQuadricNormals(quadObject%,normals%):"gluQuadricNormals"
gluQuadricTexture(quadObject%, textureCoords*):"gluQuadricTexture"
gluQuadricOrientation(quadObject%, orientation%):"gluQuadricOrientation"
gluQuadricDrawStyle(quadObject%, drawStyle%):"gluQuadricDrawStyle"


blitz_gluCylinder(qobj%, baseRadius_l%,baseRadius_r%, topRadius_l%,topRadius_r%, nheight_l%,nheight_r%, slices%, stacks%):"gluCylinder"             
blitz_gluDisk(qobj%, innerRadius_l%,innerRadius_r%, outerRadius_l%,outerRadius_r%, slices%, loops%):"gluDisk"
blitz_gluPartialDisk(qobj%, innerRadius_l%,innerRadius_r%, outerRadius_l%,outerRadius_r%, slices%, loops%, startAngle_l%,startAngle_r%, sweepAngle_l%,sweepAngle_r%):"gluPartialDisk"
blitz_gluSphere(qobj%, radius_l%,radius_r%, slices%, stacks%):"gluSphere"
gluQuadricCallback(qobj%,which%,nfn%):"gluQuadricCallback"

gluNewNurbsRenderer%():"gluNewNurbsRenderer"
gluDeleteNurbsRenderer(nobj%):"gluDeleteNurbsRenderer"

gluBeginSurface(nobj%):"gluBeginSurface"
gluBeginCurve(nobj%):"gluBeginCurve"
gluEndCurve(nobj%):"gluEndCurve"
gluEndSurface(nobj%):"gluEndSurface"
gluBeginTrim(nobj%):"gluBeginTrim"
gluEndTrim(nobj%):"gluEndTrim"
gluPwlCurve(nobj%,ncount%, narray*,stride%,ntype%):"gluPwlCurve"
gluNurbsCurve(nobj%,nknots%,knot*,stride%, ctlarray*,order%,ntype%):"gluNurbsCurve"
gluNurbsSurface(nobj%,sknot_count%,sknot*,tknot_count%,tknot*,s_stride%,t_stride%, ctlarray*, sorder%,torder%,ntype%):"gluNurbsSurface"
gluLoadSamplingMatrices(nobj%, modelMatrix*, projMatrix*, Viewport*):"gluLoadSamplingMatrices" 
gluNurbsProperty(nobj%,property%, value#):"gluNurbsProperty"        
gluGetNurbsProperty(nobj%,property%, value*):"gluGetNurbsProperty"
gluNurbsCallback(nobj%, which%,nfn%):"gluNurbsCallback"
gluBeginPolygon(tess%):"gluBeginPolygon"         
gluNextContour(tess%,ntype%):"gluNextContour"
gluEndPolygon(tess%):"gluEndPolygon"           


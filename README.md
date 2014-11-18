
 bOGL 2 beta
=============

**About:** bOGL is an OpenGL based open source 3D engine for BlitzBasic (Tested on BlitzPlus). bOGL also works with Blitz3D, if you remove the 3D canvas functions.

![](https://raw.githubusercontent.com/Leushenko/bOGL-2/master/Media/dr1.png)

While the command set is loosely based on that of Blitz3D, they are not compatible, nor are they intended to be. Several commands work differently by design.

bOGL is intended to allow for a modular addon structure. Modules are provided for [2D drawing](bOGL-Addons/Draw2D.bb), [MD2 animation](bOGL-Addons/MD2.bb), [loading and saving static meshes](bOGL-Addons/MeshLoader.bb), [loading and animating skinned meshes](bOGL-Addons/Animation.bb), [event-based entity control](bOGL-Addons/3DAction.bb), [3D collision detection](bOGL-Addons/Collision.bb), and [single-surface particles](bOGL-Addons/Particle.bb). Others may follow. Understanding a module is not required to understand the operation of the bOGL engine core.

For more information on [the core engine](Docs/bOGL.md) and addon modules, see [the Docs folder](Docs).

**Installation:** Move the folder named "userlibs" to BlitzBasic folder. Overwrite if neccessary, or add the content of the files to the existing ones. Now you should be able to run the demo and create your own projects.

Please overwrite any userlib files remaining from bOGL version 1.01. You **must** overwrite any existing OpenGL Direct userlibs, as the original distribution contained errors that are corrected by the bOGL version.

**Licence:** MIT, see [LICENSE file](LICENSE)

**Credits:**  
bOGL 1 by Andres Pajo  
bOGL 2 by Alex Gilding  
Addon modules by Alex Gilding  
OGL Direct by Peter Scheutz  


 Command reference
-------------------

[Graphics3D](Docs/bOGL.md#graphics3d)(title$, width, height, depth, mode), [EndGraphics3D](Docs/bOGL.md#endgraphics3d)()  
[CreateCanvas3D](Docs/bOGL.md#createcanvas3d)(x, y, width, height, group), [FreeCanvas3D](Docs/bOGL.md#freecanvas3d)(canvas)  
[AmbientLight](Docs/bOGL.md#ambientlight)(red, green, blue)  
[CreateCamera](Docs/bOGL.md#createcamera)([parent])  
[CameraRange](Docs/bOGL.md#camerarange)(handler, near#, far#)  
[CameraFieldOfView](Docs/bOGL.md#camerafieldofview)(handler, angle#)  
[CameraDrawMode](Docs/bOGL.md#cameradrawmode)(handler, mode), [CameraClsMode](Docs/bOGL.md#cameraclsmode)(handler, mode)  
[CameraClsColor](Docs/bOGL.md#cameraclscolor)(handler, red, green, blue)  
[CameraFogMode](Docs/bOGL.md#camerafogmode)(handler, mode[, near#, far#])  
[CameraFogColor](Docs/bOGL.md#camerafogcolor)(handler, red, green, blue[, alpha#])  
[CameraViewport](Docs/bOGL.md#cameraviewport)(handler, x, y, width, height)  
[CreateLight](Docs/bOGL.md#createlight)(red, green, blue, flag[, parent])  
[LightRange](Docs/bOGL.md#lightrange)(handler, range#)  
[CreatePivot](Docs/bOGL.md#createpivot)([parent])  
[CreateMesh](Docs/bOGL.md#createmesh)([parent])  
[AddVertex](Docs/bOGL.md#addvertex)(mesh, x#, y#, z#[, u#, v#]), [AddTriangle](Docs/bOGL.md#addtriangle)(mesh, v0, v1, v2)  
[CountTriangles](Docs/bOGL.md#counttriangles)(mesh), [CountVertices](Docs/bOGL.md#countvertices)(mesh), [TriangleVertex](Docs/bOGL.md#trianglevertex)(mesh, tri, vert)  
[VertexCoords](Docs/bOGL.md#vertexcoords)(mesh, v, x#, y#, z#), [VertexTexCoords](Docs/bOGL.md#vertextexcoords)(mesh, v, u#, v#)  
[VertexNormal](Docs/bOGL.md#vertexnormal)(mesh, v, nx#, ny#, nz#), [VertexColor](Docs/bOGL.md#vertexcolor)(mesh, v, r, g, b)  
[VertexX](Docs/bOGL.md#vertexx)#(mesh, v), [VertexY](Docs/bOGL.md#vertexy)#(mesh, v), [VertexZ](Docs/bOGL.md#vertexz)#(mesh, v), [VertexU](Docs/bOGL.md#vertexu)#(mesh, v), [VertexV](Docs/bOGL.md#vertexv)#(mesh, v)  
[CreateCube](Docs/bOGL.md#createcube)([parent])  
[CreateSprite](Docs/bOGL.md#createsprite)([parent])  
[LoadTerrain](Docs/bOGL.md#loadterrain)(terrain$[, parent])  
[PositionEntity](Docs/bOGL.md#positionentity)(handler, x#, y#, z#[, absolute])  
[MoveEntity](Docs/bOGL.md#moveentity)(handler, x#, y#, z#)  
[RotateEntity](Docs/bOGL.md#rotateentity)(handler, x#, y#, z#[, absolute])  
[TurnEntity](Docs/bOGL.md#turnentity)(handler, x#, y#, z#)  
[PointEntity](Docs/bOGL.md#pointentity)(handler, x#, y#, z#[, roll#])  
[ScaleEntity](Docs/bOGL.md#scaleentity)(handler, x#, y#, z#[, absolute])  
[PaintEntity](Docs/bOGL.md#paintentity)(handler, red, green, blue)  
[EntityAlpha](Docs/bOGL.md#entityalpha)(handler, alpha#), [EntityFX](Docs/bOGL.md#entityfx)(handler, flags)  
[EntityTexture](Docs/bOGL.md#entitytexture)(handler, texture)  
[ShowEntity](Docs/bOGL.md#showentity)(handler, state)  
[SetEntityParent](Docs/bOGL.md#setentityparent)(handler, parentH), [GetEntityParent](Docs/bOGL.md#getentityparent)(handler)  
[CountChildren](Docs/bOGL.md#countchildren)(handler), [GetChildEntity](Docs/bOGL.md#getchildentity)(handler, index), [GetChildByName](Docs/bOGL.md#getchildbyname)(handler, name$)  
[SetEntityName](Docs/bOGL.md#setentityname)(handler, name$), [GetEntityName](Docs/bOGL.md#getentityname)$(handler)  
[RegisterEntityUserDataSlot](Docs/bOGL.md#registerentityuserdataslot)(), [SetEntityUserData](Docs/bOGL.md#setentityuserdata)(handler, slot, val), [GetEntityUserData](Docs/bOGL.md#getentityuserdata)(handler, slot)  
[CopyEntity](Docs/bOGL.md#copyentity)(handler[, parentH, deepCopy])  
[FreeEntity](Docs/bOGL.md#freeentity)(handler)  
[FlipPolygons](Docs/bOGL.md#flippolygons)(handler)  
[RotateSubMesh](Docs/bOGL.md#rotatesubmesh)(handler, vf, vt, rx#, ry#, rz#, cx#, cy#, cz#), [QuatRotateSubMesh](Docs/bOGL.md#quatrotatesubmesh)(handler, vf, vt, q#[3], cx#, cy#, cz#)  
[TranslateSubMesh](Docs/bOGL.md#translatesubmesh)(handler, vf, vt, tx#, ty#, tz#)  
[ScaleSubMesh](Docs/bOGL.md#scalesubmesh)(handler, vf, vt, sx#, sy#, sz#, cx#, cy#, cz#)  
[EntityX](Docs/bOGL.md#entityx)#(handler[, absolute]), [EntityY](Docs/bOGL.md#entityy)#(handler[, absolute]), [EntityZ](Docs/bOGL.md#entityz)#(handler[, absolute])  
[EntityXAngle](Docs/bOGL.md#entityxangle)#(handler[, absolute]), [EntityYAngle](Docs/bOGL.md#entityyangle)#(handler[, absolute]), [EntityZAngle](Docs/bOGL.md#entityzangle)#(handler[, absolute])  
[CreateTexture](Docs/bOGL.md#createtexture)(width, height[, filter])  
[LoadTexture](Docs/bOGL.md#loadtexture)(file$[, quality, filter])  
[FreeTexture](Docs/bOGL.md#freetexture)(handler)  
[TextureWidth](Docs/bOGL.md#texturewidth)(handler), [TextureHeight](Docs/bOGL.md#textureheight)(handler)  
[GetTextureData](Docs/bOGL.md#gettexturedata)(handler[, doConvert]), [UpdateTexture](Docs/bOGL.md#updatetexture)(handler, x, y, width, height, pixels[, doConvert])  
[GrabBackBuffer](Docs/bOGL.md#grabbackbuffer)(x, y, width, height, pix[, doConvert])  
[RenderWorld](Docs/bOGL.md#renderworld)([stencilMode]), [RenderStencil](Docs/bOGL.md#renderstencil)()      
[Distance](Docs/bOGL.md#distance)(x1#, y1#, z1#, x2#, y2#, z2#)  
[TFormPoint](Docs/bOGL.md#tformpoint)(x#, y#, z#, src, dst, out#[2])  


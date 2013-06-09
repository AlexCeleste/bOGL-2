
# bOGL-2: A 3D engine for Blitz Basic #

* [Introduction](#intro)
 * [Installation](#install)
 * [Addon modules](#addons)
 * [Differences from Blitz3D](#diffs)
     * [Reduced feature set](#reduced)
     * [Differences in existing features](#existing)
     * [New features not in Blitz3D](#newstuff)
 * [Future directions](#future)
* [Command reference / API](#publicapi)
 * [Global](#global)
     * [Graphics3D](#graphics3d)
     * [EndGraphics3D](#endgraphics3d)
     * [CreateCanvas3D](#createcanvas3d)
     * [FreeCanvas3D](#freecanvas3d)
     * [AmbientLight](#ambientlight)
     * [GrabBackBuffer](#grabbackbuffer)
     * [RenderWorld](#renderworld)
     * [RenderStencil](#renderstencil)
     * [Distance](#distance)
     * [TFormPoint](#tformpoint)
 * [Texture](#texture)
     * [CreateTexture](#createtexture)
     * [LoadTexture](#loadtexture)
     * [FreeTexture](#freetexture)
     * [TextureWidth](#texturewidth)
     * [TextureHeight](#textureheight)
     * [GetTextureData](#gettexturedata)
     * [UpdateTexture](#updatetexture)
 * [Camera](#camera)
     * [CreateCamera](#createcamera)
     * [CameraRange](#camerarange)
     * [CameraFieldOfView](#camerafieldofview)
     * [CameraDrawMode](#cameradrawmode)
     * [CameraClsMode](#cameraclsmode)
     * [CameraClsColor](#cameraclscolor)
     * [CameraFogMode](#camerafogmode)
     * [CameraFogColor](#camerafogcolor)
     * [CameraViewport](#cameraviewport)
 * [Light](#light)
     * [CreateLight](#createlight)
     * [LightRange](#lightrange)
 * [Pivot](#pivot)
     * [CreatePivot](#createpivot)
 * [Mesh](#mesh)
     * [CreateMesh](#createmesh)
     * [AddVertex](#addvertex)
     * [AddTriangle](#addtriangle)
     * [CountTriangles](#counttriangles)
     * [CountVertices](#countvertices)
     * [TriangleVertex](#trianglevertex)
     * [VertexCoords](#vertexcoords)
     * [VertexTexCoords](#vertextexcoords)
     * [VertexNormal](#vertexnormal)
     * [VertexColor](#vertexcolor)
     * [VertexX](#vertexx)
     * [VertexY](#vertexy)
     * [VertexZ](#vertexz)
     * [VertexU](#vertexu)
     * [VertexV](#vertexv)
     * [CreateCube](#createcube)
     * [CreateSprite](#createsprite)
     * [LoadTerrain](#loadterrain)
     * [FlipPolygons](#flippolygons)
 * [Submesh](#submesh)
     * [RotateSubMesh](#rotatesubmesh)
     * [QuatRotateSubMesh](#quatrotatesubmesh)
     * [TranslateSubMesh](#translatesubmesh)
     * [ScaleSubMesh](#scalesubmesh)
 * [Entity movement](#entity movement)
     * [PositionEntity](#positionentity)
     * [MoveEntity](#moveentity)
     * [RotateEntity](#rotateentity)
     * [TurnEntity](#turnentity)
     * [PointEntity](#pointentity)
     * [ScaleEntity](#scaleentity)
 * [Entity position](#entity position)
     * [EntityX](#entityx)
     * [EntityY](#entityy)
     * [EntityZ](#entityz)
     * [EntityXAngle](#entityxangle)
     * [EntityYAngle](#entityyangle)
     * [EntityZAngle](#entityzangle)
 * [Entity appearance](#entityappearance)
     * [PaintEntity](#paintentity)
     * [EntityAlpha](#entityalpha)
     * [EntityFX](#entityfx)
     * [EntityTexture](#entitytexture)
     * [ShowEntity](#showentity)
 * [Entity status](#entitystatus)
     * [SetEntityParent](#setentityparent)
     * [GetEntityParent](#getentityparent)
     * [CountChildren](#countchildren)
     * [GetChildEntity](#getchildentity)
     * [GetChildByName](#getchildbyname)
     * [SetEntityName](#setentityname)
     * [GetEntityName](#getentityname)
     * [RegisterEntityUserDataSlot](#registerentityuserdataslot)
     * [SetEntityUserData](#setentityuserdata)
     * [GetEntityUserData](#getentityuserdata)
     * [CopyEntity](#copyentity)
     * [FreeEntity](#freeentity)
* [Internal commands (Private API)](#privateapi)


## <span id="intro"/>Introduction ##

bOGL is an OpenGL based open source 3D engine for Blitz Basic, primarily intended for use with BlitzPlus. bOGL also works with Blitz3D, if you remove the 3D canvas functions.

bOGL provides a command set similar to that of the Blitz3D and miniB3D engines. It is not intended to be directly compatible or a "drop in" replacement for Blitz3D, and code will not work unmodified, but the command set is similar enough that a Blitz3D or miniB3D veteran should have little difficulty getting up to speed very quickly. The engine is not intended to be particularly featureful or high-performance, but it should be very portable thanks to its OpenGL 1.1 foundation, and provides enough features for small but complete 3D games and apps.

bOGL is intended to allow for a modular addon structure. Official modules are provided for 2D drawing, MD2 animation, loading and saving static meshes, and loading and animating skinned meshes. Others may follow in the course of time, and adding a third-party module is designed to be a simple and painless process. Understanding a module is not required to understand the operation of the bOGL engine core: the engine is structured like a tree, so that nothing close to the "core" depends on anything further out; you can completely understand the engine's structure by reading one file and function at a time, and not having to constantly cross-reference across multiple classes or modules. The core engine is under 1500 lines, and intended to be understood in a single sitting by an interested reader.

bOGL-2 is an extended and heavily-rewritten version of [the original bOGL by Andres Pajo][bogl], built on top of Peter Schuetz's OpenGL Direct library. bOGL-2 and all of its addons are made available under [the MIT licence][mit] (see also the LICENSE file in the main distribution), which means you can use it in any commercial and non-commercial projects at no cost, as long as the authors are given a credit line somewhere in your documentation or end credits.

[bogl]: http://www.blitzbasic.com/Community/posts.php?topic=49612
[mit]: http://opensource.org/licenses/MIT

### <span id="install" />Installation ###

To install bOGL, you must have a working installation of Blitz Basic (either BlitzPlus or Blitz3D; BlitzPlus is the better option and this document assumes you are using it). The Blitz Basic compilers are available from <http://www.blitzbasic.com>; at the time of writing, BlitzPlus is available for free for commercial and non-commercial use. You may need to install compiler updates separately.

Download the bOGL distribution, either as a ZIP or by cloning the Git repository. Place it wherever you like, then copy the `bOGL/userlibs` folder and paste it over your `BlitzPlus/userlibs` folder. If your OS asks you whether you want to overwrite any files, **say yes**. This is important because there are several errors in the original version of the OpenGL Direct userlibs that are corrected in the bOGL version, so if you already had a copy of OpenGL Direct it is important to replace it.

If you want IDE syntax highlighting for the command set (which is always helpful), you can also copy the `bOGL-Addons/bOGL_Optional.decls` file to your `BlitzPlus/userlibs` folder. The other `..._Optional.decls` files provide syntax highlighting for the official Addon modules.

That's all: you can begin using bOGL (e.g. compile and run the example files) immediately.

### <span id="addons" />Addon modules ###

As explained above, bOGL is designed to be easy to extend with additional functionality without making the core engine dependent on any external files or features. You do not need to understand an addon - either how to use it, or how it was written - in order to use the bOGL core engine. While addons may build on each other, they are also supposed to do so only in a way that extends outwards, so no two addons should be mutually dependent. This makes understanding the complete engine a simple task, as you can just study one module at a time.

Using an addon (that conforms to the addon design guideline) should be a simple matter that requires you to do only three things:

1. `Include` the addon's main file from your project (e.g. `Include "bOGL-Addons/Animation.bb"`).
2. Call the addon's initialization function **once**, before you use any other commands (e.g. `InitAnimationAddon()`). It's best to do these in one big block below your `Include` directives, to keep the project tidy.
3. Call the addon's update function in your **main loop**, if it has one (e.g. `UpdateAnimations() : UpdateBonedMeshes()`).

You may also want to copy its `.decls` file (if any) to your main `userlibs` folder, to take advantage of any syntax highlighting options.

bOGL provides official addon modules for those features that we can safely assume you expect as basics from a 3D engine, but that aren't required for the basic 3D engine to work, both as useful examples for the extension process and to keep the core small:

* **MeshLoader** provides commands for loading and saving 3D meshes. You can load meshes from OBJ and the custom BO3D format, and save static meshes to OBJ format. BO3D meshes can be loaded out of Blitz banks as well as from files, making it easy to pack assets together. BO3D meshes support bones, and the entity hierarchy, and can be easily deformed or animated.
 * BO3D is a special custom mesh format inspired by Blitz3D's B3D format and optimised for bOGL. It isn't supported by any editors yet, but you can convert most B3D files to BO3D format using the file converter utility (`Tools/bo3D_conv.bb`: this is a standalone program, not something to add to your project). See `bo3d_spec.txt` for complete internal details of the format.
* **Animation** provides commands for loading animation sequences from BO3D files and applying them to hierarchical entities. You can start, pause, stop and reverse animations, set the specific frame, and get information about an animation's running time. It does not currently provide any functions to dynamically create new animation sequences.
 * This depends on the BO3D model format to load animation data (and to animate BO3D meshes), so it is a second-tier module and depends on `MeshLoader`.
* **MD2** provides commands for loading MD2 meshes and their animation sequences from Quake II-compatible MD2 files. This module provides both loading and animation commands. You can load MD2 models both from files and from Blitz banks, and multiple MD2s can be loaded into a single compound mesh if this will help with performance (it probably won't in most cases, but the option is there for large numbers of simple meshes).
* **Draw2D** provides commands for 2D graphics drawing, including text, images, and graphics primitives like lines and boxes. `Draw2D` uses OpenGL to provide hardware-accelerated drawing in the same context as bOGL's 3D graphics, giving access to very fast rotation and transparency with 2D graphics. `Draw2D` is loosely based on the `Draw3D` library for Blitz3D, and is also similar (in terms of what it does) to `Max2D` for BlitzMax.

Other official addons may follow. A 2D physics addon is currently in development and will hopefully join the other official addons as part of the official distribution soon.

The official addons have their own API documentation files, named after each addon (e.g. see `MeshLoader.md` in this folder for documentation on the `MeshLoader` addon).

### <span id="diffs" />Differences from Blitz3D ###

bOGL is inspired by Blitz3D and miniB3D, but it's not intended to be a drop-in replacement (and, importantly, it's 100% original code). As a result, while it tries hard to maintain that Blitz3D "feel", it makes *no* effort to be code-compatible; in some areas the differences are even intentional, to make you think about making your game a "true bOGL" project instead of just a weak B3D port.

#### <span id="reduced" />Reduced feature set ####

Many of the features provided by Blitz3D and miniB3D outright do not exist in bOGL, even including the official addon modules (from this point on, assume addons are included in the discussion). As a not-necessarily-exhaustive list of these missing features:

* Blitz3D permits a mesh to be made up of multiple separate "surfaces", where a "surface" is a single block of triangles and vertices with a given brush applied to it. bOGL has no concept of "surfaces": each mesh only has one block of vertices and triangles.
* Blitz3D allows you to "instance" geometry by using shallow copies of entities, meaning less data needs to be copied to the GPU, which is good for performance. bOGL does not include this important feature, so everything that gets drawn has a cost. This means that e.g. scenes with large numbers of animated characters will likely be very slow in bOGL.
* bOGL has no concept of a "brush". Colours, textures and FX need to be applied individually; other brush features like shininess do not exist at all.
* bOGL does not support multitexturing. Any details that you would achieve in Blitz3D using multiple texture layers need to be "baked" into the base texture for bOGL (this will make it a bit more difficult to use lightmaps).
* bOGL does not support any texture blend modes other than colour.
* bOGL does not support many of Blitz3D's entity types; mirrors, planes, sprites, listeners and BSPs are not available.
* Terrains are nominally supported, but are actually just naive mesh-based heightfields that form static, standard meshes. This means there is no dynamic LOD, and the dimensions of a terrain are limited by the maximum number of polygons to a mesh (machine-specific, usually 65536 or half that).
* bOGL does not provide loaders for 3DS, X, or B3D files (not to be confused with BO3D files, which are of course supported). You can convert B3D files to BO3D files using the utility at `Tools/bo3d_conv.bb`.
* There is no `AlignToVector`.
* `Animation` does not support animation sequences, only frame-by-frame animation.
* There is no `UpdateWorld`; things like animation are updated by their own addon-specific functions (such as `UpdateAnimations`).
* Render tweening is not built in.
* `UpdateNormals` is not built in (good riddance).
* Collision detection is not built in (since B3D was written, 3D physics libraries have become far more powerful anyway).

#### <span id="existing" />Differences in existing features ####

There are of course also a large number of features inherited from Blitz3D which do exist in bOGL, such as for example the hierarchical entity model. However, many of these features are incompatible with Blitz3D in various ways, some subtly and some wildly so. A non-exhaustive list includes:

* The 3D axes are different; **the Z dimension points backwards** in accordance with the standard OpenGL dimensions, in contrast to B3D's standard DirectX setup (and miniB3D's attempts to hide its OpenGL base under a DirectX translation layer). *All* movement code built for Blitz3D will need to be rewritten with this in mind.
* Several commands have been renamed slightly to reduce ambiguity, e.g. Blitz3D's `NameEntity` and `EntityName` are now `SetEntityName` and `GetEntityName`.
 * Similarly, `EntityPitch`, `EntityYaw` and `EntityRoll` are now `EntityXAngle`, `EntityYAngle` and `EntityZAngle`.
 * `FlipMesh` is now `FlipPolygons`.
 * `GetChild` and `FindChild` are now `GetChildEntity` and `GetChildByName`.
 * `ParentEntity` (didn't even exist in vanilla Blitz3D) and `EntityParent` are now the *much* less confusing `GetEntityParent` and `SetEntityParent`.
* Textures do not have frames, but they do have a parameter to set the filter quality (off, bilinear, mipmapped) and resolution-quality index.
* `PaintEntity` just applies a colour, not a brush.
* `TFormPoint` returns its result in a three-float array passed to it as an out-parameter, not with `TFormed_` helper functions.
* `RenderWorld` takes a stencil-mode parameter, not a tween value.
* `HideEntity` and `ShowEntity` are condensed into just `ShowEntity`, which takes an explicit state parameter.
* `CameraZoom` has been replaced by `CameraFieldOfView`, which takes an actual FOV angle instead of an obscure "zoom" value.
* There is no `CopyRect`, but you can copy data from the back buffer to a Blitz bank, and both ways between a Blitz bank and bOGL textures, using `GrabBackBuffer`, `GetTextureData` and `UpdateTexture` respectively.

Remember, this list is *not* exhaustive. There are many other subtle differences, especially in the way the non-core modules handle their tasks.

#### <span id="newstuff" />New features not in Blitz3D ####

There are also a small number of features new to the engine that have no backward-compatible B3D equivalent:

* 3D can be rendered to a BlitzPlus canvas instead of the main window, which is handy for writing 3D editor apps with native GUI controls (unless you're using bOGL with Blitz3D for some reason).
* There is native support for rendering to, and culling with, the stencil buffer. This should make it fairly easy to implement things like portal rendering or a stencil shadow system.
 * bOGL doesn't expose the feature directly, but unlike DirectX 7, OpenGL makes it easy to grab the Z-buffer. A couple of lines of OpenGL Direct code and you could also write a shadow-map engine.
* Entities carry a "user data array", which lets addons associate additional data with entities easily. An addon can request data slots using `RegisterEntityUserDataSlot` (it should do this once in its `init` procedure), which will register a slot in the array for that addon and return its index, and then access the data in its private slot with the `SetEntityUserData` and `GetEntityUserData` functions. With this mechanism, different addons can extend the base entity structure however they like without conflicting.
* Submeshes may be rotated, translated, and scaled independently of the rest of a mesh using the `{whatever}SubMesh` functions. This might be handy for some kinds of animation, or model transformations. These functions are faster than writing your own equivalents that use `VertexCoords` and a loop.
* It is possible to turn off texture filtering, for that blocky retro look.
* bOGL uses proper quaternion rotations instead of Euler angles, which is a feature of the original Blitz3D, but not miniB3D.
* There's a native `CreateQuad` function.
* Most of the functions that take a set of flags or similar numeric options now have the flags predeclared as named constants, so you don't need to remember obscure numbers.

This doesn't include the addon modules, which themselves add some interesting features such as loading meshes from a bank, or native 2D support; but addons are supposed to add features so that's not much of a surprise.

### <span id="future" />Future directions ###

No living project is ever finished. There are several ideas for extension to bOGL that will hopefully be implemented at some point in the future, or are currently in progress:

* A dedicated 2D physics engine module (this is currently under development).
* It might be fun to see bOGL ported to other languages: BlitzMax, Monkey, and Objective-C are all viable and worthwhile targets. There isn't much in bOGL that is specific to Blitz Basic (a few things use the built-in typelists, but not much).
* A dedicated single-surface particle engine module.
* A port to newer OpenGL versions, to provide things like hardware skinning.

## <span id="publicapi" />Command reference / API##

This section describes each command from the bOGL core API in detail.
Commands that belong to the official addon modules are not described here, but rather in the specific documentation for the relevant addon.

### <span id="global" />Global ###
#### <span id="graphics3d" />Graphics3D ####
`Graphics3D(title$, width, height, depth, mode)`  
**Parameters:** The `title` field is be used to set the app window title. Other than that, the parameters are identical to those for Blitz's built-in `Graphics` command.  
**Return value:** None.  
**Description:** This function creates a new 3D graphics context with the resolution *width* x *height*; the resolution must be supported on the local hardware (check whether it is using Blitz's builtin `CountGfxModes` and `GfxModeExists` functions. The `depth` parameter sets the colour depth (on all modern systems, the only valid value for this parameter is 32).  
The `mode` parameter sets the display mode: 0 to use fullscreen in release mode and windowed in debug mode; 1 for fullscreen; 2 for windowed; and 3 for a scaled window.  
The `title` parameter sets the application title, which is visible at the top of the window in windowed mode.

#### <span id="endgraphics3d" />EndGraphics3D ####
`EndGraphics3D()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This function destroys the current 3D graphics context, retuning the application to text mode (or its default miniwindow, if using Blitz3D). This function will also free all entities and delete all textures. It **will not** however delete any addon data, so do not rely on it as a cleanup function (think of the deleting of entities more as intentionally breaking a 3D scene, to force you to rebuild it properly if the graphics are recreated later, such as after a resolution change).  

#### <span id="createcanvas3d" />CreateCanvas3D ####
`CreateCanvas3D(x, y, width,height, group)`  
**Parameters:** Position and resolution of the canvas object, and the GUI group to place it in.  
**Return value:** The created canvas object.  
**Description:** This function creates a canvas object in the BlitzPlus GUI capable of rendering a 3D scene. This function obviously does not work with Blitz3D, and needs to be commented out if working with that compiler.  

#### <span id="freecanvas3d" />FreeCanvas3D ####
`FreeCanvas3D(canvas)`  
**Parameters:** The canvas to free.  
**Return value:** None.  
**Description:** This function frees a 3D canvas object, removing the BlitzPlus GUI element and destroying the 3D context created for it. Like `EndGraphics3D`, it destroys all entities and textures currently in use, but you cannot rely on it as a cleanup function as it **will not** destroy any addon data currently active.  

#### <span id="ambientlight" />AmbientLight ####
`AmbientLight(red, green, blue)`  
**Parameters:** The desired colour described by its integer (0-255) RGB components.  
**Return value:** None.  
**Description:** This function sets the colour of the ambient light for the world. Ambient light illuminates and colours all objects, regardless of whether they are within range of an actual light object. The default ambience on creating a new world is 128, 128, 128; this is equivalent to 50% of full illumination. For more dramatic contrast, crank this number down; if you don't want to bother with light objects at all, set this to 255, 255, 255 and get full illumination on all meshes.  

#### <span id="grabbackbuffer" />GrabBackBuffer ####
`GrabBackBuffer(x, y, width, height, pix[, doConvert])`  
**Parameters:** The position and area of the part of the backbuffer you want to grab; the bank to store the data in; an optional flag to determine whether or not to convert the pixel format.  
**Return value:** None.  
**Description:** This function grabs the pixels from an area of the backbuffer and stores them in a Blitz bank. Make sure the bank is big enough to hold the requested data!  
The optional `doConvert` parameter tells the function whether it should convert the pixels from the internal OpenGL format - RBGA - to the BGRA format used by Blitz images (and Blitz3D's textures). This parameter is True by default, so make sure to set it to False if you don't need to process the pixel data as though it were Blitz image data, as the conversion is obviously quite slow.  

#### <span id="renderworld" />RenderWorld ####
`RenderWorld([stencilMode])`  
**Parameters:** An optional flag to set the role of the stencil buffer.  
**Return value:** None.  
**Description:** Renders every visible object in the 3D scene. Every camera that is currently activated for rendering will draw to its assigned viewport on the backbuffer.  
The optional `stencilMode` parameter determines what effect the stencil buffer will have on the render. It has three valid argument values: `BOGL_STENCIL_OFF` (do not consider the stencil buffer at all), `BOGL_STENCIL_TRUE` (only render to pixels that have a value in the stencil buffer) and `BOGL_STENCIL_FALSE` (only render to those pixels that have no value in the stencil buffer). The default value is `BOGL_STENCIL_OFF`.  
Note that `RenderWorld` does *not* modify the stencil buffer in any way: that is the job of the `RenderStencil` function.  
Remember that after drawing anything to the back buffer, the buffers must be flipped so that the data is placed on the front buffer for display. Blitz's `Flip` command **will not** work with bOGL for this: you must call `SwapBuffers(bOGL_hMainDC)`. See the demo programs for this in action.

#### <span id="renderstencil" />RenderStencil ####
`RenderStencil()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This function is equivalent to `RenderWorld`, except that it renders to the stencil buffer instead of the back buffer. Only objects that have their FX flags set to appear in stencil mode will be drawn, and only cameras that are set to render in stencil mode will be used to draw.  
The stencil buffer is mainly useful for defining custom-shaped "clipping" regions in the main viewport. You can use it to implement a portal rendering or stencil shadow system; by dividing a scene into "on" and "off" areas, two different images (e.g. light and shadow; through the portal and local space) can be composed into one render, first by rendering the world (with `RenderWorld`) with the stencil mode set to true, and then a different view of the world with the stencil mode set to false.

#### <span id="distance" />Distance ####
`Distance#(x1#, y1#, z1#, x2#, y2#, z2#)`  
**Parameters:** Three floats describing one 3D point; three floats describing another 3D point.  
**Return value:** The distance between the two points.  
**Description:** This is just a simple 3D distance function for quickly getting the distance between two abstract points in 3D space. Since there are no entities involved, the function is purely mathematical and does not consider any transformations as the points have no specified connection to any real element of the 3D scene.  

#### <span id="tformpoint" />TFormPoint ####
`TFormPoint(x#, y#, z#, src, dst, out#[2])`  
**Parameters:** Three floats describing a point; the source entity to transform from; the destination entity to transform to; an out-vector to store the resulting transformed coordinates.  
**Return value:** None (the values are returned in the `out` parameter).  
**Description:** This function "transforms" a point in 3D space from the coordinate system of one entity to that of another entity. That is to say, given a point in 3D space described in terms of the entity `src`'s local space, the function will return a coordinate *that describes the same absolute point* in the local coordinate system of entity `dst`. A "local" coordinate system refers to the fact that is an object has rotated by a given amount, "a bit to its left" is no longer the same point as "a bit to the world's left"; this function takes "a bit to `src`'s left" and tells us where that point is relative to `dst`.  
The resulting 3D coordinate is placed in the array passed in the `out` parameter, with the x component in 0, the y component in 1, and the z component in 2.

### <span id="texture" />Texture ###
#### <span id="createtexture" />CreateTexture ####
`CreateTexture()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="loadtexture" />LoadTexture ####
`LoadTexture()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="freetexture" />FreeTexture ####
`FreeTexture()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="texturewidth" />TextureWidth ####
`TextureWidth()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="textureheight" />TextureHeight ####
`TextureHeight()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="gettexturedata" />GetTextureData ####
`GetTextureData()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="updatetexture" />UpdateTexture ####
`UpdateTexture()`  
**Parameters:**  
**Return value:**  
**Description:**  

### <span id="camera" />Camera ###
#### <span id="createcamera" />CreateCamera ####
`CreateCamera()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="camerarange" />CameraRange ####
`CameraRange()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="camerafieldofview" />CameraFieldOfView ####
`CameraFieldOfView()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="cameradrawmode" />CameraDrawMode ####
`CameraDrawMode()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="cameraclsmode" />CameraClsMode ####
`CameraClsMode()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="cameraclscolor" />CameraClsColor ####
`CameraClsColor()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="camerafogmode" />CameraFogMode ####
`CameraFogMode()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="camerafogcolor" />CameraFogColor ####
`CameraFogColor()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="cameraviewport" />CameraViewport ####
`CameraViewport()`  
**Parameters:**  
**Return value:**  
**Description:**  

### <span id="light" />Light ###
#### <span id="createlight" />CreateLight ####
`CreateLight()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="lightrange" />LightRange ####
`LightRange()`  
**Parameters:**  
**Return value:**  
**Description:**  

### <span id="pivot" />Pivot ###
#### <span id="createpivot" />CreatePivot ####
`CreatePivot()`  
**Parameters:**  
**Return value:**  
**Description:**  

### <span id="mesh" />Mesh ###
#### <span id="createmesh" />CreateMesh ####
`CreateMesh()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="addvertex" />AddVertex ####
`AddVertex()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="addtriangle" />AddTriangle ####
`AddTriangle()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="counttriangles" />CountTriangles ####
`CountTriangles()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="countvertices" />CountVertices ####
`CountVertices()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="trianglevertex" />TriangleVertex ####
`TriangleVertex()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="vertexcoords" />VertexCoords ####
`VertexCoords()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="vertextexcoords" />VertexTexCoords ####
`VertexTexCoords()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="vertexnormal" />VertexNormal ####
`VertexNormal()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="vertexcolor" />VertexColor ####
`VertexColor()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="vertexx" />VertexX ####
`VertexX()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="vertexy" />VertexY ####
`VertexY()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="vertexz" />VertexZ ####
`VertexZ()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="vertexu" />VertexU ####
`VertexU()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="vertexv" />VertexV ####
`VertexV()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="createcube" />CreateCube ####
`CreateCube()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="createsprite" />CreateSprite ####
`CreateSprite()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="loadterrain" />LoadTerrain ####
`LoadTerrain()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="flippolygons" />FlipPolygons ####
`FlipPolygons()`  
**Parameters:**  
**Return value:**  
**Description:**  

### <span id="submesh" />Submesh ###
#### <span id="rotatesubmesh" />RotateSubMesh ####
`RotateSubMesh()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="quatrotatesubmesh" />QuatRotateSubMesh ####
`QuatRotateSubMesh()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="translatesubmesh" />TranslateSubMesh ####
`TranslateSubMesh()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="scalesubmesh" />ScaleSubMesh ####
`ScaleSubMesh()`  
**Parameters:**  
**Return value:**  
**Description:**  

### <span id="entity movement" />Entity movement ###
#### <span id="positionentity" />PositionEntity ####
`PositionEntity()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="moveentity" />MoveEntity ####
`MoveEntity()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="rotateentity" />RotateEntity ####
`RotateEntity()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="turnentity" />TurnEntity ####
`TurnEntity()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="pointentity" />PointEntity ####
`PointEntity()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="scaleentity" />ScaleEntity ####
`ScaleEntity()`  
**Parameters:**  
**Return value:**  
**Description:**  

### <span id="entity position" />Entity position ###
#### <span id="entityx" />EntityX ####
`EntityX()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="entityy" />EntityY ####
`EntityY()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="entityz" />EntityZ ####
`EntityZ()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="entityxangle" />EntityXAngle ####
`EntityXAngle()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="entityyangle" />EntityYAngle ####
`EntityYAngle()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="entityzangle" />EntityZAngle ####
`EntityZAngle()`  
**Parameters:**  
**Return value:**  
**Description:**  

### <span id="entity appearance" />Entity appearance ###
#### <span id="paintentity" />PaintEntity ####
`PaintEntity()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="entityalpha" />EntityAlpha ####
`EntityAlpha()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="entityfx" />EntityFX ####
`EntityFX()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="entitytexture" />EntityTexture ####
`EntityTexture()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="showentity" />ShowEntity ####
`ShowEntity()`  
**Parameters:**  
**Return value:**  
**Description:**  

### <span id="entity status" />Entity status ###
#### <span id="setentityparent" />SetEntityParent ####
`SetEntityParent()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="getentityparent" />GetEntityParent ####
`GetEntityParent()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="countchildren" />CountChildren ####
`CountChildren()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="getchildentity" />GetChildEntity ####
`GetChildEntity()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="getchildbyname" />GetChildByName ####
`GetChildByName()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="setentityname" />SetEntityName ####
`SetEntityName()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="getentityname" />GetEntityName ####
`GetEntityName()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="registerentityuserdataslot" />RegisterEntityUserDataSlot ####
`RegisterEntityUserDataSlot()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="setentityuserdata" />SetEntityUserData ####
`SetEntityUserData()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="getentityuserdata" />GetEntityUserData ####
`GetEntityUserData()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="copyentity" />CopyEntity ####
`CopyEntity()`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="freeentity" />FreeEntity ####
`FreeEntity()`  
**Parameters:**  
**Return value:**  
**Description:**  


## <span id="privateapi" />Internal commands (Private API) ##

This section describes functions and structures from the bOGL internal, private API. This is for information, interest, and addon-writers only; the private API may change at any time. You should **not** use anything described in this section in your project's main code, as it is considered private and internal to bOGL.



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
 * [Entity movement](#entitymovement)
     * [PositionEntity](#positionentity)
     * [MoveEntity](#moveentity)
     * [RotateEntity](#rotateentity)
     * [TurnEntity](#turnentity)
     * [PointEntity](#pointentity)
     * [ScaleEntity](#scaleentity)
 * [Entity position](#entityposition)
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
* `EntityFX` flags are different, bearing no relation to their Blitz3D equivalents and offering a different selection of features.

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
`CreateTexture(width, height[, filter])`  
**Parameters:** Size of the texture to create; the filtering method to use.  
**Return value:** The newly-created texture.  
**Description:** This function creates a texture of the given dimensions and returns it, ready for use. For best (most portable) results, the width and height should both be powers of two. They don't have to be the same, but if they are not powers of two the texture may not draw correctly on all systems, or may even fail to create at all (this is machine-specific).  
The optional `filter` parameter has three valid argument values: 0 (no filtering - blocky and retro), 1 (bilinear filtering - smooth) or 2 (bilinear filtering with mipmaps).  
A texture that has been created in this way should at some future stage be released with `FreeTexture`.  

#### <span id="loadtexture" />LoadTexture ####
`LoadTexture(file$[, quality, filter])`  
**Parameters:** The file to load the texture data from; a flag controlling resolution re-scaling; the filtering method to use.  
**Return value:** The newly-loaded texture.  
**Description:** This function loads a texture from a file. If the file cannot be found or cannot be loaded as an image, the function will return 0 (any type accepted by Blitz's builtin `LoadImage` procedure - BMP, PNG, JPG, TGA - can be read by this function, as it uses `LoadImage` to read the pixel data).  
The optional `quality` parameter determines whether the texture will be rescaled after loading. If `quality` is non-zero, the texture's height and width will both be rescaled to `2^quality`. If `quality` is zero, the texture will be left unscaled. The default value of `quality` is 8, so by default all textures are loaded with a resolution of 256x256.  
The optional `filter` parameter has three valid argument values: 0 (no filtering - blocky and retro), 1 (bilinear filtering - smooth) or 2 (bilinear filtering with mipmaps).  
A texture that has been loaded in this way should at some future stage be released with `FreeTexture`.  

#### <span id="freetexture" />FreeTexture ####
`FreeTexture(handler)`  
**Parameters:** The texture to free.  
**Return value:** None.  
**Description:** "Frees" the reference to a texture from your code. You should call this when you are done manipulating the texture in code (e.g. applying it to things). The texture itself will not be deleted as long as there are any meshes that it has been applied to, so it is safe (and sensible) to free a texture as soon as you have finished applying it to entities unless you need to do something with it later (such as updating its pixels, or applying it to another entity).  
If a texture has been freed, it will be destroyed at the same time as the last mesh that was using it is destroyed. If no meshes are using it at the time it is freed, it will be destroyed immediately.  

#### <span id="texturewidth" />TextureWidth ####
`TextureWidth(handler)`  
**Parameters:** The texture.  
**Return value:** Its width in pixels.  
**Description:** This function returns the width of a texture in pixels.  

#### <span id="textureheight" />TextureHeight ####
`TextureHeight(handler)`  
**Parameters:** The texture.  
**Return value:** Its height in pixels.  
**Description:** This function returns the height of a texture in pixels.  

#### <span id="gettexturedata" />GetTextureData ####
`GetTextureData(handler[, doConvert])`  
**Parameters:** The texture to get data from; a flag for whether to convert the pixel format.  
**Return value:** A Blitz bank containing the pixel data of the texture.  
**Description:** This function returns the raw, packed pixel data for a texture in a newly-created Blitz bank.  
The optional `doConvert` parameter tells the function whether it should convert the pixels from the internal OpenGL format - RBGA - to the BGRA format used by Blitz images (and Blitz3D's textures). This parameter is True by default, so make sure to set it to False if you don't need to process the pixel data as though it were Blitz image data, as the conversion is obviously quite slow (or if you specifically want it in OpenGL format). If you are just copying data from one texture to another, you don't need to process the data.

#### <span id="updatetexture" />UpdateTexture ####
`UpdateTexture(handler, x, y, width, height, pixels[, doConvert])`  
**Parameters:** The texture; the position and dimensions of the rectangle to update; a Blitz bank containing the new pixel data; a flag for whether to convert the pixel format.  
**Return value:** None.  
**Description:** This function takes raw, packed pixel data in a Blitz bank and applies it to the specified rectangle of an existing texture, updating the texture with the new pixels. The change takes effect immediately.  
The optional `doConvert` parameter tells the function whether it should convert the pixels from the BGRA format used by Blitz images (and Blitz3D's textures) to the RGBA internal OpenGL format. This parameter is True by default, so make sure to set it to False if you don't need to process the pixel data as though it were Blitz image data, as the conversion is obviously quite slow. If you are just copying data from the back buffer or another texture, you shouldn't need to process the pixels, so make sure both the source and destination conversion flags are set to false.  

### <span id="camera" />Camera ###
#### <span id="createcamera" />CreateCamera ####
`CreateCamera([parent])`  
**Parameters:** A parent entity for the camera.  
**Return value:** The newly-created camera.  
**Description:** This function creates a new camera entity. It can optionally be assigned a parent on creation using the `parent` parameter. The default value for this parameter is 0, which puts the camera on its own in world space. If a parent is supplied, the camera will be created at its position; otherwise, it will be placed at world position 0, 0, 0.  
By default, the camera draws to a viewport corresponding to the whole graphics window; clears its view area to black; renders to the graphics buffer and not to the stencil buffer, does not use fog, has a FOV of 90 degrees, and a range of 1-1000.  

#### <span id="camerarange" />CameraRange ####
`CameraRange(handler, near#, far#)`  
**Parameters:** The camera; its desired near range; its desired far range.  
**Return value:** None.  
**Description:** This function sets the near and far clip planes for a camera. Only entities that fall between these two planes will be drawn.  
To help the Z-buffer provide the best possible range of depth values for your scene, this range should ideally be the smallest range possible that can completely cover the entire scene. The Z-buffer is also biased towards objects nearer to the camera, so the near clip plane should be as far from the camera as you can get away with pushing it, in order to make that nearby high-detail area available to as many objects as possible.  
Cameras are created with the default range values of 1.0, 1000.0, which is "good enough" for most generic game scenes.

#### <span id="camerafieldofview" />CameraFieldOfView ####
`CameraFieldOfView(handler, angle#)`  
**Parameters:** The camera; its desired field of view.  
**Return value:** None.  
**Description:** This function sets the field of view for a camera, measured in degrees. The field of view is the angle between the implicit "sides" of the frustum that describes a camera's field of view. Cameras are created with a default FOV of 90 degrees, which is "good enough" for most generic uses. Many triple-A FPS games have a FOV lower than that, sometimes as low as 75 degrees, to give a sense of being closer in to the action; however tight FOVs are often unpopular and sometimes blamed for motion sickness, as well as wasteful on a wide screen.  
Third-person games will often have a much wider FOV, in order to get more stuff on screen, and to reduce the amount of "fish-eye" distortion.  

#### <span id="cameradrawmode" />CameraDrawMode ####
`CameraDrawMode(handler, mode)`  
**Parameters:** The camera; its desired drawing mode.  
**Return value:** None.  
**Description:** This function sets the drawing mode for a camera. There are four valid argument values for this function:  
`BOGL_CAM_OFF`: the camera will not render anything. This is useful when some views need to be disabled for some reason.  
`BOGL_CAM_PERSPECTIVE`: the camera will render to the graphics buffer in "perspective" mode (objects get smaller as they get further away). This is the default drawing mode.  
`BOGL_CAM_ORTHO`: the camera will render to the graphics buffer in orthographic mode. This is useful for things like isometric or strategy games.  
`BOGL_CAM_STENCIL`: the camera will render to the stencil buffer instead of the graphics buffer. Stencil mode always uses the perspective projection.  
It is also possible to disable a camera by hiding it (`ShowEntity cam, False`). Which to use is mostly a matter of preference, although hiding an entity may also have other effects.  

#### <span id="cameraclsmode" />CameraClsMode ####
`CameraClsMode(handler, mode)`  
**Parameters:** The camera; a bit array of its desired clear mode flags.  
**Return value:** None.  
**Description:** This function sets the clear mode for a camera. When a camera renders a scene, it first clears its viewport to an empty colour, but only if it has been flagged to do so. Since a camera (effectively) renders to multiple buffers, some or all of the buffers can be disabled or enabled for clearing.  
The `mode` argument accepts an `Or`-ed bit array of the flags to combine for the clear mode, which are:
`BOGL_CAM_CLRCOL`: setting this flag clears the colour buffer. Anything previously rendered in the view area will not be visible. This is the visible graphics buffer.  
`BOGL_CAM_CLRZ`: setting this flag clears the Z-buffer. Objects previously rendered may appear "in front of" new objects and stop them rendering. Best not to play with this, as it gives weird results.  
`BOGL_CAM_CLRSTEN`: setting this flag clears the stencil buffer.  
By default, cameras set to draw normally clear the colour buffer and Z-buffer so that they can render a 3D scene as expected, and cameras set to draw to the stencil buffer clear that instead.  
Normally the only time you would change this flag is if you want to render a 3D object over a 2D background, in which case you would use just `BOGL_CAM_CLRCOL` on its own.  

#### <span id="cameraclscolor" />CameraClsColor ####
`CameraClsColor(handler, red, green, blue)`  
**Parameters:** The camera; the desired clear colour in RGB integer (0-255) format.  
**Return value:** None.  
**Description:** This function sets the clear colour for a camera. This is only relevant if the camera is set to clear the colour buffer before drawing. If so, the camera's viewport will be filled with a flat rect in this colour before any 3D content is drawn.  

#### <span id="camerafogmode" />CameraFogMode ####
`CameraFogMode(handler, mode[, near#, far#])`  
**Parameters:** The camera; its desired fog mode; its desired near and far fog ranges.  
**Return value:** None.  
**Description:** This function sets a camera's fog mode and ranges. Entities nearer than the `near` fog range will be drawn normally by the camera; entities further away than the camera's `far` fog range will not be drawn at all; and entities somewhere between the two will be partially faded out into the fog (the fog effect actually applies to the individual pixels of a rendered entity, not the entity as a whole; a large object may render with some pixels solid and some completely invisible). By default, fog is disabled, in which case the ranges have no effect.  
The fog parameter has four values, which affect the formula used to determine how quickly objects fade into the fog:  
0: the camera does not fade objects. The fog `near` and `far` values are ignored. This is the default setting.  
1: objects fade according to a simple linear interpolation between the two ranges.  
2: objects fade according to the formula `e^-(distance)`.  
3: objects fade according to the formula `e^-(distance)^2`.  
As a guideline, the lower parameters (such as not using fog at all, obviously) should provide better performance, while the higher parameters should provide a better looking result. Experiment to see what works best.  
If the fog's `far` range is greater than the camera's absolute range, objects will get clipped in the middle of the fog. Since the main use of fog is to prevent objects from being noticeably clipped, this is a situation you should try to avoid.  

#### <span id="camerafogcolor" />CameraFogColor ####
`CameraFogColor(handler, red, green, blue[, alpha#])`  
**Parameters:** The camera; the colour of the fog in integer (0-255) RGB format.  
**Return value:** None.  
**Description:** This function sets the colour of the fog that objects will disappear into at long ranges. The default colour is **black** (`0, 0, 0`); to get something that actually does look like fog, try setting it to `128, 128, 128`.  

#### <span id="cameraviewport" />CameraViewport ####
`CameraViewport(handler, x, y, width, height)`  
**Parameters:** The camera; the position and size of its desired viewport.  
**Return value:** None.  
**Description:** This function sets the position and dimensions of a camera viewport, within the graphics window. A camera will only draw to the area within its viewport; the scene it draws will be scaled and moved to fit within the viewport (using the width to set the scale, and clipping or extending the top and bottom).  
One fairly common example use for this is to take two cameras, and set their viewports to the top and bottom halves of the screen respectively, to create a split-screen racing game. Another use might be to limit the viewport of a camera to the size of a texture, so that the scene it renders can then be efficiently copied directly to the texture as the output of some kind of ingame display.  

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
`PositionEntity(handler, x#, y#, z#[, absolute])`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="moveentity" />MoveEntity ####
`MoveEntity(handler, x#, y#, z#)`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="rotateentity" />RotateEntity ####
`RotateEntity(handler, x#, y#, z#[, absolute])`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="turnentity" />TurnEntity ####
`TurnEntity(handler, x#, y#, z#)`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="pointentity" />PointEntity ####
`PointEntity(handler, x#, y#, z#[, roll#])`  
**Parameters:**  
**Return value:**  
**Description:**  

#### <span id="scaleentity" />ScaleEntity ####
`ScaleEntity(handler, x#, y#, z#[, absolute])`  
**Parameters:**  
**Return value:**  
**Description:**  

### <span id="entity position" />Entity position ###
#### <span id="entityx" />EntityX ####
`EntityX#(handler[, absolute])`  
**Parameters:** The entity; whether to use absolute or local coordinates.  
**Return value:** The entity's X (left and right) position.  
**Description:** This function retrieves the entity's position along the X axis, which points from left to right.  
If the optional `absolute` parameter is true, then the returned value refers to the global X axis; otherwise, it refers to the entity's position relative to its parent. The default value is false.  

#### <span id="entityy" />EntityY ####
`EntityY#(handler[, absolute])`  
**Parameters:** The entity; whether to use absolute or local coordinates.  
**Return value:** The entity's Y (up and down) position.  
**Description:** This function retrieves the entity's position along the Y axis, which points from down to up.  
If the optional `absolute` parameter is true, then the returned value refers to the global Y axis; otherwise, it refers to the entity's position relative to its parent. The default value is false.  

#### <span id="entityz" />EntityZ ####
`EntityZ#(handler[, absolute])`  
**Parameters:** The entity; whether to use absolute or local coordinates.  
**Return value:** The entity's Z (forward and backward) position.  
**Description:** This function retrieves the entity's position along the Z axis, which points **from front to back** (this is different from Blitz3D and miniB3D).  
If the optional `absolute` parameter is true, then the returned value refers to the global Z axis; otherwise, it refers to the entity's position relative to its parent. The default value is false.  

#### <span id="entityxangle" />EntityXAngle ####
`EntityXAngle#(handler[, absolute])`  
**Parameters:** The entity; whether to use absolute or local orientation.  
**Return value:** The entity's X rotation (pitch).  
**Description:** This function retrieves the entity's anticlockwise rotation around the X axis (which points to the right), measured in degrees.  
If the optional `absolute` parameter is true, the returned value is the rotation relative to the global X axis; otherwise it refers to the entity's local rotation in its parent's space. The default value is false.

#### <span id="entityyangle" />EntityYAngle ####
`EntityYAngle#(handler[, absolute])`  
**Parameters:** The entity; whether to use absolute or local orientation.  
**Return value:** The entity's Y rotation (yaw).  
**Description:** This function retrieves the entity's anticlockwise rotation around the Y axis (which points upwards), measured in degrees.  
If the optional `absolute` parameter is true, the returned value is the rotation relative to the global Y axis; otherwise it refers to the entity's local rotation in its parent's space. The default value is false.  

#### <span id="entityzangle" />EntityZAngle ####
`EntityZAngle#(handler[, absolute])`  
**Parameters:** The entity; whether to use absolute or local orientation.  
**Return value:** The entity's Z rotation (roll).  
**Description:** This function retrieves the entity's anticlockwise rotation around the Z axis (which points backwards/out of the screen), measured in degrees.  
If the optional `absolute` parameter is true, the returned value is the rotation relative to the global Z axis; otherwise it refers to the entity's local rotation in its parent's space. The default value is false.  

### <span id="entity appearance" />Entity appearance ###
#### <span id="paintentity" />PaintEntity ####
`PaintEntity(handler, red, green, blue)`  
**Parameters:** The entity; its desired colour in integer RGB (0-255) format.  
**Return value:** None.  
**Description:** This function sets the base colour of a mesh. If the mesh is untextured, it will just appear in that colour; if the mesh is textured, the texture will blend (using the multiply algorithm) with the underlying colour applied by this function. The default colour of all meshes is `255, 255, 255`. This function is only valid for meshes.

#### <span id="entityalpha" />EntityAlpha ####
`EntityAlpha(handler, alpha#)`  
**Parameters:** The entity; its desired alpha.  
**Return value:** None.  
**Description:** This function sets the transparency of an entity. An alpha value of 1.0 makes an entity completely solid; an alpha value of 0.0 makes an entity completely invisible; and an alpha somewhere in between makes an entity semitransparent and blended with the background. Alpha can cause some rendering errors for complicated, "solid" objects, and is best used only on quads or very simple shapes like cubes. Alpha values of 1 or 0 are very efficient as they do not incur an alpha test.  

#### <span id="entityfx" />EntityFX ####
`EntityFX(handler, flags)`  
**Parameters:** The entity; a bit array containing the FX flags to apply.  
**Return value:** None.  
**Description:** This function applies various effect settings ("FX") to an entity. These are only meaningful for meshes as they determine how an object is drawn to the screen. The `flags` argument accepts an `Or`-ed bit array of the different FX to apply to a mesh, which are:
`BOGL_FX_FULLBRIGHT`: ignore lights and ambience; always draw an object as fully illuminated from all angles.  
`BOGL_FX_FLATSHADED`: shade each polygon according to its surface normal instead of smoothing the shadow from edge to edge. Creates a very sharp-edged look with flat faces.  
`BOGL_FX_NOFOG`: ignore the camera's fog settings and never allow the object to fade into the background fog.  
`BOGL_FX_ADDBLEND`: use additive blending with the background when drawing an entity. Additive blending means the pixels values will be added (and clamped at 255), so e.g. a red entity in front of a blue entity will produce a pink effect.  
`BOGL_FX_MULBLEND`: use multiplicative blending with the background when drawing an entity. Multiplicative blending means the pixels will be multiplied, treating them as fractions (i.e. p / 255.0); anything multiplied with white will stay the same, anything multiplied with black will become black. In general this will darken a scene.  
`BOGL_FX_NOCULL`: disable backface culling for an entity. Normally one doesn't draw the "inside" of a mesh, because it's closed and would be a waste of effort, but meshes that aren't closed might need this to avoid being seen through from some angles.  
`BOGL_FX_STENCIL_KEEP`: this mesh should have no effect on stencil operations.  
`BOGL_FX_STENCIL_INCR`: this mesh should increment the stencil buffer value when drawn on a stencil camera.  
`BOGL_FX_STENCIL_DECR`: this mesh should decrement the stencil buffer value when drawn on a stencil camera.  
`BOGL_FX_STENCIL_BOTH`: this mesh should increment the stencil buffer when its front faces are drawn on a stencil camera, and decrement the stencil buffer when its back faces are drawn (this is useful for things like volumetric shadows).  
It may be that using additive or multiplicative blending causes unexpected results; if so, try rendering objects using these effects in a separate, second pass.  

#### <span id="entitytexture" />EntityTexture ####
`EntityTexture(handler, texture)`  
**Parameters:** The entity; the texture to apply.  
**Return value:** None.  
**Description:** This function applies a texture to an entity. Any texture previously on the entity will be removed and replaced by the passed texture. You can also pass a value of 0 to simply remove the current texture from an entity.  

#### <span id="showentity" />ShowEntity ####
`ShowEntity(handler, state)`  
**Parameters:** The entity; its desired visibility state.  
**Return value:** None.  
**Description:** This entity shows or hides an entity *and all of its children*. A hidden entity, or the child of a hidden entity, does not participate in "the world" in any observable way: a hidden mesh will not be rendered as part of a scene; a hidden light will not illuminate objects; and a hidden camera will not draw to the screen (note that while this works equally well to disable a camera, because it also hides any children, it is not quite the same as using `CameraDrawMode` to do so). Since pivot entities do not do anything, showing or hiding them makes no difference except by the effect on their children.  
A hidden entity may still be moved around, rotated, etc. and therefore can have a few uses as a "geometric template" or other kind of armature, as well as simply being quickly removed from a scene.  

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


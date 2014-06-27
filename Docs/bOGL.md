
# bOGL-2: A 3D engine for Blitz Basic #

* [Introduction](#introduction)
 * [Installation](#installation)
 * [Addon modules](#addon-modules)
 * [Differences from Blitz3D](#differences-from-blitz3d)
     * [Reduced feature set](#reduced-feature-set)
     * [Differences in existing features](#differences-in-existing-features)
     * [New features not in Blitz3D](#new-features-not-in-blitz3d)
 * [Future directions](#future-directions)
* [Command reference / API](#command-reference--api)
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
 * [Entity movement](#entity-movement)
     * [PositionEntity](#positionentity)
     * [MoveEntity](#moveentity)
     * [RotateEntity](#rotateentity)
     * [TurnEntity](#turnentity)
     * [PointEntity](#pointentity)
     * [ScaleEntity](#scaleentity)
 * [Entity position](#entity-position)
     * [EntityX](#entityx)
     * [EntityY](#entityy)
     * [EntityZ](#entityz)
     * [EntityXAngle](#entityxangle)
     * [EntityYAngle](#entityyangle)
     * [EntityZAngle](#entityzangle)
 * [Entity appearance](#entity-appearance)
     * [PaintEntity](#paintentity)
     * [EntityAlpha](#entityalpha)
     * [EntityFX](#entityfx)
     * [EntityTexture](#entitytexture)
     * [ShowEntity](#showentity)
 * [Entity status](#entity-status)
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


## <span id="intro"/>Introduction ##

bOGL is an OpenGL based open source 3D engine for Blitz Basic, primarily intended for use with BlitzPlus. bOGL also works with Blitz3D, if you remove the 3D canvas functions.

bOGL provides a command set similar to that of the Blitz3D and miniB3D engines. It is not intended to be directly compatible or a "drop in" replacement for Blitz3D, and code will not work unmodified, but the command set is similar enough that a Blitz3D or miniB3D veteran should have little difficulty getting up to speed very quickly. The engine is not intended to be particularly featureful or high-performance, but it should be very portable thanks to its OpenGL 1.1 foundation, and provides enough features for small but complete 3D games and apps.

bOGL is intended to allow for a modular addon structure. Official modules are provided for 2D drawing, MD2 animation, loading and saving static meshes, and loading and animating skinned meshes. Others may follow in the course of time, and adding a third-party module is designed to be a simple and painless process. Understanding a module is not required to understand the operation of the bOGL engine core: the engine is structured like a tree, so that nothing close to the "core" depends on anything further out; you can completely understand the engine's structure by reading one file and function at a time, and not having to constantly cross-reference across multiple classes or modules. The core engine is under 1500 lines, and intended to be understood in a single sitting by an interested reader.

bOGL-2 is an extended and heavily-rewritten version of [the original bOGL by Andres Pajo][bogl], built on top of Peter Schuetz's OpenGL Direct library. bOGL-2 and all of its addons are made available under [the MIT licence][mit] (see also the LICENSE file in the main distribution), which means you can use it in any commercial and non-commercial projects at no cost, as long as the authors are given a line of acknowledgement somewhere in your documentation or end credits.

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

You may also want to copy its `.decls` file (if any) to your main `userlibs` folder, to take advantage of any syntax highlighting options. The official addons do not require `.decls` files to work, however.

bOGL provides official addon modules for those features that we can safely assume you expect as basics from a 3D engine, but that aren't required for the basic 3D engine to work, both as useful examples for the extension process and to keep the core small:

* **[MeshLoader](MeshLoader.md)** provides commands for loading and saving 3D meshes. You can load meshes from OBJ and the custom BO3D format, and save static meshes to OBJ format. BO3D meshes can be loaded out of Blitz banks as well as from files, making it easy to pack assets together. BO3D meshes support bones, and the entity hierarchy, and can be easily deformed or animated.
 * BO3D is a special custom mesh format inspired by Blitz3D's B3D format and optimised for bOGL. It isn't supported by any editors yet, but you can convert most B3D files to BO3D format using the file converter utility (`Tools/bo3D_conv.bb`: this is a standalone program, not something to add to your project). See `bo3d_spec.txt` for complete internal details of the format.
* **[Animation](Animation.md)** provides commands for loading animation sequences from BO3D files and applying them to hierarchical entities. You can start, pause, stop and reverse animations, set the specific frame, and get information about an animation's running time. It does not currently provide any functions to dynamically create new animation sequences.
 * This depends on the BO3D model format to load animation data (and to animate BO3D meshes), so it is a second-tier module and depends on `MeshLoader`.
* **[MD2](MD2.md)** provides commands for loading MD2 meshes and their animation sequences from Quake II-compatible MD2 files. This module provides both loading and animation commands. You can load MD2 models both from files and from Blitz banks, and multiple MD2s can be loaded into a single compound mesh if this will help with performance (it probably won't in most cases, but the option is there for large numbers of simple meshes).
* **[Draw2D](Draw2D.md)** provides commands for 2D graphics drawing, including text, images, and graphics primitives like lines and boxes. `Draw2D` uses OpenGL to provide hardware-accelerated drawing in the same context as bOGL's 3D graphics, giving access to very fast rotation and transparency with 2D graphics. `Draw2D` is loosely based on the `Draw3D` library for Blitz3D, and is also similar (in terms of what it does) to `Max2D` for BlitzMax.
* **[3DAction](3DAction.md)** provides commands for declaratively creating "actions" and firing them on entities in an event-based way. This centralizes your movement code, and allows you to not bother writing tedious boilerplate like simple `MoveEntity` commands. You can set entities to follow other entities, command them to move by a vector or to a position over the next *N* frames, receive updates when actions are completed, and so on, so that your game can be structured as an elegant event/trigger system.

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
* Collision detection is extremely limited, sphere->cube *only* (this means you may need two colliders, collder and "collidee", for some objects). Since B3D was written, 3D physics libraries have become far more powerful anyway.

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
* Entities carry a "user data array", which lets addons associate additional data with entities easily. An addon can request data slots using `RegisterEntityUserDataSlot` (it should do this once in its `init` procedure), which will register a slot in the array for that addon and return its index, and then access the data in its private slot with the `SetEntityUserData` and `GetEntityUserData` functions. With this mechanism, different addons can extend the base entity structure however they like without conflicting. Addons that extend entities can also register `onCopy` and `onFree` listener banks, which will be notified when an entity has been copied or deleted, so that the addon can clean up or reallocate resources when it next updates.
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
**Parameters:** An optional parent entity.  
**Return value:** The newly-created camera.  
**Description:** This function creates a new camera entity. It can optionally be assigned a parent on creation using the `parent` parameter. The default value for this parameter is 0, which puts the camera on its own in world space. If a parent is supplied, the camera will be created at its position; otherwise, it will be placed at world position 0, 0, 0. You must have at least one camera in a 3D scene in order to render anything.  
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
`CreateLight(red, green, blue, flag[, parent])`  
**Parameters:** The colour for the light in integer RGB (0-255) format; the kind of light; the parent entity.  
**Return value:** The newly-created light.  
**Description:** This function creates a new light entity. The colour of the light will be the colour described by the integer RGB values passed in the first three parameters (e.g. passing `0, 255, 0` will create a bright green light).  
The `flag` parameter determines the kind of light to create. Valid argument values for this parameter are:  
`BOGL_LIGHT_PT`: a point light. This will illuminate a sphere of space around the light entity in the direction pointing away from the source.  
`BOGL_LIGHT_DIR`: a directional light. This will illuminate all space in the scene with light moving in one parallel direction; where the actual light entity is doesn't matter a whole lot with this type of light because it always has infinite range.  
`BOGL_LIGHT_AMB`: an ambient light. Everything within range will be illuminated with a constant, non-directional colour.  
Note that while lights will not illuminate polygons facing away from them, they will not actually cast shadows; for that you need a shadow system (building one should be easy with `RenderStencil`).  
The light can optionally be assigned a parent on creation using the `parent` parameter. The default value for this parameter is 0, which puts the light on its own in world space. If a parent is supplied, the light will be created at its position; otherwise, it will be placed at world position 0, 0, 0.  
By default the newly-created light will have an infinite range (directional lights always have infinite range).  
There is a maximum number of hardware lights that can be supported on any given system using the fixed-function pipeline: this number is usually 8, although it may be less. For scenes with a large number of lights, a special management system may be necessary to swap out which lights are actually in use. Creating more than the maximum number of hardware lights will simply result in some of them not working.  

#### <span id="lightrange" />LightRange ####
`LightRange(handler, range#)`  
**Parameters:** The light; its desired range.  
**Return value:** None.  
**Description:** This function sets the range of a light. The effect of a light falls of in a linear fashion from its position; the range is the distance from the light at which the light stops having any effect at all (so if you want a "visible pool of light" of a given size, the range should probably be bigger than that size).  
Note that directional lights are not affected by their range value.  

### <span id="pivot" />Pivot ###
#### <span id="createpivot" />CreatePivot ####
`CreatePivot([parent])`  
**Parameters:** An optional parent entity.  
**Return value:** The newly-created pivot.  
**Description:** This function creates a pivot entity. A pivot is a special type of entity that doesn't do anything by itself, but serves only to be moved around and usually to be the parent entity of meshes or cameras. It can be moved around, hidden, rotated, scaled etc. like any other entity, but will never appear on screen as it has nothing to render. A pivot is a good choice for the "root" of a scene or group, to move the whole thing around, or to show and hide several entities at once.  
The pivot can optionally be assigned a parent on creation using the `parent` parameter. The default value for this parameter is 0, which puts the pivot on its own in world space. If a parent is supplied, the pivot will be created at its position; otherwise, it will be placed at world position 0, 0, 0.  

### <span id="mesh" />Mesh ###
#### <span id="createmesh" />CreateMesh ####
`CreateMesh([parent])`  
**Parameters:** An optional parent entity.  
**Return value:** The newly-created mesh.  
**Description:** This function creates a new empty mesh. The mesh will not be rendered until you add some polygons and vertices to it. This is a base primitive form that you can use to dynamically create complex meshes in code, if you aren't just loading them from a file (or to write a function that loads meshes from a file).  
The mesh can optionally be assigned a parent on creation using the `parent` parameter. The default value for this parameter is 0, which puts the mesh on its own in world space. If a parent is supplied, the mesh will be created at its position; otherwise, it will be placed at world position 0, 0, 0.  

#### <span id="addvertex" />AddVertex ####
`AddVertex(mesh, x#, y#, z#[, u#, v#])`  
**Parameters:** The mesh; the position of the new vertex; the texture position of the new vertex.  
**Return value:** The index of the new vertex.  
**Description:** This function adds a vertex to a mesh at the given coordinates. The spatial coordinates place the vertex relative to the mesh's origin. The optional `u` and `v` parameters set the vertex's texture coordinate, which are values between 0 and 1 determining how far "across" the texture in the given direction the vertex should fall. If these are omitted, the default values are 0. The returned value is the index of the vertex, which can be used to add it to triangles or adjust its settings.  
A new vertex does not have a valid normal. Set it with `VertexNormal` or the vertex will not be illuminated properly (unless the scene is fullbright).  
Meshes can support a machine-specific maximum number of vertices. On most machines, this value is either 65536, or half of that. Meshes that use more than the maximum number of vertices will usually render with holes in them as vertices are randomly dropped.  

#### <span id="addtriangle" />AddTriangle ####
`AddTriangle(mesh, v0, v1, v2)`  
**Parameters:** The mesh; the vertices to use to build the triangle.  
**Return value:** The index of the new triangle.  
**Description:** This function adds a triangle to a mesh. The triangle will be made from the passed vertices, in the order they are specified. The order is important because it determines which way a triangle "faces": a triangle "faces" towards the direction where the vertices appear to be arranged in an anti-clockwise order. Keeping this in mind is important when building a mesh or it may end up with "holes" or inverted surfaces, because a triangle is not rendered when viewed from the back (unless the no-backface-culling FX is applied to the mesh).  

#### <span id="counttriangles" />CountTriangles ####
`CountTriangles(mesh)`  
**Parameters:** The mesh.  
**Return value:** The number of triangles in the mesh.  
**Description:** This function just returns the total number of triangles in a mesh.  

#### <span id="countvertices" />CountVertices ####
`CountVertices(mesh)`  
**Parameters:** The mesh.  
**Return value:** The number of vertices in the mesh.  
**Description:** This function just returns the total number of vertices in a mesh. Vertices that are not used by any triangles (and therefore invisible when the mesh renders) are still counted.  

#### <span id="trianglevertex" />TriangleVertex ####
`TriangleVertex(mesh, tri, vert)`  
**Parameters:** The mesh; the triangle; the vertex of the triangle.  
**Return value:** The vertex's real index.  
**Description:** This function takes a triangle and an index representing one of its points, and returns the actual index that refers to that vertex within the mesh as a whole. This is a way to find a specific vertex after searching by triangle. Only the values 0, 1 and 2 are valid argument values for `vert`.  

#### <span id="vertexcoords" />VertexCoords ####
`VertexCoords(mesh, v, x#, y#, z#)`  
**Parameters:** The mesh; the vertex index; its new position.  
**Return value:** None.  
**Description:** This function lets you set a new position for a given vertex. This might be useful for implementing some kind of deformation-based animation system, or perhaps for deforming a terrain or the surface of some water. It's not tremendously efficient and if used on a large scale, the functionality should really be rolled together into a low-level addon instead, but it should be good for non-performance-critical work and prototyping on a small scale.  

#### <span id="vertextexcoords" />VertexTexCoords ####
`VertexTexCoords(mesh, v, u#, v#)`  
**Parameters:** The mesh; the vertex index; its new texture position.  
**Return value:** None.  
**Description:** This function lets you set a new texture position for a given vertex. You might use this to glide triangles over a texture to create a flowing animated surface effect. Remember that texture coordinates are *proportional* to the texture size, not absolute: they range from 0 to 1.  

#### <span id="vertexnormal" />VertexNormal ####
`VertexNormal(mesh, v, nx#, ny#, nz#)`  
**Parameters:** The mesh; the vertex index; its new normal.  
**Return value:** None.  
**Description:** The normal of a vertex determines how triangles formed from it will be illuminated; in smooth-shading mode it is this, not the actual orientation of the triangle, which controls what angle the lights think the surface is actually at relative to the light source. (Vertex normals are not used for flat-shaded lighting.)  
When a new vertex is first created it does not have a valid normal. You must set a normal with this command if you want to use smooth-shaded hardware lighting. Invalid normals produce unpredictable, usually ugly, results.  

#### <span id="vertexcolor" />VertexColor ####
`VertexColor(mesh, v, r, g, b)`  
**Parameters:** The mesh; the vertex index; its new colour represented in integer RGB (0-255) format.  
**Return value:** None.  
**Description:** This function sets a vertex's colour. Note that meshes which use vertex colours are less efficient than meshes that just rely on texturing or flat entity colours. Applying a colour to any vertex enables vertex colouring for the entire mesh; note that there is no way to turn it off again short of destroying and rebuilding the whole structure.  

#### <span id="vertexx" />VertexX ####
`VertexX#(mesh, v)`  
**Parameters:** The mesh; the vertex index.  
**Return value:** The vertex X position.  
**Description:** This function just returns the vertex's X position relative to the host mesh's origin.  

#### <span id="vertexy" />VertexY ####
`VertexY#(mesh, v)`  
**Parameters:** The mesh; the vertex index.  
**Return value:** The vertex Y position.  
**Description:** This function just returns the vertex's Y position relative to the host mesh's origin.  

#### <span id="vertexz" />VertexZ ####
`VertexZ#(mesh, v)`  
**Parameters:** The mesh; the vertex index.  
**Return value:** The vertex Z position.  
**Description:** This function just returns the vertex's Z position relative to the host mesh's origin.  

#### <span id="vertexu" />VertexU ####
`VertexU#(mesh, v)`  
**Parameters:** The mesh; the vertex index.  
**Return value:** The vertex U coordinate.  
**Description:** This function just returns the vertex's texture U coordinate, which should be a value between 0 and 1.  

#### <span id="vertexv" />VertexV ####
`VertexV#(mesh, v)`  
**Parameters:** The mesh; the vertex index.  
**Return value:** The vertex V coordinate.  
**Description:** This function just returns the vertex's texture V coordinate, which should be a value between 0 and 1.  

#### <span id="createcube" />CreateCube ####
`CreateCube([parent])`  
**Parameters:** An optional parent entity.  
**Return value:** The newly-created cube.  
**Description:** This function creates a perfect cube. Cubes are mainly useful as placeholder objects (although by all means use them in a game if it simply doesn't need complicated art). The cube's size is 2x2x2, it is white, and it has unwelded sides for sharp lighting and separate texturing. If a texture is applied to a cube it will appear on all six faces in full.  
The cube can optionally be assigned a parent on creation using the `parent` parameter. The default value for this parameter is 0, which puts the cube on its own in world space. If a parent is supplied, the cube will be created at its position; otherwise, it will be placed at world position 0, 0, 0.  

#### <span id="createsprite" />CreateSprite ####
`CreateSprite([parent])`  
**Parameters:** An optional parent entity.  
**Return value:** The newly-created sprite.  
**Description:** This function creates a one-sided quad. It is not actually a sprite as other engines describe them, as it doesn't automatically face the camera: it's just a dumb quad mesh. If you want to use a lot of quads or sprites in your project, you would likely be better off investigating a dedicated single-surface particle engine.  
The sprite can optionally be assigned a parent on creation using the `parent` parameter. The default value for this parameter is 0, which puts the sprite on its own in world space. If a parent is supplied, the sprite will be created at its position; otherwise, it will be placed at world position 0, 0, 0.  

#### <span id="loadterrain" />LoadTerrain ####
`LoadTerrain(terrain$[, parent])`  
**Parameters:** An optional parent entity.  
**Return value:** The newly-loaded terrain.  
**Description:** This function loads a terrain mesh from a heightmap image file. Unlike Blitz3D's advanced LOD terrains, this is just a naive heightfield with one static vertex per pixel in the input image. Because of the relatively low maximum vertex count, this is unlikely to work well with images larger than 256x256. As with the other built-in mesh features, this is unlikely to be enough to power an advanced game on its own and is mainly a prototyping feature.  
The terrain can optionally be assigned a parent on creation using the `parent` parameter. The default value for this parameter is 0, which puts the terrain on its own in world space. If a parent is supplied, the terrain will be created at its position; otherwise, it will be placed at world position 0, 0, 0.  

#### <span id="flippolygons" />FlipPolygons ####
`FlipPolygons(handler)`  
**Parameters:** The mesh.  
**Return value:** None.  
**Description:** This function "flips" the polygons in a mesh; after calling this function, the mesh will appear to be inverted (e.g. if you use this on a cube, it will appear as the inside of a hollow cube instead). This function works by going through and reversing the "spin" of every triangle (so note that `TriangleVertex` may not produce expected results after calling this). It does not touch vertex normals, and the entity will still be lit *exactly* as it was before if using smooth-shading (which may look strange, and wrong); lighting will be flipped as well if using flat-shading because flat-shading relies on triangle normals. There will be no observable effect if this is used on a smooth-shaded mesh that has disabled backface culling.  

### <span id="submesh" />Submesh ###
#### <span id="rotatesubmesh" />RotateSubMesh ####
`RotateSubMesh(handler, vf, vt, rx#, ry#, rz#, cx#, cy#, cz#)`  
**Parameters:** The host mesh; the vertex range to rotate; the Euler rotation in degrees; the centre of rotation relative to the mesh.  
**Return value:** None.  
**Description:** This function allows you to rotate a contiguous range of vertices around a specified point in a single operation. Doing this is much, much faster than looping over the vertices and adjusting them with `VertexCoords`, and saves you having to work out a rotated vector for each vertex. This function might be useful for e.g. an animation system, where bones control a group of vertices. The specified centre point is relative to the mesh origin.  

#### <span id="quatrotatesubmesh" />QuatRotateSubMesh ####
`QuatRotateSubMesh(handler, vf, vt, q#[3], cx#, cy#, cz#)`  
**Parameters:** The host mesh; the vertex range to rotate; the quaternion rotation; the centre of rotation relative to the mesh.  
**Return value:** None.  
**Description:** This function allows you to rotate a contiguous range of vertices around a specified point in a single operation. Doing this is much, much faster than looping over the vertices and adjusting them with `VertexCoords`, and saves you having to work out a rotated vector for each vertex. This function might be useful for e.g. an animation system, where bones control a group of vertices. The specified centre point is relative to the mesh origin.  
The difference from `RotateSubMesh` is that this function allows you to pass the rotation defined as a quaternion.  

#### <span id="translatesubmesh" />TranslateSubMesh ####
`TranslateSubMesh(handler, vf, vt, tx#, ty#, tz#)`  
**Parameters:** The host mesh; the vertex range to translate; the translation to apply.  
**Return value:** None.  
**Description:** This function allows you to translate a contiguous range of vertices in a single operation. Doing this is much, much faster than looping over the vertices and adjusting them with `VertexCoords`. This function might be useful for e.g. an animation system, where bones control a group of vertices.  

#### <span id="scalesubmesh" />ScaleSubMesh ####
`ScaleSubMesh(handler, vf, vt, sx#, sy#, sz#, cx#, cy#, cz#)`  
**Parameters:** The host mesh; the vertex range to scale; the scale factor; the centre to scale from relative to the mesh.  
**Return value:** None.  
**Description:** This function allows you to scale a contiguous range of vertices away from a specified point in a single operation. Doing this is much, much faster than looping over the vertices and adjusting them with `VertexCoords`, and saves you having to work out a scaled vector for each vertex. This function might be useful for e.g. an animation system, where bones control a group of vertices. The specified centre point is relative to the mesh origin.  

### <span id="entity movement" />Entity movement ###
#### <span id="positionentity" />PositionEntity ####
`PositionEntity(handler, x#, y#, z#[, absolute])`  
**Parameters:** The entity; where to put it; whether to use absolute or local coordinates.  
**Return value:** None.  
**Description:** This function puts an entity at a location within its containing coordinate system.  
If the optional `absolute` parameter is true, the entity will be positioned at the given coordinates in global space; otherwise, it will be positioned at the given coordinates relative to its parent (which is the same as global space if it has no parent). Its own rotation and current position are not taken into account either way.  
The difference between using local and global coordinates is a bit like the difference between telling someone to go and stand in their kitchen, vs. telling them to go and stand in the centre of London. Neither of those instructions cares about where the person is standing or what way they are facing *right now*, but the first one (local space) still puts them somewhere in terms of their personal context, rather than an absolute position in the whole world.  

#### <span id="moveentity" />MoveEntity ####
`MoveEntity(handler, x#, y#, z#)`  
**Parameters:** The entity; the vector along which to move it.  
**Return value:** None.  
**Description:** This function moves an entity along a given vector.  
Movement using this command is always relative to the entity's current position and rotation. This is equivalent to telling someone to e.g. take two steps to their left: where they end up depends on where they were standing and what direction they were facing before you gave the instruction.  
So the command `MoveEntity ent, 0, 0, -1` will always move the entity `ent` one step forward from its perspective.

#### <span id="rotateentity" />RotateEntity ####
`RotateEntity(handler, x#, y#, z#[, absolute])`  
**Parameters:** The entity; the rotation around the X, Y and Z axes to set; whether to use absolute or local coordinates.  
**Return value:** None.  
**Description:** This function sets an entity's rotation about the X, Y and Z axes, measured as an Euler angle vector in degrees.  
If the optional `absolute` parameter is true, the entity's rotation will be set relative to the global X, Y and Z axes; otherwise, its rotation will be set relative to the current orientation of its parent entity (which is the same as using the global axes if it has no parent). Its own current orientation is not considered either way.  
The difference between using local and global coordinates for this is a bit like the difference between telling someone to turn to face a given wall of their house, and telling them to face north. Neither of those instructions cares about where they're facing *right now*, but the first one (local space) still puts them somewhere in terms of their personal context, rather than an absolute position in the whole world.  
If you want to add a rotation to an entity's existing orientation, you should *not* use this function (don't attempt to maintain a "current rotation" variable, it won't work properly) - use `TurnEntity` instead, so that the rotation will be computed with quaternions and not be subject to gimbal lock. (If you try to add to a stored Euler rotation to compute values to pass to `RotateEntity`, you will end up causing gimbal lock yourself, by bypassing the quaternion engine. Don't do that.)  

#### <span id="turnentity" />TurnEntity ####
`TurnEntity(handler, x#, y#, z#)`  
**Parameters:** The entity; the rotation to add to the X, Y and Z axes.  
**Return value:** None.  
**Description:** This function adds a rotation to an entity's current orientation, measured as an Euler angle in degrees of rotation around the X, Y and Z axes.  
Rotation using this command is always relative to the entity's current rotation, and will be added to it. You can make an object continuously rotate in a circle, for instance, by simply commanding `TurnEntity ent, 0, 1, 0` in your main loop; a degree will be added to the object's Y rotation every frame.  
Although the rotations passed to this function are described using Euler degree vectors, internally, the rotation is computed using quaternions and is thus not vulnerable to gimbal lock. You should therefore always use this function instead of `RotateEntity` to turn an entity relative top its current orientation.  

#### <span id="pointentity" />PointEntity ####
`PointEntity(handler, x#, y#, z#[, roll#])`  
**Parameters:** The entity; the coordinates to point it at; the roll to apply after pointing the entity.  
**Return value:** None.  
**Description:** This function points an entity at a coordinate somewhere in its local space. The optional `roll` parameter determines what roll the entity should have after being oriented to point at the coordinate ("pointing" doesn't take the entity's current orientation into account, instead acting as though the entity was starting from no rotation, so all entities will be "upright" after being pointed unless otherwise specified by the `roll` parameter).  
To point an entity at coordinates originating from global space or some other coordinate system, you should first transform the coordinates into the entity's local space using `TFormPoint`.  

#### <span id="scaleentity" />ScaleEntity ####
`ScaleEntity(handler, x#, y#, z#[, absolute])`  
**Parameters:** The entity; the amount to scale in each dimension; whether to use absolute or local coordinates.  
**Return value:** None.  
**Description:** This function sets an entity's scale factor in each dimension. It *sets*, rather than multiplies, the scale of an entity, so you can always return an entity to its original scale by using `ScaleEntity ent, 1, 1, 1`. A scale of 2 will double an entity's size in a given dimension; a scale of -1 will invert it. Try to avoid using a scale of 0 as it is likely to cause singularities.  
An entity is also scaled in terms of its parent's coordinate system, so if an entity is parented to an entity of scale 2, 2, 2 and itself is set to scale 1, 1, 1, it will manifest as having a scale of 2, 2, 2 (its scale, multiplied by its parent's overall scale).  
Scaling a mesh will affect how it is rendered; scaling lights and cameras may have odd effects on the way they cause the world to be presented, especially using non-uniform scale values (inverting a camera's scale can have really odd effects). Scale is most useful with meshes as it's an effectively-free way to change the size of a visible object (actually moving the vertices is very slow).  
An entity *moves* in terms of its parent's scale system, which is handy because you can dynamically blow up and shrink entities without needing to think about how it will affect movement commands: it won't. This also means that hierarchical structures like character bones always maintain the correct proportions for their movements.  
If the optional `absolute` parameter is true, then the entity will be *set to* a scale that will give it the appearance of having the requested scale in global space, e.g. you could set an entity's absolute scale to 1, 1, 1 to counteract the effect of its parent's scale multiplier. (In general doing this is likely to be quite complicated and rarely necessary.)  

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
`SetEntityParent(handler, parentH)`  
**Parameters:** The entity; the entity to set as its parent.  
**Return value:** None.  
**Description:** This function sets an entity's parent. If it already had a parent, it is unhooked from the old one; if you pass 0 as the parent handle, it will be set to have no parent and exist on its own in world space.  
The "parent" system allows entities to be grouped into a tree hierarchy. Actions applied to a parent entity are applied recursively to all of its children: if you hide a parent, all of its child nodes will disappear as well; if you move a parent two spaces to the left, all of its children will move two spaces to the left; if you rotate a parent around the Y axis, all of its children will orbit around it like planets around a star. Child entities are, by and large, unaware of the movements of their parents, so you can continue to move children within a "local space" system, defined by their parent entity's position and orientation, without needing to know where they actually are in the world as a whole. The entity hierarchy is absolutely indispensable for things like character animation or complex level design (e.g. hands need to be able to move regardless of where the arms that control them are).  
Attempting to create a circular parent structure doesn't make sense, and will cause the program to crash. Don't do that.  

#### <span id="getentityparent" />GetEntityParent ####
`GetEntityParent(handler)`  
**Parameters:** The entity.  
**Return value:** The entity's parent.  
**Description:** This function simply returns which entity is the queried entity's parent. If it has no parent, the function will return 0.  

#### <span id="countchildren" />CountChildren ####
`CountChildren(handler)`  
**Parameters:** The entity.  
**Return value:** The number of immediate children of the entity.  
**Description:** This function returns the number of immediate children attached to an entity. It only counts those children that have this entity as their *direct* parent; any "grandchildren" (children of the child entities) are not included in the returned count. If you need to enumerate an entire entity tree, you can easily use this to write a recursive version.  

#### <span id="getchildentity" />GetChildEntity ####
`GetChildEntity(handler, index)`  
**Parameters:** The parent entity; the index of the desired child.  
**Return value:** The handle of the child.  
**Description:** This function retrieves a child entity from a parent using its child index. Child entities have indices in the range 0 to (`CountChildren` - 1). There is no guarantee of their ordering, so you would mainly use this in a loop (controlled by `CountChildren`) that iterates over all children in sequence.  

#### <span id="getchildbyname" />GetChildByName ####
`GetChildByName(handler, name$)`  
**Parameters:** The parent entity; the name of the desired child.  
**Return value:** The handle of the child.  
**Description:** This function searches an entity's child tree to retrieve the child with the given name. If a child with that name is not present, the function returns 0. Note that this function differs from `GetChildEntity` in that it is recursive and will search the children of its children etc., until either an entity with a matching name is found, or the tree is completely searched.  
This function works best if every component of a hierarchy has a unique name. If entities have duplicate names, it is not specified which entity will be returned first (i.e. do not rely on this behaviour being consistent between bOGL versions).  

#### <span id="setentityname" />SetEntityName ####
`SetEntityName(handler, name$)`  
**Parameters:** The entity; the name to set.  
**Return value:** None.  
**Description:** This function sets an entity's name to the passed value. Entity names are mainly useful for searching entity trees, and most importantly for providing an identifier that persists between runs of the same program (an entity's handle will change from run to run because it's totally random, but a name can be set in an editor and later be used to find that entity within the game engine). Newly-created entities have a default name of the empty string.  
Some game engines (especially those made with the original Blitz3D) also use the name string to store custom data on an entity. Thanks to user data slots, this is not necessary in bOGL (do it anyway if you like, it's not very efficient though).  

#### <span id="getentityname" />GetEntityName ####
`GetEntityName$(handler)`  
**Parameters:** The entity.  
**Return value:** The name of the entity.  
**Description:** This function just returns an entity's name.  

#### <span id="registerentityuserdataslot" />RegisterEntityUserDataSlot ####
`RegisterEntityUserDataSlot()`  
**Parameters:** None.  
**Return value:** The slot dedicated to your custom entity data.  
**Description:** This function assigns a slot in all entities' custom data vectors for any data of your choice (that can be converted to an int, anyway). This provides a modular way to extend entities with extra information (e.g. animation sequences). As long as you request the slot with this function instead of assuming it will be any specific value, your extra data will not conflict with any other data added by addon modules or other programmers.  
Ideally this function should be called once per addon or module that wants to define its own custom extensions, before the program begins creating entities (otherwise some entities will already have data vectors and not have all of the right slots...). Addons should call it in their `Init` function.  
Since the entity and the core bOGL functions have no idea what the entity user data represents, addons should define their own copy and free functions if necessary that can copy extra data and delete it when done. Take a look at the `MD2` and `Animation` addons for examples of how to share complicated data structures, and automatically cleanup redundant data.  
**Future direction:** A port of bOGL to a language supporting callbacks (BlitzMax, C, Monkey) should extend this function to accept `copy` and `free` functions for the extended data. This will make code using extended objects much more generic as the user no longer has to remember "this entity is an MD2, copy it using the special MD2 copy function" or anything like that.  

#### <span id="setentityuserdata" />SetEntityUserData ####
`SetEntityUserData(handler, slot, val[, cell])`  
**Parameters:** The entity; the slot to set; the value to store. Optionally, the cell to set.  
**Return value:** None.  
**Description:** This function sets a slot in an entity's custom data vector to the given value. Use this to update extension data associated with an entity.  
Optionally, the `cell` parameter can be used to set the userdata's onCopy (`cell = 1`) or onFree (`cell = 2`) listener banks. If these are set, the banks will be updated when the entity is copied or freed, respectively, so that extension functionality knows when to delete or reallocate additional data.  

#### <span id="getentityuserdata" />GetEntityUserData ####
`GetEntityUserData(handler, slot[, cell])`  
**Parameters:** The entity; the slot to retrieve a value from. Optionally, the cell to read.  
**Return value:** The value in the given slot.  
**Description:** This function retrieves a value from the given slot in an entity's custom data vector. Use this to retrieve extension data associated with an entity.  
Optionally, the `cell` parameter can be used to check the userdata's onCopy (`cell = 1`) or onFree (`cell = 2`) listener banks (see `SetEntityUserData`).  

#### <span id="copyentity" />CopyEntity ####
`CopyEntity(handler)`  
**Parameters:** The entity.  
**Return value:** A newly-created copy of the entity.  
**Description:** This function creates a complete copy of an entity. The function performs a "deep copy" of all built-in bOGL data associated with the entity, so for example all of the entity's children are recursively copied, and the copies attached to the new entity.  
The custom data vector itself is copied, but since bOGL has no idea what the data in the vector represents, the copy is not guaranteed to be "deep" and addons may need to define wrapper copy routines that properly update or copy this extension data as necessary.  
If the entity's userdata contains any `onCopy` listeners, they will be updated with the copied entity's handle in order to properly complete the operation on their next update.  
**Future direction:** If bOGL is ported to a language supporting callbacks, this function can be extended to handle deep copies of user data properly by storing the correct `copy` callbacks in the vector alongside the data. In the meantime this service is provided by the `onCopy` listener slots.  

#### <span id="freeentity" />FreeEntity ####
`FreeEntity(handler)`  
**Parameters:** The entity.  
**Return value:** None.  
**Description:** This function destroys an entity. All built-in bOGL data associated with the entity will be released: any children of the entity will be recursively freed, textures will have their internal reference counts decremented, and the custom data vector will be deleted.  
Since bOGL has no idea what the data in the custom data vector represents, any referenced extension objects are not guaranteed to be freed.  
If the entity's userdata contains any `onFree` listeners, they will be updated with the freed entity's handle in order to properly deal with the operation on their next update (since the handle may be reused, this information is only partially useful, e.g. as a free count).  
Addons may choose to define a "free wrapper" function that cleans up the custom data before destroying the entity, or give extension data the ability to tell when its host entity has been destroyed by storing internal nullable pointers (e.g. the `ANIM_ClearUnused` function in `Animation` can run after many entities are freed and clear up any hanging animation data; the free count provided by the `onFree` listener tells it when it needs to do this).  
**Future direction:** If bOGL is ported to a language supporting callbacks, this function can be extended to properly free user data by storing the correct `free` callbacks in the vector alongside the data. In the meantime this service is provided by the `onFree` listener slots.  


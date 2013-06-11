
# MD2: an addon for bOGL-2 #

* [Introduction](#intro)
* [Quick start](#quickstart)
* [Command reference / API](#publicapi)
 * [InitMD2Addon](#initf)
 * [UpdateMD2Anims](#updatemd2anims)
 * [LoadMD2Model](#loadmd2model)
 * [LoadMD2SubMesh](#loadmd2submesh)
 * [CopyMD2Mesh](#copymd2mesh)
 * [AnimateMD2](#animatemd2)
 * [SetMD2AutoMove](#setmd2automove)
 * [SetMD2AnimTime](#setmd2animtime)
 * [GetMD2AnimTime](#getmd2animtime)
 * [GetMD2NumFrames](#getmd2numframes)
 * [GetMD2AnimMode](#getmd2animmode)
 * [GetMD2SeqByName](#getmd2seqbyname)
 * [MD2_ClearUnused](#md2_clearunused)

## <span id="intro"/>Introduction ##

This module provides functions to load animations and meshes from .md2 files. MD2 is the character model and animation format introduced by Quake II; it is a popular and simple standard, and is supported by many editors.

MD2 models may be loaded directly from files, or out of Blitz banks; this module also provides the capability for separate MD2 entities to share a single mesh surface, in the hopes that reducing the number of active surfaces may improve performance (in practice, this is unlikely to help at all except for largish numbers of very small and simple MD2 models).

This module does not depend on any other addon modules, and can be used with bOGL on its own.

## <span id="quickstart"/>Quick start ##

To quickly get set up animating characters with `MD2`:

1. `Include "bOGL-Addons/MD2.bb"` at the top of your main project file.
2. Call the initialization function (`InitMD2Addon`) in your initialization block. This should happen before you do anything else with the 3D engine.
3. Add a call to `UpdateMD2Anims` in your main loop, before the main call to `RenderWorld`.
4. After any major entity deletion events, add a call to `MD2_ClearUnused` to tidy up any hanging `MD2` data (you don't need to do this for each entity, just once after several of them).

That's all you need to do to support using animated MD2 characters in your 3D project! Now add some `LoadMD2Model` and `AnimateMD2` calls and get some badass Quake II characters into your game!

If you use IDEal, consider adding `MD2.bb` to your IDEal project file in order to provide proper syntax highlighting and code support for the commands in this module. If you don't use IDEal, or just don't want to clutter your project, there's also an optional .decls file (`MD2_Optional.decls`) that you can add to your `userlibs` folder to get syntax highlighting that way.

## <span id="publicapi"/>Command reference / API ##

#### <span id="initf" />InitMD2Addon ####
`InitAnimationAddon()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This is the initialization function for the `MD2` addon module. Call this *once* in your program initialization block, before doing anything with the 3D engine. This sets up necessary user data slots and other shared values for the library as a whole. Do not skip this step.  

#### <span id="updatemd2anims" />UpdateMD2Anims ####
`UpdateMD2Anims()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This function steps all running MD2 animations forward by the necessary number of frames, and updates the MD2 mesh's vertices  to the correct key or tweened positions for that animation frame. Call it in your main loop, after all of your movement update code and before calling `RenderWorld` or `RenderStencil`.  

#### <span id="loadmd2model" />LoadMD2Model ####
`LoadMD2Model(file$[, parent, numInstances])`  
**Parameters:** The file to load from; a parent entity; the number of instances of the model to load.  
**Return value:** Either the handle of the loaded MD2, or of the parent pivot of all loaded MD2 instances.  
**Description:** This function loads an MD2 model from the specified file.  
In order to try to reduce surface count, multiple instances of the same model may share a surface (to be honest this rarely helps). If you know you want to load several of the same model, you can pass a value greater than 1 to the `numInstances` parameter, and instead of returning a single MD2 entity, the function will return a pivot with that number of instances of the MD2 entity parented to it. The instances will share a single surface, but can be moved around like any normal mesh entity as they work like "bones", controlling their share of the shared surface.  
You can also optionally specify a parent entity for the whole assemblage by passing it to the `parent` parameter. The default value for this parameter is 0, which puts the structure on its own in world space. If a parent is supplied, the structure will be created at its position; otherwise, it will be placed at world position 0, 0, 0.  

#### <span id="loadmd2submesh" />LoadMD2SubMesh ####
`LoadMD2SubMesh(bk, st, sz, targetMesh, numInstances[, doAutoMove])`  
**Parameters:** A Blitz bank to load MD2 data from; the start and size of the MD2 data in the bank; the mesh to load the MD2 data onto; the number of copes to make; the AutoMove flag.  
**Return value:** A loaded MD2 entity structure of one or more MD2 entities.  
**Description:** This function loads MD2 data from a bank. This function expects a "host mesh" to accept the MD2 polygon data and host the "bones" that control the actual frame data. It is possible for multiple MD2 entities to share a single host mesh by simply copying the polygon data and adding more "bones" to the host.  
All MD2 entities actually exist as "bones" attached to a host mesh, but when an MD2 entity is loaded on its own (with `LoadMD2Model`), a fresh host is created and the handle to that returned instead of the handle to the "bone" entity. When multiple instances of the same MD2 are attached to one host, then they are individually controlled through the "bone" handles instead.  
The AutoMove flag, when set, updates the position of an MD2 instance's vertices according to the movement of its "bone". This is computationally expensive, so for single MD2 instances that do not need to refer back to the "bone", the flag is disabled and the whole entity just controlled using the mesh handle instead. (For single instances loaded with `LoadMD2Model`, one need not be aware of the concept of "bone" entities at all.)
This function provides the actual loading mechanism used by `LoadMD2Model`. It is mainly useful if you want to load MD2 entities out of an archive that has been loaded as a bank; if you have the MD2 files separately on disk, just use `LoadMD2Model` as it is far less complicated.

#### <span id="copymd2mesh" />CopyMD2Mesh ####
`CopyMD2Mesh(rootMesh[, parent])`  
**Parameters:** The MD2 entity to copy; a parent entity.  
**Return value:** The newly-created MD2 entity.  
**Description:** This function copies an MD2 entity. Calling this instead of simply using `CopyEntity` is essential to ensure that frame data is properly copied as necessary and the new mesh will animate properly.  
The copy can optionally be assigned a parent on creation using the `parent` parameter. The default value for this parameter is 0, which puts the copy on its own in world space. If a parent is supplied, the copy will be created at its position; otherwise, it will be placed at world position 0, 0, 0.  
**Future direction:** If bOGL is ported to a language with support for callbacks (BlitzMax, C, Monkey), `CopyEntity` will be able to take care of custom user data properly and this function will become redundant.  

#### <span id="animatemd2" />AnimateMD2 ####
`AnimateMD2(ent[, mode, speed#, fF, lF, trans])`  
**Parameters:** The MD2 entity to animate; the animation mode; the animation speed multiplier; the first and last frames; the transition speed.  
**Return value:** None.  
**Description:** This function sets an MD2 entity to animating. All parameters other than the entity itself are optional.  
The animation mode determines how the animation will play. Valid argument values for this parameter are:  
`MD2_MODE_STOP`: stop moving; do not animate past the start position.  
`MD2_MODE_LOOP`: loop the animation. When it reaches the last frame, go back to the first and play again (a transition may occur between the last and first frames).  
`MD2_MODE_PING`: "ping pong" animation; when it reaches the last frame play it backwards towards the beginning again, and vice versa.  
`MD2_MODE_ONCE`: play the animation only once and then stop animating when it reaches the last frame.  
The default animation mode is `MD2_MODE_LOOP`.  
The `speed` parameter controls how quickly the animation will play. At a speed of 1, each step of the application loop will advance the animation by one frame; at a speed of 3, each step of the application will advance the animation by three frames; at a speed of 0.2; each step of the application will advance the animation by 0.2 frames. The default speed is 1.  
The `fF` and `lF` parameters select the start and end frames of the animation to play from within all of the frames making up the available animation data for the MD2 entity. Most character models will have twenty or so different animations packed into one long sequence, and expect you to have a table of offsets to choose the right start and end points to show a given action. You can extract the start and end frames of each animation as long as you know the animation's name; most Quake II models (and by extension, MD2 models in general) use the same set of animation names to make this easy (see the `MD2` demo for an example). By default, if these are omitted the animation will just start at frame 0 and play through the entire available sequence.  
The `trans` parameter determines the number of frames to use as a transition, either between the end and start of a looping animation, or between the entity's current position and the start of the next sequence to play. The default value for this parameter is 0. A good transition is short and only exists to smooth the movement from position to position, e.g. 8 frames is plenty if your game runs at 60FPS.  

#### <span id="setmd2automove" /> SetMD2AutoMove ####
`SetMD2AutoMove(ent, doAutoMove)`  
**Parameters:** The entity; whether to AutoMove its mesh.  
**Return value:** None. 
**Description:** This function sets the AutoMove flag for an MD2 entity. If the flag is set, the mesh associated with the MD2 entity will be moved around by any movement of its associated "bone". This is only relevant when multiple MD2 entities are sharing a single surface, as the mesh object itself cannot be used to move component MD2 entities around without moving all visible objects; instead, the polygons assigned to one MD2 entity track its pivot's location.  
This flag is assigned for MD2 loaded into a mesh alongside each other, and not set for MD2s loaded as single instances (which can just use the mesh root as their entity handle, and ignore the "bone"). In general you should not need to switch this flag.  

#### <span id="setmd2animtime" />SetMD2AnimTime ####
`SetMD2AnimTime(ent, time#)`  
**Parameters:** The entity; the time to place the animation at.  
**Return value:** None.  
**Description:** This function places an MD2 entity's animation at a specific place in the sequence manually. This is useful if you can't rely on `AnimateMD2` for some reason, or just want to set the MD2 entity to a static position.  

#### <span id="getmd2animtime" />GetMD2AnimTime ####
`GetMD2AnimTime#(ent)`  
**Parameters:** The entity.  
**Return value:** The entity's place in the animation sequence.  
**Description:** This function returns where exactly the queried MD2 entity is in its animation sequence. Since it may be animating at a fractional number of frames per application frame, the value returned is a float.  

#### <span id="getmd2numframes" />GetMD2NumFrames ####
`GetMD2NumFrames(ent)`  
**Parameters:** The entity.  
**Return value:** The number of frames in its animation sequence.  
**Description:** This function just returns the number of frames in an MD2 entity's animation sequence.  

#### <span id="getmd2animmode" />GetMD2AnimMode ####
`GetMD2AnimMode(ent)`  
**Parameters:** The entity.  
**Return value:** Its animation mode.  
**Description:** This function returns the current animation mode of an MD2 entity. If it is not animating, the value `MD2_MODE_STOP` is returned.  

#### <span id="getmd2seqbyname" />GetMD2SeqByName ####
`GetMD2SeqByName(out[1], ent, name$)`  
**Parameters:** An out vector for the start and end frames; the entity; the name to search for.  
**Return value:** None (the values are returned in the `out` parameter).  
**Description:** MD2 files are able to store the names of animation subsequences alongside the actual frame data. This makes it easier to look for specific actions in the main frame list, since you don't necessarily have to maintain a separate key table to externally determine when actions begin and end: you can just ask the model.  
This function searches `ent`'s MD2 sequence index for the subsequence named by `name`. If it finds it, the start and end frames are placed in `out` (in elements 0 and 1 respectively). If not, `out` will contain -1 for both indices.

#### <span id="md2_clearunused" />MD2_ClearUnused ####
`MD2_ClearUnused()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This function cleans up any extension data left hanging after any `MD2` entities have been freed with `FreeEntity`. It is not necessary to call it after every free call, only after several entities have been freed and you are ready to move onto doing something else.  
**Future direction:** If bOGL is ported to a language with support for callbacks (BlitzMax, C, Monkey), `FreeEntity` will be able to take care of custom user data properly and this function will become redundant.  


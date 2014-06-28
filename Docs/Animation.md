
# Animation: an addon for bOGL-2 #

* [Introduction](#introduction)
* [Quick start](#quick-start)
* [Command reference / API](#publicapi)
 * [InitAnimationAddon](#initanimationaddon)
 * [UpdateAnimations](#updateanimations)
 * [LoadAnimation](#loadanimation)
 * [LoadAnimBank](#loadanimbank)
 * [CopyAnimation](#copyanimation)
 * [Animate](#animate)
 * [SetAnimTime](#setanimtime)
 * [GetAnimTime](#getanimtime)
 * [GetNumFrames](#getnumframes)
 * [GetAnimMode](#getanimmode)
 * [IsAnimated](#isanimated)

## <span id="intro"/>Introduction ##

This module provides functions to load animations from .bo3d files, and apply them to hierarchical mesh systems. Both skinned (deformable) and segmented (hierarchical mesh) animation is supported by this module, but only for .bo3d format models.

This module depends on `MeshLoader` to load the surfaces to combine into complete animated meshes and for some .bo3d file functions.

See also `MD2` for a more limited but potentially faster animation system.

## <span id="quickstart"/>Quick start ##

To quickly get set up animating scenes with `Animation`:

1. First set up your project to work with `MeshLoader`, because this addon depends on it.
2. `Include "bOGL-Addons/Animation.bb"` at the top of your main project file (after `MeshLoader` is clearest).
3. Call the initialization function (`InitAnimationAddon`) in your initialization block. This should happen before you do anything else with the 3D engine.
4. Add a call to `UpdateAnimations` in your main loop, before the main call to `RenderWorld`. Just before `UpdateBonedMeshes` is best.

That's all you need to do to support loading animated meshes in your 3D project! Now add some `LoadMesh` and `Animate` calls and get some living characters into your game!

If you use IDEal, consider adding `Animation.bb` to your IDEal project file in order to provide proper syntax highlighting and code support for the commands in this module. If you don't use IDEal, or just don't want to clutter your project, there's also an optional .decls file (`Animation_Optional.decls`) that you can add to your `userlibs` folder to get syntax highlighting that way.

## <span id="publicapi"/>Command reference / API ##

#### <span id="initf" />InitAnimationAddon ####
`InitAnimationAddon()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This is the initialization function for the `Animation` addon module. Call this *once* in your program initialization block, before doing anything with the 3D engine. This sets up necessary user data slots and other shared values for the library as a whole. Do not skip this step.  

#### <span id="updateanimations" />UpdateAnimations ####
`UpdateAnimations([rate#])`  
**Parameters:** Speed to run animations.  
**Return value:** None.  
**Description:** This function steps all running animations forward by the necessary number of frames, and moves the entities animated as part of that animation frame to the correct key or tweened positions. Call it in your main loop, after all of your movement update code and before calling `RenderWorld` or `RenderStencil`. Right before `UpdateBonedMeshes` is best.  
If the optional `rate` parameter is passed (as some value other than 1.0), animations will be updated at a faster or slower rate; this is useful for delta-timing, slow-motion effects, etc. Set this value to zero to just run update checks without actually moving any animations at all. The rate should not be negative, and may produce overshoot or unexpected results if significantly greater than 1.0.  

#### <span id="loadanimation" />LoadAnimation ####
`LoadAnimation(root, file$)`  
**Parameters:** The entity to load an animation onto; the file to load an animation from.  
**Return value:** None.  
**Description:** This function loads an animation from a file and applies it to the hierarchy that is parented by the entity passed as `root`. The function will search the child hierarchy of `root` for entities with the same names as those described in the animation file, and apply the animations there to them. This is best used with a model loaded from the same file, since .bo3d files contain both model and animation data together. Using it on another model may produce odd results, if the node structure is different or not all of the names can be matched.  

#### <span id="loadanimbank" />LoadAnimBank ####
`LoadAnimBank(root, bk, start, size)`  
**Parameters:** The entity to load an animation onto; a Blitz bank to load the animation from; the offset and size of the animation within the bank.  
**Return value:** None.  
**Description:** This function loads an animation from a Blitz bank and applies it to the hierarchy that is parented by the entity passed as `root`. This function is mainly useful if you have a large number of animations in a single archive and don't want them to clutter your project as separate files on disk. If your animations are in separate files on disk, just use `LoadAnimation`.  

#### <span id="copyanimation" />CopyAnimation ####
`CopyAnimation(root, src)`  
**Parameters:** The entity to copy an animation onto; the source animated entity to copy from.  
**Return value:** None.  
**Description:** This function attempts to copy the animation hierarchy, from `src` and its children, onto the equivalently-named entities in the tree described by `root` and its own children. This will work best with model structures of the same type, perhaps loaded from the same base. If not everything in `src` can be matched to a child node in `root`, odd results may ensue.  

#### <span id="animate" />Animate ####
`Animate(ent[, mode, speed#, fF, lF, trans])`  
**Parameters:** The entity to animate; the animation mode; the animation speed multiplier; the first and last frames; the transition speed.  
**Return value:** None.  
**Description:** This function sets an entity to animating. All parameters other than the entity itself are optional.  
The animation mode determines how the animation will play. Valid argument values for this parameter are:  
`ANIM_MODE_STOP`: stop moving; do not animate past the start position.  
`ANIM_MODE_LOOP`: loop the animation. When it reaches the last frame, go back to the first and play again (a transition may occur between the last and first frames).  
`ANIM_MODE_PING`: "ping pong" animation; when it reaches the last frame play it backwards towards the beginning again, and vice versa.  
`ANIM_MODE_ONCE`: play the animation only once and then stop animating when it reaches the last frame.  
The default animation mode is `ANIM_MODE_LOOP`.  
The `speed` parameter controls how quickly the animation will play. At a speed of 1, each step of the application loop will advance the animation by one frame; at a speed of 3, each step of the application will advance the animation by three frames; at a speed of 0.2; each step of the application will advance the animation by 0.2 frames. The default speed is 1.  
The `fF` and `lF` parameters select the start and end frames of the animation to play from within all of the frames making up the available animation data for the entity. Most character models will have twenty or so different animations packed into one long sequence, and expect you to have a table of offsets to choose the right start and end points to show a given action (see the `Animation` demo for an example). By default, if these are omitted the animation will just start at frame 0 and play through the entire available sequence.  
The `trans` parameter determines the number of frames to use as a transition, either between the end and start of a looping animation, or between the entity's current position and the start of the next sequence to play. The default value for this parameter is 0. A good transition is short and only exists to smooth the movement from position to position, e.g. 8 frames is plenty if your game runs at 60FPS.  

#### <span id="setanimtime" />SetAnimTime ####
`SetAnimTime(ent, time#)`  
**Parameters:** The entity; the time to place the animation at.  
**Return value:** None.  
**Description:** This function places an entity's animation at a specific place in the sequence manually. This is useful if you can't rely on `Animate` for some reason, or just want to set the entity to a static position.  

#### <span id="getanimtime" />GetAnimTime ####
`GetAnimTime#(ent)`  
**Parameters:** The entity.  
**Return value:** The entity's place in the animation sequence.  
**Description:** This function returns where exactly the queried entity is in its animation sequence. Since it may be animating at a fractional number of frames per application frame, the value returned is a float.  

#### <span id="getnumframes" />GetNumFrames ####
`GetNumFrames(ent)`  
**Parameters:** The entity.  
**Return value:** The number of frames in its animation sequence.  
**Description:** This function just returns the number of frames in an animated entity's animation sequence.  

#### <span id="getanimmode" />GetAnimMode ####
`GetAnimMode(ent)`  
**Parameters:** The entity.  
**Return value:** Its animation mode.  
**Description:** This function returns the current animation mode of an animated entity. If it is not animating, the value `ANIM_MODE_STOP` is returned.  

#### <span id="isanimated" />IsAnimated ####
`IsAnimated(ent)`  
**Parameters:** The entity.  
**Return value:** Whether the entity is animated.  
**Description:** This function returns true if an entity has animation data associated with it. Testing entities with this function before exposing them to the animation system is important, since non-animated entities being passed to other `Animation` functions may cause the program to crash when it looks for nonexistent data.  



# Collision: an addon for bOGL-2 #

* [Introduction](#introduction)
* [Quick start](#quick-start)
* [Command reference / API](#command-reference--api)
 * [InitCollisionAddon](#initcollisionaddon)
 * [UpdateCollisions](#updatecollisions)
 * [SetCollisionSpaceBounds](#setcollisionspacebounds)
 * [SetCollisionListener](#setcollisionlistener)
 * [MakeCollider](#makecollider)
 * [MakeBlocker](#makeblocker)
 * [SetBlockerType](#setblockertype)
 * [GetBlockerType](#getblockertype)
 * [SetCollisionState](#setcollisionstate)
 * [RayPick](#raypick)

## <span id="intro"/>Introduction ##

This addon module extends bOGL with very basic collision detection.

A bOGL entity can be assigned to one of two kinds of collision object: collider, or blocker. Colliders may move around, are (theoretically) spherical, and will be stopped if they move to a position overlapping a blocker. Blockers are cubiod, and are not obstructed by other blockers; if they move to a position overlapping a collider the collider will be pushed out of the way. (So colliders generally represent players; blockers generally represent floors or elevators.)

You don't have to use stop/push type collisions; you can also set no-response or event-notification type collisions to occur, for special actions such as warp zones, triggers, etc.

One entity cannot be both a collider and a blocker. If you want movable entities that block each other from occupying the same space, you'll need to create a child pivot on the given collider, to act as the secondary blocker.

This collision engine uses hash space partitioning to reduce the algorithmic complexity of scenes with a lot of colliders and blockers. Hopefully this will make it faster than a brute-force search for collisions would. You should be able to scale the world up with lots more blocks and see only a small and linear slowdown rather than the explosive O(N^2) slowdown of a dual-loop; you shouldn't need to deactivate (not hide! that won't work!) objects for performance. (No promises.)

This module does not depend on any other addon modules, and can be used with bOGL on its own.

## <span id="quickstart"/>Quick start ##

To quickly get set up moving things with `Collision`:

1. `Include "bOGL-Addons/Collision.bb"` at the top of your main project file.
1. Call the initialization function (`InitCollisionAddon`) in your initialization block. This should happen before you do anything else with the 3D engine.
1. Add a call to `UpdateCollisions` in your main loop, before the main call to `RenderWorld`.

That's all you need to do to support solid walls, ramps, floors and elevators in your 3D project! Now add some `MakeCollider` and `MakeBlocker` calls and give your characters physical interactions!

If you use IDEal, consider adding `Collision.bb` to your IDEal project file in order to provide proper syntax highlighting and code support for the commands in this module. If you don't use IDEal, or just don't want to clutter your project, there's also an optional .decls file (`Collision_Optional.decls`) that you can add to your `userlibs` folder to get syntax highlighting that way.

## <span id="command-reference--api"/>Command reference / API ##

#### <span id="initcollisionaddon" />InitCollisionAddon ####
`InitCollisionAddon()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This is the initialization function for the `Collision` addon module. Call this *once* in your program initialization block, before doing anything with the 3D engine. This sets up necessary user data slots and other shared values for the library as a whole. Do not skip this step.  

#### <span id="updatecollisions" />UpdateCollisions ####
`UpdateCollisions()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This checks for collisions between colliders and blockers. Collisions are responded to immediately: any collider intersecting with a stop/push block will be pushed out of it by the shortest possible path (this essentially means you can slide up and down ramps). If a collider intersects with a "post" block, the listener bank will be updated with the collision information. (The listener bank contains only information for the most recent call to `UpdateCollisions`.)  
For best results, call this function in your main loop, after all your movement and animation is done and before the call to `RenderWorld` or `RenderStencil`.  

#### <span id="SetCollisionSpaceBounds" />SetCollisionSpaceBounds ####
`SetCollisionSpaceBounds(minX#, maxX#, minY#, maxY#, minZ#, maxZ#)`  
**Parameters:** The minimum and maximum X/Y/Z bounds for the collision universe.  
**Return value:** None.  
**Description:** This function sets the size limits for the collision "universe"; this defines the space that collisions can be detected inside. This is not enforced internally by the collision engine; it's up to you to check that colliders don't "fall into the void" or anything (there are several simple ways to do this, e.g. teleporter pads). You can afford to make this space fairly large, but you'll see unexpected strangeness if you do let colliders or blockers go outside it.  

#### <span id="SetCollisionListener" />SetCollisionListener ####
`SetCollisionListener(bank)`  
**Parameters:** An empty bank to use to report collision events.  
**Return value:** None.  
**Description:** This function sets the "listener" for collision events. This is just a bank that will receive the information about "post" type collisions that occured on the last call to `UpdateCollisions`.  
The actual information posted to this bank is very simple: an eight-byte slot is added for each "post" collision, containing the handle of the collider and the handle of the blocker involved in the collision event. It's up to you to decide what to do with this information. Use this to implement teleporters, trigger switches, death or warp zones, etc.  

#### <span id="makecollider" />MakeCollider ####
`MakeCollider(ent, radius#)`  
**Parameters:** The entity to make collidable; its collision radius.  
**Return value:** None.  
**Description:** This function takes an *existing* bOGL entity and grants it collider status (note: it does *not* create a new entity, which is why the function does not return anything). The collider will have the given radius.  
You can call this function again on the same entity to update its radius in-place, if you need to change it (see below).
Note that radius is *not* affected by entity scale (in the interests of performance). If you rescale the entity, you will need to change the entity's collision radius manually to reflect that fact.  

#### <span id="makeblocker" />MakeBlocker ####
`MakeBlocker(ent, xSize#, ySize#, zSize#, response)`  
**Parameters:** The entity to make blockable; its X/Y/Z dimensions; the collision reponse(s).  
**Return value:** None.  
**Description:** This function takes an *existing* bOGL entity and grants it blocker status (note: it does *not* create a new entity, which is why the function does not return anything). The blocker will have the given X/Y/Z dimensions. The response is a bitvector that can combine any of the following values:  
`COLL_RESPONSE_NONE`: do not respond to collisions at all (i.e. not active).  
`COLL_RESPONSE_STOP`: block colliders from moving through. This may potentially cause them to slide around.  
`COLL_RESPONSE_POST`: report collision events to the listener bank.  
Note that after a collider is moved away from a blocker, it no longer counts as touching it unless it tries to move in again, so a blocker with both `STOP` and `POST` response enabled will only post updates when a collider moves directly towards it.  
You can call this function again on the same entity to update its dimensions and response in-place, if you need to change them (see below).  
Blockers *are* affected by entity rotation, but just like colliders, are *not* affected by entity scale. If you need to change the scale (and therefore dimensions) of a blocker, you will need to rescale it manually with this command.  

#### <span id="setblockertype" />SetBlockerType ####
`SetBlockerType(ent, btype)`  
**Parameters:** The blocker entity; its new type tag.  
**Return value:** None.  
**Description:** This sets an integer "type" tag on a given blocker object. What this means is up to you - unlike Blitz3D colltypes, this has no effect at all on actual collision detection, but it might make it easier for you to respond appropriately to "post" collision events (e.g. group blockers into teleport, water, spike action types rather than having to actually lookup an entity by handle).  
Blocker type is also used by `RayPick` (see below).  

#### <span id="getblockertype" />GetBlockerType ####
`GetBlockerType(ent)`  
**Parameters:** The blocker entity.  
**Return value:** Its type tag.  
**Description:** This function returns the integer "type" tag associated with a given blocker entity, as set with `SetBlockerType`.  

#### <span id="setcollisionstate" />SetCollisionState ####
`SetCollisionState(ent, active)`  
**Parameters:** The entity; whether it should be involved in collisions.  
**Return value:** None.  
**Description:** This function activates and deactivates collision entities. The `active` parameter is a simple `True`/`False` value: if `True`, the entity will be involved in collisions; if not, it will be deactivated for the time being. Deactivated entities do not slow down a scene.  
This function applies to both blockers and colliders.  

#### <span id="raypick" />RayPick ####
`RayPick(x0#, y0#, z0#, x1#, y1#, z1#, out#[2], btype = 0)`  
**Parameters:** Origin X/Y/Z of the ray; destination X/Y/Z of the ray; output vector; type tag.  
**Return value:** The picked entity.  
**Description:** This function casts a ray between `x0, y0, z0` and `x1, y1, z1` in global space, and returns the closest blocker object to the origin that intersects the ray. If no object could be picked, the function returns 0.  
The eaxct position of the pick on the object (in world space) is returned in the `out` vector, if an object could be found.  
The optional `btype` parameter specifies a blocker "type" tag integer; only blockers that match this type are tested for. Blockers of a different type are completely ignored. This is useful if you want to be able to e.g. shoot through invisible trigger zones, rather than allow them to stop bullets like a wall.  
Be warned that `RayPick` does not use hash space partitioning, and therefore may be considerably slower than basic collision detection. You probably don't want to use it multiple times every frame.  


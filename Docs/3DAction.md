
# 3DAction: an addon for bOGL-2 #

* [Introduction](#introduction)
* [Quick start](#quick-start)
* [Command reference / API](#command-reference--api)
 * [Init3DAction](#init3daction)
 * [Update3DActions](#update3dactions)
 * [Stop3DActions](#stop3dactions)
 * [RunAction](#runaction)
 * [StopActionsFor](#stopactionsfor)
 * [LoopAction](#loopaction)
 * [ComposeActions](#composeactions)
 * [SequenceActions](#sequenceactions)
 * [MoveBy](#moveby)
 * [TurnBy](#turnby)
 * [ScaleBy](#scaleby)
 * [FadeBy](#fadeby)
 * [TintBy](#tintby)
 * [MoveTo](#moveto)
 * [TurnTo](#turnto)
 * [ScaleTo](#scaleto)
 * [FadeTo](#fadeto)
 * [TintTo](#tintto)
 * [WaitFor](#waitfor)
 * [SendAction](#sendaction)
 * [TrackByPoint](#trackbypoint)
 * [TrackByDistance](#trackbydistance)

## <span id="intro"/>Introduction ##

This addon module extends bOGL with the ability to declare movement "actions", and fire them on entities.

Using actions lets you factor a lot of boilerplate out of your main loop. You can trigger a movement from A to B once, and not have to constantly check if an object should be moving and of so, move it; a move action will simply see that it goes there smoothly in the given number of frames. All you have to do is call `Update3DActions` in your update loop. This enables a much cleaner "event-based" style of game coding.

Actions are provided for movement, scale, rotation, colour, and alpha. Actions can be sequenced together, composed (run at the same time), and looped (finitely or infinitely). There are also action commands to "track" an object, useful for things like 3rd person camera control; and to wait for a given amount of time, or update a listener object with a message.

Actions are in a way like a more generic, less detailed version of animations: you trigger them and wait for them to do their thing over several frames. Actions are not a substitute for animation - animations are much more efficient - but they can do things animations can't, like compose together a set of movements dynamically, or interact with and update values.

The `WaitFor` and `SendAction` commands don't do anything to 3D entities, but provide the basic minimal framework needed to hook your own events into the action system: you can leave simple object movement to the general actions, and write your app-specific code to check for listener updates and maybe trigger new actions, change the game state, or all kinds of complex logic like that.

In order to make it easy and expressive to string together actions, place action expressions in other expressions and so on, all action values are represented (to the user) as strings, so that there's no need to worry about object ownership or deletion. Internally they use a more efficient representation so that the performance overhead isn't too heavy.

This module does not depend on any other addon modules, and can be used with bOGL on its own.

## <span id="quickstart"/>Quick start ##

To quickly get set up moving things with `3DAction`:

1. `Include "bOGL-Addons/3DAction.bb"` at the top of your main project file.
1. Call the initialization function (`Init3DAction`) in your initialization block. This should happen before you do anything else with the 3D engine.
1. Add a call to `Update3DActions` in your main loop, before the main call to `RenderWorld`.

That's all you need to do to support self-updating actions in your 3D project! Now add some `RunAction` calls and start organising your game around events and triggers like a pro!

If you use IDEal, consider adding `3DAction.bb` to your IDEal project file in order to provide proper syntax highlighting and code support for the commands in this module. If you don't use IDEal, or just don't want to clutter your project, there's also an optional .decls file (`3DAction_Optional.decls`) that you can add to your `userlibs` folder to get syntax highlighting that way.

## <span id="command-reference--api"/>Command reference / API ##

#### <span id="init3daction" />Init3DAction ####
`Init3DAction()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This is the initialization function for the `3DAction` addon module. Call this *once* in your program initialization block, before doing anything with the 3D engine. This sets up necessary user data slots and other shared values for the library as a whole. Do not skip this step.  

#### <span id="update3dactions" />Update3DActions ####
`Update3DActions([rate#])`  
**Parameters:** Speed to run actions.  
**Return value:** None.  
**Description:** This function steps all running actions forward by one frame. Entities with movement actions engaged are moved, entities set to track other entities are pointed at their target and moved towards their position, and any send actions update their listener objects with a message. Expired actions are deleted here.  
If the optional `rate` parameter is passed (as some value other than 1.0), actions will be updated at a faster or slower rate; this is useful for delta-timing, slow-motion effects, etc. If the parameter is zero, most actions will not update, but zero-length (instant) actions will still fire. The rate should not be negative, and may produce unexpected results if significantly greater than 1.0 (although overshoot should not occur).  

#### <span id="stop3dactions" />Stop3DActions ####
`Stop3DActions()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This function stops and deletes all currently running actions. This clears the entire system; actions cannot be "resumed" after this, but must be fired again.  

#### <span id="runaction" />RunAction ####
`RunAction(ent, act$)`  
**Parameters:** The action's target; the action to run.  
**Return value:** None.  
**Description:** This function runs an action on an entity. "Fire" the action once with this command, and it will be updated for its duration by the `Update3DActions` function. You should do this outside of loops (taking the logic out of your loop is of course the entire point of this)! It is possible to fire more than one action on an entity, and their effects will "stack" (so don't fire the same action twice unless you mean it).  

#### <span id="stopactionsfor" />StopActionsFor ####
`StopActionsFor(ent)`  
**Parameters:** The entity to stop.  
**Return value:** None.  
**Description:** This function stops all actions currently running on a given entity. They cannot be "resumed" after this, and must be fired again. The entity will no longer be updated by the `Update3DActions` function unless more actions are applied to it later.  

#### <span id="loopaction" />LoopAction ####
`LoopAction$(act$[, n])`  
**Parameters:** The action to loop; number of times to repeat.  
**Return value:** The looped version of the action.  
**Description:** This function sequences an action into a repeating loop over that action. Without the loop count parameter `n`, it will produce an infinite loop (never expires unless killed manually). Otherwise, the action will loop the given number of times and then expire.  

#### <span id="composeactions" />ComposeActions ####
`ComposeActions$(a0$, a1$[, a2$, a3$, a4$, a5$, a6$, a7$, a8$, a9$])`  
**Parameters:** At least two actions to compose into one.  
**Return value:** The merged action.  
**Description:** This function takes two or more (up to a maximum of ten) actions, and combines them into a single one; when the resulting action is applied to an entity, their effects will happen to it *at the same time*. If the actions have different durations, the combined action will expire when the last component action ends.  
If you really need more than ten components, you can always compose the result of this function again with some more (the ten-element limit is because BlitzPlus doesn't allow infinite argument lists).  

#### <span id="sequenceactions" />SequenceActions ####
`SequenceActions$(a0$, a1$, a2$, a3$, a4$, a5$, a6$, a7$, a8$, a9$)`  
**Parameters:** At least two actions to sequence into one.  
**Return value:** The sequenced action.  
**Description:** This function takes two or more (up to a maximum of ten) actions, and sequences them into a single one; when the resulting action is applied to an entity, their effects will happen to it *one at a time*: when the first action in the sequence expires, the second one will automatically fire, and so on.  
If you really need more than ten components, you can always sequence the result of this function again with some more (the ten-element limit is because BlitzPlus doesn't allow infinite argument lists).  

#### <span id="moveby" />MoveBy ####
`MoveBy$(time, x#, y#, z#[, rate])`  
**Parameters:** Duration of the action; relative movement vector; easing mode.  
**Return value:** The action value.  
**Description:** This function creates an action representing a relative movement. When it is applied to an entity, the entity will move from wherever it was initially, by the given amount in its *relative* coordinate system. The cumulative effect of the action once it completes will have been equivalent to `MoveEntity ent, x, y, z`, but moving smoothly over `time` frames.  
The optional `rate` parameter determines the style of movement to use. Valid argument values for this parameter are:  
`ACT3_RATE_LINEAR`: move at a constant speed from start to finish. This is the default if no rate is specified.  
`ACT3_RATE_EASEIN`: gradually accelerate from zero to full speed, "snapping" into the final position.  
`ACT3_RATE_EASEOUT`: start at full speed, and smoothly decelerate to zero.  
`ACT3_RATE_EASEBOTH`: gradually accelerate up to full speed, then gradually decelerate to zero.  

#### <span id="turnby" />TurnBy ####
`TurnBy$(time, x#, y#, z#[, rate])`  
**Parameters:** Duration of the action; relative rotation vector; easing mode.  
**Return value:** The action value.  
**Description:** This function creates an action representing a relative rotation. When it is applied to an entity, the entity will turn from its original orientation, by the given amount around its local axes. The cumulative effect of the action once it completes will have been equivalent to `TurnEntity ent, x, y, z`, but turning smoothly over `time` frames.  
The optional `rate` parameter determines the style of transition to use. See `MoveBy` for valid arguments.  

#### <span id="scaleby" />ScaleBy ####
`ScaleBy$(time, x#, y#, z#[, rate])`  
**Parameters:** Duration of the action; relative scale vector; easing mode.  
**Return value:** The action value.  
**Description:** This function creates an action representing a relative scale. When it is applied to an entity, the entity will resize from its original scale, by the given multiplier. This is a *relative* change in scale and has no immediately equivalent bOGL command.  
The optional `rate` parameter determines the style of transition to use. See `MoveBy` for valid arguments.  

#### <span id="fadeby" />FadeBy ####
`FadeBy$(time, alpha#[, rate])`  
**Parameters:** Duration of the action; relative alpha; easing mode.  
**Return value:** The action value.  
**Description:** This function creates an action representing a change in transparency. When it is applied to an entity, the entity will fade from its original alpha, by the given amount. This is a *relative* change in transparency and has no immediately equivalent bOGL command.  
The optional `rate` parameter determines the style of transition to use. See `MoveBy` for valid arguments.  

#### <span id="tintby" />TintBy ####
`TintBy$(time, r, g, b[, rate])`  
**Parameters:** Duration of the action; relative colour vector; easing mode.  
**Return value:** The action value.  
**Description:** This function creates an action representing a change in colour. When it is applied to an entity, the entity will tint from its original colour, by the given amounts in `r`, `g` and `b`. This is a *relative* change in colour and has no immediately equivalent bOGL command.  
The optional `rate` parameter determines the style of transition to use. See `MoveBy` for valid arguments.  

#### <span id="moveto" />MoveTo ####
`MoveTo$(time, x#, y#, z#[, rate])`  
**Parameters:** Duration of the action; absolute movement target; easing mode.  
**Return value:** The action value.  
**Description:** This function creates an action representing a movement to an absolute location. When it is applied to an entity, the entity will move from its original location to the position (in its containing coordinate system) at `x`, `y`, `z`. The cumulative effect of the action once it completes will have been equivalent to `PositionEntity ent, x, y, z`, but moving smoothly over `time` frames.  
The optional `rate` parameter determines the style of transition to use. See `MoveBy` for valid arguments.  

#### <span id="turnto" />TurnTo ####
`TurnTo$(time, x#, y#, z#[, rate])`  
**Parameters:** Duration of the action; target orientation; easing mode.  
**Return value:** The action value.  
**Description:** This function creates an action representing a rotation to an absolute orientation. When it is applied to an entity, the entity will turn from its original orientation to the new angle (in its containing coordinate system) given by `x`, `y`, `z`. The cumulative effect of the action once it completes will have been equivalent to `RotateEntity ent, x, y, z`, but turning smoothly over `time` frames.  
The optional `rate` parameter determines the style of transition to use. See `MoveBy` for valid arguments.  

#### <span id="scaleto" />ScaleTo ####
`ScaleTo$(time, x#, y#, z#[, rate])`  
**Parameters:** Duration of the action; target scale; easing mode.  
**Return value:** The action value.  
**Description:** This function creates an action representing a scale to an absolute size. When it is applied to an entity, the entity will resize from its original scale to the new value given by `x`, `y`, `z`. The cumulative effect of the action once it completes will have been equivalent to `ScaleEntity ent, x, y, z`, but resizing smoothly over `time` frames.  
The optional `rate` parameter determines the style of transition to use. See `MoveBy` for valid arguments.  

#### <span id="fadeto" />FadeTo ####
`FadeTo$(time, alpha#[, rate])`  
**Parameters:** Duration of the action; target alpha; easing mode.  
**Return value:** The action value.  
**Description:** This function creates an action representing a fade to an absolute transparency. When it is applied to an entity, the entity will fade from its original alpha to the value given. The cumulative effect of the action once it completes will have been equivalent to `EntityAlpha ent, alpha`, but fading smoothly over `time` frames.  
The optional `rate` parameter determines the style of transition to use. See `MoveBy` for valid arguments.  

#### <span id="tintto" />TintTo ####
`TintTo$(time, r, g, b[, rate])`  
**Parameters:** Duration of the action; target colour; easing mode.  
**Return value:** The action value.  
**Description:** This function creates an action representing a tint to a target colour. When it is applied to an entity, the entity will tint from its original colour to the RGB value given. The cumulative effect of the action once it completes will have been equivalent to `PaintEntity ent, r, g, b`, but tinting smoothly over `time` frames.  
The optional `rate` parameter determines the style of transition to use. See `MoveBy` for valid arguments.  


#### <span id="waitfor" />WaitFor ####
`WaitFor$(time)`  
**Parameters:** Duration to do nothing.  
**Return value:** The action value.  
**Description:** This function creates an action representing a delay. Nothing happens when it is applied to an entity (if it is part of a composition, it will not stop parallel actions from executing). Its main purpose is to form part of sequences, delaying the start of the next action in the sequence.  
This action type combined with `SendAction` can be used to form the basis of a simple timed event system, by sequencing messages to be sent after wait actions have expired. Together these form a powerful way to hook your own behaviours into action streams.  

#### <span id="sendaction" />SendAction ####
`SendAction$(target.ActionListener, msg)`  
**Parameters:** An ActionListener object; an integer representing your message.  
**Return value:** The action value.  
**Description:** This function creates an action representing a message update. When the action fires, it will update the listener target with the `msg` value, and the handle of its target entity. This is mainly useful to be sequenced after other actions, to let your main logic code know when something has "happened" and it ought to take action. `msg` is any integer; whatever it means is up to your program.  
This action type can be extremely powerful as the core of an event-based game logic system. If your program listens for updates, it can tell when things have happened and take action in response to that, rather than constantly having to loop around and check by hand. A system that builds and fires new actions in response to messages and certain conditions could be very powerful and express extremely complex game logic.  

#### <span id="trackbypoint" />TrackByPoint ####
`TrackByPoint$(target, x#, y#, z#[, strength#])`  
**Parameters:** A target entity; a position to maintain; strength of the connection.  
**Return value:** The action value.  
**Description:** This function creates an action that "tracks" a target entity. When this action is run on an entity that you want to be a follower, it will try to always put itself at position `x`, `y`, `z` in the target's local space, and pointing at the entity. This could be useful for some kind of third-person camera control, for instance.    
The optional `strength` parameter determines how smoothly and quickly the follower will move to the target location. This should be a value between 0 and 1. Very low values will result in a very slow, smooth, relaxed movement (that may never arrive); a value of 1 is functionally equivalent to simply parenting the follower to the target; a value of 0 will point the follower at the target, but never move it.  
This action never expires. If you want an object to stop following its target, you must use `StopActionsFor` on it.  

#### <span id="trackbydistance" />TrackByDistance ####
`TrackByDistance$(target, dist#[, strength#])`  
**Parameters:** A target entity; a distance to maintain; strength of the connection.  
**Return value:** The action value.  
**Description:** This function creates an action that "tracks" a target entity. When this action is run on an entity that you want to be a follower, it will try to always maintain a distance of `dist` from the target's position, pointing at it. It will not be placed on any particular side and will therefore just move to the closest point on the surrounding virtual sphere. This could be useful for a more lazy kind of third-person camera control.  
The optional `strength` parameter determines how smoothly and quickly the follower will retreat or advance to the specified distance. This should be a value between 0 and 1. Very low values will result in quite a slow, smooth, relaxed movement (that may never arrive); a value of 1 will "lock" the follower onto the surface of an imaginary sphere surrounding the target; a value of 0 will point the follower at the target, but never move it.  
This action never expires. If you want an object to stop following its target, you must use `StopActionsFor` on it.  


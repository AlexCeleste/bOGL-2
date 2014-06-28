
# Particle: an addon for bOGL-2 #

* [Introduction](#introduction)
* [Quick start](#quick-start)
* [Command reference / API](#command-reference--api)
 * [InitParticleAddon](#initparticleaddon)
 * [UpdateParticles](#updateparticles)
 * [CreateEmitter](#createemitter)
 * [FireEmitter](#fireemitter)
 * [PauseEmitter](#pauseemitter)
 * [ResumeEmitter](#resumeemitter)
 * [ClearEmitter](#clearemitter)
 * [SetEmitterRate](#setemitterrate)
 * [SetParticleTexture](#setparticletexture)
 * [SetParticleFX](#setparticlefx)
 * [SetParicleDirection](#setparicledirection)
 * [SetParticleRGB](#setparticlergb)
 * [SetParticleSpeed](#setparticlespeed)
 * [SetParticleLifetime](#setparticlelifetime)
 * [SetParticleSize](#setparticlesize)
 * [SetParticleParentMode](#setparticleparentmode)

## <span id="intro"/>Introduction ##

This addon provides a simple, single-surface particle system for bOGL.

Particles are produced by "emitters", which are bOGL entities. All particles belonging to one emitter share a single surface; different emitters use separate surfaces. Individual particles are lightweight and are not implemented as full entities, which should reduce memory load and improve performance.

Particles can have a randomly variable {direction/speed/colour/size}, and may be free-floating, or have their position parented to the emitter that produced them. Particles may also be textured and FX-ed like meshes.

In the absence of vertex alpha this system is kinda ugly, but it does the job.

This module does not depend on any other addon modules, and can be used with bOGL on its own.

## <span id="quickstart"/>Quick start ##

To quickly get set up making a mess with `Particle`:

1. `Include "bOGL-Addons/Particle.bb"` at the top of your main project file (after `MeshLoader` is clearest).
1. Call the initialization function (`InitParticleAddon`) in your initialization block. This should happen before you do anything else with the 3D engine.
1. Add a call to `UpdateParticles` in your main loop, before the main call to `RenderWorld`.

That's all you need to do to support particle effects in your 3D project! Now put a bit of *fire* in your game!

If you use IDEal, consider adding `Particle.bb` to your IDEal project file in order to provide proper syntax highlighting and code support for the commands in this module. If you don't use IDEal, or just don't want to clutter your project, there's also an optional .decls file (`Particle_Optional.decls`) that you can add to your `userlibs` folder to get syntax highlighting that way.

## <span id="command-reference--api"/>Command reference / API ##

#### <span id="initf" />InitParticleAddon ####
`InitParticleAddon()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This is the initialization function for the `Particle` addon module. Call this *once* in your program initialization block, before doing anything with the 3D engine. This sets up necessary user data slots and other shared values for the library as a whole. Do not skip this step.  

#### <span id="updateparticles" />UpdateParticles ####
`UpdateParticles(camera[, rate#])`  
**Parameters:** Perspective camera; speed to run particle lifetimes.  
**Return value:** None.  
**Description:** This function moves all particle lifetimes forward, causing particles to move, fade out, change colour, or whatever they've been set to do. For best results, call it in your main loop, after all of your movement update code and before calling `RenderWorld`.  
If the optional `rate` parameter is passed (as some value other than 1.0), particles will be updated at a faster or slower rate; this is useful for delta-timing, slow-motion effects, etc. Set this value to zero to just run update checks and align perspective without actually moving any particles at all. The rate should not be negative, and may produce unexpected results if greater than 1.0.  
Particles will be updated so that their meshes are correctly aligned towards the given perspective camera. For best results, this needs to be the same camera used for the following call to `RenderWorld`. If rendering multiple perspectives, you may need to call this function multiple times (with a `rate` of zero) between separated `RenderWorld` calls.  

#### <span id="createemitter" />CreateEmitter ####
`CreateEmitter([parent, rate, texture])`  
**Parameters:** An optional parent entity; rate of particle emission; particle texture.  
**Return value:** The newly created emitter object.  
**Description:** This function creates a new particle emitter entity.  
If the optional `parent` parameter is passed, the emitter will be created as a child of the given entity (as usual).  
The optional `rate` parameter sets the number of particles for the emitter to produce per second (assuming an update rate of 1.0 in the main loop).  
The optional `texture` parameter sets a texture to use for each particle.  

#### <span id="fireemitter" />FireEmitter ####
`FireEmitter(emit, duration)`  
**Parameters:** The emitter to fire; how long it should last in milliseconds.  
**Return value:** None.  
**Description:** This function starts the given particle emitter producing particles. It will continue to do so at the previously-specified rate for the number of milliseconds passed in `duration` (so a value of 1000 will emit particles for one second).  
You can safely call this function at any time; if an emitter is already firing, it will simply extend its lifespan by setting it to the new amount.  

#### <span id="pauseemitter" />PauseEmitter ####
`PauseEmitter(emit)`  
**Parameters:** The emitter to pause.  
**Return value:** None.  
**Description:** This function temporarily pauses a particle emitter. It will not be updated while in this state; its particles will appear frozen. You can reactivate it with `ResumeEmitter`.  
It is safe (albeit pointless) to call this repeatedly on the same emitter.  

#### <span id="resumeemitter" />ResumeEmitter ####
`ResumeEmitter(emit)`  
**Parameters:** The emitter to resume.  
**Return value:** None.  
**Description:** This function resumes updates for a particle emitter that was paused with `PauseEmitter`. It is safe (albeit pointless) to call this repeatedly on the same emitter.  

#### <span id="clearemitter" />ClearEmitter ####
`ClearEmitter(emit)`  
**Parameters:** The emitter to clear.  
**Return value:** None.  
**Description:** This function stops updates for a "live" particle emitter and gets rid of its spawned particles. You should use this when you want to actually *end* emission for the time being, rather than just pause it.  

#### <span id="setemitterrate" />SetEmitterRate ####
`SetEmitterRate(emit, rate)`  
**Parameters:** The emitter; the rate of emission (particles/sec).  
**Return value:** None.  
**Description:** This function sets the emission rate for a particle emitter, in average particles emitted per second.  

#### <span id="setparticletexture" />SetParticleTexture ####
`SetParticleTexture(emit, tex)`  
**Parameters:** The emitter; the particle texture.  
**Return value:** None.  
**Description:** This function sets a texture for the particles of the given emitter.  

#### <span id="setparticlefx" />SetParticleFX ####
`SetParticleTexture(emit, FXflags)`  
**Parameters:** The emitter; particle FX flags.  
**Return value:** None.  
**Description:** This function sets FX flags for the particle mesh of the given emitter. See [the main bOGL documentation](bOGL.md#entityfx) for information about FX flags.  

#### <span id="setparticledirection" />SetParticleDirection ####
`SetParticleDirection(emit, dx#, dy#, dz#[, var#])`  
**Parameters:** The emitter; the particle X/Y/Z direction; variance.  
**Return value:** None.  
**Description:** This function sets the direction of particle emission from the given emitter. Particles will be emitted in the given direction, with a randomisation of `+/- var` (zero if no variance value is passed).  
The default direction vector is zero, with a variance of 1.0 (in other words, completely randomised direction).  

#### <span id="setparticlergb" />SetParticleRGB ####
`SetParticleRGB(emit, r, g, b[, var#])`  
**Parameters:** The emitter; the particle R/G/B colour; variance.  
**Return value:** None.  
**Description:** This function sets the colour of particles produced by the given emitter. Particles will be emitted in the given RGB, with a randomisation of `+/- var` (zero if no variance value is passed). `var` is a value from 0 to 1 that is multiplied with the 0-255 byte colour; a variance of 1.0 means complete colour randomisation.  
The default colour is white, with a variance of 1.0 (completely randomised).  

#### <span id="setparticlespeed" />SetParticleSpeed ####
`SetParticleSpeed(emit, spd#[, var#])`  
**Parameters:** The emitter; the particle movement speed; variance.  
**Return value:** None.  
**Description:** This function sets the speed of particle movement from the given emitter. Particles will be emitted with the given speed, with a randomisation of `+/- var` (zero if no variance value is passed). `var` is a value from 0 to 1 that is used to scale the speed value; e.g. a speed of 2.0 and a variance of 0.2 will produce particles with speeds ranging from 1.6 to 2.4.  
The default speed is 0.2, with a variance of 0.1.  

#### <span id="setparticlelifetime" />SetParticleLifetime ####
`SetParticleLifetime(emit, dx#, dy#, dz#[, var#])`  
**Parameters:** The emitter; the particle lifetime in milliseconds; variance.  
**Return value:** None.  
**Description:** This function sets the lifetime of particles from the given emitter. Particles will disappear after the given numbre of milliseconds, with a randomisation of `+/- var` (zero if no variance value is passed). `var` is a value from 0 to 1 that is used to scale the lifetime value; e.g. a lifetime of 2000 and a variance of 0.2 will produce particles with lives ranging from 1600 to 2400 ms.  
The default particle lifetime is 1000 (one second), with a variance of 0.1.  

#### <span id="setparticlesize" />SetParticleSize ####
`SetParticleTexture(emit, size#[, var#])`  
**Parameters:** The emitter; the particle radius; variance.  
**Return value:** None.  
**Description:** This function sets the radius of particles from the given emitter. Particles will be emitted in the given radius, with a randomisation of `+/- var` (zero if no variance value is passed). `var` is a value from 0 to 1 that is used to scale the size value; e.g. a size of 2.0 and a variance of 0.2 will produce particles with sizes ranging from 1.6 to 2.4.  
The default size is 0.05, with a variance of 1.0.  

#### <span id="setparticleparentmode" />SetParticleParentMode ####
`SetParticleTexture(emit, particlesParented)`  
**Parameters:** The emitter; whether particles should move with the emitter.  
**Return value:** None.  
**Description:** This function sets the "parenting mode" for the given emitter. By default, particles are not parented to their emitter and will move through space independently; if the emitter moves, it emits a trail of particles behind like an exhaust pipe. If this flag is set to `True`, particles will follow their emitter like parented 3D entities would, which might be useful for e.g. small flames, sparkly objects.  


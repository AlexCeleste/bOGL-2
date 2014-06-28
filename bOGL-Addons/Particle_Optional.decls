
; Optional function declarations for the Particle bOGL addon
; This file is only really to provide syntax highlighting

.lib " "

InitParticleAddon()
UpdateParticles(camera, rate#)
CreateEmitter%(parent, rate, texture)
FireEmitter(emit, duration)
PauseEmitter(emit)
ResumeEmitter(emit)
ClearEmitter(emit)
SetEmitterRate(emit, rate)
SetParticleTexture(emit, tex)
SetParticleFX(emit, FXflags)
SetParticleDirection(emit, dx#, dy#, dz#, var#)
SetParticleRGB(emit, r, g, b, var#)
SetParticleSpeed(emit, spd#, var#)
SetParticleLifetime(emit, life, var#)
SetParticleSize(emit, size#, var#)
SetParticleParentMode(emit, particlesParented)
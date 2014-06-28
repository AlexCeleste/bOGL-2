
; Optional function declarations for the Collision bOGL addon
; This file is only really to provide syntax highlighting

; Array parameters (e.g. 'arr[3]') are represented with underscores (e.g. 'arr__3__')

.lib " "

InitCollisionAddon()
UpdateCollisions()
SetCollisionSpaceBounds(minX#, maxX#, minY#, maxY#, minZ#, maxZ#)
SetCollisionListener(bank)
MakeCollider(ent, radius#)
MakeBlocker(ent, xSize#, ySize#, zSize#, response)
SetBlockerType(ent, btype)
GetBlockerType%(ent)
SetCollisionState(ent, active)
RayPick%(x0#, y0#, z0#, x1#, y1#, z1#, out__2__#, btype)
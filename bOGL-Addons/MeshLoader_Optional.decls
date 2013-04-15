
; Optional function declarations for the MeshLoader bOGL addon
; This file is only really to provide syntax highlighting

; Array parameters (e.g. 'arr[3]') are represented with underscores (e.g. 'arr__3__')

.lib " "

InitMeshLoaderAddon()
LoadMesh%(file$, parent)
LoadBO3D%(bk, start, size)
LoadOBJMesh%(file$)
SaveOBJMesh(mesh, file$)
UpdateBonedMeshes()
ActivateMeshBones(ent)
DeactivateMeshBones(ent)
CopyBonedMesh%(ent)
LOADER_ClearUnused()


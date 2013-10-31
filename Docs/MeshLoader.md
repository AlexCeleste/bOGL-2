
# MeshLoader: an addon for bOGL-2 #

* [Introduction](#introduction)
* [Quick start](#quick-start)
* [Command reference / API](#command-reference--api)
 * [InitMeshLoaderAddon](#initmeshloaderaddon)
 * [LoadMesh](#loadmesh)
 * [LoadBO3D](#loadbo3d)
 * [LoadOBJMesh](#loadobjmesh)
 * [SaveOBJMesh](#saveobjmesh)
 * [UpdateBonedMeshes](#updatebonedmeshes)
 * [ActivateMeshBones](#activatemeshbones)
 * [DeactivateMeshBones](#deactivatemeshbones)

## <span id="intro"/>Introduction ##

This addon module extends bOGL with the ability to load and save static and skinned meshes.

Static meshes may be loaded from .obj and .bo3d file formats; static meshes may also be saved to the .obj format for export to most 3D model editing programs. Meshes loaded from .bo3d files may also be "skinned" (i.e. deformable by a hierarchy of bone pivots). To aid in loading large amounts of data at once, meshes in the .bo3d format (but not the .obj format, which is not efficient anyway) may also be loaded directly from Blitz banks in program memory.

This module does not provide any animation functionality: see `Animation` for procedures to also load animation key data for skinned meshes, or `MD2` for a different animation system altogether.

This module does not depend on any other addon modules, and can be used with bOGL on its own.

(The .bo3d model format is a custom format optimised for bOGL, described in `Docs/bo3d_spec.txt`. You can convert compatible .b3d models using the conversion tool in `Tools/bo3d_conv.bb`.)

## <span id="quickstart"/>Quick start ##

To quickly get set up loading meshes with `MeshLoader`:

1. `Include "bOGL-Addons/MeshLoader.bb"` at the top of your main project file.
2. Call the initialization function (`InitMeshLoaderAddon`) in your initialization block. This should happen before you do anything else with the 3D engine.
3. Add a call to `UpdateBonedMeshes` in your main loop, before the main call to `RenderWorld`. (You don't need this in the loop if you don't want to use .bo3d files.)
4. After any major entity deletion or copy events, make sure `UpdateBonedMeshes` gets called to "complete" them (if you aren't using it in your main loop already).

That's all you need to do to support loading meshes in your 3D project! Now add some `LoadMesh` calls and get some nice static art into your game!

If you use IDEal, consider adding `MeshLoader.bb` to your IDEal project file in order to provide proper syntax highlighting and code support for the commands in this module. If you don't use IDEal, or just don't want to clutter your project, there's also an optional .decls file (`MeshLoader_Optional.decls`) that you can add to your `userlibs` folder to get syntax highlighting that way.

## <span id="publicapi"/>Command reference / API ##

#### <span id="initf" />InitMeshLoaderAddon ####
`InitMeshLoaderAddon()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This is the initialization function for the `MeshLoader` addon module. Call this *once* in your program initialization block, before doing anything with the 3D engine. This sets up necessary user data slots and other shared values for the library as a whole. Do not skip this step.  

#### <span id="loadmesh" />LoadMesh ####
`LoadMesh(file$[, parent])`  
**Parameters:** The file to load a mesh from; a parent for the new mesh.  
**Return value:** The newly-loaded mesh.  
**Description:** This function loads a mesh from a file. It accepts both .obj and .bo3d files as arguments. If for some reason the file failed to load, the function will return 0; otherwise, it will return the newly-loaded entity.  
The mesh can optionally be assigned a parent on creation using the `parent` parameter. The default value for this parameter is 0, which puts the mesh on its own in world space. If a parent is supplied, the mesh will be created at its position; otherwise, it will be placed at world position 0, 0, 0.  

#### <span id="loadbo3d" />LoadBO3D ####
`LoadBO3D(bk, start, size)`  
**Parameters:** A Blitz bank to load from; the start of the model data; the size of the model data.  
**Return value:** The newly-loaded mesh.  
**Description:** This function loads a model in .bo3d format out of a bank at the specified offset. This function is mainly useful if you have a large number of models in a single archive and don't want them to clutter your project as separate files on disk. If the model could not be loaded for some reason, the function will return 0; otherwise it will return the newly-loaded entity. A large number of integrity checks are performed on the model data and it must be a valid model definition according to the .bo3d specification.  
This function provides the loading mechanism used by `LoadMesh` if it detects that its argument is a .bo3d filename. If your models are in separate files on disk, just use `LoadMesh`.  

#### <span id="loadobjmesh" />LoadOBJMesh ####
`LoadOBJMesh(file$)`  
**Parameters:** The file to load a mesh from.  
**Return value:** The newly-loaded mesh.  
**Description:** This function loads a model in .obj format from the specified file. If the model could not be loaded for some reason, the function will return 0; otherwise it will return the newly-loaded entity.  
The .obj format is not a very efficient way to store model data, but it is extremely easy to read and write and is supported by practically every 3D model editor in existence. This makes it very easy to quickly get static props into your game from any toolchain. It also zips well, even though it takes up a lot of space uncompressed. bOGL supports a limited subset of .obj features relevant to its engine (single-surface, single-mesh models made out of vertices and triangles and with at most one texture and one groupname).  
This function provides the loading mechanism used by `LoadMesh` if it detects that its argument is an .obj filename. There's not a whole lot of reason to use it on its own.  

#### <span id="saveobjmesh" />SaveOBJMesh ####
`SaveOBJMesh(mesh, file$)`  
**Parameters:** The mesh to save; the file to save it to.  
**Return value:** None.  
**Description:** This function saves a static mesh to an .obj format model. Because .obj is supported by practically every 3D model editor in existence, this is a great way to save anything you created using the bOGL engine and export it to tweak or examine in a more powerful tool. However, only static, non-boned meshes are supported by the format subset emitted by bOGL (.obj doesn't support things like animation anyway). If you try to save an animated or deformed mesh of any sort, you'll just get a snapshot of whatever position it was in at the time. This does have its uses though.  

#### <span id="updatebonedmeshes" />UpdateBonedMeshes ####
`UpdateBonedMeshes()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This function updates all active boned meshes so that their surfaces are deformed properly to match the movement of their component bone pivots. Call it in your main loop, after all of your movement update code and before calling `RenderWorld` or `RenderStencil`.  
You don't need to call this function if you are not using boned meshes for anything.  

#### <span id="activatemeshbones" />ActivateMeshBones ####
`ActivateMeshBones(ent)`  
**Parameters:** The mesh to activate.  
**Return value:** None.  
**Description:** This function sets a boned mesh into "update" mode, so that it gets deformed by the movement of its component bones when `UpdateBonedMeshes` runs. By default, a newly loaded boned mesh is already in "update" mode, so you only need to call this if you have manually deactivated any meshes for some reason.  

#### <span id="deactivatemeshbones" />DeactivateMeshBones ####
`DeactivateMeshBones(ent)`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This function pauses updating of a boned mesh so that it is left untouched when `UpdateBonedMeshes` is called. The meain reason for doing this is performance: since updating a mesh surface is quite expensive (and the update has no way to know if it actually needs to be done this frame), any meshes that you know aren't currently being animated or otherwise deformed can have their updates turned off to save some CPU time.  



# Draw2D: an addon for bOGL-2 #

* [Introduction](#introduction)
* [Quick start](#quick-start)
* [Command reference / API](#command-reference--api)
 * [InitDraw2D](#initdraw2d)
 * [SetViewport2D](#setviewport2d)
 * [SetVirtualResolution2D](#setvirtualresolution2d)
 * [BeginDraw2D](#begindraw2d)
 * [EndDraw2D](#enddraw2d)
 * [SetColor2D](#setcolor2d)
 * [SetBlend2D](#setblend2d)
 * [SetClsColor2D](#setclscolor2d)
 * [Cls2D](#cls2d)
 * [LoadFont2D](#loadfont2d)
 * [FreeFont2D](#freefont2d)
 * [SetFont2D](#setfont2d)
 * [Text2D](#text2d)
 * [SetMaterial2D](#setmaterial2d)
 * [Plot2D](#plot2d)
 * [Line2D](#line2d)
 * [Rect2D](#rect2d)
 * [Oval2D](#oval2d)
 * [Poly2D](#poly2d)
 * [SetScale2D](#setscale2d)
 * [SetRotation2D](#setrotation2d)
 * [DrawImage2D](#drawimage2d)
 * [GrabImage2D](#grabimage2d)
 * [DrawSubRect2D](#drawsubrect2d)
 * [DrawImageLine2D](#drawimageline2d)
 * [DrawImageQuad2D](#drawimagequad2d)
 * [ApplyAlphaMap](#applyalphamap)
 * [ApplyMaskColor](#applymaskcolor)
 * [GetBuffer2D](#getbuffer2d)
 * [CommitBuffer2D](#commitbuffer2d)
 * [GetPixel2D](#getpixel2d)
 * [SetPixel2D](#setpixel2d)
 * [StringWidth2D](#stringwidth2d)

## <span id="intro"/>Introduction ##

This module provides functions for hardware-accelerated 2D drawing of graphics primitives, images, and text, to a bOGL OpenGL context. These features are particularly important for bOGL since the 3D graphics window overrides BlitzPlus's normal ability to render 2D graphics using the Blitz2D command set, and there is otherwise no way to render a HUD or even debug text to the screen.

`Draw2D` provides high-performance rotation, scaling, and transparency of images and text thanks to OpenGL hardware acceleration. It supports bitmap fonts, as well as simpler drawing with a selection of textured graphics primitives. `Draw2D`'s command set is based directly on the `Draw3D` library for Blitz3D, although its implementation is rather more efficient as it can use GL drawing commands directly.

This module does not depend on any other addon modules, and can be used with bOGL on its own.

## <span id="quickstart"/>Quick start ##

To quickly get set up drawing graphics with `Draw2D`:

1. `Include "bOGL-Addons/Draw2D.bb"` at the top of your main project file.
2. Call the initialization function (`InitDraw2D`) in your initialization block. This should happen before you do anything else with the 3D engine.
3. Even if you don't intend to use any 3D graphics, **you must** create your graphics window with `Graphics3D`, *not* Blitz's native `Graphics` command.
4. Add calls to `BeginDraw2D` and `EndDraw2D` in your main loop. Any `Draw2D` drawing commands must go in between these two; *no other* bOGL commands should appear between these.
5. Remember that if you want to draw over a 3D scene, `RenderWorld` should happen before your 2D drawing or it will cover it up!

That's all you need to do to support drawing hardware-accelerated images and text in your bOGL project!

If you use IDEal, consider adding `Draw2D.bb` to your IDEal project file in order to provide proper syntax highlighting and code support for the commands in this module. If you don't use IDEal, or just don't want to clutter your project, there's also an optional .decls file (`Draw2D_Optional.decls`) that you can add to your `userlibs` folder to get syntax highlighting that way.

## <span id="command-reference--api"/>Command reference / API ##

#### <span id="initdraw2d" />InitDraw2D ####
`InitDraw2D()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This is the initialization function for the `MD2` addon module. Call this *once* in your program initialization block, before doing anything with the 3D engine. This sets up necessary user data slots and other shared values for the library as a whole. Do not skip this step.  

#### <span id="setviewport2d" />SetViewport2D ####
`SetViewport2D(x, y, w, h)`  
**Parameters:** Position and size of the draw area.  
**Return value:** None.  
**Description:** This function sets the "drawing viewport": all draw operations have their locations given in terms of the origin of *this viewport*, not of the host window; and draw operations are clipped to its edges.  
By default the draw viewport is set to the size and resolution of the host 3D window, so you only need to bother with this if you want to clip it to a smaller area for some reason.  

#### <span id="setvirtualresolution2d" />SetVirtualResolution2D ####
`SetVirtualResolution2D(x, y)`  
**Parameters:** The resolution of the draw area.  
**Return value:** None.  
**Description:** This function sets the virtual resolution to use when translating coordinates for drawing commands.  
The virtual resolution is distinct from the *size* of the drawing viewport in that the viewport's size simply dictates how much absolute space it takes up in the graphics window; the resolution determines how many "points" there are within it. This is effectively like scaling the contents of a viewport up or down.  

#### <span id="begindraw2d" />BeginDraw2D ####
`BeginDraw2D()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This function sets up the graphics window for 2D drawing, storing whatever it was set to do before. You must call this in your main loop before using any actual drawing commands. Once you have done so it is not safe to use any 3D commands until `EndDraw2D` has restored the window to 3D mode.  

#### <span id="enddraw2d" />EndDraw2D ####
`EndDraw2D()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** This function restores the settings of the graphics window to whatever they were doing before it was put into 2D mode (i.e. back to 3D graphics). You must call this function after finishing your 2D drawing to restore the window's ability to draw in 3D.  

#### <span id="setcolor2d" />SetColor2D ####
`SetColor2D(r, g, b, a#)`  
**Parameters:** The colour (integer RGB) and alpha (float) to tint all drawing operations with.  
**Return value:** None.  
**Description:** This function sets the "operating draw colour" for the 2D engine. All drawing operations will be tinted with this colour and alpha, *including* text and images. To restore drawing operations to solid and untinted, call this function with the arguments 255, 255, 255, 1.0. If you want to draw several primitives and images with different colours and levels of transparency, you'll need to interleave several calls to this function in between drawing operations.  

#### <span id="setblend2d" />SetBlend2D ####
`SetBlend2D(mode)`  
**Parameters:** The blend mode to use for all drawing operations.  
**Return value:** None.  
**Description:** This function sets the "operating blend mode" for the 2D engine. All drawing operations will use the set blend mode, *including* text and images.  
Valid argument values for the `mode` parameter are:  
`B2D_BLEND_ADD`: additive blending. Anything drawn will appear to lighten whatever is behind it.  
`B2D_BLEND_MUL`: multiplicative blending. Anything drawn will appear to darken whatever is behind it (note that because the three colour channels are blended separately, multiplying e.g. red with green will produce black even though red and green have similar apparent brightness).  
`B2D_BLEND_ALPHA`: alpha blending. Graphics will obscure whatever is behind them unless the texture has an alpha component, or the operating draw colour has an alpha value of less than 1, in which case the pixel colour is a linear interpolation.  
`B2D_BLEND_NONE`: no blending. Pixels always simply overwrite whatever is behind them.  
The default blend mode is `B2D_BLEND_ALPHA`.  

#### <span id="setclscolor2d" />SetClsColor2D ####
`SetClsColor2D(r, g, b)`  
**Parameters:** The colour to clear the screen, represented as integer (0-255) RGB.  
**Return value:** None.  
**Description:** This function sets the colour that will be used to blank the viewport when the `Cls2D` command is used. If you're rendering over 3D, you will be relying on the 3D scene to clear the screen for you and thus this command is irrelevant.  
The default clear colour is black.  

#### <span id="cls2d" />Cls2D ####
`Cls2D()`  
**Parameters:** None.  
**Return value:** None.  
**Description:** Fills the viewport with a solid colour (black by default). This is useful to erase the results of whatever was drawn in the previous frame, important if graphics are moving.  
When drawing over 3D, remember that this will erase the 3D scene, so you won't want to use this command; the 3D scene will usually completely draw over the buffer anyway.  

#### <span id="loadfont2d" />LoadFont2D ####
`LoadFont2D%(fontname$)`  
**Parameters:** The name of the font file to load.  
**Return value:** A handle for the loaded font.  
**Description:** This function loads a font to use for drawing text. The "font" is actually a texture with the letters arranged in a specific fashion so that the engine knows where to look for the letters (a "bitmap font"). See `Media/Blitz.PNG` for an example of how to do this. The image should ideally also contain an alpha channel so that the space around the letters themselves does not get drawn.  
There is no font loaded by default. You must load and set a font before you can render any text with `Text2D`.  

#### <span id="freefont2d" />FreeFont2D ####
`FreeFont2D(font)`  
**Parameters:** The font to free.  
**Return value:** None.  
**Description:** This function frees a font that was previously loaded with `LoadFont2D`. After a font has been freed, it is no longer available for use.  

#### <span id="setfont2d" />SetFont2D ####
`SetFont2D(font[, size, height#, spacing#, italic#])`  
**Parameters:** The font to set; its size; a vertical scale; the letter spacing; how much to slant the letters.  
**Return value:** None.  
**Description:** This function sets the specified font as the current text drawing font to use with `Text2D`. All parameters other than the font itself are optional.  
The `size` parameter is a multiplier against the absolute size of the character cells. Since the size of the character cells is dependent on the resolution of the source texture, a letter from a 256x256 font will have half the native size of the same letter from a 512x512 font; if you set the `size` of the first font to 2.0, the letters will appear to have the same size onscreen. The default value for this parameter is 1.0.  
The `height` parameter is an extra multiplier that only affects the vertical size of letters. It is applied on top of `size`. The default value for this parameter is 1.0.  
The `spacing` parameter is used to move letters closer together or further apart by multiplying the offset between characters. Some fonts may look too spread out with the default placement so you can adjust the "kerning" manually with this option. The default value for this parameter is 1.0.  
The `italic` parameter is used to tilt letters so that they appear to be written in italic. The number refers to the number of points the top of the letters will be offset from the bottom (you can even reverse the effect by passing a negative value here). This effect is absolute, so it needs to take into account both the resolution of the source texture and the current value of `size`. The default value for this parameter is 0.0.

#### <span id="text2d" />Text2D ####
`Text2D(x#, y#, s$[, align])`  
**Parameters:** The position to draw the text; the text string to draw; whether to align the string.  
**Return value:** None.  
**Description:** This function draws the specified line of text at the specified coordinates.  
The optional `align` parameter determines whether the text should be centred on the point in its X axis, Y axis, or both. `align` is a bitarray whose value is created by `Or`-ing together flags:  
`B2D_TEXT_CENTX` to centre the text in the X dimension.  
`B2D_TEXT_CENTY` to centre the text in the Y dimension.  
By default text is not aligned n either axis, and appears below and to the right of the passed position.
`Text2D` is affected by the current draw rotation, which should be used if you want dynamically rotated text.  

#### <span id="setmaterial2d" />SetMaterial2D ####
`SetMaterial2D(tex)`  
**Parameters:** The texture to set as active material.  
**Return value:** None.  
**Description:** This function sets a texture to use as the "operating drawing material". The material is used to draw any graphics primitives (rectangles etc.). This means you can quickly construct dynamic shapes onscreen out of primitives that still look artistically consistent as they use the same textures as your actual art assets, or just use softedged white textures to give your drawing primitives anti-aliased smooth edges.  
You do not need to use a material for drawing with primitives, and can disable use of a material by passing 0 as the value.  
"Materials" themselves are just textures loaded with bOGL's standard `LoadTexture` command, and are not distinguished from other `Draw2D` images except by usage.  

#### <span id="plot2d" />Plot2D ####
`Plot2D(x#, y#[, radius#])`  
**Parameters:** Position of the point; size of the point.  
**Return value:** None.  
**Description:** This function plots a "point" at the specified coordinates. A point is basically a square centred on the passed position, textured with the operating drawing material. It is mainly intended for drawing very small points, and the default value for `radius` is 1.0 as a result.  

#### <span id="line2d" />Line2D ####
`Line2D(x1#, y1#, x2#, y2#[, width#])`  
**Parameters:** Position to draw from; position to draw to; width of the line.  
**Return value:** None.  
**Description:** This function draws a line across the screen between the specified points. The line will have the thickness specified by the optional `width` parameter, whose default value is 2.0. Lines work best with small thickness values. The line will be textured with the operating drawing material.  

#### <span id="rect2d" />Rect2D ####
`Rect2D(x#, y#, w#, h#[, fill, border#])`  
**Parameters:** Point to draw from; dimensions of rectangle; whether to fill; thickness of edge.  
**Return value:** None.  
**Description:** This function draws a rectangle from the position specified by `x, y` of dimensions `w, h`.  
If the optional `fill` parameter is True, the rectangle will be filled; otherwise it will just be a box and the middle part will be left empty. The default value for this parameter is True.  
If the optional `border` parameter is nonzero, a border will be drawn around the rectangle of that thickness (the position does not take this into account, so the border will reach up and left past the rectangle's origin). If the `fill` parameter is False, the border will also be duplicated on the inside edge of the rectangle. The default value for this parameter is 0.0.  
If both the `fill` and `border` parameters are zero, nothing will be visible.  

#### <span id="oval2d" />Oval2D ####
`Oval2D(xc#, yc#, xr#, yr#[, fill, border#])`  
**Parameters:** Point to draw from; dimensions of oval; whether to fill; thickness of edge.  
**Return value:** None.  
**Description:** This function draws an oval *centred on* the position specified by `x, y`, with its radii specified by `xr, yr`. Unlike the other primitive drawing commands, `Oval2D` is affected by the current draw rotation, which may be useful if you want to draw a rotated oval (e.g. slanted to one side).  
If the optional `fill` parameter is True, the oval will be filled; otherwise it will just be a ring and the middle part will be left empty. The default value for this parameter is True.  
If the optional `border` parameter is nonzero, a border will be drawn around the oval of that thickness. If the `fill` parameter is False, the border will also be duplicated on the inside edge of the ring. The default value for this parameter is 0.0.  
If both the `fill` and `border` parameters are zero, nothing will be visible.  

#### <span id="poly2d" />Poly2D ####
`Poly2D(x1#, y1#, x2#, y2#, x3#, y3#)`  
**Parameters:** Three positions in 2D viewport coordinates.  
**Return value:** None.  
**Description:** This function draws a triangle between the three passed points. This is useful for composing more complicated geometric shapes using the primitive drawing commands. This is the lowest-level primitive drawing command.  

#### <span id="setscale2d" />SetScale2D ####
`SetScale2D(xscale#, yscale#)`  
**Parameters:** The X scale multiplier; the Y scale multiplier.  
**Return value:** None.  
**Description:** This function sets the global scale multipliers for drawing operations. Image drawing is affected by these scale multipliers, which is useful for dynamically changing the size of drawn images.  
Primitive drawing commands and text are *not* affected by these scale multipliers.  

#### <span id="setrotation2d" />SetRotation2D ####
`SetRotation2D(angle#)`  
**Parameters:** The rotation angle.  
**Return value:** None.  
**Description:** This function sets the global rotation angle for drawing operations. Image drawing, text, and the oval primitive are affected by the current draw rotation.  
Primitive drawing commands other than `Oval2D` are *not* affected by the draw rotation.  

#### <span id="drawimage2d" />DrawImage2D ####
`DrawImage2D(img, x#, y#)`  
**Parameters:** The image; the coordinates to draw at.  
**Return value:** None.  
**Description:** This function draws an image. The image is drawn centred on the specified coordinates. This command is affected by the current draw rotation and scale as specified by `SetScale2D` and `SetRotation2D`.  
A `Draw2D` image is just a bOGL texture, loaded with bOGL's main `LoadTexture` command.  

#### <span id="grabimage2d" />GrabImage2D ####
`GrabImage2D(img, x, y, width, height)`  
**Parameters:** The image to grab from; the starting coordinate; the dimensions to grab.  
**Return value:** A newly-created image copy.  
**Description:** This function returns a completely new image, containing data copied from the image specified by `img`. The data is copied from the rectangle described by the start and size parameters, and the image copy is sized to contain this image data.  

#### <span id="drawsubrect2d" />DrawSubRect2D ####
`DrawSubRect2D(img, x#, y#, fromx, fromy, width, height)`  
**Parameters:** The image; the position to draw at; the origin and dimensions of the subimage to draw.  
**Return value:** None.  
**Description:** This function draws only a portion of an image, the portion within the rectangle described by the origin and dimension parameters. Otherwise, this function is identical to `DrawImage2D`, drawing the subrect centred on the specified coordinate, and affected by the current draw scale and rotation settings.  

#### <span id="drawimageline2d" />DrawImageLine2D ####
`DrawImageLine2D(img, x1#, y1#, x2#, y2#, width#[, stretch])`  
**Parameters:** The image; the point to draw from; the point to draw to; the width of the drawn line; how to apply the image.  
**Return value:** None.  
**Description:** This function draws a line from the point described by `x1, y1` to the point described by `x2, y2`, with the width specified in `width`. The line is interesting because instead of using the operating drawing material, it is drawn using the specified image as its texture. Making the line reasonably thick can make this a possible way to "wedge" an image between two points.  
The optional `stretch` parameter determines how the image's width is fitted into the length of the line (the image's height will be compressed or stretched to match the line width in all cases).  
If `stretch` is passed True, the image's entire width will always match the length of the line. The entire image will be distorted to fit within the entire line.  
If `stretch` is passed False, the image's width will not be distorted at all. The image will be fitted into the line starting from the left and wrapping over again from the right if the line is longer than the image, or cutting off partway if the line is shorter than the width of the image. This option would be a great way to texture a line with a repeating pattern that extends rather than stretches.  
The default value for the `stretch` parameter is False.  
This function is not affected by the current draw scale or rotation.  

#### <span id="drawimagequad2d" />DrawImageQuad2D ####
`DrawImageQuad2D(img, x1#, y1#, x2#, y2#, x3#, y3#, x4#, y4#)`  
**Parameters:** The image; four points in viewport coordinates.  
**Return value:** None.  
**Description:** This function draws a quad described by the four specified points, textured with the specified image. This function is not affected by the current draw scale or rotation.  

#### <span id="applyalphamap" />ApplyAlphaMap ####
`ApplyAlphaMap(img, amap)`  
**Parameters:** The image to receive an alpha channel; a Blitz bank containing alpha data.  
**Return value:** None.  
**Description:** This function applies an alpha map to an image.  
The alpha map is passed in a Blitz bank; the bank should represent each pixel as one byte with the alpha value being represented by the range 0-255. No other data should be present in the bank. It is the user's responsibility to ensure that the bank contains enough data. The alpha values will be *applied* to the image rather than composed with it; any existing alpha values will simply be lost.  

#### <span id="applymaskcolor" />ApplyMaskColor ####
`ApplyMaskColor(img, mask)`  
**Parameters:** The image to be masked; the colour to mask out, represented as a three-part integer RGB value.  
**Return value:** None.  
**Description:** This function applies a colour mask to an image.  
Any pixels in the image which match the colour passed in `mask` will be made transparent; all other pixels will be made opaque. This is an easy way to get rid of the "background" for an image generated in code and that therefore has no builtin alpha channel of its own.  
The colour passed in `mask` must be a three-part integer RGB value: R in the low byte, G in the second byte, B in the third byte. The high byte (which would otherwise represent alpha) is ignored.  

#### <span id="getbuffer2d" />GetBuffer2D ####
`GetBuffer2D(tex[, asBRGA])`  
**Parameters:** The texture to grab; whether to convert the pixel format.  
**Return value:** A newly-created pixel buffer for the texture.  
**Description:** This function creates a "pixel buffer" from an image/bOGL texture. The pixel buffer provides an easy way to read and edit the image's pixel data without needing to think about offsets and multipliers and so on.  
The optional `asBGRA` parameter determines whether the data in the buffer should be converted to the format used by Blitz images, or left in OpenGL format (faster). The default value for this parameter is True (i.e. do the conversion).  
This function is roughly analogous to Blitz's `LockBuffer`, except that the image is under no obligation to actually be locked and in fact can continue to be used safely.  

#### <span id="commitbuffer2d" />CommitBuffer2D ####
`CommitBuffer2D(buf)`  
**Parameters:** The pixel buffer.  
**Return value:** None.  
**Description:** After editing a pixel buffer, call this command to "commit" changes back to the image itself. The pixel data in the image proper will be updated upon calling this function, and the buffer object freed. If the pixel buffer contains Blitz-style pixel data, it will be converted back to OpenGL format (this may be slow).  
This function is roughly analogous to Blitz's `UnlockBuffer`, in that it is called after editing pixel data to commit those changes to an image.  

#### <span id="getpixel2d" />GetPixel2D ####
`GetPixel2D(buf, x, y)`  
**Parameters:** A pixel buffer; the coordinates of a pixel.  
**Return value:** The colour value of the pixel.  
**Description:** This function retrieves a pixel from a buffer by its coordinates. The returned pixel is represented in a pixel format as determined by the `GetBuffer2D` function that created the buffer; either OpenGL RGBA or Blitz BGRA format.  

#### <span id="setpixel2d" />SetPixel2D ####
`SetPixel2D(buf, x, y, pixel)`  
**Parameters:** A pixel buffer; the coordinates of a pixel; a colour to set.  
**Return value:** None.  
**Description:** This function sets a pixel in a buffer to the specified colour value. Whether the value passed to `pixel` needs to be in OpenGL RGBA format or Blitz's BGRA format is determined by the `GetBuffer2D` call that created the buffer.  

#### <span id="stringwidth2d" />StringWidth2D ####
`StringWidth2D(s$)`  
**Parameters:** A string.  
**Return value:** The width of the string in pixels.  
**Description:** This function returns the horizontal viewport size measured in pixels that would be needed to draw the specified string, if it were to be rendered using the currently set font. This is useful for working out whether a word will fit in a given area on the screen, or for dynamically sizing boxes around pieces of text.  


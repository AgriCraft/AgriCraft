package com.infinityraider.agricraft.renderers.tessellation;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import net.minecraft.util.math.Vec3d;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public interface ITessellator {

	/**
	 * Method to start constructing quads
	 *
	 * @param format vertex format
	 */
	void startDrawingQuads(VertexFormat format);

	/**
	 * Method to get all quads constructed
	 *
	 * @return list of quads, may be emtpy but never null
	 */
	List<BakedQuad> getQuads();

	/**
	 * Method to finalize drawing
	 */
	void draw();

	/**
	 * Gets the current vertex format the tessellator is drawing with
	 *
	 * @return the vertex format
	 */
	VertexFormat getVertexFormat();

	/**
	 * Adds a list of quads to be rendered
	 *
	 * @param quads list of quads
	 */
	void addQuads(List<BakedQuad> quads);

	/**
	 * Adds a vertex
	 *
	 * @param x the x-coordinate for the vertex
	 * @param y the y-coordinate for the vertex
	 * @param z the z-coordinate for the vertex
	 * @param u u value for the vertex
	 * @param v v value for the vertex
	 */
	void addVertexWithUV(float x, float y, float z, float u, float v);

	/**
	 * Adds a vertex
	 *
	 * @param x the x-coordinate for the vertex
	 * @param y the y-coordinate for the vertex
	 * @param z the z-coordinate for the vertex
	 * @param u u value for the vertex
	 * @param v v value for the vertex
	 * @param color color modifier
	 */
	void addVertexWithUV(float x, float y, float z, float u, float v, int color);

	/**
	 * Adds a vertex
	 *
	 * @param x the x-coordinate for the vertex
	 * @param y the y-coordinate for the vertex
	 * @param z the z-coordinate for the vertex
	 * @param icon the icon
	 * @param u u value for the vertex
	 * @param v v value for the vertex
	 */
	void addVertexWithUV(float x, float y, float z, TextureAtlasSprite icon, float u, float v);

	/**
	 * Adds a vertex
	 *
	 * @param x the x-coordinate for the vertex
	 * @param y the y-coordinate for the vertex
	 * @param z the z-coordinate for the vertex
	 * @param icon the icon
	 * @param u u value for the vertex
	 * @param v v value for the vertex
	 * @param color color modifier
	 */
	void addVertexWithUV(float x, float y, float z, TextureAtlasSprite icon, float u, float v, int color);

	/**
	 * Adds a vertex scaled by 1/16th of a block
	 *
	 * @param x the x-coordinate for the vertex
	 * @param y the y-coordinate for the vertex
	 * @param z the z-coordinate for the vertex
	 * @param icon the icon
	 * @param u u value for the vertex
	 * @param v v value for the vertex
	 */
	void addScaledVertexWithUV(float x, float y, float z, TextureAtlasSprite icon, float u, float v);

	/**
	 * Adds a vertex scaled by 1/16th of a block
	 *
	 * @param x the x-coordinate for the vertex
	 * @param y the y-coordinate for the vertex
	 * @param z the z-coordinate for the vertex
	 * @param icon the icon
	 * @param u u value for the vertex
	 * @param v v value for the vertex
	 * @param color color modifier
	 */
	void addScaledVertexWithUV(float x, float y, float z, TextureAtlasSprite icon, float u, float v, int color);

	/**
	 * Adds a quad for a scaled face, the face is defined by minimum and maximum
	 * coordinates
	 *
	 * @param minX minimum 2D x-coordinate of the face
	 * @param minY minimum 2D y-coordinate of the face
	 * @param maxX maximum 2D x-coordinate of the face
	 * @param maxY maximum 2D y-coordinate of the face
	 * @param face orientation of the face
	 * @param icon icon to render the face with
	 * @param offset offset of the face along its normal
	 */
	void drawScaledFace(float minX, float minY, float maxX, float maxY, EnumFacing face, TextureAtlasSprite icon, float offset);

	/**
	 * Adds a quad for a scaled face, the face is defined by minimum and maximum
	 * 2D coordinates
	 *
	 * @param minX minimum 2D x-coordinate of the face
	 * @param minY minimum 2D y-coordinate of the face
	 * @param maxX maximum 2D x-coordinate of the face
	 * @param maxY maximum 2D y-coordinate of the face
	 * @param face orientation of the face
	 * @param icon icon to render the face with
	 * @param offset offset of the face along its normal
	 * @param color color multiplier
	 */
	void drawScaledFace(float minX, float minY, float maxX, float maxY, EnumFacing face, TextureAtlasSprite icon, float offset, int color);

	/**
	 * Adds two quads for a scaled face, this face will have both sides drawn.
	 * The face is defined by minimum and maximum coordinates
	 *
	 * @param minX minimum 2D x-coordinate of the face
	 * @param minY minimum 2D y-coordinate of the face
	 * @param maxX maximum 2D x-coordinate of the face
	 * @param maxY maximum 2D y-coordinate of the face
	 * @param face orientation of the face
	 * @param icon icon to render the face with
	 * @param offset offset of the face along its normal
	 */
	void drawScaledFaceDouble(float minX, float minY, float maxX, float maxY, EnumFacing face, TextureAtlasSprite icon, float offset);

	/**
	 * Adds two quads for a scaled face, this face will have both sides drawn.
	 * The face is defined by minimum and maximum coordinates
	 *
	 * @param minX minimum 2D x-coordinate of the face
	 * @param minY minimum 2D y-coordinate of the face
	 * @param maxX maximum 2D x-coordinate of the face
	 * @param maxY maximum 2D y-coordinate of the face
	 * @param face orientation of the face
	 * @param icon icon to render the face with
	 * @param offset offset of the face along its normal
	 * @param color color multiplier
	 */
	void drawScaledFaceDouble(float minX, float minY, float maxX, float maxY, EnumFacing face, TextureAtlasSprite icon, float offset, int color);

	/**
	 * Adds 6 quads for a scaled prism, the prism is defined by maximum and
	 * minimum 3D coordinates
	 *
	 * @param minX minimum x-coordinate of the face
	 * @param minY minimum y-coordinate of the face
	 * @param minZ maximum z-coordinate of the face
	 * @param maxX maximum x-coordinate of the face
	 * @param maxY maximum y-coordinate of the face
	 * @param maxZ maximum z-coordinate of the face
	 * @param icon icon to render the prism with
	 */
	void drawScaledPrism(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, TextureAtlasSprite icon);

	/**
	 * Adds 6 quads for a scaled prism, the prism is defined by maximum and
	 * minimum 3D coordinates
	 *
	 * @param minX minimum x-coordinate of the face
	 * @param minY minimum y-coordinate of the face
	 * @param minZ maximum z-coordinate of the face
	 * @param maxX maximum x-coordinate of the face
	 * @param maxY maximum y-coordinate of the face
	 * @param maxZ maximum z-coordinate of the face
	 * @param icon icon to render the prism with
	 * @param color color multiplier
	 */
	void drawScaledPrism(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, TextureAtlasSprite icon, int color);

	/**
	 * Sets the translation components relative to the absolute coordinate
	 * system
	 *
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return this
	 */
	ITessellator setTranslation(double x, double y, double z);

	/**
	 * Sets the rotation components relative to the absolute coordinate system
	 *
	 * @param angle rotation ange
	 * @param x the x-direction
	 * @param y the y-direction
	 * @param z the z-direction
	 * @return this
	 */
	ITessellator setRotation(double angle, double x, double y, double z);

	/**
	 * Translates the matrix by a vector defined by a BlockPos
	 *
	 * @param pos the BlockPos
	 * @return this
	 */
	ITessellator translate(BlockPos pos);

	/**
	 * Translates the matrix by a vector defined by 3 coordinates
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return this
	 */
	ITessellator translate(double x, double y, double z);

	/**
	 * Rotates the matrix by an angle around the given direction, rotation
	 * center is the current origin
	 *
	 * @param angle angle to rotate by
	 * @param x the x direction
	 * @param y the y direction
	 * @param z the z direction
	 * @return this
	 */
	ITessellator rotate(double angle, double x, double y, double z);

	/**
	 * Scales along each axis with the corresponding factor
	 *
	 * @param x the x-axis scale factor
	 * @param y the y-axis scale factor
	 * @param z the z-axis scale factor
	 * @return this
	 */
	ITessellator scale(double x, double y, double z);

	/**
	 * Gets a TextureAtlasSprite icon from a ResourceLocation
	 *
	 * @param loc the ResourceLocation
	 * @return the icon
	 */
	TextureAtlasSprite getIcon(ResourceLocation loc);

	/**
	 * Sets the normal for the tessellator
	 *
	 * @param x the normal x direction
	 * @param y the normal y direction
	 * @param z the normal z direction
	 * @return this
	 */
	ITessellator setNormal(float x, float y, float z);

	/**
	 * Sets the normal for the tessellator
	 *
	 * @param vec the normal vector
	 * @return this
	 */
	ITessellator setNormal(Vec3d vec);

	/**
	 * Gets the current normal for the tessellator
	 *
	 * @return the normal vector
	 */
	Vec3d getNormal();

	/**
	 * Sets the current opaque color multiplier for the quads
	 *
	 * @param color the rgb color value
	 * @return this
	 */
	ITessellator setColor(int color);

	/**
	 * Sets the current transparent color multiplier for the quads
	 *
	 * @param color the rgba color value
	 * @return this
	 */
	ITessellator setColorTransparent(int color);

	/**
	 * Gets the current color value as an rgb int
	 *
	 * @return the color multiplier
	 */
	int getColor();

	/**
	 * Sets the current color value based on red, green and blue float values,
	 * all arguments should be between 0F and 1F
	 *
	 * @param red the rgb red value
	 * @param green the rgb green value
	 * @param blue the rgb blue value
	 * @return this
	 */
	ITessellator setColorRGB(float red, float green, float blue);

	/**
	 * Sets the current color value based on red, green, blue and alpha values,
	 * all arguments should be between 0F and 1F
	 *
	 * @param red the rgb red value
	 * @param green the rgb green value
	 * @param blue the rgb blue value
	 * @param alpha the rgb alpha value
	 * @return this
	 */
	ITessellator setColorRGBA(float red, float green, float blue, float alpha);

	/**
	 * Sets the current color value based on red, green and blue int values, all
	 * arguments should be between 0 and 255
	 *
	 * @param red the rgb red value
	 * @param green the rgb green value
	 * @param blue the rgb blue value
	 * @return this
	 */
	ITessellator setColorRGB(int red, int green, int blue);

	/**
	 * Sets the current color value based on red, green, blue and alpha values,
	 * all arguments should be between 0 and 255
	 *
	 * @param red the rgb red value
	 * @param green the rgb green value
	 * @param blue the rgb blue value
	 * @param alpha the rgb alpha value
	 * @return this
	 */
	ITessellator setColorRGBA(int red, int green, int blue, int alpha);

	/**
	 * @return current blue value as float, will be between 0 and 1
	 */
	float getRedValueFloat();

	/**
	 * @return current green value as float, will be between 0 and 1
	 */
	float getGreenValueFloat();

	/**
	 * @return current blue value as float, will be between 0 and 1
	 */
	float getBlueValueFloat();

	/**
	 * @return current alpha value as float, will be between 0 and 1
	 */
	float getAlphaValueFloat();

	/**
	 * @return current red value as int, will be between 0 and 255
	 */
	int getRedValueInt();

	/**
	 * @return current green value as int, will be between 0 and 255
	 */
	int getGreenValueInt();

	/**
	 * @return current red value as int, will be between 0 and 255
	 */
	int getBlueValueInt();

	/**
	 * @return current alpha value as int, will be between 0 and 255
	 */
	int getAlphaValueInt();

	/**
	 * Sets the brightness of the tessellator
	 *
	 * @param value the brightness value
	 * @return this
	 */
	ITessellator setBrightness(int value);

	/**
	 * Gets the brightness of the tessellator
	 *
	 * @return the brightness value
	 */
	int getBrightness();

	/**
	 * Sets the tint index value to use for the quads
	 *
	 * @param index the tint index
	 * @return this
	 */
	ITessellator setTintIndex(int index);

	/**
	 * Gets the current tint index value to use for the quads
	 *
	 * @return the tint index
	 */
	int getTintIndex();

	/**
	 * Sets if diffuse lighting should be applied to the quads
	 *
	 * @param value the diffuse lighting setting
	 * @return this
	 */
	ITessellator setApplyDiffuseLighting(boolean value);

	/**
	 * Gets if diffuse lighting is applied to the quads
	 *
	 * @return the diffuse lighting setting
	 */
	boolean getApplyDiffuseLighting();

	/**
	 * Pushes the current transformation matrix onto the stack.
	 */
	void pushMatrix();

	/**
	 * Pops the last matrix from the stack.
	 */
	void popMatrix();

}

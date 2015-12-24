package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.utility.TransformationMatrix;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Note that this class isn't used by vanilla minecraft, the matrix operations done by this class will be ignored by the calls made by vanilla to the Tessellator
 * I chose not to replace the vanilla Tessellator.instance field with this one for obvious reasons.
 */
@SideOnly(Side.CLIENT)
public class TessellatorV2 {
    public static final TessellatorV2 instance = new TessellatorV2();
    private static final Tessellator tessellator = Tessellator.getInstance();
    private static final WorldRenderer worldRenderer = tessellator.getWorldRenderer();

    /** Transformation matrix */
    private TransformationMatrix matrix = new TransformationMatrix();

    /** The static instance of the Tessellator. */
    public static TessellatorV2 getInstance() {
        return instance;
    }

    //---------------------------------------------
    //methods requiring some linear transformations
    //---------------------------------------------

    /**
     * Adds a vertex specifying both x,y,z and the texture u,v for it.
     */
    public void addVertexWithUV(double x, double y, double z, double u, double v) {
        this.setTextureUV(u, v);
        this.addVertex(x, y, z);
    }

    /**
     * Adds a vertex with the specified x,y,z to the current draw call. It will trigger a draw() if the buffer gets
     * full.
     */
    public void addVertex(double x, double y, double z) {
        //transform the coordinates
        double[] coords = this.matrix.transform(x, y, z);
        //apply the transformed coordinates as a vertex to the world renderer
        worldRenderer.func_181662_b(coords[0], coords[1], coords[2]);
        //finish the vertex
        worldRenderer.func_181675_d();
    }

    /**
     * Sets the texture coordinates.
     */
    public void setTextureUV(double u, double v) {
        worldRenderer.func_181673_a(u, v);
    }

    /**
     * Sets the translation relative to the absolute coordinates
     */
    public void setTranslation(double x, double y, double z) {
        this.matrix.setTranslation(x, y, z);
    }

    /**
     * Adds a translation to the current coordinate system
     */
    public void addTranslation(float x, float y, float z) {
        this.matrix.multiplyRightWith(new TransformationMatrix(x, y, z));
    }

    /**
     *  Sets the rotation relative to the absolute coordinates
     */
    public void setRotation(double angle, double x, double y, double z) {
        this.matrix.setRotation(angle, x, y, z);
    }

    /**
     *  Rotates around the current coordinate system
     */
    public void addRotation(double angle, double x, double y, double z) {
        this.matrix.multiplyRightWith(new TransformationMatrix(angle, x, y, z));
    }

    public void scale(double x, double y, double z) {
        this.matrix.scale(x, y, z);
    }

    /** Applies a coordinate transformation */
    @SuppressWarnings("unused")
    public void applyTranformation(TransformationMatrix transformationMatrix) {
        this.matrix.multiplyRightWith(transformationMatrix);
    }

    public TransformationMatrix getTransformationMatrix() {
        return this.matrix;
    }

    public void draw() {
        tessellator.draw();
    }

    /**
     * Sets draw mode in the worldRenderer to draw quads.
     */
    public void startDrawingQuads() {
        worldRenderer.func_181668_a(7, DefaultVertexFormats.BLOCK);
    }

    public void setBrightness(int value) {
        //worldRenderer.putBrightness4();
    }

    /**
     * Sets the RGB values as specified, converting from floats between 0 and 1 to integers from 0-255.
     */
    public void setColorOpaque_F(float red, float green, float blue) {
        this.setColorRGBA_F(red, green, blue, 255);
    }

    /**
     * Sets the RGBA values for the color, converting from floats between 0 and 1 to integers from 0-255.
     */
    public void setColorRGBA_F(float red, float green, float blue, float alpha) {
        this.setColorRGBA((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
    }

    /**
     * Sets the RGB values as specified, and sets alpha to opaque.
     */
    public void setColorOpaque(int red, int green, int blue) {
        this.setColorRGBA(red, green, blue, 255);
    }

    /**
     * Sets the RGBA values for the color. Also clamps them to 0-255.
     */
    public void setColorRGBA(int red, int green, int blue, int alpha) {

    }
}

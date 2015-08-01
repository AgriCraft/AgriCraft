package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.utility.TransformationMatrix;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;

@SideOnly(Side.CLIENT)
public class TessellatorV2 extends Tessellator {
    public static final TessellatorV2 instance = new TessellatorV2(2097152);
    private static final Tessellator tessellator = Tessellator.instance;

    private TransformationMatrix matrix = new TransformationMatrix();

    private TessellatorV2(int a) {}

    static {
        instance.defaultTexture = true;
    }

    //---------------------------------------------
    //methods requiring some linear transformations
    //---------------------------------------------

    /**
     * Adds a vertex specifying both x,y,z and the texture u,v for it.
     */
    @Override
    public void addVertexWithUV(double x, double y, double z, double u, double v) {
        this.setTextureUV(u, v);
        this.addVertex(x, y, z);
    }

    /**
     * Adds a vertex with the specified x,y,z to the current draw call. It will trigger a draw() if the buffer gets
     * full.
     */
    @Override
    public void addVertex(double x, double y, double z) {
        double[] coords = this.matrix.transform(x, y, z);
        tessellator.addVertex(coords[0], coords[1], coords[2]);
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
        this.matrix.multiplyLeftWith(new TransformationMatrix(x, y, z));
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
        this.matrix.multiplyLeftWith(new TransformationMatrix(angle, x, y, z));
    }

    //---------------
    //other overrides
    //---------------
    public int draw() {
        return tessellator.draw();
    }

    public TesselatorVertexState getVertexState(float x, float y, float z) {
        return tessellator.getVertexState(x, y, z);
    }

    public void setVertexState(TesselatorVertexState state) {
        tessellator.setVertexState(state);
    }

    /**
     * Sets draw mode in the tessellator to draw quads.
     */
    public void startDrawingQuads() {
        tessellator.startDrawing(7);
    }

    /**
     * Resets tessellator state and prepares for drawing (with the specified draw mode).
     */
    public void startDrawing(int drawMode) {
        tessellator.startDrawing(drawMode);
    }

    /**
     * Sets the texture coordinates.
     */
    public void setTextureUV(double u, double v) {
        tessellator.setTextureUV(u, v);
    }

    public void setBrightness(int value) {
        tessellator.setBrightness(value);
    }

    /**
     * Sets the RGB values as specified, converting from floats between 0 and 1 to integers from 0-255.
     */
    public void setColorOpaque_F(float red, float green, float blue) {
        tessellator.setColorOpaque_F(red, green, blue);
    }

    /**
     * Sets the RGBA values for the color, converting from floats between 0 and 1 to integers from 0-255.
     */
    public void setColorRGBA_F(float red, float green, float blue, float alpha) {
        tessellator.setColorRGBA_F(red, green, blue, alpha);
    }

    /**
     * Sets the RGB values as specified, and sets alpha to opaque.
     */
    public void setColorOpaque(int red, int green, int blue) {
        tessellator.setColorOpaque(red, green, blue);
    }

    /**
     * Sets the RGBA values for the color. Also clamps them to 0-255.
     */
    public void setColorRGBA(int red, int green, int blue, int alpha) {
        tessellator.setColorRGBA(red, green, blue, alpha);
    }

    public void func_154352_a(byte red, byte green, byte blue) {
        tessellator.func_154352_a(red, green, blue);
    }

    /**
     * Sets the color to the given opaque value (stored as byte values packed in an integer).
     */
    public void setColorOpaque_I(int value) {
        tessellator.setColorOpaque_I(value);
    }

    /**
     * Sets the color to the given color (packed as bytes in integer) and alpha values.
     */
    public void setColorRGBA_I(int value, int alpha) {
        tessellator.setColorRGBA_I(value, alpha);
    }

    /**
     * Disables colors for the current draw call.
     */
    public void disableColor() {
        tessellator.disableColor();
    }

    /**
     * Sets the normal for the current draw call.
     */
    public void setNormal(float x, float y, float z) {
        tessellator.setNormal(x, y, z);
    }

}

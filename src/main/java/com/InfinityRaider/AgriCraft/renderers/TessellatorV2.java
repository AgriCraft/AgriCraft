package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.utility.TransformationMatrix;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

/**
 * Note that this class isn't used by vanilla minecraft, the matrix operations done by this class will be ignored by the calls made by vanilla to the Tessellator
 * I chose not to replace the vanilla Tessellator.instance field with this one for obvious reasons.
 */
@SideOnly(Side.CLIENT)
public class TessellatorV2 {
    private static final Map<WorldRenderer, TessellatorV2> instances = new HashMap<>();
    private final Tessellator tessellator ;
    private final WorldRenderer worldRenderer;

    /** Transformation matrix */
    private TransformationMatrix matrix = new TransformationMatrix();

    public static TessellatorV2 getInstance() {
        return getInstance(Tessellator.getInstance());
    }

    public static TessellatorV2 getInstance(WorldRenderer renderer) {
        return instances.containsKey(renderer) ? instances.get(renderer) : new TessellatorV2(renderer);
    }

    public static TessellatorV2 getInstance(Tessellator tessellator) {
        return instances.containsKey(tessellator.getWorldRenderer()) ? instances.get(tessellator.getWorldRenderer()) : new TessellatorV2(tessellator);
    }

    private TessellatorV2(WorldRenderer worldRenderer) {
        this.worldRenderer = worldRenderer;
        this.tessellator = null;
        instances.put(worldRenderer, this);
    }

    private TessellatorV2(Tessellator tessellator) {
        this.worldRenderer = tessellator.getWorldRenderer();
        this.tessellator = tessellator;
        instances.put(worldRenderer, this);
    }

    /** Color values */
    float red;
    float green;
    float blue;
    float alpha;

    /** Brightness value */
    int light;

    public void draw() {
        if(tessellator != null) {
            tessellator.draw();
        }
    }

    /**
     * Sets draw mode in the worldRenderer to draw quads.
     */
    public void startDrawingQuads() {
        if(tessellator != null) {
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        }
    }

    /**
     * Adds a vertex specifying both x,y,z and the texture u,v for it.
     */
    public void addVertexWithUV(double x, double y, double z, float u, float v) {
        double[] coords = this.matrix.transform(x, y, z);
        worldRenderer.pos(coords[0], coords[1], coords[2]);
        worldRenderer.color(red, green, blue, alpha);
        setTextureUV(u, v);
        worldRenderer.lightmap(light >> 16 & 65535, light& 65535);
        worldRenderer.endVertex();
    }

    /**
     * Sets the texture coordinates.
     */
    public void setTextureUV(double u, double v) {
        worldRenderer.tex(u, v);
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
    public void addTranslation(double x, double y, double z) {
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

    public void setBrightness(int value) {
        this.light = value;
    }

    /**
     * Sets the RGB values as specified, converting from floats between 0 and 1 to integers from 0-255.
     */
    public void setColorOpaque_F(float red, float green, float blue) {
        this.setColorRGBA_F(red, green, blue, 1);
    }

    /**
     * Sets the RGBA values for the color, converting from floats between 0 and 1 to integers from 0-255.
     */
    public void setColorRGBA_F(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
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
        this.setColorRGBA_F(((float) red)/255.0F, ((float) green)/255.0F, ((float) blue)/255.0F, ((float) alpha)/255.0F);
    }
}

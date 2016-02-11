package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.utility.TransformationMatrix;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is to have a Tessellator like the one in 1.7.10
 * It's also extended with any possible linear transformation you can think of
 */
@SideOnly(Side.CLIENT)
public class TessellatorV2 {
	
    private static final Map<WorldRenderer, TessellatorV2> instances = new HashMap<>();
	
    private final Tessellator tessellator ;
    private final WorldRenderer worldRenderer;
	
	private Deque<TransformationMatrix> matrixes;
	
	private TessellatorV2(Tessellator tessellator) {
        this.worldRenderer = tessellator.getWorldRenderer();
        this.tessellator = tessellator;
		this.matrixes = new ArrayDeque<>();
		this.matrixes.add(new TransformationMatrix());
		// Leaking...
        instances.put(worldRenderer, this);
    }
	
	private TessellatorV2(WorldRenderer worldRenderer) {
        this.worldRenderer = worldRenderer;
        this.tessellator = null;
		this.matrixes = new ArrayDeque<>();
		this.matrixes.add(new TransformationMatrix());
        instances.put(worldRenderer, this);
    }

    public static TessellatorV2 getInstance() {
        return getInstance(Tessellator.getInstance());
    }

    public static TessellatorV2 getInstance(WorldRenderer renderer) {
        return instances.containsKey(renderer) ? instances.get(renderer) : new TessellatorV2(renderer);
    }

    public static TessellatorV2 getInstance(Tessellator tessellator) {
        return instances.containsKey(tessellator.getWorldRenderer()) ? instances.get(tessellator.getWorldRenderer()) : new TessellatorV2(tessellator);
    }

    /** Color values */
    float red;
    float green;
    float blue;
    float alpha;

    /** Brightness value */
    int light1;
    int light2;

    public void draw() {
        if(tessellator != null) {
            tessellator.draw();
        } else {
            this.worldRenderer.finishDrawing();
        }
    }

    /**
     * Sets draw mode in the worldRenderer to draw quads.
     */
    public void startDrawingQuads() {
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
    }

    /**
     * Adds a vertex specifying both x,y,z and the texture u,v for it.
     */
    public void addVertexWithUV(double x, double y, double z, float u, float v) {
        double[] coords = this.matrixes.getFirst().transform(x, y, z);
        worldRenderer.pos(coords[0], coords[1], coords[2]);
        worldRenderer.color(red, green, blue, alpha);
        worldRenderer.tex(u, v);
        worldRenderer.lightmap(light1, light2);
        worldRenderer.endVertex();
    }

    /**
     * Sets the translation relative to the absolute coordinates
     */
    public void setTranslation(double x, double y, double z) {
        this.matrixes.getFirst().setTranslation(x, y, z);
    }

    /**
     * Adds a translation to the current coordinate system
     */
    public void translate(double x, double y, double z) {
        this.matrixes.getFirst().multiplyRightWith(new TransformationMatrix(x, y, z));
    }

    /**
     *  Sets the rotation relative to the absolute coordinates
     */
    public void setRotation(double angle, double x, double y, double z) {
        this.matrixes.getFirst().setRotation(angle, x, y, z);
    }

    /**
     *  Rotates around the current coordinate system
     */
    public void addRotation(double angle, double x, double y, double z) {
        this.matrixes.getFirst().multiplyRightWith(new TransformationMatrix(angle, x, y, z));
    }

    public void scale(double x, double y, double z) {
        this.matrixes.getFirst().scale(x, y, z);
    }

    /** Applies a coordinate transformation */
    @SuppressWarnings("unused")
    public void applyTranformation(TransformationMatrix transformationMatrix) {
        this.matrixes.getFirst().multiplyRightWith(transformationMatrix);
    }

    public TransformationMatrix getTransformationMatrix() {
        return this.matrixes.getFirst();
    }

    public void setBrightness(int value) {
        light1 = value >> 16 & 65535;
        light2 = value & 65535;
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
    public void setColorOpaque_I(int red, int green, int blue) {
        this.setColorRGBA_I(red, green, blue, 255);
    }

    /**
     * Sets the RGBA values for the color. Also clamps them to 0-255.
     */
    public void setColorRGBA_I(int red, int green, int blue, int alpha) {
        this.setColorRGBA_F(((float) red)/255.0F, ((float) green)/255.0F, ((float) blue)/255.0F, ((float) alpha)/255.0F);
    }
	
	/**
	 * Push & Pop are better, because then you don't accrue roundoff errors.
	 */
	public void pushMatrix() {
		this.matrixes.push(new TransformationMatrix(this.matrixes.getFirst()));
	}
	
	public void popMatrix() {
		if (this.matrixes.size() > 1) {
			this.matrixes.pop();
		}
	}
}

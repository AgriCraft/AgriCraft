package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.utility.TransformationMatrix;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;

@SideOnly(Side.CLIENT)
public class TessellatorV2 extends Tessellator {
    public static final TessellatorV2 instance = new TessellatorV2(2097152);

    private TransformationMatrix matrix = new TransformationMatrix();

    private TessellatorV2(int a) {}

    static {
        instance.defaultTexture = true;
    }

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
        super.addVertex(coords[0], coords[1], coords[2]);
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

}

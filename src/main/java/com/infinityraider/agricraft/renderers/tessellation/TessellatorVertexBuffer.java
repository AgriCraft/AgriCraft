package com.infinityraider.agricraft.renderers.tessellation;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class TessellatorVertexBuffer extends TessellatorAbstractBase {
    private static final Map<VertexBuffer, TessellatorVertexBuffer> instances = new HashMap<>();

    private final Tessellator tessellator;
    private final VertexBuffer buffer;

    private TessellatorVertexBuffer(VertexBuffer buffer, Tessellator tessellator) {
        this.buffer = buffer;
        this.tessellator = tessellator;
    }

    public static TessellatorVertexBuffer getInstance() {
        return getInstance(Tessellator.getInstance());
    }

    public static TessellatorVertexBuffer getInstance(Tessellator tessellator) {
        final VertexBuffer buffer = tessellator.getBuffer();
        if (instances.containsKey(buffer)) {
            return instances.get(buffer).reset();
        } else {
            final TessellatorVertexBuffer tess = new TessellatorVertexBuffer(buffer, tessellator);
            instances.put(buffer, tess);
            return tess;
        }
    }

    public static TessellatorVertexBuffer getInstance(VertexBuffer buffer) {
        if (instances.containsKey(buffer)) {
            return instances.get(buffer).reset();
        } else {
            final TessellatorVertexBuffer tess = new TessellatorVertexBuffer(buffer, null);
            instances.put(buffer, tess);
            return tess;
        }
    }

    /**
     * @return VertexBuffer object which this is currently tessellating vertices for
     */
    public VertexBuffer getVertexBuffer() {
        return buffer;
    }

    /**
     * Sub delegated method call of the startDrawingQuads() method to ensure correct call chain
     */
    @Override
    protected void onStartDrawingQuadsCall() {
        buffer.begin(GL11.GL_QUADS, getVertexFormat());
    }
    /**
     * Method to get all quads constructed
     * @return emtpy list, no quads are constructed here
     */
    @Override
    public List<BakedQuad> getQuads() {
        return ImmutableList.of();
    }

    /**
     * Sub delegated method call of the draw() method to ensure correct call chain
     */
    @Override
    protected void onDrawCall() {
        if (tessellator != null) {
            tessellator.draw();
        } else {
            buffer.finishDrawing();
        }
    }

    /**
     * Adds a list of quads to be rendered
     * @param quads list of quads
     */
    @Override
    public void addQuads(List<BakedQuad> quads) {
        for(BakedQuad quad : quads) {
            buffer.addVertexData(quad.getVertexData());
        }
    }

    /**
     * Adds a vertex
     * @param x the x-coordinate for the vertex
     * @param y the y-coordinate for the vertex
     * @param z the z-coordinate for the vertex
     * @param u u value for the vertex
     * @param v v value for the vertex
     * @param color color modifier
     */
    @Override
    public void addVertexWithUV(float x, float y, float z, float u, float v, int color) {
        double[] coords = this.getTransformationMatrix().transform(x, y, z);
        buffer.pos(coords[0], coords[1], coords[2]);
        buffer.color(getRedValueInt(), getGreenValueInt(), getBlueValueInt(), getAlphaValueInt());
        buffer.tex(u, v);
        buffer.lightmap(getBrightness()>> 16 & 65535, getBrightness() & 65535);
        //buffer.normal(getNormal().x, getNormal().y, getNormal().z);
        buffer.endVertex();
    }

    /**
     * Resets the tessellator
     * @return this
     */
    private TessellatorVertexBuffer reset() {
        this.resetMatrix();
        this.setColorRGBA(1, 1, 1, 1);
        this.setBrightness(15 << 24);
        return this;
    }
}

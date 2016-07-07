package com.infinityraider.agricraft.renderers.tessellation;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class VertexData {
    private final VertexFormat format;
    private float x, y, z;
    private float u, v;
    private float r, g, b, a;
    private float n_X, n_Y, n_Z;

    public VertexData(VertexFormat format) {
        this.format = format;
    }

    public VertexData(VertexFormat format, float x, float y, float z) {
        this(format);
        this.setXYZ(x, y, z);
    }

    public VertexData(VertexFormat format, float x, float y, float z, float u, float v) {
        this(format, x, y, z);
        this.setUV(u, v);
    }

    public VertexData setXYZ(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public VertexData setUV(float u, float v) {
        this.u = u;
        this.v = v;
        return this;
    }

    public VertexData setRGB(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        return this;
    }

    public VertexData setRGBA(float r, float g, float b, float a) {
        this.a = a;
        return this.setRGB(r, g, b);
    }

    public VertexData setNormal(float x, float y, float z) {
        this.n_X = x;
        this.n_Y = y;
        this.n_Z = z;
        return this;
    }

    public void applyVertexData(UnpackedBakedQuad.Builder builder) {
        for(int index = 0; index < format.getElementCount(); index++) {
            applyVertexDataForType(index, format.getElement(index).getUsage(), builder);
        }
    }

    private void applyVertexDataForType(int index, VertexFormatElement.EnumUsage type, UnpackedBakedQuad.Builder builder) {
        switch(type) {
            case POSITION:
                builder.put(index, x, y, z, 1);
                break;
            case UV:
                builder.put(index, u, v, 0, 1);
                break;
            case COLOR:
                builder.put(index, r, g, b, a);
                break;
            case NORMAL:
                builder.put(index, n_X, n_Y, n_Z, 0);
                break;
            case PADDING:
                //TODO: figure this one out
                builder.put(index, 1, 1, 1, 1);
                break;
            case GENERIC:
                //TODO: figure this one out
                builder.put(index, 1, 1, 1, 1);

        }
    }
}

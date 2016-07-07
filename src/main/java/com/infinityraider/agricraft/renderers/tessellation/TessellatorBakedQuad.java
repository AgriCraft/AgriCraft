package com.infinityraider.agricraft.renderers.tessellation;

import com.google.common.base.Function;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to construct vertices
 */
@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class TessellatorBakedQuad extends TessellatorAbstractBase {
    /** Draw mode when no vertices are being constructed */
    public static final int DRAW_MODE_NOT_DRAWING = -1;
    /** Draw mode when vertices are being constructed for quads */
    public static final int DRAW_MODE_QUADS = 4;

    /** The VertexCreator instance */
    private static final TessellatorBakedQuad INSTANCE = new TessellatorBakedQuad();

    /** Getter for the VertexCreator instance */
    public static TessellatorBakedQuad getInstance() {
        return INSTANCE;
    }

    /** Currently constructed quads */
    private final List<BakedQuad> quads;
    /** Currently constructed vertices */
    private final List<VertexData> vertexData;

    /** Current drawing mode */
    private int drawMode;
    /** Texture function */
    private Function<ResourceLocation, TextureAtlasSprite> textureFunction;

    /** Private constructor */
    private TessellatorBakedQuad() {
        super();
        this.quads = new ArrayList<>();
        this.vertexData = new ArrayList<>();
        this.drawMode = DRAW_MODE_NOT_DRAWING;
    }

    /**
     * Sub delegated method call of the startDrawingQuads() method to ensure correct call chain
     */
    @Override
    protected void onStartDrawingQuadsCall() {
        this.startDrawing(DRAW_MODE_QUADS);
    }

    /**
     * Method to start constructing vertices
     * @param mode draw mode
     */
    public void startDrawing(int mode) {
        if(drawMode == DRAW_MODE_NOT_DRAWING) {
            this.drawMode = mode;
        } else {
            throw new RuntimeException("ALREADY CONSTRUCTING VERTICES");
        }
    }

    /**
     * Method to get all quads constructed
     * @return list of quads, may be emtpy but never null
     */
    @Override
    public List<BakedQuad> getQuads() {
        List<BakedQuad> list = new ArrayList<>();
        list.addAll(this.quads);
        return list;
    }

    /**
     * Sub delegated method call of the draw() method to ensure correct call chain
     */
    @Override
    protected void onDrawCall() {
        if(drawMode != DRAW_MODE_NOT_DRAWING) {
            quads.clear();
            vertexData.clear();
            this.drawMode = DRAW_MODE_NOT_DRAWING;
            this.textureFunction = null;
        } else {
            throw new RuntimeException("NOT CONSTRUCTING VERTICES");
        }
    }

    /**
     * Adds a list of quads to be rendered
     * @param quads list of quads
     */
    @Override
    public void addQuads(List<BakedQuad> quads) {
        if(drawMode != DRAW_MODE_NOT_DRAWING) {
            this.quads.addAll(quads);
        } else {
            throw new RuntimeException("NOT CONSTRUCTING VERTICES");
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
        if(drawMode == DRAW_MODE_NOT_DRAWING) {
            throw new RuntimeException("NOT CONSTRUCTING VERTICES");
        }
        double[] coords = this.getTransformationMatrix().transform(x, y, z);
        vertexData.add(new VertexData(getVertexFormat(), (float) coords[0], (float) coords[1], (float) coords[2], u, v)
                .setRGBA(getRedValueFloat(), getGreenValueFloat(), getBlueValueFloat(), getAlphaValueFloat())
                .setNormal((float)getNormal().xCoord, (float)getNormal().yCoord, (float)getNormal().zCoord));
        if(vertexData.size() == drawMode) {
            UnpackedBakedQuad.Builder quadBuilder = new UnpackedBakedQuad.Builder(getVertexFormat());
            quadBuilder.setQuadTint(getTintIndex());
            quadBuilder.setApplyDiffuseLighting(getApplyDiffuseLighting());
            for(VertexData vertex : vertexData) {
                vertex.applyVertexData(quadBuilder);
            }
            quads.add(quadBuilder.build());
            vertexData.clear();
        }
    }

    @Override
    public TextureAtlasSprite getIcon(ResourceLocation loc) {
        if(this.textureFunction == null) {
            return super.getIcon(loc);
        } else {
            return this.textureFunction.apply(loc);
        }
    }

    public TessellatorBakedQuad setTextureFunction(Function<ResourceLocation, TextureAtlasSprite> function) {
        this.textureFunction = function;
        return this;
    }
}

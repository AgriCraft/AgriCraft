package com.infinityraider.agricraft.render.plant;

import com.infinityraider.agricraft.api.v1.misc.IAgriPlantQuadGenerator;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tessellation.TessellatorBakedQuad;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AgriPlantQuadGenerator implements IAgriPlantQuadGenerator, IRenderUtilities {
    private static final AgriPlantQuadGenerator INSTANCE = new AgriPlantQuadGenerator();

    public static AgriPlantQuadGenerator getInstance() {
        return INSTANCE;
    }

    private final ThreadLocal<TessellatorBakedQuad> tess;

    private AgriPlantQuadGenerator() {
        this.tess = ThreadLocal.withInitial(this::getQuadTessellator);
    }

    @Nonnull
    @Override
    public List<BakedQuad> bakeQuadsForCrossPattern(@Nonnull Direction direction, @Nonnull ResourceLocation sprite) {
        TextureAtlasSprite icon = this.getSprite(sprite);
        TessellatorBakedQuad tessellator = this.tess.get();

        tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
        tessellator.setCurrentFace(direction);

        tessellator.pushMatrix();
        tessellator.translate(0.5f, 0, 0.5f);
        tessellator.rotate(45, 0, 1, 0);
        tessellator.translate(-0.5f, 0, -0.5f);
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.NORTH, icon, 8);
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.EAST, icon, 8);
        tessellator.popMatrix();

        List<BakedQuad> quads = tessellator.getQuads();
        tessellator.draw();
        return quads;
    }

    @Nonnull
    @Override
    public List<BakedQuad> bakeQuadsForHashPattern(@Nonnull Direction direction, @Nonnull ResourceLocation sprite) {
        TextureAtlasSprite icon = this.getSprite(sprite);
        TessellatorBakedQuad tessellator = this.tess.get();

        tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
        tessellator.setCurrentFace(direction);

        tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.NORTH, icon, 4);
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.EAST, icon, 4);
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.NORTH, icon, 12);
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.EAST, icon, 12);

        List<BakedQuad> quads = tessellator.getQuads();
        tessellator.draw();
        return quads;
    }
}

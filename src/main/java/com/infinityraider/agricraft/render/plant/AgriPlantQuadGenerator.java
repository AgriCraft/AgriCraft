package com.infinityraider.agricraft.render.plant;

import com.infinityraider.agricraft.api.v1.misc.IAgriPlantQuadGenerator;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AgriPlantQuadGenerator implements IAgriPlantQuadGenerator, IRenderUtilities {
    private static final AgriPlantQuadGenerator INSTANCE = new AgriPlantQuadGenerator();

    public static AgriPlantQuadGenerator getInstance() {
        return INSTANCE;
    }

    private final ThreadLocal<ITessellator> tess;

    private AgriPlantQuadGenerator() {
        this.tess = ThreadLocal.withInitial(this::getBakedQuadTessellator);
    }

    @Nonnull
    @Override
    public List<BakedQuad> bakeQuadsForHashPattern(@Nullable Direction direction, @Nonnull ResourceLocation sprite, int yOffset) {
        TextureAtlasSprite icon = this.getSprite(sprite);
        ITessellator tessellator = this.tess.get();

        tessellator.startDrawingQuads();
        tessellator.setFace(direction);

        tessellator.pushMatrix();

        tessellator.translate(0, yOffset, 0);

        tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.NORTH, icon, 4);
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.EAST, icon, 4);
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.NORTH, icon, 12);
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.EAST, icon, 12);

        tessellator.popMatrix();

        List<BakedQuad> quads = tessellator.getQuads();
        tessellator.draw();
        return quads;
    }

    @Nonnull
    @Override
    public List<BakedQuad> bakeQuadsForCrossPattern(@Nullable Direction direction, @Nonnull ResourceLocation sprite, int yOffset) {
        TextureAtlasSprite icon = this.getSprite(sprite);
        ITessellator tessellator = this.tess.get();

        tessellator.startDrawingQuads();
        tessellator.setFace(direction);

        tessellator.pushMatrix();

        tessellator.translate(0.5f, yOffset, 0.5f);
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
    public List<BakedQuad> bakeQuadsForPlusPattern(@Nullable Direction direction, @Nonnull ResourceLocation sprite, int yOffset) {
        TextureAtlasSprite icon = this.getSprite(sprite);
        ITessellator tessellator = this.tess.get();

        tessellator.startDrawingQuads();
        tessellator.setFace(direction);

        tessellator.pushMatrix();

        tessellator.translate(0, 12.0F*yOffset/16.0F, 0);

        tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.NORTH, icon, 4.001F, 0, 0, 16, 16);
        tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.NORTH, icon, 3.999F, 0, 0, 16, 16);

        tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.EAST, icon, 4.001F, 0, 0, 16, 16);
        tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.EAST, icon, 3.999F, 0, 0, 16, 16);

        tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.NORTH, icon, 12.001F, 0, 0, 16, 16);
        tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.NORTH, icon, 11.999F, 0, 0, 16, 16);

        tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.EAST, icon, 12.001F, 0, 0, 16, 16);
        tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.EAST, icon, 11.999F, 0, 0, 16, 16);

        tessellator.popMatrix();

        List<BakedQuad> quads = tessellator.getQuads();
        tessellator.draw();
        return quads;
    }

    @Nonnull
    @Override
    public List<BakedQuad> bakeQuadsForRhombusPattern(@Nullable Direction direction, @Nonnull ResourceLocation sprite, int yOffset) {
        TextureAtlasSprite icon = this.getSprite(sprite);
        ITessellator tessellator = this.tess.get();

        tessellator.startDrawingQuads();
        tessellator.setFace(direction);

        float d = MathHelper.sqrt(128);

        tessellator.pushMatrix();
        tessellator.translate(0, d*yOffset/16.0F, 0.5f);
        tessellator.rotate(45, 0, 1, 0);

        tessellator.drawScaledFaceDouble(0, 0, d, d, Direction.NORTH, icon, 0);
        tessellator.drawScaledFaceDouble(0, 0, d, d, Direction.NORTH, icon, d);
        tessellator.drawScaledFaceDouble(0, 0, d, d, Direction.EAST, icon, 0);
        tessellator.drawScaledFaceDouble(0, 0, d, d, Direction.EAST, icon, d);

        tessellator.popMatrix();

        List<BakedQuad> quads = tessellator.getQuads();
        tessellator.draw();
        return quads;
    }
}

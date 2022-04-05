package com.infinityraider.agricraft.render.plant;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.client.AgriPlantRenderType;
import com.infinityraider.agricraft.api.v1.client.IAgriPlantQuadGenerator;
import com.infinityraider.agricraft.api.v1.client.PlantQuadBakeEvent;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;
import java.util.function.IntFunction;

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

    public ITessellator getTessellator() {
        return this.tess.get();
    }

    public List<BakedQuad> bakeQuads(IAgriGrowable plant, IAgriGrowthStage stage, String type,
                                     @Nullable Direction direction, List<ResourceLocation> textures) {
        return AgriPlantRenderType.fetchFromIdentifier(type).map(renderType -> {
            IntFunction<TextureAtlasSprite> spriteFunction = (i) -> this.getSprite(textures.get(i));
            List<BakedQuad> quads = renderType.bakedQuads(plant, stage, direction, spriteFunction);
            PlantQuadBakeEvent event = new PlantQuadBakeEvent(this, plant, stage, direction, textures, spriteFunction, renderType, quads);
            MinecraftForge.EVENT_BUS.post(event);
            return event.getOutputQuads();
        }).orElse(ImmutableList.of());
    }

    @SuppressWarnings("deprecation")
    public List<BakedQuad> fetchQuads(@Nonnull ResourceLocation location) {
        ModelLoader loader = ModelLoader.instance();
        if(loader == null) {
            return ImmutableList.of();
        }
        BakedModel model = loader.getBakedModel(location, ModelRotation.X0_Y0, this::getSprite);
        return model == null ? ImmutableList.of() : model.getQuads(null, null, this.getRandom());
    }

    @Nonnull
    @Override
    public List<BakedQuad> bakeQuadsForHashPattern(IAgriGrowable plant, IAgriGrowthStage stage, @Nullable Direction face, IntFunction<TextureAtlasSprite> spriteFunc) {
        if(face != null) {
            return ImmutableList.of();
        }

        ITessellator tessellator = this.getTessellator();

        tessellator.startDrawingQuads();
        tessellator.setFace((Direction) null);

        tessellator.pushMatrix();

        double height = plant.getPlantHeight(stage);
        int layer = 0;
        int layers = (int) Math.ceil(height / 16.0);
        while ((16 * layer) < height) {
            TextureAtlasSprite sprite = spriteFunc.apply(layers - layer - 1);

            tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.NORTH, sprite, 4);
            tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.EAST, sprite, 4);
            tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.NORTH, sprite, 12);
            tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.EAST, sprite, 12);

            tessellator.translate(0, 1, 0);
            layer++;
        }

        tessellator.popMatrix();

        List<BakedQuad> quads = tessellator.getQuads();
        tessellator.draw();
        return quads;
    }

    @Nonnull
    public List<BakedQuad> bakeQuadsForCrossPattern(IAgriGrowable plant, IAgriGrowthStage stage, @Nullable Direction face, IntFunction<TextureAtlasSprite> spriteFunc) {
        if(face != null) {
            return ImmutableList.of();
        }

        ITessellator tessellator = this.getTessellator();

        tessellator.startDrawingQuads();
        tessellator.setFace((Direction) null);

        tessellator.pushMatrix();

        tessellator.translate(0.5f, 0, 0.5f);
        tessellator.rotate(45, 0, 1, 0);
        tessellator.translate(-0.5f, 0, -0.5f);

        double height = plant.getPlantHeight(stage);
        int layers = (int) Math.ceil(height / 16.0);
        int layer = 0;
        while ((16 * layer) < height) {
            TextureAtlasSprite sprite = spriteFunc.apply(layers - layer - 1);

            tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.NORTH, sprite, 8);
            tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.EAST, sprite, 8);

            tessellator.translate(0, 1, 0);
            layer++;
        }

        tessellator.popMatrix();

        List<BakedQuad> quads = tessellator.getQuads();
        tessellator.draw();
        return quads;
    }

    @Nonnull
    public List<BakedQuad> bakeQuadsForPlusPattern(IAgriGrowable plant, IAgriGrowthStage stage, @Nullable Direction face, IntFunction<TextureAtlasSprite> spriteFunc) {
        if(face != null) {
            return ImmutableList.of();
        }

        ITessellator tessellator = this.getTessellator();

        tessellator.startDrawingQuads();
        tessellator.setFace((Direction) null);

        tessellator.pushMatrix();

        double height = plant.getPlantHeight(stage);
        int layers = (int) Math.ceil(height / 12.0);
        int layer = 0;
        while ((12 * layer) < height) {
            TextureAtlasSprite sprite = spriteFunc.apply(layers - layer - 1);

            tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.NORTH, sprite, 4.001F, 0, 0, 16, 16);
            tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.NORTH, sprite, 3.999F, 0, 0, 16, 16);

            tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.EAST, sprite, 4.001F, 0, 0, 16, 16);
            tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.EAST, sprite, 3.999F, 0, 0, 16, 16);

            tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.NORTH, sprite, 12.001F, 0, 0, 16, 16);
            tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.NORTH, sprite, 11.999F, 0, 0, 16, 16);

            tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.EAST, sprite, 12.001F, 0, 0, 16, 16);
            tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.EAST, sprite, 11.999F, 0, 0, 16, 16);

            tessellator.translate(0, 12.0F/16.0F, 0);
            layer++;
        }

        tessellator.popMatrix();

        List<BakedQuad> quads = tessellator.getQuads();
        tessellator.draw();
        return quads;
    }

    @Nonnull
    public List<BakedQuad> bakeQuadsForRhombusPattern(IAgriGrowable plant, IAgriGrowthStage stage, @Nullable Direction face, IntFunction<TextureAtlasSprite> spriteFunc) {
        if(face != null) {
            return ImmutableList.of();
        }

        ITessellator tessellator = this.getTessellator();

        tessellator.startDrawingQuads();
        tessellator.setFace((Direction) null);

        float d = MathHelper.sqrt(128);

        tessellator.pushMatrix();
        tessellator.translate(0, 0, 0.5F);
        tessellator.rotate(45, 0, 1, 0);

        double height = plant.getPlantHeight(stage);
        int layers = (int) Math.ceil(height / 16.0);
        int layer = 0;
        while ((16 * layer) < height) {
            TextureAtlasSprite sprite = spriteFunc.apply(layers - layer - 1);

            tessellator.drawScaledFaceDouble(0, 0, d, d, Direction.NORTH, sprite, 0);
            tessellator.drawScaledFaceDouble(0, 0, d, d, Direction.NORTH, sprite, d);
            tessellator.drawScaledFaceDouble(0, 0, d, d, Direction.EAST, sprite, 0);
            tessellator.drawScaledFaceDouble(0, 0, d, d, Direction.EAST, sprite, d);

            tessellator.translate(0, d/16.0F, 0);
            layer++;
        }

        tessellator.popMatrix();

        List<BakedQuad> quads = tessellator.getQuads();
        tessellator.draw();
        return quads;
    }

    @Nonnull
    @Override
    public List<BakedQuad> bakeQuadsForGourdPattern(IAgriGrowable plant, IAgriGrowthStage stage, @Nullable Direction face, IntFunction<TextureAtlasSprite> spriteFunc) {
        if(face != null) {
            return ImmutableList.of();
        }
        List<BakedQuad> baseQuads = this.bakeQuadsForHashPattern(plant, stage, face, spriteFunc);
        if(stage.isFinal()) {
            TextureAtlasSprite sprite = spriteFunc.apply(1);
            ITessellator tessellator = this.getTessellator();

            tessellator.startDrawingQuads();
            tessellator.setFace((Direction) null);

            tessellator.pushMatrix();

            tessellator.drawScaledPrism(7, 0, 2, 11, 4, 6, sprite);
            tessellator.drawScaledPrism(10, 0, 7, 14, 4, 11, sprite);
            tessellator.drawScaledPrism(5, 0, 10, 9, 4, 14, sprite);
            tessellator.drawScaledPrism(2, 0, 5, 6, 4, 9, sprite);

            tessellator.popMatrix();

            List<BakedQuad> quads = tessellator.getQuads();
            tessellator.draw();

            return new ImmutableList.Builder<BakedQuad>().addAll(baseQuads).addAll(quads).build();
        } else {
            return baseQuads;
        }
    }
}

package com.infinityraider.agricraft.api.v1.client;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;
import java.util.function.IntFunction;

/**
 * Interface for the AgriCraft default quad generator, its instance can be retrieved via AgriApi.getPlantQuadGenerator()
 */
@OnlyIn(Dist.CLIENT)
public interface IAgriPlantQuadGenerator {
    /**
     * @return the AgriCraft IAgriPlantQuadGenerator instance
     */
    static IAgriPlantQuadGenerator getInstance() {
        return AgriApi.getPlantQuadGenerator();
    }

    /**
     * Generates quads for the HASH AgriPlantRenderType
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForHashPattern(IAgriGrowable plant, IAgriGrowthStage stage, @Nullable Direction face, IntFunction<TextureAtlasSprite> spriteFunc);

    /**
     * Generates quads for the CROSS AgriPlantRenderType
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForCrossPattern(IAgriGrowable plant, IAgriGrowthStage stage, @Nullable Direction face, IntFunction<TextureAtlasSprite> spriteFunc);

    /**
     * Generates quads for the PLUS AgriPlantRenderType
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForPlusPattern(IAgriGrowable plant, IAgriGrowthStage stage, @Nullable Direction face, IntFunction<TextureAtlasSprite> spriteFunc);

    /**
     * Generates quads for the RHOMBUS AgriPlantRenderType
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForRhombusPattern(IAgriGrowable plant, IAgriGrowthStage stage, @Nullable Direction face, IntFunction<TextureAtlasSprite> spriteFunc);

    /**
     * Generates quads for the GOURD AgriPlantRenderType
     * here yOffset = 0 is the stem, and yOffset = 1 is the gourd
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForGourdPattern(IAgriGrowable plant, IAgriGrowthStage stage, @Nullable Direction face, IntFunction<TextureAtlasSprite> spriteFunc);
}

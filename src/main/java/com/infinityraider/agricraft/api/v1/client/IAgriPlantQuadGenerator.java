package com.infinityraider.agricraft.api.v1.client;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

import java.util.List;

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
    List<BakedQuad> bakeQuadsForHashPattern(@Nonnull TextureAtlasSprite sprite, int yOffset);

    /**
     * Generates quads for the CROSS AgriPlantRenderType
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForCrossPattern(@Nonnull TextureAtlasSprite sprite, int yOffset);

    /**
     * Generates quads for the PLUS AgriPlantRenderType
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForPlusPattern(@Nonnull TextureAtlasSprite sprite, int yOffset);

    /**
     * Generates quads for the RHOMBUS AgriPlantRenderType
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForRhombusPattern(@Nonnull TextureAtlasSprite sprite, int yOffset);

    /**
     * Generates quads for the GOURD AgriPlantRenderType
     * here yOffset = 0 is the stem, and yOffset = 1 is the gourd
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForGourdPattern(@Nonnull TextureAtlasSprite sprite, int yOffset);
}

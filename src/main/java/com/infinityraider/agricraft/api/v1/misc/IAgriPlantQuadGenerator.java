package com.infinityraider.agricraft.api.v1.misc;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

/**
 * Interface for the AgriCraft default quad generator, it's instance can be retrieved via AgriApi.getPlantQuadGenerator()
 */
@OnlyIn(Dist.CLIENT)
public interface IAgriPlantQuadGenerator {
    /**
     * Use for plants which are normally rendered with a hash pattern in the world (for example wheat)
     *
     * This method will return a list of quads to render such a hash pattern centered inside the four crop sticks
     *
     * @param sprite the ResourceLocation for the texture to use
     * @return list of BakedQuads
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForHashPattern(@Nullable Direction direction, @Nonnull ResourceLocation sprite);

    /**
     * Use for plants which are normally rendered with a cross pattern in the world (for example grass)
     *
     * This method will return a list of quads to render such a cross, centered on the crop sticks
     *
     * @param sprite the ResourceLocation for the texture to use
     * @return list of BakedQuads
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForCrossPattern(@Nullable Direction direction, @Nonnull ResourceLocation sprite);

    /**
     * Use for plants which are normally rendered with a cross pattern in the world (for example grass)
     *
     * This method will return a list of quads to render such a crosses, but scaled down near each of the four crop sticks
     *
     * @param sprite the ResourceLocation for the texture to use
     * @return list of BakedQuads
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForPlusPattern(@Nullable Direction direction, @Nonnull ResourceLocation sprite);

    /**
     * Use for plants which are normally rendered with a rhombus pattern in the world (no vanilla examples)
     *
     * This method will return a list of quads to render a rhombus, centered on the crop sticks
     *
     * In AgriCraft, this is only used for Weeds to avoid overlap with other plants.
     *
     * @param sprite the ResourceLocation for the texture to use
     * @return list of BakedQuads
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForRhombusPattern(@Nullable Direction direction, @Nonnull ResourceLocation sprite);

    /**
     * Default renderer, for use when no pattern is defined.
     *
     * This method will return a list of quads to render such a hash pattern centered inside the four crop sticks
     *
     * @param sprite the ResourceLocation for the texture to use
     * @return list of BakedQuads
     */
    @Nonnull
    default List<BakedQuad> bakeQuadsForDefaultPattern(@Nullable Direction direction, @Nonnull ResourceLocation sprite) {
        return this.bakeQuadsForCrossPattern(direction, sprite);
    }
}

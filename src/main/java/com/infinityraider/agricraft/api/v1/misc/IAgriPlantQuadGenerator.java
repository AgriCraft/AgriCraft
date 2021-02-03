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
     * The yOffset parameter is used for plants which are taller than 1 block.
     * For plants which are at most one block tall, use yOffset = 0.
     * For taller plants this method must be called multiple times, for instance for a plant that is three blocks tall,
     * this method will need to be called three times: once for the base layer (yOffset = 0), again for the middle layer
     * (yOffset = 1), and a final time for the top layer (yOffset = 2).
     *
     * @param direction the face for the quads currently being rendered
     * @param sprite the ResourceLocation for the texture to use
     * @param yOffset the yOffset in number of blocks
     * @return list of BakedQuads
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForHashPattern(@Nullable Direction direction, @Nonnull ResourceLocation sprite, int yOffset);

    /**
     * Use for plants which are normally rendered with a cross pattern in the world (for example grass)
     *
     * This method will return a list of quads to render such a cross, centered on the crop sticks
     *
     * The yOffset parameter is used for plants which are taller than 1 block.
     * For plants which are at most one block tall, use yOffset = 0.
     * For taller plants this method must be called multiple times, for instance for a plant that is three blocks tall,
     * this method will need to be called three times: once for the base layer (yOffset = 0), again for the middle layer
     * (yOffset = 1), and a final time for the top layer (yOffset = 2).
     *
     * @param direction the face for the quads currently being rendered
     * @param sprite the ResourceLocation for the texture to use
     * @param yOffset the yOffset in number of blocks
     * @return list of BakedQuads
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForCrossPattern(@Nullable Direction direction, @Nonnull ResourceLocation sprite, int yOffset);

    /**
     * Use for plants which are normally rendered with a cross pattern in the world (for example grass)
     *
     * This method will return a list of quads to render such a crosses, but scaled down near each of the four crop sticks
     *
     * The yOffset parameter is used for plants which are taller than 1 block.
     * For plants which are at most one block tall, use yOffset = 0.
     * For taller plants this method must be called multiple times, for instance for a plant that is three blocks tall,
     * this method will need to be called three times: once for the base layer (yOffset = 0), again for the middle layer
     * (yOffset = 1), and a final time for the top layer (yOffset = 2).
     *
     * @param direction the face for the quads currently being rendered
     * @param sprite the ResourceLocation for the texture to use
     * @param yOffset the yOffset in number of blocks
     * @return list of BakedQuads
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForPlusPattern(@Nullable Direction direction, @Nonnull ResourceLocation sprite, int yOffset);

    /**
     * Use for plants which are normally rendered with a rhombus pattern in the world (no vanilla examples)
     *
     * This method will return a list of quads to render a rhombus, centered on the crop sticks
     *
     * The yOffset parameter is used for plants which are taller than 1 block.
     * For plants which are at most one block tall, use yOffset = 0.
     * For taller plants this method must be called multiple times, for instance for a plant that is three blocks tall,
     * this method will need to be called three times: once for the base layer (yOffset = 0), again for the middle layer
     * (yOffset = 1), and a final time for the top layer (yOffset = 2).
     *
     * In AgriCraft, this is only used for Weeds to avoid overlap with other plants.
     *
     * @param direction the face for the quads currently being rendered
     * @param sprite the ResourceLocation for the texture to use
     * @param yOffset the yOffset in number of blocks
     * @return list of BakedQuads
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForRhombusPattern(@Nullable Direction direction, @Nonnull ResourceLocation sprite, int yOffset);

    /**
     * Default renderer, for use when no pattern is defined.
     *
     * This method will return a list of quads to render such a hash pattern centered inside the four crop sticks
     *
     * The yOffset parameter is used for plants which are taller than 1 block.
     * For plants which are at most one block tall, use yOffset = 0.
     * For taller plants this method must be called multiple times, for instance for a plant that is three blocks tall,
     * this method will need to be called three times: once for the base layer (yOffset = 0), again for the middle layer
     * (yOffset = 1), and a final time for the top layer (yOffset = 2).
     *
     * @param direction the face for the quads currently being rendered
     * @param sprite the ResourceLocation for the texture to use
     * @param yOffset the yOffset in number of blocks
     * @return list of BakedQuads
     */
    @Nonnull
    default List<BakedQuad> bakeQuadsForDefaultPattern(@Nullable Direction direction, @Nonnull ResourceLocation sprite, int yOffset) {
        return this.bakeQuadsForCrossPattern(direction, sprite, yOffset);
    }
}

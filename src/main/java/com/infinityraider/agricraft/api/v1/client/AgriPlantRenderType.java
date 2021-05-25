package com.infinityraider.agricraft.api.v1.client;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IExtensibleEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Enum representing the different types for which AgriCraft plants / weeds are rendered
 */
@OnlyIn(Dist.CLIENT)
public enum AgriPlantRenderType implements IExtensibleEnum {
    /** Renders in a hashtag pattern (#); 4 faces parallel with the block faces, similar to Vanilla wheat */
    HASH(AgriApi.getPlantQuadGenerator()::bakeQuadsForHashPattern),

    /** Renders in a cross pattern (x); 2 faces along the diagonals, similar to Vanilla flowers */
    CROSS(AgriApi.getPlantQuadGenerator()::bakeQuadsForCrossPattern),

    /** Renders in a plus pattern (+); similar to cross, but instead 4 crosses at each crop stick */
    PLUS(AgriApi.getPlantQuadGenerator()::bakeQuadsForPlusPattern),

    /** Renders in a rhombus pattern (◇); 4 faces spanning between the centers of the block faces, only used for weeds */
    RHOMBUS(AgriApi.getPlantQuadGenerator()::bakeQuadsForRhombusPattern),

    /** Renders for a gourd pattern (@); i.e. for pumpkins and melons: renders a hash pattern for the initial stages, with a small gourd for the final stage */
    GOURD(AgriApi.getPlantQuadGenerator()::bakeQuadsForGourdPattern);

    private final IQuadGenerator generator;

    AgriPlantRenderType(IQuadGenerator generator) {
        this.generator = generator;
    }

    /**
     * Generates the quads for the render type
     * @param direction the cull-face
     * @param sprite the texture to use
     * @param yOffset the yOffset for the quads
     * @return list of BakedQuads
     */
    @Nonnull
    public List<BakedQuad> bakedQuads(@Nullable Direction direction, @Nonnull TextureAtlasSprite sprite, int yOffset) {
        return this.generator.bakeQuads(direction, sprite, yOffset);
    }

    /**
     * Static method to create a custom render type.
     *
     * @param name the name (i.e. equivalent to "HASH", "CROSS", "PLUS", or "RHOMBUS"
     * @param generator the quad generator
     * @return a new AgriPlantRenderType instance
     */
    @SuppressWarnings("unused")
    public static AgriPlantRenderType create(String name, IQuadGenerator generator) {
        throw new IllegalStateException("Enum not extended");
    }

    @FunctionalInterface
    public interface IQuadGenerator {
        /**
         * Method which will generate quads for a plant.
         *
         * The yOffset parameter is used for plants which are taller than 1 block.         *
         * For taller plants this method will be called multiple times, for instance for a plant that is three blocks tall,
         * this method will be called three times: once for the base layer (yOffset = 0), again for the middle layer
         * (yOffset = 1), and a final time for the top layer (yOffset = 2).
         *
         * @param direction the cull-face for the quads currently being baked
         * @param sprite the sprite for the texture to use
         * @param yOffset the yOffset in number of blocks
         * @return list of BakedQuads
         */
        @Nonnull
        List<BakedQuad> bakeQuads(@Nullable Direction direction, @Nonnull TextureAtlasSprite sprite, int yOffset);
    }
}

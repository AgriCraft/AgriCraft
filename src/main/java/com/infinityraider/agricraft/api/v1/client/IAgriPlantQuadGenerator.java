package com.infinityraider.agricraft.api.v1.client;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
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
     * Method which will generate quads for a plant according to the given render type.
     *
     * The yOffset parameter is used for plants which are taller than 1 block.
     * For plants which are at most one block tall, use yOffset = 0.
     * For taller plants this method must be called multiple times, for instance for a plant that is three blocks tall,
     * this method will need to be called three times: once for the base layer (yOffset = 0), again for the middle layer
     * (yOffset = 1), and a final time for the top layer (yOffset = 2).
     *
     * @param direction the face for the quads currently being baked
     * @param sprite the sprite for the texture to use
     * @param yOffset the yOffset in number of blocks
     * @param renderType the render type to use
     * @return list of BakedQuads
     */
    List<BakedQuad> bakeQuads(@Nullable Direction direction, @Nonnull TextureAtlasSprite sprite, int yOffset, AgriPlantRenderType renderType);
}

package com.infinityraider.agricraft.api.v1.misc;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Interface for the AgriCraft default quad generator, it's instance can be retrieved via AgriApi.getPlantQuadGenerator()
 */
@OnlyIn(Dist.CLIENT)
public interface IAgriPlantQuadGenerator {
    /**
     * Use for plants which are normally rendered with a cross pattern in the world (for example sugar cane)
     *
     * This method will return a list of quads to render such crosses, but scaled down near each of the four crop sticks
     *
     * @param sprite the ResourceLocation for the texture to use
     * @return list of BakedQuads
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForCrossPattern(ResourceLocation sprite);

    /**
     * Use for plants which are normally rendered with a hash pattern in the world (for example wheat)
     *
     * This method will return a list of quads to render such a hash pattern centered inside the four crop sticks
     *
     * @param sprite the ResourceLocation for the texture to use
     * @return list of BakedQuads
     */
    @Nonnull
    List<BakedQuad> bakeQuadsForHashPattern(ResourceLocation sprite);
}

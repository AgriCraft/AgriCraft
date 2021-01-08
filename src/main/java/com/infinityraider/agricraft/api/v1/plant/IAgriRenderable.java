package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Common interface between all "things" which need to be custom rendered on crop sticks
 */
public interface IAgriRenderable {
    /**
     * Method to render plants or weeds on crop sticks,
     * only called when a plant or weed of this growth stage is about to be rendered.
     *
     * Given the large amount of combinations of plants, weeds and growth stages,
     * AgriCraft internally caches quads for each combination of plant and growth stage,
     * only baking the quads when they are required and then caching them to preserve memory.
     * If multiple growth stages have identical quads, it is advised to cache the quads on the implementing side as well.
     *
     * Note that it is possible to use the IAgriPlantQuadGenerator to generate quads according to AgriCraft's
     * default crop patterns.
     *
     * @param stage the current growth stage of the plant
     * @return a list of quads for rendering the plant
     */
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    List<BakedQuad> bakeQuads(IAgriGrowthStage stage);

    /**
     * Method which fetches all required texture to render a plant or weed for a certain growth stage,
     * used to stitch these textures to the texture atlas.
     *
     * @param stage the growth stage
     * @return a list of ResourceLocations pointing to the required textures, can be empty
     */
    @Nonnull
    List<ResourceLocation> getTexturesFor(IAgriGrowthStage stage);
}

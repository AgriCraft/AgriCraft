package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.client.IAgriGrowableGuiRenderer;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Common interface between all "things" that can grow on crop sticks
 */
public interface IAgriGrowable {
    /**
     * Determines the initial Growth Stage of a plant or weed when first planted
     *
     * @return the IAgriGrowthStage for the initial growth stage
     */
    @Nonnull
    IAgriGrowthStage getInitialGrowthStage();

    /**
     * Gets all the possible growth stages that a plant or weed can have.
     *
     * For AgriCraft specifically, the conventional number of growth stages is 8.
     *
     * @return a set containing all the possible growth stages of the plant.
     */
    @Nonnull
    Collection<IAgriGrowthStage> getGrowthStages();

    /**
     * @return the final growth stage for this plant
     */
    default IAgriGrowthStage getFinalStage() {
        return this.getGrowthStages().stream().filter(IAgriGrowthStage::isFinal).findAny()
                // This should not ever be reached
                .orElseThrow(() -> new IllegalStateException("Plant without final growth stage"));
    }

    /**
     * Fetches the height of the plant at the given growth stage in 1/16ths of a block.
     * Note that it is possible to return heights taller than 16
     *
     * For instance, returning 8 corresponds to a plant half a block high,
     * while returning 16 corresponds to a full block high,
     * and returning 32 corresponds to a plant which is two blocks high.     *
     *
     * @param stage the growth stage
     * @return height of the plant in 1/16ths of a block
     */
    double getPlantHeight(IAgriGrowthStage stage);

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
     * @param face the face to get quads for
     * @return a list of quads for rendering the plant
     */
    @Nonnull
    default List<?> bakeQuads(@Nullable Direction face, IAgriGrowthStage stage) {
        return Collections.emptyList();
    }

    /**
     * Method which fetches all required texture to render a plant or weed for a certain growth stage,
     * used to stitch these textures to the texture atlas.
     *
     * @param stage the growth stage
     * @return a list of ResourceLocations pointing to the required textures, can be empty
     */
    @Nonnull
    List<ResourceLocation> getTexturesFor(IAgriGrowthStage stage);

    /**
     * @return gui renderer
     */
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    default IAgriGrowableGuiRenderer getGuiRenderer() {
        return IAgriGrowableGuiRenderer.DEFAULT;
    }

    /**
     * Sub-interface for plants with seeds
     */
    interface WithSeed extends IAgriGrowable {
        /**
         * @return The resource location for the texture of the seed
         */
        @Nonnull
        ResourceLocation getSeedTexture();

        /**
         * @return The resource location for the model of the seed
         */
        @Nonnull
        ResourceLocation getSeedModel();

        /**
         * @return gui renderer
         */
        @Nonnull
        @Override
        @OnlyIn(Dist.CLIENT)
        default IAgriGrowableGuiRenderer.WithSeed getGuiRenderer() {
            return IAgriGrowableGuiRenderer.WithSeed.DEFAULT;
        }
    }
}

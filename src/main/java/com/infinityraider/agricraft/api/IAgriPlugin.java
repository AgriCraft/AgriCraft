/*
 */
package com.infinityraider.agricraft.api;

import com.infinityraider.agricraft.api.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.adapter.IAgriAdapterRegistry;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.mutation.IAgriCrossStrategy;
import com.infinityraider.agricraft.api.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.mutation.IAgriMutationEngine;
import com.infinityraider.agricraft.api.mutation.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.plant.IAgriPlantRegistry;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.api.soil.IAgriSoil;
import com.infinityraider.agricraft.api.soil.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.api.stat.IAgriStatCalculator;
import com.infinityraider.agricraft.api.stat.IAgriStatCalculatorRegistry;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.util.ResourceLocation;

/**
 * Interface for mod plugins to AgriCraft.
 * <p>
 * All classes implementing this interface <em>must</em> have a valid no-args
 * constructor, with which AgriCraft may create the plugin instance.
 * <p>
 * Plugin instantiation occurs during the pre-init phase of FML, at which time
 * very little of AgriCraft has been initialized. It is strongly recommended
 * that you do not attempt to AgriCraft in this method, as AgriCraft will be in
 * an indeterminate state.
 * <p>
 * Plugin initialization occurs during the init phase of FML, at which point,
 * AgriCraft is only partially initialized. Consequently, proceed with extreme
 * caution in referencing other parts of the mod at such time, given that
 * AgriCraft will still be in an indeterminate state.
 * <p>
 * Registrations occur during the post-init phase of FML, at which point
 * AgriCraft is considered functionally initialized. Thereby, this is the first
 * phase during which it is considered relatively safe to access AgriCraft, so
 * attempt to make connections to AgriCraft following the registration process.
 * <p>
 * Note, registrations proceed in a very specific order, as to minimize the
 * possibility of out-of-order cross-references between the registries (i.e.
 * plants depend on soils, therefore soils must be registered first). The
 * registration order is as follows:
 * <ol>
 * <li> All {@link IAgriSoil}s are registered using
 * {@link #registerSoils(IAgriSoilRegistry)}
 * <li> All {@link IAgriPlant}s are registered using
 * {@link #registerPlants(IAgriPlantRegistry)}
 * <li> All {@link IAgriMutation}s are registered using
 * {@link #registerMutations(IAgriMutationRegistry)}
 * <li> All {@link IAgriStat}s are registered using
 * {@link #registerStats(IAgriAdapterRegistry)}
 * <li> All {@link AgriSeed} adapters are registered using
 * {@link #registerSeeds(IAgriAdapterRegistry)}
 * <li> All {@link IAgriFertilizer} adapters are registered using
 * {@link #registerFertilizers(IAgriAdapterRegistry)}
 * <li> All {@link IAgriStatCalculator}s are registered using
 * {@link #registerStatCalculators(IAgriStatCalculatorRegistry)}
 * <li> All {@link IAgriCrossStrategy}s are registered using
 * {@link #registerCrossStrategies(IAgriMutationEngine)}
 * </ol>
 */
public interface IAgriPlugin {

    boolean isEnabled();

    default void initPlugin() {
        // Default Implementation: Do nothing.
    }

    default void registerSoils(@Nonnull IAgriSoilRegistry soilRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerPlants(@Nonnull IAgriPlantRegistry plantRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerMutations(@Nonnull IAgriMutationRegistry mutationRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerStats(@Nonnull IAgriAdapterRegistry<IAgriStat> statRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerStatCalculators(@Nonnull IAgriStatCalculatorRegistry statCalculatorRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerCrossStrategies(@Nonnull IAgriMutationEngine mutationEngine) {
        // Default Implementation: Do nothing.
    }

    default void registerSeeds(@Nonnull IAgriAdapterRegistry<AgriSeed> seedRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerFertilizers(@Nonnull IAgriAdapterRegistry<IAgriFertilizer> fertilizerRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerTextures(@Nonnull Consumer<ResourceLocation> textureRegistry) {
        // Default Implementation: Do nothing.
    }

}

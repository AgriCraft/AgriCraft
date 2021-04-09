package com.infinityraider.agricraft.api.v1.plugin;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneRegistry;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSeasonLogic;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;

import javax.annotation.Nonnull;

import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import net.minecraft.block.BlockState;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

/**
 * Interface for mod plugins to AgriCraft.
 * <p>
 * All classes implementing this interface <em>must</em> have a valid no-args constructor, with
 * which AgriCraft may create the plugin instance.
 * <p>
 * Note, registrations proceed in a very specific order, as to minimize the possibility of
 * out-of-order cross-references between the registries (i.e. plants depend on soils, therefore
 * soils must be registered first). The registration order is as follows:
 * <ol>
 * <li> All {@link IAgriSoil}s are registered using {@link #registerSoils(IAgriSoilRegistry>)}
 * <li> All {@link IAgriWeed}s are registered using {@link #registerWeeds(IAgriRegistry<IAgriWeed)}
 * <li> All {@link IAgriPlant}s are registered using {@link #registerPlants(IAgriRegistry<IAgriPlant>)}
 * <li> All {@link IAgriMutation}s are registered using {@link #registerMutations(IAgriMutationRegistry)}
 * <li> All {@link IAgriStat}s are registered using {@link #registerStats(IAgriStatRegistry)}
 * <li> All {@link IAgriGene}s are registered using {@link #registerGenes(IAgriGeneRegistry)}
 * <li> All {@link IAgriSoil} adapters are registered using {@link #registerSoilAdapters(IAgriAdapterizer<BlockState>)}
 * <li> All {@link AgriSeed} adapters are registered using {@link #registerSeeds(IAgriAdapterizer<AgriSeed>)}
 * <li> All {@link IAgriFertilizer} adapters are registered using {@link #registerFertilizers(IAgriAdapterizer<IAgriFertilizer>)}
 * </ol>
 */
@SuppressWarnings("unused")
public interface IAgriPlugin {

    boolean isEnabled();
    
    String getId();
    
    String getName();
    
    default void onInterModEnqueueEvent(InterModEnqueueEvent event) {
        // Default Implementation: Do nothing.
    }

    default void onInterModProcessEvent(InterModProcessEvent event) {
        // Default Implementation: Do nothing.
    }

    default void registerSoils(@Nonnull IAgriSoilRegistry soilRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerWeeds(@Nonnull IAgriRegistry<IAgriWeed> weedRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerPlants(@Nonnull IAgriRegistry<IAgriPlant> plantRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerMutations(@Nonnull IAgriMutationRegistry mutationRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerStats(@Nonnull IAgriStatRegistry statRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerGenes(@Nonnull IAgriGeneRegistry geneRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerSoilAdapters(@Nonnull IAgriAdapterizer<BlockState> adapterizer) {
        // Default Implementation: Do nothing
    }

    default void registerSeeds(@Nonnull IAgriAdapterizer<AgriSeed> adapterizer) {
        // Default Implementation: Do nothing.
    }

    default void registerFertilizers(@Nonnull IAgriAdapterizer<IAgriFertilizer> adapterizer) {
        // Default Implementation: Do nothing.
    }

    default void registerSeasonLogic(@Nonnull IAgriSeasonLogic seasonLogic) {
        // Default Implementation: Do nothing.
    }
}

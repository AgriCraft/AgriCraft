package com.agricraft.agricraft.api.plugin;

import com.agricraft.agricraft.api.adapter.IAgriAdapterizer;
import com.agricraft.agricraft.api.fertilizer.IAgriFertilizer;
import com.agricraft.agricraft.api.genetics.IAgriGene;
import com.agricraft.agricraft.api.genetics.IAgriGeneRegistry;
import com.agricraft.agricraft.api.genetics.IAgriGenome;
import com.agricraft.agricraft.api.genetics.IAgriMutation;
import com.agricraft.agricraft.api.genetics.IAgriMutationRegistry;
import com.agricraft.agricraft.api.plant.IAgriPlant;
import com.agricraft.agricraft.api.plant.IAgriWeed;
import com.agricraft.agricraft.api.requirement.IAgriSeasonLogic;
import com.agricraft.agricraft.api.requirement.IAgriSoil;
import com.agricraft.agricraft.api.requirement.IAgriSoilProvider;
import com.agricraft.agricraft.api.requirement.IAgriSoilRegistry;
import com.agricraft.agricraft.api.stat.IAgriStat;
import com.agricraft.agricraft.api.stat.IAgriStatRegistry;
import com.agricraft.agricraft.api.util.IAgriRegistry;
import org.jetbrains.annotations.NotNull;


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
 * <li> All {@link IAgriSoil}s and {@link IAgriSoilProvider}s are registered using {@link #registerSoils(IAgriSoilRegistry >)}
 * <li> All {@link IAgriWeed}s are registered using {@link #registerWeeds(IAgriRegistry <IAgriWeed)}
 * <li> All {@link IAgriPlant}s are registered using {@link #registerPlants(IAgriRegistry<IAgriPlant>)}
 * <li> All {@link IAgriMutation}s are registered using {@link #registerMutations(IAgriMutationRegistry)}
 * <li> All {@link IAgriStat}s are registered using {@link #registerStats(IAgriStatRegistry)}
 * <li> All {@link IAgriGene}s are registered using {@link #registerGenes(IAgriGeneRegistry)}
 * <li> All {@link IAgriGenome} adapters are registered using {@link #registerGenomes(IAgriAdapterizer<IAgriGenome>)}
 * <li> All {@link IAgriFertilizer} adapters are registered using {@link #registerFertilizers(IAgriAdapterizer<IAgriFertilizer>)}
 * </ol>
 */
@SuppressWarnings("unused")
public interface IAgriPlugin {

	boolean isEnabled();

	String getId();

	String getDescription();

// FIXME: update
//    default void onCommonSetupEvent(FMLCommonSetupEvent event) {
//        // Default Implementation: Do nothing.
//    }
//
//    default void onClientSetupEvent(FMLClientSetupEvent event) {
//        // Default Implementation: Do nothing.
//    }
//
//    default void onServerSetupEvent(FMLDedicatedServerSetupEvent event) {
//        // Default Implementation: Do nothing.
//    }
//
//    default void onInterModEnqueueEvent(InterModEnqueueEvent event) {
//        // Default Implementation: Do nothing.
//    }
//
//    default void onInterModProcessEvent(InterModProcessEvent event) {
//        // Default Implementation: Do nothing.
//    }

	default void registerSoils(@NotNull IAgriSoilRegistry soilRegistry) {
		// Default Implementation: Do nothing.
	}

	default void registerWeeds(@NotNull IAgriRegistry<IAgriWeed> weedRegistry) {
		// Default Implementation: Do nothing.
	}

	default void registerPlants(@NotNull IAgriRegistry<IAgriPlant> plantRegistry) {
		// Default Implementation: Do nothing.
	}

	default void registerMutations(@NotNull IAgriMutationRegistry mutationRegistry) {
		// Default Implementation: Do nothing.
	}

	default void registerStats(@NotNull IAgriStatRegistry statRegistry) {
		// Default Implementation: Do nothing.
	}

	default void registerGenes(@NotNull IAgriGeneRegistry geneRegistry) {
		// Default Implementation: Do nothing.
	}

	default void registerGenomes(@NotNull IAgriAdapterizer<IAgriGenome> adapterizer) {
		// Default Implementation: Do nothing.
	}

	default void registerFertilizers(@NotNull IAgriAdapterizer<IAgriFertilizer> adapterizer) {
		// Default Implementation: Do nothing.
	}

	default void registerSeasonLogic(@NotNull IAgriSeasonLogic seasonLogic) {
		// Default Implementation: Do nothing.
	}

// FIXME: update
//    default <T> LazyOptional<T> getCropCapability(@NotNull Capability<T> cap, IAgriCrop crop) {
//        // Default Implementation: Return empty optional.
//        return LazyOptional.empty();
//    }
}

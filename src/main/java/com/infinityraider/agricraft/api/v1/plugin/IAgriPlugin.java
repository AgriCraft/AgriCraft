package com.infinityraider.agricraft.api.v1.plugin;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.*;
import com.infinityraider.agricraft.api.v1.util.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSeasonLogic;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoilProvider;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;

import javax.annotation.Nonnull;

import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.event.lifecycle.*;

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
 * <li> All {@link IAgriSoil}s and {@link IAgriSoilProvider}s are registered using {@link #registerSoils(IAgriSoilRegistry>)}
 * <li> All {@link IAgriWeed}s are registered using {@link #registerWeeds(IAgriRegistry<IAgriWeed)}
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

    default void onCommonSetupEvent(FMLCommonSetupEvent event) {
        // Default Implementation: Do nothing.
    }

    default void onClientSetupEvent(FMLClientSetupEvent event) {
        // Default Implementation: Do nothing.
    }

    default void onServerSetupEvent(FMLDedicatedServerSetupEvent event) {
        // Default Implementation: Do nothing.
    }
    
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

    default void registerGenomes(@Nonnull IAgriAdapterizer<IAgriGenome> adapterizer) {
        // Default Implementation: Do nothing.
    }

    default void registerFertilizers(@Nonnull IAgriAdapterizer<IAgriFertilizer> adapterizer) {
        // Default Implementation: Do nothing.
    }

    default void registerSeasonLogic(@Nonnull IAgriSeasonLogic seasonLogic) {
        // Default Implementation: Do nothing.
    }

    default <T> LazyOptional<T> getCropCapability(@Nonnull Capability<T> cap, IAgriCrop crop) {
        // Default Implementation: Return empty optional.
        return LazyOptional.empty();
    }
}

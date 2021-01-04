package com.infinityraider.agricraft.api.v1.plugin;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;

import java.util.function.Consumer;
import javax.annotation.Nonnull;

import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

/**
 * Interface for mod plugins to AgriCraft.
 * <p>
 * All classes implementing this interface <em>must</em> have a valid no-args constructor, with
 * which AgriCraft may create the plugin instance.
 * <p>
 * Plugin instantiation occurs during the pre-init phase of FML, at which time very little of
 * AgriCraft has been initialized. It is strongly recommended that you do not attempt to AgriCraft
 * in this method, as AgriCraft will be in an indeterminate state.
 * <p>
 * Plugin initialization occurs during the init phase of FML, at which point, AgriCraft is only
 * partially initialized. Consequently, proceed with extreme caution in referencing other parts of
 * the mod at such time, given that AgriCraft will still be in an indeterminate state.
 * <p>
 * Registrations occur during the post-init phase of FML, at which point AgriCraft is considered
 * functionally initialized. Thereby, this is the first phase during which it is considered
 * relatively safe to access AgriCraft, so attempt to make connections to AgriCraft following the
 * registration process.
 * <p>
 * Note, registrations proceed in a very specific order, as to minimize the possibility of
 * out-of-order cross-references between the registries (i.e. plants depend on soils, therefore
 * soils must be registered first). The registration order is as follows:
 * <ol>
 * <li> All {@link IAgriSoil}s are registered using {@link #registerSoils(IAgriSoilRegistry)}
 * <li> All {@link IAgriPlant}s are registered using {@link #registerPlants(IAgriRegistry<IAgriPlant>)}
 * <li> All {@link IAgriMutation}s are registered using
 * {@link #registerMutations(IAgriMutationRegistry)}
 * <li> All {@link IAgriStat}s are registered using {@link #registerStats(IAgriStatRegistry)}
 * <li> All {@link AgriSeed} adapters are registered using
 * {@link #registerSeeds(IAgriAdapterizer<AgriSeed>)}
 * <li> All {@link IAgriFertilizer} adapters are registered using
 * {@link #registerFertilizers(IAgriAdapterizer<IAgriFertilizer>)}
 * </ol>
 */
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

    default void registerPlants(@Nonnull IAgriRegistry<IAgriPlant> plantRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerMutations(@Nonnull IAgriMutationRegistry mutationRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerStats(@Nonnull IAgriStatRegistry statRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerSeeds(@Nonnull IAgriAdapterizer<AgriSeed> seedRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerFertilizers(@Nonnull IAgriAdapterizer<IAgriFertilizer> fertilizerRegistry) {
        // Default Implementation: Do nothing.
    }

    default void registerTextures(@Nonnull Consumer<ResourceLocation> textureRegistry) {
        // Default Implementation: Do nothing.
    }
}

package com.infinityraider.agricraft.api.v1.event;

import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.api.v1.util.IAgriRegisterable;
import com.infinityraider.agricraft.api.v1.util.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * This class contains an event hierarchy for events fired on the MinecraftForge.EVENT_BUS,
 * whenever an object related to AgriCraft registries is registered.
 *
 * These events allow you to modify or substitute the registered elements with your own
 * The events will not be called for key elements necessary for AgriCraft's functioning.
 *
 * These events cover registration of objects such as:
 *  - IAgriPlants
 *  - IAgriWeeds
 *  - IAgriSoils
 *  - IAgriGenes
 *  - IAgriMutations
 *
 *  Note that these events are not fired for IAgriStats nor IAgriGrowthStages, and these can, as a result,
 *  not be substituted as they are tied to IAgriGene and IAgriPlant objects respectively.
 */
public class AgriRegistryEvent<T extends IAgriRegisterable<T>> extends Event {
    private final IAgriRegistry<T> registry;
    private final T original;
    private T substitute;

    private AgriRegistryEvent(IAgriRegistry<T> registry, T element) {
        this.registry = registry;
        this.original = element;
        this.setSubstitute(element);
    }

    /**
     * @return the registry to which the element is being registered
     */
    @Nonnull
    public IAgriRegistry<T> getRegistry() {
        return this.registry;
    }

    /**
     * @return the element which was originally being registered
     */
    @Nonnull
    public T getOriginal() {
        return this.original;
    }

    /**
     * @return the element currently substituting the original element (can be identical in case no substitute is defined)
     */
    @Nonnull
    public T getSubstitute() {
        return this.substitute;
    }

    /**
     * Sets the substitute to replace the original element in the registry.
     * The substitute must have the same id as the original
     * @param substitute the substitute
     */
    public void setSubstitute(@Nonnull T substitute) {
        Objects.requireNonNull(substitute);
        Preconditions.checkArgument(substitute.getId().equals(this.getOriginal().getId()),
                "Substituted element must have the same id as the original");
        this.substitute = substitute;
    }

    /**
     * Fired right before IAgriPlant objects are registered
     */
    public static class Plant extends AgriRegistryEvent<IAgriPlant> {
        public Plant(IAgriRegistry<IAgriPlant> registry, IAgriPlant element) {
            super(registry, element);
        }
    }

    /**
     * Fired right before IAgriWeed objects are registered
     */
    public static class Weed extends AgriRegistryEvent<IAgriWeed> {
        public Weed(IAgriRegistry<IAgriWeed> registry, IAgriWeed element) {
            super(registry, element);
        }
    }

    /**
     * Fired right before IAgriSoil objects are registered
     */
    public static class Soil extends AgriRegistryEvent<IAgriSoil> {
        public Soil(IAgriRegistry<IAgriSoil> registry, IAgriSoil element) {
            super(registry, element);
        }
    }

    /**
     * Fired right before IAgriGene objects are registered
     */
    public static class Gene extends AgriRegistryEvent<IAgriGene<?>> {
        public Gene(IAgriRegistry<IAgriGene<?>> registry, IAgriGene<?> element) {
            super(registry, element);
        }
    }

    /**
     * Fired right before IAgriMutation objects are registered
     */
    public static class Mutation extends AgriRegistryEvent<IAgriMutation> {
        public Mutation(IAgriRegistry<IAgriMutation> registry, IAgriMutation element) {
            super(registry, element);
        }
    }
}

package com.infinityraider.agricraft.api.v1.genetics;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface IAgriGeneRegistry extends IAgriRegistry<IAgriGene<?>> {
    /**
     * @return the AgriCraft IAgriGeneRegistry instance
     */
    @SuppressWarnings("unused")
    static IAgriGeneRegistry getInstance() {
        return AgriApi.getGeneRegistry();
    }

    /**
     * @return The gene responsible for the plant species
     */
    @Nonnull
    IAgriGene<IAgriPlant> getPlantGene();

    /**
     * Every stat will automatically get a gene associated with it, this method can be used to obtain the corresponding
     * gene for a given stat
     * @param stat the stat
     * @return an optional containing the gene, or empty (only if no such stat has been registered)
     */
    Optional<IAgriGene<Integer>> get(@Nullable IAgriStat stat);

    /**
     * Registers both the gene and stat for the given stat.
     * Can either be called directly, but is also automatically called when registering a new stat in the stat registry
     *
     * @param stat the stat to be registered.
     * @return {@literal true} if the element was registered, {@literal false} otherwise (i.e. in
     * the case of a conflict).
     */
    boolean addGeneForStat(@Nullable IAgriStat stat);

    /**
     * Removes both the gene and stat for the given stat.
     * Can either be called directly, but is also automatically called when removing an existing stat in the stat registry
     *
     * @param stat the stat to be removed from the registry.
     * @return {@literal true} if the stat was removed, {@literal false} otherwise (i.e. in the
     * case that the element was not actually in the registry).
     */
    boolean removeGeneForStat(@Nullable IAgriStat stat);

    /**
     * Fetches the stat corresponding to a gene
     *
     * @param gene the IAgriGene
     * @return an Optional containing the stat, or empty if the gene has not been registered
     */
    Optional<IAgriStat> getStatForGene(@Nullable IAgriGene<Integer> gene);
}

/*
 */
package com.infinityraider.agricraft.api.stat;

import com.infinityraider.agricraft.api.adapter.IAgriAdapterRegistry;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 *
 * @author Ryan
 */
public interface IAgriStatCalculatorRegistry extends IAgriAdapterRegistry<IAgriStatCalculator> {
    
    /**
     * Calculates the stats for a spread event.
     *
     * @param child the child that was the result of the spreading.
     * @param neighbors a List containing all neighboring crops
     * @return an ISeedStats object containing the resulting stats
     */
    @Nonnull
    default Optional<IAgriStat> calculateSpreadStats(IAgriPlant child, Collection<IAgriCrop> neighbors) {
        return this.valueOf(child)
                .map(calc -> calc.calculateSpreadStats(child, neighbors));
    }

    /**
     * Calculates the stats for a mutation.
     *
     * @param mutation The mutation that occurred.
     * @param neighbors a List containing all neighboring crops
     * @return an ISeedStats object containing the resulting stats
     */
    @Nonnull
    default Optional<IAgriStat> calculateMutationStats(IAgriMutation mutation, Collection<IAgriCrop> neighbors) {
        return this.valueOf(mutation)
                .map(calc -> calc.calculateMutationStats(mutation, neighbors));
    }

}

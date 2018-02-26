package com.infinityraider.agricraft.api.v1.stat;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import java.util.Collection;
import javax.annotation.Nonnull;

/**
 * Interface to create custom Stat Calculator logic use API.setStatCalculator(IStatCalculator
 * calculator) to set the active calculator
 */
public interface IAgriStatCalculator {

    /**
     * Calculates the stats for a spread event.
     *
     * @param child the child that was the result of the spreading.
     * @param input a List containing all neighboring crops
     * @return an ISeedStats object containing the resulting stats
     */
    @Nonnull
    IAgriStat calculateSpreadStats(@Nonnull IAgriPlant child, @Nonnull Collection<IAgriCrop> input);

    /**
     * Calculates the stats for a mutation.
     *
     * @param mutation The mutation that occurred.
     * @param input a List containing all neighboring crops
     * @return an ISeedStats object containing the resulting stats
     */
    @Nonnull
    IAgriStat calculateMutationStats(@Nonnull IAgriMutation mutation, @Nonnull Collection<IAgriCrop> input);

}

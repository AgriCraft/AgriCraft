package com.infinityraider.agricraft.api.v1.genetics;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

/**
 * AgriCraft's mutation logic is executed by the implementation of this interface.
 *
 * It's active / default implementations can be obtained from the IAgriMutationHandler instance
 * Overriding implementations can be activated with the IAgriMutationHandler instance as well
 */
public interface IAgriMutationEngine {
    /**
     * Handles a growth tick resulting in a mutation, is only fired for cross crops.
     * This method does not return anything, any results from the success or failure of a mutation must be fired from within this method as well.
     *
     * @param crop the crop for which the mutation tick has been fired
     * @param neighbours A stream of the crop's neighbouring crops (this includes all crops, regardless if these contain plants, weeds, are fertile, or mature)
     * @param random pseudo-random generator to take decisions
     * @return optional holding the plant which was spawned on the crop sticks, or empty in case the mutation / spread failed
     */
    Optional<IAgriPlant> handleMutationTick(IAgriCrop crop, Stream<IAgriCrop> neighbours, Random random);
}

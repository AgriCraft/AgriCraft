package com.infinityraider.agricraft.api.v1.mutation;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;

import java.util.List;
import java.util.Random;

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
     * @param neighbours A list of the crop's neighbouring crops (this includes all crops, regardless if these contain plants, weeds, are fertile, or mature)
     * @param random pseudo-random generator to take decisions
     */
    void handleMutationTick(IAgriCrop crop, List<IAgriCrop> neighbours, Random random);
}

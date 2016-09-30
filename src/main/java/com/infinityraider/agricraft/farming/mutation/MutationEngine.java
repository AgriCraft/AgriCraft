package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.api.mutation.IAgriCrossStrategy;
import com.infinityraider.agricraft.api.mutation.IAgriMutationEngine;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;

import java.util.Random;

/**
 * This class decides whether a plant is spreading or mutating and also
 * calculates the new stats (growth, gain, strength) of the new plant based on
 * the 4 neighbours.
 */
public class MutationEngine implements IAgriMutationEngine {
    
    private static MutationEngine INSTANCE = new MutationEngine();

    private MutationEngine() {

    }
    
    public static MutationEngine getInstance() {
        return INSTANCE;
    }

    /*
     * Applies one of the 2 strategies and notifies the TE if it should update
     */
    @Override
    public void executeCrossOver(TileEntityCrop crop, Random rand) {
        rollStrategy(rand)
                .executeStrategy(crop, rand)
                .filter(crop::isFertile)
                .ifPresent(seed -> {
                    crop.setCrossCrop(false);
                    crop.setSeed(seed);
                });
    }

    public IAgriCrossStrategy rollStrategy(Random rand) {
        if (rand.nextDouble() < AgriCraftConfig.mutationChance) {
            return MutateStrategy.getInstance();
        } else {
            return SpreadStrategy.getInstance();
        }
    }

}

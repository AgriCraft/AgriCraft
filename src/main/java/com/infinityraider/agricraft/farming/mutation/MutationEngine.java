package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;

import java.util.Random;

/**
 * This class decides whether a plant is spreading or mutating and also
 * calculates the new stats (growth, gain, strength) of the new plant based on
 * the 4 neighbours.
 */
public final class MutationEngine {
    
    private static final MutationEngine INSTANCE = new MutationEngine();

    private MutationEngine() {

    }
    
    public static MutationEngine getInstance() {
        return INSTANCE;
    }

    /*
     * Applies one of the 2 strategies and notifies the TE if it should update
     */
    public void executeCrossOver(TileEntityCrop crop, Random rand) {
        rollStrategy(rand)
                .executeStrategy(crop, rand)
                .filter(crop::isFertile)
                .ifPresent(seed -> {
                    crop.setCrossCrop(false);
                    crop.setSeed(seed);
                });
    }

    public ICrossOverStrategy rollStrategy(Random rand) {
        if (rand.nextDouble() < AgriCraftConfig.mutationChance) {
            return MutateStrategy.getInstance();
        } else {
            return SpreadStrategy.getInstance();
        }
    }

}

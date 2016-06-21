package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.tiles.TileEntityCrop;

import java.util.Random;

/**
 * This class decides whether a plant is spreading or mutating and also
 * calculates the new stats (growth, gain, strength) of the new plant based on
 * the 4 neighbours.
 */
public class MutationEngine {

    private final TileEntityCrop crop;
    private final Random random;

    public MutationEngine(TileEntityCrop crop) {
        this(crop, new Random());
    }

    public MutationEngine(TileEntityCrop crop, Random random) {
        this.crop = crop;
        this.random = random;
    }

    /**
     * Applies one of the 2 strategies and notifies the TE if it should update
     */
    public void executeCrossOver() {
        ICrossOverStrategy strategy = rollStrategy();
        CrossOverResult result = strategy.executeStrategy();
        if (result == null) {
            return;
        }
        if (resultIsValid(result) && random.nextDouble() < result.getChance()) {
            crop.applyCrossOverResult(result);
        }
    }

    private boolean resultIsValid(CrossOverResult result) {
        return result != null && result.getPlant().getGrowthRequirement().canGrow(crop.getWorld(), crop.getPos());
    }

    public ICrossOverStrategy rollStrategy() {
        boolean spreading = random.nextDouble() > AgriCraftConfig.mutationChance;
        return spreading ? new SpreadStrategy(this) : new MutateStrategy(this);
    }

    public TileEntityCrop getCrop() {
        return crop;
    }

    public Random getRandom() {
        return random;
    }
}

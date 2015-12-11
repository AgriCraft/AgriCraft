package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

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
        if (result == null || result.getSeed()==null) {
            return;
        }
        if (resultIsValid(result) && random.nextDouble() < result.getChance()) {
            crop.applyCrossOverResult(result);
        }
    }

    private boolean resultIsValid(CrossOverResult result) {
        IGrowthRequirement growthReq = CropPlantHandler.getGrowthRequirement(result.getSeed(), result.getMeta());

        boolean valid = result.getSeed() != null && CropPlantHandler.isValidSeed(result.toStack());
        return valid && growthReq.canGrow(crop.getWorldObj(), crop.xCoord, crop.yCoord, crop.zCoord);
    }

    public ICrossOverStrategy rollStrategy() {
        boolean spreading = random.nextDouble() > ConfigurationHandler.mutationChance;
        return spreading ? new SpreadStrategy(this) : new MutateStrategy(this);
    }

    public TileEntityCrop getCrop() {
        return crop;
    }

    public Random getRandom() {
        return random;
    }
}

package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v3.*;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.mutation.statcalculator.StatCalculator;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

import java.util.Random;

/**
 * This class decides whether a plant is spreading or mutating and also
 * calculates the new stats (growth, gain, strength) of the new plant based on
 * the 4 neighbours.
 */
public class MutationEngine implements IMutationEngine {
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
        ICrossOverResult result = rollAndExecuteStrategy();
        if (result == null || result.getSeed()==null) {
            return;
        }
        if (resultIsValid(result) && random.nextDouble() < result.getChance()) {
            crop.applyCrossOverResult(result, result.getStats());
        }
    }

    private boolean resultIsValid(ICrossOverResult result) {
        IGrowthRequirement growthReq = CropPlantHandler.getGrowthRequirement(result.getSeed(), result.getMeta());

        boolean valid = result.getSeed() != null && CropPlantHandler.isValidSeed(result.getSeed(), result.getMeta());
        return valid && growthReq.canGrow(crop.getWorldObj(), crop.xCoord, crop.yCoord, crop.zCoord);
    }

    public ICrossOverResult rollAndExecuteStrategy() {
        boolean spreading = random.nextDouble() > ConfigurationHandler.mutationChance;
        return spreading ?
                getMutationHandler().getMutationLogic().getSpreadingResult(this) :
                getMutationHandler().getMutationLogic().getMutationResult(this);
    }

    public ICrop getCrop() {
        return crop;
    }

    public Random getRandom() {
        return random;
    }

    @Override
    public IMutationHandler getMutationHandler() {
        return MutationHandler.getInstance();
    }

    @Override
    public IStatCalculator getStatCalculator() {
        return StatCalculator.getInstance();
    }
}

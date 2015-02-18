package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.farming.GrowthRequirement;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirements;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;

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
     * Applies one of the 2 strategies and updates the TileEntity
     * @return true, if a new plant was set in the TileEntity
     */
    public boolean executeCrossOver() {
        INewSeedStrategy strategy = rollStrategy();
        StrategyResult result = strategy.executeStrategy();
        if (result == null) {
            return false;
        }

        if (resultIsValid(result) && random.nextDouble() < result.getChance()) {
            applyResult(result);
            return true;
        }

        return false;
    }

    private boolean resultIsValid(StrategyResult result) {
        GrowthRequirement growthReq = GrowthRequirements.getGrowthRequirement(result.getSeed(), result.getMeta());

        boolean valid = result.getSeed() != null && SeedHelper.isValidSeed(result.getSeed(), result.getMeta());
        return valid && growthReq.canGrow(crop.getWorldObj(), crop.xCoord, crop.yCoord, crop.zCoord);
    }

    private void applyResult(StrategyResult result) {
        crop.crossCrop = false;
        crop.setPlant(result.getGrowth(), result.getGain(), result.getStrength(), false, result.getSeed(), result.getMeta());
    }

    public INewSeedStrategy rollStrategy() {
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

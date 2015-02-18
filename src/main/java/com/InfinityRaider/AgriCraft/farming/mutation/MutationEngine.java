package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.farming.GrowthRequirements;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.item.ItemSeeds;

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
        StrategyResult strategyResult = strategy.executeStrategy();

        //flag to check if the crop needs to update
        boolean change = false;

        // TODO: remove this after we have moved stats also to the strategies
        boolean didMutate = strategy instanceof MutateStrategy;

        ItemSeeds result = strategyResult.getSeed();
        int resultMeta = strategyResult.getMeta();
        double chance = strategyResult.getChance();

        //try to set the new plant
        if(result!=null && SeedHelper.isValidSeed(result, resultMeta) && GrowthRequirements.getGrowthRequirement(result, resultMeta).canGrow(crop.getWorldObj(), crop.xCoord, crop.yCoord, crop.zCoord)) {
            if(Math.random()<chance) {
                crop.crossCrop = false;
                int[] stats = MutationHandler.getStats(crop.getNeighbours(), didMutate);
                crop.setPlant(stats[0], stats[1], stats[2], false, result, resultMeta);
                change = true;
            }
        }

        return change;
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

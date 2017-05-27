package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.mutation.IAgriCrossStrategy;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.StatCalculatorRegistry;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Class defining the default method in which plants spread to new crops.
 * 
 * TODO: Clean up this class!
 */
public class SpreadStrategy implements IAgriCrossStrategy {

    @Override
    public double getRollChance() {
        return Math.abs(1f - AgriCraftConfig.mutationChance);
    }

    @Override
    public Optional<AgriSeed> executeStrategy(IAgriCrop crop, Random rand) {
        List<IAgriCrop> matureNeighbours = WorldHelper.getTileNeighbors(crop.getWorld(), crop.getPos(), IAgriCrop.class);
        matureNeighbours.removeIf(c -> !c.isMature());
        if (!matureNeighbours.isEmpty()) {
            int index = rand.nextInt(matureNeighbours.size());
            AgriSeed seed = matureNeighbours.get(index).getSeed();
            if (seed != null && rand.nextDouble() < seed.getPlant().getSpreadChance()) {
                return StatCalculatorRegistry.getInstance()
                        .calculateSpreadStats(seed.getPlant(), matureNeighbours)
                        .map(stat -> new AgriSeed(seed.getPlant(), stat));
            }
        }
        return Optional.empty();
    }
}

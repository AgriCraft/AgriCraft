package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.mutation.IAgriCrossStrategy;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.infinitylib.utility.WorldHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
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
    @Nonnull
    public Optional<AgriSeed> executeStrategy(@Nonnull IAgriCrop crop, @Nonnull Random rand) {
        List<IAgriCrop> allNeighbours = WorldHelper.getTileNeighbors(crop.getCropWorld(), crop.getCropPos(), IAgriCrop.class);
        List<IAgriCrop> matureNeighbours = new ArrayList<>(allNeighbours);
        matureNeighbours.removeIf(c -> !c.isMature());
        if (!matureNeighbours.isEmpty()) {
            int index = rand.nextInt(matureNeighbours.size());
            AgriSeed seed = matureNeighbours.get(index).getSeed();
            if (seed != null && rand.nextDouble() < seed.getPlant().getSpreadChance()) {
                return AgriApi.getStatCalculatorRegistry()
                        .valueOf(seed.getPlant())
                        .map(calc -> calc.calculateSpreadStats(seed.getPlant(), allNeighbours))
                        .map(stat -> new AgriSeed(seed.getPlant(), stat));
            }
        }
        return Optional.empty();
    }
}

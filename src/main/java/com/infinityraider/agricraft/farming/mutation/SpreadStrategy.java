package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.api.mutation.IAgriCrossStrategy;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.farming.mutation.statcalculator.StatCalculator;

import java.util.List;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import java.util.Optional;
import java.util.Random;

public class SpreadStrategy implements IAgriCrossStrategy {

    private static final SpreadStrategy INSTANCE = new SpreadStrategy();

    private SpreadStrategy() {

    }

    public final static SpreadStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<AgriSeed> executeStrategy(IAgriCrop crop, Random rand) {
        List<IAgriCrop> matureNeighbours = crop.getMatureNeighbours();
        if (!matureNeighbours.isEmpty()) {
            int index = rand.nextInt(matureNeighbours.size());
            IAgriPlant plant = matureNeighbours.get(index).getPlant();
            if (plant != null && rand.nextDouble() < plant.getSpreadChance()) {
                IAgriStat stat = StatCalculator.getInstance().calculateStats(plant, matureNeighbours, false);
                return Optional.of(new AgriSeed(plant, stat));
            }
        }
        return Optional.empty();
    }
}

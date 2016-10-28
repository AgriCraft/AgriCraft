package com.infinityraider.agricraft.farming.mutation;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.mutation.IAgriCrossStrategy;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.StatCalculatorRegistry;
import com.infinityraider.agricraft.config.AgriCraftConfig;

public class SpreadStrategy implements IAgriCrossStrategy {

    @Override
    public double getRollChance() {
        return Math.abs(1 - AgriCraftConfig.mutationChance);
    }

    @Override
    public Optional<AgriSeed> executeStrategy(IAgriCrop crop, Random rand) {
        List<IAgriCrop> matureNeighbours = crop.getMatureNeighbours();
        if (!matureNeighbours.isEmpty()) {
            int index = rand.nextInt(matureNeighbours.size());
            IAgriPlant plant = matureNeighbours.get(index).getPlant();
            if (plant != null && rand.nextDouble() < plant.getSpreadChance()) {
                return StatCalculatorRegistry.getInstance()
                        .calculateStats(plant, matureNeighbours, false)
                        .map(stat -> new AgriSeed(plant, stat));
            }
        }
        return Optional.empty();
    }
}

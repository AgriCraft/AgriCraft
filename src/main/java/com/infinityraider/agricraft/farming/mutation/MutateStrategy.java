package com.infinityraider.agricraft.farming.mutation;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.mutation.IAgriCrossStrategy;
import com.infinityraider.agricraft.api.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.apiimpl.MutationRegistry;
import com.infinityraider.agricraft.apiimpl.StatCalculatorRegistry;
import com.infinityraider.agricraft.config.AgriCraftConfig;

public class MutateStrategy implements IAgriCrossStrategy {

    @Override
    public double getRollChance() {
        return AgriCraftConfig.mutationChance;
    }

    @Override
    public Optional<AgriSeed> executeStrategy(IAgriCrop crop, Random rand) {
        List<IAgriCrop> matureNeighbors = crop.getMatureNeighbours();
        List<IAgriMutation> crossOvers = MutationRegistry.getInstance().getPossibleMutations(
                matureNeighbors.stream()
                .map(IAgriCrop::getPlant)
                .filter(TypeHelper::isNonNull)
                .collect(Collectors.toList())
        );
        if (!crossOvers.isEmpty()) {
            int index = rand.nextInt(crossOvers.size());
            IAgriMutation mutation = crossOvers.get(index);
            Optional<IAgriStat> stat = StatCalculatorRegistry.getInstance().calculateStats(mutation, matureNeighbors);
            if (stat.isPresent() && rand.nextDouble() < mutation.getChance()) {
                return Optional.of(new AgriSeed(mutation.getChild(), stat.get()));
            }
        }
        return Optional.empty();
    }

}

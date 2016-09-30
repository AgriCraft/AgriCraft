package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.api.mutation.IAgriCrossStrategy;
import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.farming.mutation.statcalculator.StatCalculator;
import com.infinityraider.agricraft.api.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.apiimpl.MutationRegistry;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class MutateStrategy implements IAgriCrossStrategy {

    private static final MutateStrategy INSTANCE = new MutateStrategy();

    private MutateStrategy() {

    }

    public static MutateStrategy getInstance() {
        return INSTANCE;
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
            IAgriStat stat = StatCalculator.getInstance().calculateStats(mutation.getChild(), matureNeighbors, true);
            if (rand.nextDouble() < mutation.getChance()) {
                return Optional.of(new AgriSeed(mutation.getChild(), stat));
            }
        }
        return Optional.empty();
    }

}

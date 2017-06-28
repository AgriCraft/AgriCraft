package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.mutation.IAgriCrossStrategy;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class MutateStrategy implements IAgriCrossStrategy {

    @Override
    public double getRollChance() {
        return AgriCraftConfig.mutationChance;
    }

    @Override
    public Optional<AgriSeed> executeStrategy(IAgriCrop crop, Random rand) {
        // Validate the parameters.
        Objects.requireNonNull(crop, "You cannot execute a mutation on a null crop!");
        Objects.requireNonNull(rand, "The random passed to a mutation strategy should not be null!");

        // Fetch all neighboring crop instances.
        final List<IAgriCrop> neighbors = WorldHelper.getTileNeighbors(crop.getCropWorld(), crop.getCropPos(), IAgriCrop.class);

        // Determine all possible parents.
        final List<IAgriPlant> parents = neighbors.stream()
                // Filter out all crops that are not mature.
                .filter(IAgriCrop::isMature)
                // Map to the seed associated with the crop.
                .map(IAgriCrop::getSeed)
                // Filter out all null objects (just in case).
                .filter(Objects::nonNull)
                // Map to the plant associated with the crop's seed.
                .map(AgriSeed::getPlant)
                // Collect into a list.
                .collect(Collectors.toList());

        // If we have less than two parents, might as well as abort.
        if (parents.size() < 2) {
            return Optional.empty();
        }

        // Determine the list of possible cross-over mutations.
        final List<IAgriMutation> mutations = AgriApi.getMutationRegistry()
                // Stream all mutations.
                .stream()
                // Filter out mutations with both parents in the 'parents' list.
                .filter(m -> m.areParentsIn(parents))
                // Filter out mutations whose child wouldn't be viable here.
                // Notice, this operation is by far the most costly.
                // That' why we perform this operation last.
                .filter(m -> crop.isFertile(m.getChild()))
                // Collect the list of all possibilties.
                .collect(Collectors.toList());

        // If we didn't find any valid mutations, might as well as abort.
        if (mutations.isEmpty()) {
            return Optional.empty();
        }

        // Choose a random index in the list.
        final int index = rand.nextInt(mutations.size());

        // Fetch the chosen mutation from the list.
        final IAgriMutation mutation = mutations.get(index);

        // Determine if we should actually go through with this.
        if (mutation.getChance() <= rand.nextDouble()) {
            return Optional.empty();
        }

        // Calculate the stat associated with the new plant.
        Optional<IAgriStat> stat = AgriApi.getStatCalculatorRegistry().valueOf(mutation.getChild()).map(c -> c.calculateMutationStats(mutation, neighbors));

        // Return the mutation result.
        return stat
                // Map the stat to an AgriSeed by adding the plant.
                .map(s -> new AgriSeed(mutation.getChild(), s));
    }

}

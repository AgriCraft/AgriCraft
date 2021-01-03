package com.infinityraider.agricraft.core.genetics;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.impl.v1.AgriRegistry;

import java.util.*;
import javax.annotation.Nonnull;

/**
 * The mutation registry implementation.
 * <p>
 * Comment: Wow, this class has an arcane type-signature...
 *
 * @author The AgriCraft Team
 */
public class AgriMutationRegistry extends AgriRegistry<IAgriMutation> implements IAgriMutationRegistry {
    private static final AgriMutationRegistry INSTANCE = new AgriMutationRegistry();

    public static final AgriMutationRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<IAgriPlant, Integer> complexities;

    private AgriMutationRegistry() {
        super("mutation", IAgriMutation.class);
        this.complexities = Maps.newIdentityHashMap();
    }

    @Override
    public boolean add(@Nonnull String id, double chance, @Nonnull String childId, @Nonnull String... parentIds) {
        return this.add(id, chance, childId, Arrays.asList(parentIds));
    }

    @Override
    public boolean add(@Nonnull String id, double chance, @Nonnull String childId, @Nonnull List<String> parentIds) {
        // Step I. Validate Parameters.
        Objects.requireNonNull(id, "The id of a mutation may not be null!");
        Objects.requireNonNull(childId, "The id of the child plant for a mutation may not be null!");
        Objects.requireNonNull(parentIds, "The supplied list of parents for a mutation may not be null!");

        // Step II. Validate Parents.
        parentIds.forEach(parentId -> Objects.requireNonNull(parentId, "The id of a parent for a mutation may not be null!"));

        // Step III. Map Child.
        final IAgriPlant childPlant = AgriApi.getPlantRegistry().get(id).orElse(null);

        // Step IV. Abort If Child Missing.
        if (childPlant == null) {
            // We tried, so don't throw error, just return false.
            return false;
        }

        // Step V. Allocate Parent Plant List.
        final List<IAgriPlant> parentPlants = new ArrayList<>(parentIds.size());

        // Step VI. Map Parents, Aborting If Missing.
        for (String parentId : parentIds) {
            final IAgriPlant parentPlant = AgriApi.getPlantRegistry().get(parentId).orElse(null);
            if (parentPlant != null) {
                parentPlants.add(parentPlant);
            } else {
                return false;
            }
        }

        // Step VII. Create the new mutation.
        final IAgriMutation mutation = new Mutation(id, chance, childPlant, parentPlants);

        // Step VIII. Register the new muation.
        return this.add(mutation);
    }

    @Override
    public int complexity(IAgriPlant plant) {
        if(!this.complexities.containsKey(plant)) {
            this.complexities.put(plant, this.calculateComplexity(plant));
        }
        return this.complexities.get(plant);
    }

    private int calculateComplexity(IAgriPlant plant) {
        return this.stream()
                .filter(mutation -> mutation.getChild().equals(plant))
                .min(Comparator.comparingInt(this::sumParentsComplexity))
                .flatMap(mut -> mut.getParents().stream().min(Comparator.comparingInt(this::complexity)))
                .map(parent -> this.complexity(parent) + 1)
                .orElse(0);
    }

    private int sumParentsComplexity(IAgriMutation mutation) {
        return mutation.getParents().stream().mapToInt(this::complexity).sum();
    }
}

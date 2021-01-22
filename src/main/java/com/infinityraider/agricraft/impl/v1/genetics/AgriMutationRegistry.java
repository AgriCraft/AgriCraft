package com.infinityraider.agricraft.impl.v1.genetics;

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

    public static AgriMutationRegistry getInstance() {
        return INSTANCE;
    }

    // TODO: Update complexities as mutations are added
    private final Map<IAgriPlant, Integer> complexities;

    private AgriMutationRegistry() {
        super();
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

        // Step VIII. Register the new mutation.
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
        // Plants which are not the child of any mutation are assumed to be easily obtainable e.g. from grass drops.
        // The complexity of such, easily obtainable plants is set to 1, which will be the lowest possible value.
        // Complexity of plants obtained through mutations is set equal to the sum of the complexity of the parents.
        // In case a plant can be obtained through multiple different mutations, the lowest complexity value is used.
        return this.stream()
                .filter(mutation -> mutation.getChild().equals(plant))
                .mapToInt(this::sumParentsComplexity)
                .min()
                .orElse(1);
    }

    private int sumParentsComplexity(IAgriMutation mutation) {
        // TODO: Ensure no infinite loops can originate here
        return mutation.getParents().stream().mapToInt(this::complexity).sum();
    }
}

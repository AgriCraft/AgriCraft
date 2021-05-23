package com.infinityraider.agricraft.impl.v1.genetics;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.event.AgriRegistryEvent;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.impl.v1.AgriRegistryAbstract;

import java.util.*;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The mutation registry implementation.
 * <p>
 * Comment: Wow, this class has an arcane type-signature...
 *
 * @author The AgriCraft Team
 */
public class AgriMutationRegistry extends AgriRegistryAbstract<IAgriMutation> implements IAgriMutationRegistry {
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
    public boolean add(@Nullable IAgriMutation mutation) {
        boolean result = super.add(mutation);
        // Update the complexity map
        if(result && mutation != null) {
            if(this.updateComplexity(mutation.getChild())) {
                this.updateComplexityForChildren(mutation.getChild());
            }
        }
        return result;
    }

    @Override
    public boolean remove(@Nullable IAgriMutation mutation) {
        boolean result = super.remove(mutation);
        if(result&& mutation != null) {
            if(this.updateComplexity(mutation.getChild())) {
                this.updateComplexityForChildren(mutation.getChild());
            }
        }
        return result;
    }

    protected boolean updateComplexity(IAgriPlant plant) {
        // Calculate the new complexity as it would appear from the mutation tree
        int newComplexity = this.calculateComplexity(plant);
        // Fetch the old complexity as it existed before the plant was updated
        int oldComplexity = this.complexity(plant);
        // If the new complexity is different, modifications must be made
        if(newComplexity != oldComplexity) {
            // If the nex complexity is zero, that means no parents exist for this plant
            if (newComplexity == 0) {
                // Remove the complexity
                this.complexities.remove(plant);
            } else {
                // Put the new complexity
                this.complexities.put(plant, newComplexity);
            }
            return true;
        }
        return false;
    }

    protected void updateComplexityForChildren(IAgriPlant plant) {
        Set<IAgriPlant> visited = Sets.newIdentityHashSet();
        visited.add(plant);
        this.updateComplexityForChildren(plant, visited);
    }

    protected void updateComplexityForChildren(IAgriPlant plant, Set<IAgriPlant> visited) {
        this.stream()
                // Iterate over all mutations which have the given plant as a parent
                .filter(mutation -> mutation.hasParent(plant))
                // Map to the child of each mutation which needs updating (the others are mapped to null)
                .map(mutation -> {
                    IAgriPlant child = mutation.getChild();
                    if(visited.contains(child)) {
                        // Prevent infinite loops
                        return null;
                    }
                    if(this.updateComplexity(child)) {
                        // Complexity has been updated, add it to the visited plants and return it
                        visited.add(child);
                        return child;
                    }
                    // No changes, return null
                    return null;
                })
                // Filter out null plants
                .filter(Objects::nonNull)
                // Iterate recursively
                .forEach(child -> this.updateComplexityForChildren(child, visited));
    }

    /**
     * Calculates the complexity for a plant by returning the minimum of all mutations which produce the given plant as child
     *
     * @param plant the plant
     * @return the calculated complexity, if it returns 0, no mutations result in this plant
     */
    protected int calculateComplexity(IAgriPlant plant) {
        return this.stream().filter(mutation -> mutation.hasChild(plant))
                .mapToInt(this::calculateComplexity)
                .min().orElse(0);
    }

    /**
     * Calculates the complexity for a mutation by summing the complexities (as they are currently defined) of the parents
     *
     * @param mutation the mutation
     * @return the calculated complexity, its minimum is 1
     */
    protected int calculateComplexity(IAgriMutation mutation) {
        return mutation.getParents().stream()
                .mapToInt(this::complexity)
                .sum() + 1;
    }

    @Override
    public int complexity(IAgriPlant plant) {
        return this.complexities.getOrDefault(plant, 0);
    }


    @Override
    public Stream<IAgriMutation> getMutationsFromParents(List<IAgriPlant> plants) {
        return this.stream().filter(mutation -> mutation.areParentsIn(plants));
    }
    @Nullable
    @Override
    protected AgriRegistryEvent<IAgriMutation> createEvent(IAgriMutation element) {
        return new AgriRegistryEvent.Mutation(this, element);
    }
}

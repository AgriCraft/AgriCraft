/*
 */
package com.infinityraider.agricraft.impl.v1;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import scala.actors.threadpool.Arrays;

/**
 * The mutation registry implementation.
 * <p>
 * Comment: Wow, this class has an arcane type-signature...
 *
 * @author The AgriCraft Team
 */
public class AgriMutationRegistry extends AgriRegistry<IAgriMutation> implements IAgriMutationRegistry {

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

}

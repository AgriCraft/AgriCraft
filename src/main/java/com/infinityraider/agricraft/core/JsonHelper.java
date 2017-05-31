/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.plant.AgriMutation;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class aiding in the handling of the JSON representation of AgriCraft objects.
 */
public final class JsonHelper {

    @Nonnull
    public static Optional<IAgriMutation> wrap(@Nullable AgriMutation mutation) {
        // Step I. Abort If Null Mutation.
        if (mutation == null) {
            return Optional.empty();
        }

        // Step II. Determine Chance.
        final double chance = mutation.getChance();

        // Step III. Determine ID.
        final String mutationId = mutation.getChild().getId().replace("_plant", "_mutation");

        // Step IV. Determine Child.
        final Optional<IAgriPlant> child = AgriApi.getPlantRegistry().get(mutation.getChild().getId());

        // Step V. Abort If Child Missing.
        if (!child.isPresent()) {
            return Optional.empty();
        }

        // Step VI. Determine Parents.
        final Optional<IAgriPlant> parentOne = AgriApi.getPlantRegistry().get(mutation.getParent1().getId());
        final Optional<IAgriPlant> parentTwo = AgriApi.getPlantRegistry().get(mutation.getParent2().getId());

        // Step VII. Abort If Missing Parent.
        if ((!parentOne.isPresent()) && (!parentTwo.isPresent())) {
            return Optional.empty();
        }

        // Step VIII. Create New Mutation
        return Optional.of(new Mutation(mutationId, chance, child.get(), parentOne.get(), parentTwo.get()));
    }

}

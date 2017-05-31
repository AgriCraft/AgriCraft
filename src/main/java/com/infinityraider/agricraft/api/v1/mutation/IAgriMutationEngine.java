/*
 */
package com.infinityraider.agricraft.api.v1.mutation;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for working with the AgriCraft mutation engine.
 */
public interface IAgriMutationEngine {

    boolean registerStrategy(@Nullable IAgriCrossStrategy strategy);

    @Nonnull
    List<IAgriCrossStrategy> getStrategies();

    boolean hasStrategy(@Nullable IAgriCrossStrategy strategy);

    @Nonnull
    Optional<IAgriCrossStrategy> rollStrategy(Random rand);

    default boolean attemptCross(@Nonnull IAgriCrop crop, @Nonnull Random rand) {
        // Validate the parameters.
        Objects.requireNonNull(crop, "You cannot mutate or spread a null crop! Why would you even try this‽");
        Objects.requireNonNull(rand, "The mutation engine requires a non-null random instance to work!");

        return rollStrategy(rand)
                .flatMap(s -> s.executeStrategy(crop, rand))
                .filter(crop::isFertile)
                .map(seed -> {
                    crop.setCrossCrop(false);
                    crop.setSeed(seed);
                    return true;
                })
                .orElse(false);
    }

}

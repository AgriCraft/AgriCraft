/*
 */
package com.infinityraider.agricraft.api.v1.mutation;

import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for working with the AgriCraft mutation engine.
 */
public interface IAgriMutationEngine {

    boolean registerStrategy(@Nonnull IAgriCrossStrategy strategy);

    @Nonnull
    List<IAgriCrossStrategy> getStrategies();

    boolean hasStrategy(@Nullable IAgriCrossStrategy strategy);

    @Nonnull
    Optional<IAgriCrossStrategy> rollStrategy(@Nonnull Random rand);

    default boolean attemptCross(@Nonnull IAgriCrop crop, @Nonnull Random rand) {
        // Validate the parameters.
        Preconditions.checkNotNull(crop, "You cannot mutate or spread a null crop! Why would you even try this‽");
        Preconditions.checkNotNull(rand, "The mutation engine requires a non-null random instance to work!");

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

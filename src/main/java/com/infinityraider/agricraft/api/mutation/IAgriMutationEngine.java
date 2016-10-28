/*
 */
package com.infinityraider.agricraft.api.mutation;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.infinityraider.agricraft.api.crop.IAgriCrop;

/**
 *
 */
public interface IAgriMutationEngine {

    boolean registerStrategy(IAgriCrossStrategy strategy);

    List<IAgriCrossStrategy> getStrategies();

    boolean hasStrategy(IAgriCrossStrategy strategy);

    Optional<IAgriCrossStrategy> rollStrategy(Random rand);

    default boolean attemptCross(IAgriCrop crop, Random rand) {
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

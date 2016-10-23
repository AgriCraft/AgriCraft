/*
 */
package com.infinityraider.agricraft.api.stat;

import com.infinityraider.agricraft.api.adapter.IAgriAdapterRegistry;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.apiimpl.StatCalculatorRegistry;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Ryan
 */
public interface IAgriStatCalculatorRegistry extends IAgriAdapterRegistry<IAgriStatCalculator> {
    
    default Optional<IAgriStat> calculateStats(IAgriPlant plant, List<? extends IAgriCrop> matureNeighbors) {
        return calculateStats(plant, matureNeighbors, false);
    }

    default Optional<IAgriStat> calculateStats(IAgriMutation mutation, List<? extends IAgriCrop> matureNeighbors) {
        return calculateStats(mutation.getChild(), matureNeighbors, true);
    }

    default Optional<IAgriStat> calculateStats(IAgriPlant child, List<? extends IAgriCrop> matureNeighbors, boolean mutation) {
        return StatCalculatorRegistry.getInstance().valueOf(child)
                .map(calc -> calc.calculateStats(child, matureNeighbors, mutation));
    }

}

package com.infinityraider.agricraft.farming.mutation.statcalculator;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatCalculator;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import java.util.Optional;

public class StatCalculatorNormal extends StatCalculatorBase {

    /**
     * calculates the new stats based on an input stat, the nr of neighbours and a divisor
     */
    @Override
    protected int calculateStat(int input, int neighbours, int divisor) {
        if (neighbours == 1 && AgriCraftConfig.singleSpreadsIncrement) {
            neighbours = 2;
        }
        int newStat = Math.max(1, (input + (int) Math.round(Math.abs(neighbours - 1) * Math.random())) / divisor);
        return Math.min(newStat, AgriCraftConfig.cropStatCap);
    }

    @Override
    public boolean accepts(Object obj) {
        return (!AgriCraftConfig.hardCoreStats) && (obj instanceof IAgriPlant);
    }

    @Override
    public Optional<IAgriStatCalculator> valueOf(Object obj) {
        return accepts(obj) ? Optional.of(this) : Optional.empty();
    }
}

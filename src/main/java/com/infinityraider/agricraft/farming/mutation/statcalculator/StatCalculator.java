package com.infinityraider.agricraft.farming.mutation.statcalculator;

import com.infinityraider.agricraft.config.AgriCraftConfig;

import java.util.List;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.stat.IAgriStatCalculator;

public abstract class StatCalculator implements IAgriStatCalculator {

    private static IAgriStatCalculator instance;

    protected StatCalculator() {
    }

    public static IAgriStatCalculator getInstance() {
        if (instance == null) {
            if (AgriCraftConfig.hardCoreStats) {
                instance = new StatCalculatorHardcore();
            } else {
                instance = new StatCalculatorNormal();
            }
        }
        return instance;
    }

    public static void setStatCalculator(IAgriStatCalculator calculator) {
        instance = calculator;
    }

    @Override
    public abstract IAgriStat calculateStats(IAgriPlant child, List<? extends IAgriCrop> input, boolean mutation);

}

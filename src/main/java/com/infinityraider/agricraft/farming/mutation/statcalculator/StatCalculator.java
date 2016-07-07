package com.infinityraider.agricraft.farming.mutation.statcalculator;

import com.infinityraider.agricraft.farming.mutation.CrossOverResult;
import com.infinityraider.agricraft.config.AgriCraftConfig;

import java.util.List;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.stat.IAgriStatCalculator;

public abstract class StatCalculator implements IAgriStatCalculator {
    private static IAgriStatCalculator instance;

    protected StatCalculator() {}

    public static IAgriStatCalculator getInstance() {
        if(instance == null) {
            if(AgriCraftConfig.hardCoreStats) {
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

    /**
     * Applies the stats to the resulting crop after a spread or mutation
     * @param result The result from the spread/mutation
     * @param input A list with all the neighbouring crops, any neighbouring crop is in this list (with or without plant, mature or not, with weeds or not, ...)
     * @param mutation if this result comes from a mutation or from a spread
     */
    public static void setResultStats(CrossOverResult result, List<? extends IAgriCrop> input, boolean mutation) {
        result.setStats(instance.calculateStats(result.getPlant(), input, mutation));
    }

	@Override
    public abstract IAgriStat calculateStats(IAgriPlant child, List<? extends IAgriCrop> input, boolean mutation);
}

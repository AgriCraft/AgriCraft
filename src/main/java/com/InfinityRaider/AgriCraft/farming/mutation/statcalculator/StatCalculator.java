package com.InfinityRaider.AgriCraft.farming.mutation.statcalculator;

import com.InfinityRaider.AgriCraft.api.v2.ISeedStats;
import com.InfinityRaider.AgriCraft.api.v2.ICrop;
import com.InfinityRaider.AgriCraft.api.v2.IStatCalculator;
import com.InfinityRaider.AgriCraft.farming.mutation.CrossOverResult;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class StatCalculator implements IStatCalculator {
    private static IStatCalculator instance;

    protected StatCalculator() {}

    public static IStatCalculator getInstance() {
        if(instance == null) {
            if(ConfigurationHandler.hardCoreStats) {
                instance = new StatCalculatorHardcore();
            } else {
                instance = new StatCalculatorNormal();
            }
        }
        return instance;
    }

    public static void setStatCalculator(IStatCalculator calculator) {
        instance = calculator;
    }

    /**
     * Applies the stats to the resulting crop after a spread or mutation
     * @param result The result from the spread/mutation
     * @param input A list with all the neighbouring crops, any neighbouring crop is in this list (with or without plant, mature or not, with weeds or not, ...)
     * @param mutation if this result comes from a mutation or from a spread
     */
    public static void setResultStats(CrossOverResult result, List<? extends ICrop> input, boolean mutation) {
        ISeedStats stats = instance.calculateStats(result.toStack(), input, mutation);
        result.setStats(stats.getGrowth(), stats.getGain(), stats.getStrength());
    }

    public abstract ISeedStats calculateStats(ItemStack result, List<? extends ICrop> input, boolean mutation);
}

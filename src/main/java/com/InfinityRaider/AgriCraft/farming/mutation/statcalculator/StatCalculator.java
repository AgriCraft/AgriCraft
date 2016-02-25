package com.InfinityRaider.AgriCraft.farming.mutation.statcalculator;

import com.InfinityRaider.AgriCraft.api.v2.ISeedStats;
import com.InfinityRaider.AgriCraft.api.v2.ICrop;
import com.InfinityRaider.AgriCraft.api.v3.IStatCalculator;
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

    @Override
    public abstract ISeedStats calculateStats(ItemStack result, List<? extends ICrop> input, boolean mutation);
}

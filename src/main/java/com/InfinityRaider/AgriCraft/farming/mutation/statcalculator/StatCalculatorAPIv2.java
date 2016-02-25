package com.InfinityRaider.AgriCraft.farming.mutation.statcalculator;

import com.InfinityRaider.AgriCraft.api.v2.ICrop;
import com.InfinityRaider.AgriCraft.api.v2.ISeedStats;
import com.InfinityRaider.AgriCraft.api.v3.IStatCalculator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class StatCalculatorAPIv2 implements IStatCalculator {
    private final com.InfinityRaider.AgriCraft.api.v2.IStatCalculator calculator;

    public StatCalculatorAPIv2(com.InfinityRaider.AgriCraft.api.v2.IStatCalculator calculator) {
        this.calculator = calculator;
    }
    @Override
    public ISeedStats calculateStats(Item result, int resultMeta, List<? extends ICrop> input, boolean mutation) {
        return calculator.calculateStats(new ItemStack(result, 1, resultMeta), input, mutation);
    }

    @Override
    public ISeedStats calculateStats(ItemStack result, List<? extends ICrop> input, boolean mutation) {
        return calculator.calculateStats(result, input, mutation);
    }
}

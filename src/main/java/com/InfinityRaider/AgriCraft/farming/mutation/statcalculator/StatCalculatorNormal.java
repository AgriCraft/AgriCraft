package com.InfinityRaider.AgriCraft.farming.mutation.statcalculator;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;

public class StatCalculatorNormal extends StatCalculatorBase {
    /** calculates the new stats based on an input stat, the nr of neighbours and a divisor*/
    protected int calculateStats(int input, int neighbours, int divisor) {
        if(neighbours == 1 && ConfigurationHandler.singleSpreadsIncrement) {
            neighbours = 2;
        }
        int newStat = Math.max(1, (input + (int) Math.round(Math.abs(neighbours-1)*Math.random()))/divisor);
        return Math.min(newStat, ConfigurationHandler.cropStatCap);
    }
}

package com.InfinityRaider.AgriCraft.farming.mutation.statcalculator;

import com.InfinityRaider.AgriCraft.farming.mutation.CrossOverResult;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

import java.util.List;

public abstract  class StatCalculatorBase extends StatCalculator {
    public void setResultStats(CrossOverResult result, List<TileEntityCrop> input, boolean mutation) {
        TileEntityCrop[] parents = filterParents(input);
        int nrParents = parents.length;
        int nrValidParents = 0;
        int[] growth = new int[nrParents];
        int[] gain = new int[nrParents];
        int[] strength = new int[nrParents];
        for (int i = 0; i < nrParents; i++) {
            boolean canInherit = canInheritStats(result.getSeed(), result.getMeta(), parents[i].getSeedStack().getItem(), parents[i].getSeedStack().getItemDamage());
            if (canInherit) {
                nrValidParents = nrValidParents + 1;
            }
            //multiplier is the difficulty
            //-1: if neighbour is a non-parent crops and non parent crops do not affect stats, multiplier is -1 (negative values get filtered)
            //0 : if neighbour is a non-parent crop, and non parent crops affect stat gain negatively, multiplier is 0 (0 will reduce the average)
            //1 : if neighbour is parent crop, multiplier is 1
            int multiplier = canInherit ? 1 : (ConfigurationHandler.otherCropsAffectStatsNegatively ? 0 : -1);
            growth[i] = multiplier * parents[i].getGrowth();
            gain[i] = multiplier * parents[i].getGain();
            strength[i] = multiplier * parents[i].getStrength();
        }
        int meanGrowth = getMeanIgnoringNegativeValues(growth);
        int meanGain = getMeanIgnoringNegativeValues(gain);
        int meanStrength = getMeanIgnoringNegativeValues(strength);
        int divisor = mutation ? ConfigurationHandler.cropStatDivisor : 1;
        result.setStats(calculateStats(meanGrowth, nrValidParents, divisor), calculateStats(meanGain, nrValidParents, divisor), calculateStats(meanStrength, nrValidParents, divisor));
    }

    /**returns the mean value of an int array, this ignores negative values in the array*/
    private int getMeanIgnoringNegativeValues(int[] input) {
        int sum = 0;
        int total = input.length;
        int mean = 0;
        if(total>0) {
            for (int nr : input) {
                if(nr>=0) {
                    sum = sum + nr;
                }
                else {
                    total--;
                }
            }
            if(total>0) {
                mean = Math.round(((float) sum) / ((float) total));
            }
        }
        return mean;
    }

    /** calculates the new stats based on an input stat, the nr of neighbours and a divisor*/
    protected abstract int calculateStats(int input, int neighbours, int divisor);
}

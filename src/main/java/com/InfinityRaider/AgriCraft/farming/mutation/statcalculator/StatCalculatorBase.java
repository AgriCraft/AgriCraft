package com.InfinityRaider.AgriCraft.farming.mutation.statcalculator;

import com.InfinityRaider.AgriCraft.api.v2.ISeedStats;
import com.InfinityRaider.AgriCraft.api.v2.ICrop;
import com.InfinityRaider.AgriCraft.farming.PlantStats;
import com.InfinityRaider.AgriCraft.farming.mutation.Mutation;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract  class StatCalculatorBase extends StatCalculator {
    @Override
    public ISeedStats calculateStats(ItemStack result, List<? extends ICrop> input, boolean mutation) {
        ICrop[] parents = filterParents(input);
        int nrParents = parents.length;
        int nrValidParents = 0;
        int[] growth = new int[nrParents];
        int[] gain = new int[nrParents];
        int[] strength = new int[nrParents];
        for (int i = 0; i < nrParents; i++) {
            boolean canInherit = canInheritStats(result.getItem(), result.getItemDamage(), parents[i].getSeedStack().getItem(), parents[i].getSeedStack().getItemDamage());
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
        return new PlantStats(calculateStats(meanGrowth, nrValidParents, divisor), calculateStats(meanGain, nrValidParents, divisor), calculateStats(meanStrength, nrValidParents, divisor));
    }

    //gets an array of all the possible parents from the array containing all the neighbouring crops
    protected ICrop[] filterParents(List<? extends ICrop> input) {
        ArrayList<ICrop> list = new ArrayList<ICrop>();
        for(ICrop crop:input) {
            if (crop != null && crop.isMature()) {
                list.add(crop);
            }
        }
        return list.toArray(new ICrop[list.size()]);
    }

    protected boolean canInheritStats(Item child, int childMeta, Item seed, int seedMeta) {
        int validParentId = ConfigurationHandler.validParents;
        //1: any crop
        //2: only parent crops and identical crops
        //3: only identical crops
        if(validParentId == 1) {
            return true;
        }
        if(validParentId == 3) {
            return child==seed && childMeta==seedMeta;
        }
        boolean b = child==seed && childMeta==seedMeta;
        if(!b) {
            for(Mutation mutation: MutationHandler.getMutationsFromChild(child, childMeta)) {
                if(mutation!=null) {
                    ItemStack parent1Stack = mutation.getParents()[0];
                    ItemStack parent2Stack = mutation.getParents()[1];
                    if(parent1Stack.getItem()==seed && parent1Stack.getItemDamage()==seedMeta) {
                        b = true;
                        break;
                    }
                    else if(parent2Stack.getItem()==seed && parent2Stack.getItemDamage()==seedMeta) {
                        b = true;
                        break;
                    }
                }
            }
        }
        return b;
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

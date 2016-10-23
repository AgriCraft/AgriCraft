package com.infinityraider.agricraft.farming.mutation.statcalculator;

import com.infinityraider.agricraft.api.adapter.IAgriAdapter;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.config.AgriCraftConfig;

import java.util.List;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.stat.IAgriStatCalculator;
import com.infinityraider.agricraft.apiimpl.MutationRegistry;

public abstract class StatCalculatorBase implements IAgriStatCalculator, IAgriAdapter<IAgriStatCalculator> {

    @Override
    public IAgriStat calculateStats(IAgriPlant child, List<? extends IAgriCrop> parents, boolean mutation) {
        
        // Remove immature parents.
        parents.removeIf(p -> !p.isMature());
        
        // Variables
        int validParents = 0;
        int growth = 0;
        int gain = 0;
        int strength = 0;
        
        // Sum values
        for (IAgriCrop parent : parents) {
            if (canInheritStats(child, parent.getPlant())) {
                validParents++;
                growth += parent.getStat().getGrowth();
                gain += parent.getStat().getGain();
                strength += parent.getStat().getStrength();
            }
        }
        
        // Calculate Mean
        int num = AgriCraftConfig.otherCropsAffectStatsNegatively ? parents.size() : validParents;
        num = num < 1 ? 1 : num; // Ensure above one...
        growth /= num;
        gain /= num;
        strength /= num;
        int divisor = mutation ? AgriCraftConfig.cropStatDivisor : 1;
        
        return new PlantStats(
                calculateStats(growth, validParents, divisor),
                calculateStats(gain, validParents, divisor),
                calculateStats(strength, validParents, divisor)
        );
    }

    // TODO: Investigate Config Setting.
    protected boolean canInheritStats(IAgriPlant child, IAgriPlant parent) {
        int validParentId = AgriCraftConfig.validParents;
        //1: any crop
        //2: only identical crops
        //3: only parent crops and identical crops 
        switch (validParentId) {
            case 1:
            case 2:
                return child.equals(parent);
            case 3:
                if (child.equals(parent)) {
                    return true;
                }
                for (IAgriMutation mutation : MutationRegistry.getInstance().getMutationsForChild(child)) {
                    if (mutation.getParents().contains(parent)) {
                        return true;
                    }
                }
            default:
                return false;
        }
    }

    /**
     * calculates the new stats based on an input stat, the nr of neighbours and
     * a divisor
     */
    protected abstract int calculateStats(int input, int neighbours, int divisor);

}

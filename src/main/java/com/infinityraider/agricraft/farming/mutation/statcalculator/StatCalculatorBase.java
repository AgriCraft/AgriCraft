package com.infinityraider.agricraft.farming.mutation.statcalculator;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatCalculator;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import java.util.Collection;
import java.util.Objects;

public abstract class StatCalculatorBase implements IAgriStatCalculator, IAgriAdapter<IAgriStatCalculator> {

    @Override
    public IAgriStat calculateSpreadStats(IAgriPlant child, Collection<IAgriCrop> parents) {
        // Validate parameters.
        Objects.requireNonNull(child, "The child plant to calculate the stats for must not be null!");
        Objects.requireNonNull(parents, "The set of parents to calculate the child's stats from must not be null!");

        // Variables
        int invalidParents = 0;
        int validParents = 0;
        int growth = 0;
        int gain = 0;
        int strength = 0;

        // Sum values
        for (IAgriCrop parent : parents) {
            // Skip parent if null.
            if (parent == null) {
                continue;
            }

            // Fetch the seed associated with the parent.
            final AgriSeed parentSeed = parent.getSeed();

            // Skip if parent seed is null.
            if (parentSeed == null) {
                continue;
            }

            // If the parent is not mature, counts as invalid parent.
            if (!parent.isMature()) {
                invalidParents++;
                continue;
            }

            // If the parent plant does not match the child plant, invalid parent.
            if (!Objects.equals(child, parentSeed.getPlant())) {
                invalidParents++;
                continue;
            }

            // Otherwise everything is aok.
            validParents++;
            growth += parentSeed.getStat().getGrowth();
            gain += parentSeed.getStat().getGain();
            strength += parentSeed.getStat().getStrength();
        }

        // Determine the stat divisor.
        final int meanDivisor = calculateStatMeanDivisor(validParents, invalidParents);

        // Perform averages.
        growth = growth / meanDivisor;
        gain = gain / meanDivisor;
        strength = strength / meanDivisor;

        // Return the new plant stat.
        return new PlantStats(
                calculateStat(growth, validParents, 1),
                calculateStat(gain, validParents, 1),
                calculateStat(strength, validParents, 1)
        );
    }

    @Override
    public IAgriStat calculateMutationStats(IAgriMutation mutation, Collection<IAgriCrop> parents) {
        // Validate parameters.
        Objects.requireNonNull(mutation, "The mutation to calculate the stats for must not be null!");
        Objects.requireNonNull(parents, "The set of parents to calculate the mutation result stats from must not be null!");

        // Variables
        int invalidParents = 0;
        int validParents = 0;
        int growth = 0;
        int gain = 0;
        int strength = 0;

        // Sum values
        for (IAgriCrop parent : parents) {
            // Skip parent if null.
            if (parent == null) {
                continue;
            }

            // Fetch the seed associated with the parent.
            final AgriSeed parentSeed = parent.getSeed();

            // Skip if parent seed is null.
            if (parentSeed == null) {
                continue;
            }

            // If the parent is not mature, counts as invalid parent.
            if (!parent.isMature()) {
                invalidParents++;
                continue;
            }

            // If the parent plant does not match the child plant, invalid parent.
            if (!mutation.hasParent(parentSeed.getPlant())) {
                invalidParents++;
                continue;
            }

            // Otherwise everything is aok.
            validParents++;
            growth += parentSeed.getStat().getGrowth();
            gain += parentSeed.getStat().getGain();
            strength += parentSeed.getStat().getStrength();
        }

        // Determine the stat divisor.
        final int meanDivisor = calculateStatMeanDivisor(validParents, invalidParents);

        // Perform averages.
        growth = growth / meanDivisor;
        gain = gain / meanDivisor;
        strength = strength / meanDivisor;

        // Return the new plant stat.
        return new PlantStats(
                calculateStat(growth, validParents, AgriCraftConfig.cropStatDivisor),
                calculateStat(gain, validParents, AgriCraftConfig.cropStatDivisor),
                calculateStat(strength, validParents, AgriCraftConfig.cropStatDivisor)
        );
    }

    /**
     * calculates the new stats based on an input stat, the nr of neighbours and a divisor
     */
    protected abstract int calculateStat(int input, int neighbours, int divisor);

    /**
     * Calculates the divisor to use for the mean operation on the stats of the parent plants.
     *
     * @param validParents The total number of valid parents.
     * @param invalidParents The total number of invalid parents.
     * @return The divisor to use for averaging the stats of the parent plants.
     */
    public static final int calculateStatMeanDivisor(int validParents, int invalidParents) {
        // Allocate the divisor.
        int meanDivisor = validParents;

        // Account for invalid parents, if enabled in config.
        if (AgriCraftConfig.otherCropsAffectStatsNegatively) {
            meanDivisor = meanDivisor + invalidParents;
        }

        // Verify that the mean divisor is at least one.
        if (meanDivisor < 1) {
            meanDivisor = 1;
        }

        // Return the calculated divisor.
        return meanDivisor;
    }

}

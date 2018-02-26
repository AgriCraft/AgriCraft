package com.infinityraider.agricraft.reference;

/**
 * A class containing the constants and default values used in the coding of the AgriCraft mod.
 */
public interface Constants {

    /**
     * The number of units in a block.
     */
    int WHOLE = 16;

    /**
     * The number of units in a quarter block.
     */
    int QUARTER = WHOLE / 4;

    /**
     * The number of units in a half block.
     */
    int HALF = WHOLE / 2;

    /**
     * The number of units in a 3/4 block.
     */
    int THREE_QUARTER = QUARTER * 3;

    /**
     * The value of 1/16 as represented in float form. Pre-calculated as to cut back on
     * calculations.
     */
    float UNIT = 1.0f / WHOLE;

    /**
     * The representation of 1 bucket(b) in millibuckets(mB).
     */
    int BUCKET_mB = 1000;

    /**
     * The representation of 1/2 a bucket(b) in millibuckets(mB).
     */
    int HALF_BUCKET_mB = BUCKET_mB / 2;

    /**
     * The representation of 1/4 a bucket(b) in millibuckets(mB).
     */
    int QUARTER_BUCKET_mB = BUCKET_mB / 4;

    /**
     * The meta-data value representing a mature crop.
     *
     * Mature = {@value}
     */
    int MATURE = 7;

    /**
     * An array of the possible growth tiers. Ranges from 0-5, with 0 containing the default value.
     */
    int[] GROWTH_TIER = {
        50, //Tier 0, a placeholder and default value.
        50, //Tier I
        45, //Tier II
        35, //Tier III
        25, //Tier VI
        20 //Tier V
    };

    /*
     * Default Plant Stats.
     */
    /**
     * The default growth of the crop.
     *
     * Growth = {@value}
     */
    short DEFAULT_GROWTH = 1;

    /**
     * The default gain of the crop.
     *
     * Represents the base number of drops from harvest.
     *
     * Gain = {@value}
     */
    short DEFAULT_GAIN = 1;

    /**
     * The default strength of the crop.
     *
     * Represents the base resistance to weed propagation.
     *
     * Strength = {@value}
     */
    short DEFAULT_STRENGTH = 1;

    /**
     * The default mutation chance of the crop.
     *
     * Represents the percent chance to mutate on any given tick.
     *
     * Chance = {@value}
     */
    float DEFAULT_MUTATION_CHANCE = 0.2F;

}

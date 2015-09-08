package com.InfinityRaider.AgriCraft.reference;

/**
 *	A class containing the constants and default values used in the coding of the AgriCraft mod.
 */
public final class Constants {

    /**
     * Whether or not the mod should log render calls.
     */
    public static final boolean LOG_RENDER_CALLS = false;
    
    /**
     * The value of 1/16 as represented in float form.
     * Pre-calculated as to cut back on calculations.
     */
    public static final float UNIT = 0.06250F;

    /**
     * The representation of 1 bucket(b) in millibuckets(mB).
     */
    public static final int BUCKET_mB = 1000;

    /**
     * The meta-data value representing a mature crop.
     * 
     * Mature = {@value}
     */
    public static final int MATURE = 7;
    
    /**
     * An array of the possible growth tiers.
     * Ranges from 0-5, with 0 containing the default value.
     */
    public static final int[] GROWTH_TIER = {
    		50, //Tier 0, a placeholder and default value.
    		50, //Tier I
    		45, //Tier II
    		35, //Tier III
    		25, //Tier VI
    		20  //Tier V
    	};

    /*
     * Default Plant Stats.
     */
    /**
     * The default growth of the crop.
     * Possibly unneeded, should be a GROWTH_TIER?
     * 
     * Growth = {@value}
     */
    public static final short DEFAULT_GROWTH = 1;
    /**
     * The default gain of the crop.
     * 
     * Represents the base number of drops from harvest.
     * 
     * Gain = {@value}
     */
    public static final short DEFAULT_GAIN = 1;
    /**
     * The default strength of the crop.
     * 
     * Represents the base resistance to weed propagation.
     * 
     * Strength = {@value}
     */
    public static final short DEFAULT_STRENGTH = 1;

    /**
     * The default mutation chance of the crop.
     * 
     * Represents the percent chance to mutate on any given tick.
     * 
     * Chance = {@value}
     */
    public static final double DEFAULT_MUTATION_CHANCE = 0.2;

    //constants for positioning item textures on the NEI recipe
    public static final int nei_X_parent1 = 44;
    public static final int nei_X_parent2 = 106;
    public static final int nei_X_result = 75;
    public static final int nei_Y_seeds = 21;
    public static final int nei_Y_soil = 47;
    public static final int nei_Y_base = 68;
}

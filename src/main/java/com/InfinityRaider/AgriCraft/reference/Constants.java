package com.InfinityRaider.AgriCraft.reference;

import java.util.Random;

public final class Constants {

    public static final boolean LOG_RENDER_CALLS = false;

    public static final Random RAND = new Random(); //This is an odd choice.
    // 1/16th
    public static final float UNIT = 0.06250F;

    // 1 bucket in millibuckets
    public static final int mB = 1000;

    // Mature meta-data value.
    public static final int MATURE = 7; //As per java standards, constants are to be in all-caps.
    
    // Growth Rates, as per Tier.
    public static final int[] GROWTH_TIER = { //This provides for faster tier lookup based off of a numeric. Also allows for easier addition of tier.
    		50, //Tier 0, a placeholder and default value.
    		50, //Tier I
    		45, //Tier II
    		35, //Tier III, By changing this from 30 to 35 a more normalized s-curve is achieved.
    		25, //Tier VI
    		20  //Tier V
    	};

    //default plant stats
    public static final short DEFAULT_GROWTH = 1;
    public static final short DEFAULT_GAIN = 1;
    public static final short DEFAULT_STRENGTH = 1;
    public static final int DEFAULT_TIER = 1;

    //default mutation chance
    public static final double DEFAULT_MUTATION_CHANCE = 0.2;

    //constants for positioning item textures on the NEI recipe
    public static final int nei_X_parent1 = 44;
    public static final int nei_X_parent2 = 106;
    public static final int nei_X_result = 75;
    public static final int nei_Y_seeds = 21;
    public static final int nei_Y_soil = 47;
    public static final int nei_Y_base = 68;
}

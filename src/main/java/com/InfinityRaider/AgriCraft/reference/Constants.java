package com.InfinityRaider.AgriCraft.reference;

import java.util.Random;

public final class Constants {

    public static final boolean LOG_RENDER_CALLS = false;

    public static final Random rand = new Random();
    // 1/16th
    public static final float unit = 0.06250F;

    // 1 bucket in millibuckets
    public static final int mB = 1000;

    //growth tiers
    public static final int growthTier1 = 50;
    public static final int growthTier2 = 45;
    public static final int growthTier3 = 30;
    public static final int growthTier4 = 25;
    public static final int growthTier5 = 20;

    //default plant stats
    public static final short defaultGrowth = 1;
    public static final short defaultGain = 1;
    public static final short defaultStrength = 1;
    public static final int defaultTier = 1;

    //default mutation chance
    public static final double defaultMutationChance = 0.2;

    //constants for positioning item textures on the NEI recipe
    public static final int nei_X_parent1 = 44;
    public static final int nei_X_parent2 = 106;
    public static final int nei_X_result = 75;
    public static final int nei_Y_seeds = 21;
    public static final int nei_Y_soil = 47;
    public static final int nei_Y_base = 68;

    //id's
    public static final int cropId = 0;
    public static final int tankId = 1;
    public static final int channelId = 2;
    public static final int valveId = 3;
    public static final int channelFullId = 4;
    public static final int waterPadId = 5;
}

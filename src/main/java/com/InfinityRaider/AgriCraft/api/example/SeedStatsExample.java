package com.InfinityRaider.AgriCraft.api.example;

import com.InfinityRaider.AgriCraft.api.v2.ISeedStats;
import cpw.mods.fml.common.Optional;

/**
 * Example implementation of the ISeedStats interface
 */
@Optional.Interface(modid = "AgriCraft", iface = "com.InfinityRaider.AgriCraft.api.v2.ISeedStats")
public class SeedStatsExample implements ISeedStats {
    private short growth;
    private short gain;
    private short strength;
    private boolean analyzed;

    public SeedStatsExample(short growth, short gain, short strength, boolean analyzed) {
        this.growth = growth;
        this.gain = gain;
        this.strength = strength;
        this.analyzed = analyzed;
    }

    @Override
    public short getGrowth() {
        return growth;
    }

    @Override
    public short getGain() {
        return gain;
    }

    @Override
    public short getStrength() {
        return strength;
    }

    @Override
    public short getMaxGrowth() {
        return ExampleAgriCraftAPIwrapper.getInstance().exampleMethodGetSeedStatsCap();
    }

    @Override
    public short getMaxGain() {
        return ExampleAgriCraftAPIwrapper.getInstance().exampleMethodGetSeedStatsCap();
    }

    @Override
    public short getMaxStrength() {
        return ExampleAgriCraftAPIwrapper.getInstance().exampleMethodGetSeedStatsCap();
    }

    @Override
    public boolean isAnalyzed() {
        return analyzed;
    }

    @Override
    public void setAnalyzed(boolean value) {
        this.analyzed = value;
    }
}

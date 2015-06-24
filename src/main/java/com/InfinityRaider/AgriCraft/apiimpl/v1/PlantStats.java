package com.InfinityRaider.AgriCraft.apiimpl.v1;

import com.InfinityRaider.AgriCraft.api.v1.ISeedStats;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.nbt.NBTTagCompound;

public class PlantStats implements ISeedStats {
    private static final short MAX = (short) ConfigurationHandler.cropStatCap;
    private static final short MIN = 1;

    private short growth;
    private short gain;
    private short strength;

    public PlantStats() {
        this(MIN, MIN, MIN);
    }

    public PlantStats(int growth, int gain, int strength) {
        this.setStats(growth, gain, strength);
    }

    public void setStats(int growth, int gain, int strength) {
        setGrowth(growth);
        setGain(gain);
        setStrength(strength);
    }

    public short getGrowth() {
        return growth;
    }

    public short getGain() {
        return gain;
    }

    public short getStrength() {
        return strength;
    }

    @Override
    public short getMaxGrowth() {
        return MAX;
    }

    @Override
    public short getMaxGain() {
        return MAX;
    }

    @Override
    public short getMaxStrength() {
        return MAX;
    }

    public void setGrowth(int growth) {
        this.growth = moveIntoBounds(growth);
    }

    public void setGain(int gain) {
        this.gain = moveIntoBounds(gain);
    }

    public void setStrength(int strength) {
        this.strength = moveIntoBounds(strength);
    }

    private short moveIntoBounds(int stat) {
        int lowerLimit = Math.max(MIN, stat);
        return (short) Math.min(MAX, lowerLimit);
    }

    public static PlantStats readFromNBT(NBTTagCompound tag) {
        PlantStats stats = new PlantStats();
        stats.setGrowth(tag.getShort(Names.NBT.growth));
        stats.setGain(tag.getShort(Names.NBT.gain));
        stats.setStrength(tag.getShort(Names.NBT.strength));
        return stats;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setShort(Names.NBT.growth, growth);
        tag.setShort(Names.NBT.gain, gain);
        tag.setShort(Names.NBT.strength, strength);
        return tag;
    }
}

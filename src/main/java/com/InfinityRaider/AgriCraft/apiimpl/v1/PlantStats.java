package com.InfinityRaider.AgriCraft.apiimpl.v1;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.InfinityRaider.AgriCraft.api.v1.ISeedStats;
import com.InfinityRaider.AgriCraft.api.v1.ITrowel;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;

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

    public PlantStats copy() {
        return new PlantStats(getGrowth(), getGain(), getStrength());
    }

    public static PlantStats getStatsFromStack(ItemStack stack) {
        if(stack==null || stack.getItem()==null) {
            return null;
        }
        if(stack.getItem() instanceof ITrowel) {
            ((ITrowel) stack.getItem()).getStats(stack);
        }
        return readFromNBT(stack.getTagCompound());
    }

    public static PlantStats readFromNBT(NBTTagCompound tag) {
        if(tag !=null && tag.hasKey(Names.NBT.growth) && tag.hasKey(Names.NBT.gain) && tag.hasKey(Names.NBT.strength)) {
            PlantStats stats = new PlantStats();
            stats.setGrowth(tag.getShort(Names.NBT.growth));
            stats.setGain(tag.getShort(Names.NBT.gain));
            stats.setStrength(tag.getShort(Names.NBT.strength));
            return stats;
        }
        return null;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setShort(Names.NBT.growth, growth);
        tag.setShort(Names.NBT.gain, gain);
        tag.setShort(Names.NBT.strength, strength);
        return tag;
    }
}

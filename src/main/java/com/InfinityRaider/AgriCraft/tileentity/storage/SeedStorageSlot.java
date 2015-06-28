package com.InfinityRaider.AgriCraft.tileentity.storage;

import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Comparator;

public class SeedStorageSlot {
    private NBTTagCompound tag;
    public int count;

    final int slotId;
    final int invId;

    public SeedStorageSlot(NBTTagCompound tag, int nr, int slotId, int invId) {
        this.tag = tag;
        this.count = nr;
        this.slotId = slotId;
        this.invId = invId;
    }

    public ItemStack getStack(Item item, int meta) {
        ItemStack stack = new ItemStack(item, count, meta);
        stack.stackTagCompound = (NBTTagCompound) tag.copy();
        return stack;
    }

    public NBTTagCompound getTag() {
        return (NBTTagCompound) this.tag.copy();
    }

    private int getTotalStat() {
        return tag.getInteger(Names.NBT.growth) + tag.getInteger(Names.NBT.gain) + tag.getInteger(Names.NBT.strength);
    }

    public int getId() {
        return invId>=0?(1000*invId) + slotId:slotId;
    }

    /** Compares 2 SeedStorageSlots by the given stat */
    public static class SlotComparator implements Comparator<SeedStorageSlot> {

        private final String stat;

        public SlotComparator(String stat) {
            this.stat = stat;
        }

        @Override
        public int compare(SeedStorageSlot o1, SeedStorageSlot o2) {
            int stat1 = o1.tag.getInteger(stat);
            int stat2 = o2.tag.getInteger(stat);
            if(stat2 == stat1) {
                return o2.getTotalStat() - o1.getTotalStat();
            }
            return stat2 - stat1;
        }
    }
}

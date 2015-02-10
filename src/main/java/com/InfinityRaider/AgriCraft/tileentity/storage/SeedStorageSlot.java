package com.InfinityRaider.AgriCraft.tileentity.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Comparator;

public class SeedStorageSlot {
    public NBTTagCompound tag;
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
        stack.stackTagCompound = tag;
        return stack;
    }

    public int getId() {
        return 1000*invId + slotId;
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
            return stat1 - stat2;
        }
    }
}

package com.infinityraider.agricraft.blocks.tiles.storage;

import java.util.Comparator;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.apiimpl.StatRegistry;

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
        stack.setTagCompound((NBTTagCompound) tag.copy());
        return stack;
    }

    public NBTTagCompound getTag() {
        return (NBTTagCompound) this.tag.copy();
    }

    private int getTotalStat() {
        IAgriStat stats = StatRegistry.getInstance().valueOf(this.tag).get();
        return stats.getGrowth() + stats.getGain() + stats.getStrength();
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

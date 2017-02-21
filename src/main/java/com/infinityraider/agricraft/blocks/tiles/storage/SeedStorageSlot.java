package com.infinityraider.agricraft.blocks.tiles.storage;

import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.apiimpl.SeedRegistry;
import com.infinityraider.agricraft.reference.AgriNBT;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SeedStorageSlot {

    private AgriSeed seed;
    public int count;
    private int slotId;
    private int invId;

    public SeedStorageSlot(AgriSeed seed, int count, int slotId, int invId) {
        this.seed = seed;
        this.count = count;
        this.slotId = slotId;
        this.invId = invId;
    }
    
    public ItemStack toStack() {
        return seed.toStack(count);
    }

    public AgriSeed getSeed() {
        return seed;
    }

    private int getTotalStat() {
        return this.seed.getStat().getGrowth() + this.seed.getStat().getGain() + this.seed.getStat().getStrength();
    }

    public int getId() {
        return invId >= 0 ? (1000 * invId) + slotId : slotId;
    }
    
    public void writeToNbt(NBTTagCompound tag) {
        this.seed.toStack().writeToNBT(tag);
        tag.setInteger(AgriNBT.COUNT, this.count);
        tag.setInteger(AgriNBT.ID, this.slotId);
    }
    
    public static final Optional<SeedStorageSlot> readFromNbt(NBTTagCompound tag, int invId) {
        Optional<AgriSeed> seed = SeedRegistry.getInstance().valueOf(ItemStack.loadItemStackFromNBT(tag));
        if (seed.isPresent()) {
            int id = tag.getInteger(AgriNBT.ID);
            int count = tag.getInteger(AgriNBT.COUNT);
            count = count < 0 ? 0 : count;
            return Optional.of(new SeedStorageSlot(seed.get(), count, id, invId));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Compares 2 SeedStorageSlots by the given stat
     */
    public static class SlotComparator implements Comparator<SeedStorageSlot> {

        private final String stat;

        public SlotComparator(String stat) {
            this.stat = stat;
        }

        @Override
        public int compare(SeedStorageSlot o1, SeedStorageSlot o2) {
            
            final IAgriStat s1 = o1.getSeed().getStat();
            final IAgriStat s2 = o2.getSeed().getStat();
            
            final int[] a1 = new int[]{ s1.getGain(), s1.getGrowth(), s1.getStrength() };
            final int[] a2 = new int[]{ s2.getGain(), s2.getGrowth(), s2.getStrength() };
            
            for (int i = 0; i < 3; i++) {
                if (a1[i] < a2[i]) {
                    return -1;
                } else if (a1[i] > a2[i]) {
                    return 1;
                }
            }
            
            return 0;
        }
    }
}

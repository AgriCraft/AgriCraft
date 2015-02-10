package com.InfinityRaider.AgriCraft.tileentity.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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
}

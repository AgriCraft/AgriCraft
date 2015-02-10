package com.InfinityRaider.AgriCraft.tileentity.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SeedStorageSlot {
    public NBTTagCompound tag;
    public int count;

    public final int slotId;
    public final int invId;

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
}

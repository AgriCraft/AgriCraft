package com.InfinityRaider.AgriCraft.tileentity.storage;

import net.minecraft.nbt.NBTTagCompound;

public class SeedStorageSlot {
    public int count;
    public NBTTagCompound tag;

    public SeedStorageSlot(NBTTagCompound tag, int count) {
        this.tag = (NBTTagCompound) tag.copy();
        this.count = count;
    }

}

package com.InfinityRaider.AgriCraft.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.InfinityRaider.AgriCraft.tileentity.storage.SeedStorageSlot;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;

public class ContainerSeedStorage extends ContainerSeedStorageBase {
    private static final int invOffsetX = 6;
    private static final int invOffsetY = 49;
    private TileEntitySeedStorage te;

    public ContainerSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(inventory, invOffsetX, invOffsetY);
        this.te = te;
    }

    public boolean addSeedToStorage(ItemStack seedStack) {
        return this.te.addStackToInventory(seedStack);
    }

    @Override
    public List<ItemStack> getSeedEntries() {
        ArrayList<ItemStack> list = this.te.getInventory();
        return list;
    }

    @Override
    public List<SeedStorageSlot> getSeedSlots(Item seed, int meta) {
        return this.te.getSlots(seed, meta);
    }

    @Override
    public TileEntity getTileEntity() {
        return this.te;
    }
}
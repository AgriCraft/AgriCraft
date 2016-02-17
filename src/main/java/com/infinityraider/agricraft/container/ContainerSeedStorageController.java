package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.tileentity.storage.SeedStorageSlot;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorageController;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

public class ContainerSeedStorageController extends ContainerSeedStorageBase {
    public TileEntitySeedStorageController te;
    private static final int invOffsetX = 82;
    private static final int invOffsetY = 94;

    public ContainerSeedStorageController(InventoryPlayer inventory, TileEntitySeedStorageController te) {
        super(inventory, invOffsetX, invOffsetY);
        this.te = te;
    }

    @Override
    public boolean addSeedToStorage(ItemStack stack) {
        return this.te.addStackToInventory(stack);
    }

    @Override
    public List<ItemStack> getSeedEntries() {
        return this.te.getControlledSeeds();
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

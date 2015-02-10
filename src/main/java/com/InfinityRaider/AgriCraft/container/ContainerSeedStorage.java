package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.tileentity.storage.SeedStorageSlot;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ContainerSeedStorage extends ContainerSeedStorageDummy {
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
}
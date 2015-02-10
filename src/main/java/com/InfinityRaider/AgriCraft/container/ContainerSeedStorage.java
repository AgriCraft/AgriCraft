package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.tileentity.storage.SeedStorageSlot;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ContainerSeedStorage extends ContainerSeedStorageDummy {
    private static final int invOffsetX = 6;
    private static final int invOffsetY = 49;
    private static final int maxVertSlots = 0;
    private static final int maxHorSlots = 14;

    private TileEntitySeedStorage te;

    public ContainerSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(inventory, invOffsetX, invOffsetY, maxVertSlots, maxHorSlots);
        //super(inventory, invOffsetX, invOffsetY, maxVertSlots, maxHorSlots);
        this.te = te;
        //add the first #maxHorSlots to the gui
        for(int i=0;i<Math.min(maxHorSlots, te.getSizeInventory()-1);i++) {
            this.addSlotToContainer(new Slot(te, PLAYER_INVENTORY_SIZE+i, invOffsetX+16*i, invOffsetY));
        }
    }

    public boolean addSeedToStorage(ItemStack seedStack) {
        boolean success = false;

        return success;
    }

    @Override
    public List<ItemStack> getSeedEntries() {
        return null;
    }

    @Override
    public List<SeedStorageSlot> getSeedSlots(ItemSeeds seed, int meta) {
        return this.te.getSlots(seed, meta);
    }
}
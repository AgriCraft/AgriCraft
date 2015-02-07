package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.gui.GuiSeedStorage;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class ContainerSeedStorage extends ContainerSeedStorageDummy {
    private TileEntitySeedStorage te;

    public ContainerSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(inventory,  6, 49);
        this.te = te;
    }

    @Override
    public void setActiveEntries(ItemStack stack, int offset) {
        int xOffset = 82;
        int yOffset = 8;
        int stopIndex = Math.min(te.getInventorySlots(this).size(), offset + GuiSeedStorage.maxNrHorizontalSeeds);
        ArrayList<SlotSeedStorage> activeEntries = te.getInventorySlots(this);
        for (int i = offset; i < stopIndex; i++) {
            SlotSeedStorage slot = activeEntries.get(i);
            slot.set(xOffset + 16 * i, yOffset, this.PLAYER_INVENTORY_SIZE + i);
            this.inventorySlots.add(slot);
            this.inventoryItemStacks.add(slot.getStack());
        }
    }
}

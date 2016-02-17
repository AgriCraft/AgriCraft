package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.items.ItemJournal;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotSeedAnalyzerJournal extends Slot {
    public SlotSeedAnalyzerJournal(IInventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack != null && stack.stackSize > 0 && stack.getItem() != null && stack.getItem() instanceof ItemJournal;
}
}

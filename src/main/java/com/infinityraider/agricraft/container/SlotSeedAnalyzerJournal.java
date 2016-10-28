package com.infinityraider.agricraft.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.infinityraider.agricraft.items.ItemJournal;
import com.infinityraider.agricraft.utility.StackHelper;

public class SlotSeedAnalyzerJournal extends Slot {

    public SlotSeedAnalyzerJournal(IInventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return StackHelper.isValid(stack, ItemJournal.class);
    }
}

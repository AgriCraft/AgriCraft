package com.infinityraider.agricraft.container;

import com.infinityraider.agricraft.items.ItemJournal;
import com.infinityraider.agricraft.utility.StackHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotSeedAnalyzerJournal extends Slot {

    public SlotSeedAnalyzerJournal(IInventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return StackHelper.isValid(stack, ItemJournal.class);
    }
}

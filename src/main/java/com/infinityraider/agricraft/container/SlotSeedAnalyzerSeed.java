package com.infinityraider.agricraft.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.infinityraider.agricraft.blocks.tiles.analyzer.TileEntitySeedAnalyzer;

public class SlotSeedAnalyzerSeed extends Slot{
    public SlotSeedAnalyzerSeed(IInventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return TileEntitySeedAnalyzer.isValid(stack);
    }
}

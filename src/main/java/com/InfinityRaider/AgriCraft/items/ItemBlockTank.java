package com.InfinityRaider.AgriCraft.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockTank extends ItemBlock {
    public ItemBlockTank(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName()+"";
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }
}

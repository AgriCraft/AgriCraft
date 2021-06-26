package com.infinityraider.agricraft.content.core;

import com.infinityraider.infinitylib.block.BlockDynamicTexture;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;

public abstract class ItemCustomWood extends BlockItemDynamicTexture {
    private static final ItemStack DEFAULT = new ItemStack(Blocks.OAK_PLANKS);

    protected ItemCustomWood(BlockDynamicTexture<?> block, Properties properties) {
        super(block, properties);
    }

    @Override
    public ItemStack getDefaultMaterial() {
        return DEFAULT;
    }
}

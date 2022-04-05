package com.infinityraider.agricraft.content.core;

import com.infinityraider.infinitylib.block.BlockDynamicTexture;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

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

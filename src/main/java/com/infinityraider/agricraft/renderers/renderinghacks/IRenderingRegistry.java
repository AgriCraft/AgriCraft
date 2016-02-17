package com.infinityraider.agricraft.renderers.renderinghacks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IRenderingRegistry {
    void registerBlockRenderingHandler(Block block, ISimpleBlockRenderingHandler renderer);

    void registerItemRenderingHandler(Item item, IItemRenderer renderer);

    ISimpleBlockRenderingHandler getRenderingHandler(Block block);

    IItemRenderer getItemRenderer(Item item);

    boolean hasRenderingHandler(Block block);

    boolean hasRenderingHandler(Item item);
}

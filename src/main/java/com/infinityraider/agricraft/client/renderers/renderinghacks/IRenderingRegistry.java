package com.infinityraider.agricraft.client.renderers.renderinghacks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IRenderingRegistry {

	void registerBlockRenderingHandler(Block block, IRenderingHandler renderer);

	void registerItemRenderingHandler(Item item, IRenderingHandler renderer);

	IRenderingHandler getRenderingHandler(Block block);

	IRenderingHandler getItemRenderer(Item item);

	boolean hasRenderingHandler(Block block);

	boolean hasRenderingHandler(Item item);

}

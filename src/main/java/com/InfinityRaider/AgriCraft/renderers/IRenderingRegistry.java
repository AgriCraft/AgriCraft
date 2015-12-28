package com.InfinityRaider.AgriCraft.renderers;

import net.minecraft.block.Block;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IRenderingRegistry {
    void registerRenderingHandler(Block block, ISimpleBlockRenderingHandler renderer);

    ISimpleBlockRenderingHandler getRenderingHandler(Block block);

    boolean hasRenderingHandler(Block block);
}

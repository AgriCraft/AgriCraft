package com.InfinityRaider.AgriCraft.api.v1;

import net.minecraft.block.Block;

public interface IBlockWithMeta {

	public abstract Block getBlock();

	public abstract int getMeta();

	public abstract boolean ignoreMeta();

}
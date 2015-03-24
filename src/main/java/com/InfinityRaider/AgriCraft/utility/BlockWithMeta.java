package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.api.v1.IBlockWithMeta;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 * Simple utility class combining a Block with a meta value.
 */
public class BlockWithMeta implements IBlockWithMeta {

    private final Block block;
    private final int meta;
    private final boolean ignoreMeta;

    public BlockWithMeta(Block block) {
        this(block, 0, true);
    }

    public BlockWithMeta(Block block, int meta) {
        this(block, meta, false);
    }

    public BlockWithMeta(Block block, int meta, boolean fuzzy) {
        this.block = block;
        this.meta = meta;
        this.ignoreMeta = fuzzy;
    }

    /* (non-Javadoc)
	 * @see com.InfinityRaider.AgriCraft.utility.IBlockWithMeta#getBlock()
	 */
    @Override
	public Block getBlock() {
        return block;
    }

    /* (non-Javadoc)
	 * @see com.InfinityRaider.AgriCraft.utility.IBlockWithMeta#getMeta()
	 */
    @Override
	public int getMeta() {
        return meta;
    }

    /* (non-Javadoc)
	 * @see com.InfinityRaider.AgriCraft.utility.IBlockWithMeta#ignoreMeta()
	 */
    @Override
	public boolean ignoreMeta() {
        return ignoreMeta;
    }

    public ItemStack toStack() {
        return new ItemStack(this.block, 1, this.meta);
    }

    @Override
    public String toString() {
        return Block.blockRegistry.getNameForObject(this.block)+":"+this.meta;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof BlockWithMeta) {
            BlockWithMeta block = (BlockWithMeta) obj;
            if(this.ignoreMeta || block.ignoreMeta) {
                return block.block == this.block;
            }
            else {
                return block.block == this.block && block.meta == this.meta;
            }
        }
        return false;
    }
}

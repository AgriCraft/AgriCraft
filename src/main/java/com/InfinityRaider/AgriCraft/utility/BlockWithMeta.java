package com.InfinityRaider.AgriCraft.utility;

import com.google.common.hash.Hashing;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 * Simple utility class combining a Block with a meta value.
 */
public class BlockWithMeta {

    private final Block block;
    private final int meta;

    public BlockWithMeta(Block block) {
        this(block, 0);
    }

    public BlockWithMeta(Block block, int meta) {
        this.block = block;
        this.meta = meta;
    }

    public Block getBlock() {
        return block;
    }

    public int getMeta() {
        return meta;
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
        if (obj == null) return false;
        if (obj instanceof BlockWithMeta) {
            BlockWithMeta block = (BlockWithMeta) obj;
            return block.block == this.block && block.meta == this.meta;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Hashing.md5().newHasher()
                .putInt(block.hashCode())
                .putInt(meta).hash().asInt();
    }
}

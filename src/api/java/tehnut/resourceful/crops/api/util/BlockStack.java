package tehnut.resourceful.crops.api.util;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * A set {@link Block} and meta to check against.
 */
public class BlockStack {

    private Block block;
    private int meta = 0;

    public BlockStack(@Nullable Block block, int meta) {
        this.block = block;
        this.meta = meta;
    }

    public BlockStack(Block block) {
        this(block, 0);
    }

    public Block getBlock() {
        return block;
    }

    public int getMeta() {
        return meta;
    }

    public String getDisplayName() {
        return toItemStack().getDisplayName();
    }

    public ItemStack toItemStack() {
        return toItemStack(1);
    }

    public ItemStack toItemStack(int amount) {
        return new ItemStack(getBlock(), amount, getMeta());
    }

    @Override
    public String toString() {
        return "BlockStack{" +
                "block=" + GameData.getBlockRegistry().getNameForObject(block) +
                ", meta=" + meta +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        BlockStack that = (BlockStack) o;

        if (meta != that.meta)
            return false;

        return !(block != null ? !block.equals(that.block) : that.block != null);
    }
}

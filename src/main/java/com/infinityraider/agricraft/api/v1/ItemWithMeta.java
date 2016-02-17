package com.InfinityRaider.AgriCraft.api.v1;


import com.google.common.hash.Hashing;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemWithMeta {

    private final Item item;
    private final int meta;

    public ItemWithMeta(Item item, int meta) {
        this.item = item;
        this.meta = meta;
    }

    public Item getItem() {
        return item;
    }

    public int getMeta() {
        return meta;
    }

    public ItemStack toStack() {
        return new ItemStack(item, 1, meta);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof ItemWithMeta) {
            ItemWithMeta item = (ItemWithMeta) obj;
            return item.item == this.item && item.meta == this.meta;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Hashing.md5().newHasher()
                .putInt(item.hashCode())
                .putInt(meta).hash().asInt();
    }
}

package com.infinityraider.agricraft.api.v1.content.items;

import net.minecraft.item.Item;

/**
 * Marker interface for Items that act as clippers.
 */
public interface IAgriClipperItem {
    /**
     * @return this, but cast to Item
     */
    default Item asItem() {
        return (Item) this;
    }
}

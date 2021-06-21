package com.infinityraider.agricraft.api.v1.content.items;

import net.minecraft.item.Item;

/**
 * Marker interface for objects that may be used as a rake.
 */
public interface IAgriRakeItem {
    /**
     * @return this, but cast to Item
     */
    default Item asItem() {
        return (Item) this;
    }
}

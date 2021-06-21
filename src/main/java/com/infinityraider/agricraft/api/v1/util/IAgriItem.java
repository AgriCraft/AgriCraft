package com.infinityraider.agricraft.api.v1.util;

import net.minecraft.item.Item;

/**
 * Interface to easily cast AgriCraft Item interfaces to Items
 */
public interface IAgriItem {
    /**
     * @return this, but cast to Item
     */
    default Item toItem() {
        return (Item) this;
    }
}

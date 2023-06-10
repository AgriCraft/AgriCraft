package com.agricraft.agricraft.api.util;

import net.minecraft.world.item.Item;

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

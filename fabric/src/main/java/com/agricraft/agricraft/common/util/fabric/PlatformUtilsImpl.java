package com.agricraft.agricraft.common.util.fabric;

import com.agricraft.agricraft.common.item.AgriSeedItem;
import net.minecraft.world.item.Item;

public class PlatformUtilsImpl {

	public static AgriSeedItem createAgriSeedItem(Item.Properties properties) {
		return new AgriSeedItem(properties);
	}

}

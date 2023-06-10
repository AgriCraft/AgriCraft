package com.agricraft.agricraft.common.util;

import com.agricraft.agricraft.common.item.AgriSeedItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.NotImplementedException;

public class PlatformUtils {

	@ExpectPlatform
	public static AgriSeedItem createAgriSeedItem(Item.Properties properties) {
		throw new NotImplementedException();
	}

}

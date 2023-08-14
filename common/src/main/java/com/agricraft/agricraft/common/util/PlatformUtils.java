package com.agricraft.agricraft.common.util;

import com.agricraft.agricraft.common.codecs.AgriSeed;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.NotImplementedException;

public class PlatformUtils {

	@ExpectPlatform
	public static AgriSeedItem createAgriSeedItem(Item.Properties properties) {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static ResourceKey<Registry<AgriSeed>> getSeedRegistryKey() {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static CreativeModeTab createMainCreativeTab() {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static CreativeModeTab createSeedsCreativeTab() {
		throw new NotImplementedException();
	}

}

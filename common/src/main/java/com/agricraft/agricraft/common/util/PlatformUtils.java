package com.agricraft.agricraft.common.util;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriSeed;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

public class PlatformUtils {
	public static final ResourceKey<Registry<AgriSeed>> AGRISEEDS = ResourceKey.createRegistryKey(new ResourceLocation(AgriCraft.MOD_ID, "seed"));
	public static final ResourceKey<Registry<AgriPlant>> AGRIPLANTS = ResourceKey.createRegistryKey(new ResourceLocation(AgriCraft.MOD_ID, "plants"));

	@ExpectPlatform
	public static AgriSeedItem createAgriSeedItem(Item.Properties properties) {
		throw new NotImplementedException();
	}

	public static ResourceKey<Registry<AgriSeed>> getSeedRegistryKey() {
		return AGRISEEDS;
	}

	public static ResourceKey<Registry<AgriPlant>> getPlantRegistryKey() {
		return AGRIPLANTS;
	}

	@ExpectPlatform
	public static CreativeModeTab createMainCreativeTab() {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static CreativeModeTab createSeedsCreativeTab() {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static String getIdFromPlant(AgriPlant plant) {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static AgriPlant getPlantFromId(String id) {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static List<Item> getItemsFromTag(ResourceLocation tag) {
		throw new NotImplementedException();
	}

}

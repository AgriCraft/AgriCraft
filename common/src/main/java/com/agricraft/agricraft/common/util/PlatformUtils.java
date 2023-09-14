package com.agricraft.agricraft.common.util;

import com.agricraft.agricraft.common.item.AgriSeedItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;
import java.util.Optional;

public class PlatformUtils {

	@ExpectPlatform
	public static AgriSeedItem createAgriSeedItem(Item.Properties properties) {
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

	@ExpectPlatform
	public static <T> Optional<Registry<T>> getRegistry(ResourceKey<Registry<T>> resourceKey) {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static List<Item> getItemsFromTag(ResourceLocation tag) {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static List<Block> getBlocksFromLocation(ExtraCodecs.TagOrElementLocation tag) {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static List<Fluid> getFluidsFromLocation(ExtraCodecs.TagOrElementLocation tag) {
		throw new NotImplementedException();
	}



}

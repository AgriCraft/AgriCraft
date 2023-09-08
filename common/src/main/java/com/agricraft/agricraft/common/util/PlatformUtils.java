package com.agricraft.agricraft.common.util;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriSeed;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;
import java.util.Optional;

public class PlatformUtils {
	public static final ResourceKey<Registry<AgriPlant>> AGRIPLANTS = ResourceKey.createRegistryKey(new ResourceLocation(AgriCraft.MOD_ID, "plants"));
	public static final ResourceKey<Registry<AgriSoil>> AGRISOILS = ResourceKey.createRegistryKey(new ResourceLocation(AgriCraft.MOD_ID, "soils"));

	@ExpectPlatform
	public static AgriSeedItem createAgriSeedItem(Item.Properties properties) {
		throw new NotImplementedException();
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
	public static String getIdFromSoil(AgriSoil soil) {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static Optional<AgriSoil> getSoilFromBlock(BlockState blockState) {
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

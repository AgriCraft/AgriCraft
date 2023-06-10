package com.agricraft.agricraft.common.util.forge;

import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.item.forge.ForgeAgriSeedItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class PlatformUtilsImpl {

	public static AgriSeedItem createAgriSeedItem(Item.Properties properties) {
		return new ForgeAgriSeedItem(properties);
	}

}

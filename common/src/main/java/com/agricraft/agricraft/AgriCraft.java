package com.agricraft.agricraft;

import com.agricraft.agricraft.common.registry.ModCreativeTabs;
import com.agricraft.agricraft.common.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.function.Consumer;

public class AgriCraft {

	public static final String MOD_ID = "agricraft";
	public static final Logger LOGGER = LogUtils.getLogger();

//	private static Consumer<NonNullList<ItemStack>> populateSeeds;

	public static void init() {
		ModItems.ITEMS.init();
		ModCreativeTabs.CREATIVE_MODE_TAB.init();
		LOGGER.info("Intializing API for " + MOD_ID);
	}

//	public static void setPopulateSeedsConsumer(Consumer<NonNullList<ItemStack>> consumer) {
//		if (populateSeeds != null) {
//			throw new UnsupportedOperationException("Can only set the populateSeeds consumer once");
//		}
//		populateSeeds = consumer;
//	}
//
//	public static void populateSeeds(NonNullList<ItemStack> items) {
//		if (populateSeeds != null) {
//			populateSeeds.accept(items);
//		}
//	}

}

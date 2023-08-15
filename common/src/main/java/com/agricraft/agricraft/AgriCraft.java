package com.agricraft.agricraft;

import com.agricraft.agricraft.common.registry.ModBlockEntityTypes;
import com.agricraft.agricraft.common.registry.ModBlocks;
import com.agricraft.agricraft.common.registry.ModCreativeTabs;
import com.agricraft.agricraft.common.registry.ModItems;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class AgriCraft {

	public static final String MOD_ID = "agricraft";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static void init() {
		ModBlocks.BLOCKS.init();
		ModItems.ITEMS.init();
		ModBlockEntityTypes.BLOCK_ENTITY_TYPES.init();
		ModCreativeTabs.CREATIVE_MODE_TAB.init();
		LOGGER.info("Intializing API for " + MOD_ID);
	}

}

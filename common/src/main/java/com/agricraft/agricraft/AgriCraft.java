package com.agricraft.agricraft;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.config.AgriCraftConfig;
import com.agricraft.agricraft.common.registry.ModBlockEntityTypes;
import com.agricraft.agricraft.common.registry.ModBlocks;
import com.agricraft.agricraft.common.registry.ModCreativeTabs;
import com.agricraft.agricraft.common.registry.ModItems;
import com.agricraft.agricraft.common.registry.ModMenus;
import com.agricraft.agricraft.common.registry.ModRecipeSerializers;
import com.mojang.logging.LogUtils;
import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import org.slf4j.Logger;

public class AgriCraft {

	public static final Logger LOGGER = LogUtils.getLogger();
	public static final Configurator CONFIGURATOR = new Configurator(AgriApi.MOD_ID);

	public static void init() {
		CONFIGURATOR.register(AgriCraftConfig.class);

		ModBlocks.BLOCKS.init();
		ModItems.ITEMS.init();
		ModBlockEntityTypes.BLOCK_ENTITY_TYPES.init();
		ModCreativeTabs.CREATIVE_MODE_TAB.init();
		ModMenus.MENUS.init();
		ModRecipeSerializers.RECIPE_SERIALIZERS.init();
		LOGGER.info("Intializing API for " + AgriApi.MOD_ID);
	}

}

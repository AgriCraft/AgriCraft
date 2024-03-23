package com.agricraft.agricraft.common.config.fabric;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.config.AgriCraftConfig;
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig;
import com.teamresourceful.resourcefulconfig.client.ConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuConfig implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			ResourcefulConfig config = AgriCraft.CONFIGURATOR.getConfig(AgriCraftConfig.class);
			if (config == null) {
				return null;
			}
			return new ConfigScreen(null, config);
		};
	}

}

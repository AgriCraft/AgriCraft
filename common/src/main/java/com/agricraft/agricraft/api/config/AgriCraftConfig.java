package com.agricraft.agricraft.api.config;

import com.agricraft.agricraft.api.AgriApi;
import com.teamresourceful.resourcefulconfig.api.annotations.Config;

@Config(
		value = "agricraft",
		categories = {
				CoreConfig.class,
				StatsConfig.class
		})
public final class AgriCraftConfig {

}

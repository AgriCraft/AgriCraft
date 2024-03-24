package com.agricraft.agricraft.api.config;

import com.teamresourceful.resourcefulconfig.api.annotations.Config;

@Config(
		value = "agricraft",
		categories = {
				CoreConfig.class,
				StatsConfig.class
		})
public final class AgriCraftConfig {

}

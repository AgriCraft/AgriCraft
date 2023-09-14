package com.agricraft.agricraft.common.config;

import com.agricraft.agricraft.api.AgriApi;
import com.teamresourceful.resourcefulconfig.common.annotations.Config;
import com.teamresourceful.resourcefulconfig.common.annotations.InlineCategory;

@Config(AgriApi.MOD_ID)
public final class AgriCraftConfig {

	@InlineCategory
	public static CoreConfig coreConfig;

	@InlineCategory
	public static StatsConfig statsConfig;

}

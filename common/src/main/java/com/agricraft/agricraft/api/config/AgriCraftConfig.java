package com.agricraft.agricraft.api.config;

import com.teamresourceful.resourcefulconfig.common.annotations.Config;
import com.teamresourceful.resourcefulconfig.common.annotations.InlineCategory;

@Config("agricraft")
public final class AgriCraftConfig {

	@InlineCategory
	public static CoreConfig core;

	@InlineCategory
	public static StatsConfig stats;

}

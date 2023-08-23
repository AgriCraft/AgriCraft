package com.agricraft.agricraft.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.Category;
import com.teamresourceful.resourcefulconfig.common.annotations.Comment;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.annotations.DoubleRange;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;

@Category(id = "core_config", translation = "agricraft.option.core_config")
public final class CoreConfig {

	@ConfigEntry(id = "growth_multiplier", type = EntryType.DOUBLE, translation = "agricraft.option.core_config.growth_multiplier")
	@DoubleRange(min = 0.0, max = 3.0)
	@Comment("Global growth rate multiplier for crops planted on crop sticks.")
	public static double growthMultiplier = 1.0;

	@ConfigEntry(id = "only_mature_seed_drops", type = EntryType.BOOLEAN, translation = "agricraft.option.core_config.only_mature_seed_drops")
	@Comment("Set this to true to make only mature crops drop seeds (to encourage trowel usage).")
	public static boolean onlyMatureSeedDrops = false;

}

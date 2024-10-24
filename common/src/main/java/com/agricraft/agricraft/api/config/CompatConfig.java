package com.agricraft.agricraft.api.config;

import com.teamresourceful.resourcefulconfig.common.annotations.Category;
import com.teamresourceful.resourcefulconfig.common.annotations.Comment;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.annotations.DoubleRange;
import com.teamresourceful.resourcefulconfig.common.annotations.FloatRange;
import com.teamresourceful.resourcefulconfig.common.annotations.IntRange;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;

/**
 * Agricraft mode compatibility configuration category.
 */
@Category(id = "compat", translation = "config.agricraft.compat")
public final class CompatConfig {

	@ConfigEntry(id = "botania", type = EntryType.BOOLEAN, translation = "config.agricraft.compat.botania")
	@Comment("Set to false to disable compatibility with Botania (in case things break)")
	public static boolean enableBotania = true;


	@ConfigEntry(id = "mysticalagriculture", type = EntryType.BOOLEAN, translation = "config.agricraft.compat.mysticalagriculture")
	@Comment("Set to false to disable compatibility with Mystical Agriculture (in case things break)")
	public static boolean enableMysticalAgriculture = true;


}

package com.agricraft.agricraft.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.Category;
import com.teamresourceful.resourcefulconfig.common.annotations.Comment;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.annotations.IntRange;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;

/**
 * Agricraft configuration category related to statistics.
 */
@Category(id = "stats_config", translation = "agricraft.option.stats_config")
public final class StatsConfig {

	@ConfigEntry(id = "gain_min", type = EntryType.INTEGER, translation = "agricraft.option.stats_config.gain_min")
	@IntRange(min = 1, max = 10)
	@Comment("Minimum allowed value of the Gain stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int gainMin = 1;

	@ConfigEntry(id = "gain_max", type = EntryType.INTEGER, translation = "agricraft.option.stats_config.gain_max")
	@IntRange(min = 1, max = 10)
	@Comment("Maximum allowed value of the Gain stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int gainMax = 10;

	@ConfigEntry(id = "gain_hidden", type = EntryType.BOOLEAN, translation = "agricraft.option.stats_config.gain_hidden")
	@Comment("Set to true to hide the Gain stat (hidden stats will not show up in tooltips or seed analysis). Setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
	public static boolean gainHidden = false;

	@ConfigEntry(id = "growth_min", type = EntryType.INTEGER, translation = "agricraft.option.stats_config.growth_min")
	@IntRange(min = 1, max = 10)
	@Comment("Minimum allowed value of the Growth stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int growthMin = 1;

	@ConfigEntry(id = "growth_max", type = EntryType.INTEGER, translation = "agricraft.option.stats_config.growth_max")
	@IntRange(min = 1, max = 10)
	@Comment("Maximum allowed value of the Growth stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int growthMax = 10;

	@ConfigEntry(id = "growth_hidden", type = EntryType.BOOLEAN, translation = "agricraft.option.stats_config.growth_hidden")
	@Comment("Set to true to hide the Growth stat (hidden stats will not show up in tooltips or seed analysis). Setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
	public static boolean growthHidden = false;

	@ConfigEntry(id = "strength_min", type = EntryType.INTEGER, translation = "agricraft.option.stats_config.strength_min")
	@IntRange(min = 1, max = 10)
	@Comment("Minimum allowed value of the Strength stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int strengthMin = 1;

	@ConfigEntry(id = "strength_max", type = EntryType.INTEGER, translation = "agricraft.option.stats_config.strength_max")
	@IntRange(min = 1, max = 10)
	@Comment("Maximum allowed value of the Strength stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int strengthMax = 10;

	@ConfigEntry(id = "strength_hidden", type = EntryType.BOOLEAN, translation = "agricraft.option.stats_config.strength_hidden")
	@Comment("Set to true to hide the Strength stat (hidden stats will not show up in tooltips or seed analysis). Setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
	public static boolean strengthHidden = false;

	@ConfigEntry(id = "resistance_min", type = EntryType.INTEGER, translation = "agricraft.option.stats_config.resistance_min")
	@IntRange(min = 1, max = 10)
	@Comment("Minimum allowed value of the Resistance stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int resistanceMin = 1;

	@ConfigEntry(id = "resistance_max", type = EntryType.INTEGER, translation = "agricraft.option.stats_config.resistance_max")
	@IntRange(min = 1, max = 10)
	@Comment("Maximum allowed value of the Resistance stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int resistanceMax = 10;

	@ConfigEntry(id = "resistance_hidden", type = EntryType.BOOLEAN, translation = "agricraft.option.stats_config.resistance_hidden")
	@Comment("Set to true to hide the Resistance stat (hidden stats will not show up in tooltips or seed analysis). Setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
	public static boolean resistanceHidden = false;

	@ConfigEntry(id = "fertility_min", type = EntryType.INTEGER, translation = "agricraft.option.stats_config.fertility_min")
	@IntRange(min = 1, max = 10)
	@Comment("Minimum allowed value of the Fertility stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int fertilityMin = 1;

	@ConfigEntry(id = "fertility_max", type = EntryType.INTEGER, translation = "agricraft.option.stats_config.fertility_max")
	@IntRange(min = 1, max = 10)
	@Comment("Maximum allowed value of the Fertility stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int fertilityMax = 10;

	@ConfigEntry(id = "fertility_hidden", type = EntryType.BOOLEAN, translation = "agricraft.option.stats_config.fertility_hidden")
	@Comment("Set to true to hide the Fertility stat (hidden stats will not show up in tooltips or seed analysis). Setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
	public static boolean fertilityHidden = false;

	@ConfigEntry(id = "mutativity_min", type = EntryType.INTEGER, translation = "agricraft.option.stats_config.mutativity_min")
	@IntRange(min = 1, max = 10)
	@Comment("Minimum allowed value of the Mutativity stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int mutativityMin = 1;

	@ConfigEntry(id = "mutativity_max", type = EntryType.INTEGER, translation = "agricraft.option.stats_config.mutativity_max")
	@IntRange(min = 1, max = 10)
	@Comment("Maximum allowed value of the Mutativity stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int mutativityMax = 10;

	@ConfigEntry(id = "mutativity_hidden", type = EntryType.BOOLEAN, translation = "agricraft.option.stats_config.mutativity_hidden")
	@Comment("Set to true to hide the Mutativity stat (hidden stats will not show up in tooltips or seed analysis). Setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
	public static boolean mutativityHidden = false;

}

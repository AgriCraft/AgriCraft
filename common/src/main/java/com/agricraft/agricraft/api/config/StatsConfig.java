package com.agricraft.agricraft.api.config;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.Comment;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType;

/**
 * Agricraft configuration category related to statistics.
 */
@Category("config.agricraft.stats")
public final class StatsConfig {

	@ConfigEntry(id = "gain_min", type = EntryType.INTEGER, translation = "config.agricraft.stats.gain_min")
	@ConfigOption.Range(min = 1, max = 10)
	@Comment("Minimum allowed value of the Gain stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int gainMin = 1;

	@ConfigEntry(id = "gain_max", type = EntryType.INTEGER, translation = "config.agricraft.stats.gain_max")
	@ConfigOption.Range(min = 1, max = 10)
	@Comment("Maximum allowed value of the Gain stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int gainMax = 10;

	@ConfigEntry(id = "gain_hidden", type = EntryType.BOOLEAN, translation = "config.agricraft.stats.gain_hidden")
	@Comment("Set to true to hide the Gain stat (hidden stats will not show up in tooltips or seed analysis). Setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
	public static boolean gainHidden = false;

	@ConfigEntry(id = "growth_min", type = EntryType.INTEGER, translation = "config.agricraft.stats.growth_min")
	@ConfigOption.Range(min = 1, max = 10)
	@Comment("Minimum allowed value of the Growth stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int growthMin = 1;

	@ConfigEntry(id = "growth_max", type = EntryType.INTEGER, translation = "config.agricraft.stats.growth_max")
	@ConfigOption.Range(min = 1, max = 10)
	@Comment("Maximum allowed value of the Growth stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int growthMax = 10;

	@ConfigEntry(id = "growth_hidden", type = EntryType.BOOLEAN, translation = "config.agricraft.stats.growth_hidden")
	@Comment("Set to true to hide the Growth stat (hidden stats will not show up in tooltips or seed analysis). Setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
	public static boolean growthHidden = false;

	@ConfigEntry(id = "strength_min", type = EntryType.INTEGER, translation = "config.agricraft.stats.strength_min")
	@ConfigOption.Range(min = 1, max = 10)
	@Comment("Minimum allowed value of the Strength stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int strengthMin = 1;

	@ConfigEntry(id = "strength_max", type = EntryType.INTEGER, translation = "config.agricraft.stats.strength_max")
	@ConfigOption.Range(min = 1, max = 10)
	@Comment("Maximum allowed value of the Strength stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int strengthMax = 10;

	@ConfigEntry(id = "strength_hidden", type = EntryType.BOOLEAN, translation = "config.agricraft.stats.strength_hidden")
	@Comment("Set to true to hide the Strength stat (hidden stats will not show up in tooltips or seed analysis). Setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
	public static boolean strengthHidden = false;

	@ConfigEntry(id = "resistance_min", type = EntryType.INTEGER, translation = "config.agricraft.stats.resistance_min")
	@ConfigOption.Range(min = 1, max = 10)
	@Comment("Minimum allowed value of the Resistance stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int resistanceMin = 1;

	@ConfigEntry(id = "resistance_max", type = EntryType.INTEGER, translation = "config.agricraft.stats.resistance_max")
	@ConfigOption.Range(min = 1, max = 10)
	@Comment("Maximum allowed value of the Resistance stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int resistanceMax = 10;

	@ConfigEntry(id = "resistance_hidden", type = EntryType.BOOLEAN, translation = "config.agricraft.stats.resistance_hidden")
	@Comment("Set to true to hide the Resistance stat (hidden stats will not show up in tooltips or seed analysis). Setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
	public static boolean resistanceHidden = false;

	@ConfigEntry(id = "fertility_min", type = EntryType.INTEGER, translation = "config.agricraft.stats.fertility_min")
	@ConfigOption.Range(min = 1, max = 10)
	@Comment("Minimum allowed value of the Fertility stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int fertilityMin = 1;

	@ConfigEntry(id = "fertility_max", type = EntryType.INTEGER, translation = "config.agricraft.stats.fertility_max")
	@ConfigOption.Range(min = 1, max = 10)
	@Comment("Maximum allowed value of the Fertility stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int fertilityMax = 10;

	@ConfigEntry(id = "fertility_hidden", type = EntryType.BOOLEAN, translation = "config.agricraft.stats.fertility_hidden")
	@Comment("Set to true to hide the Fertility stat (hidden stats will not show up in tooltips or seed analysis). Setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
	public static boolean fertilityHidden = false;

	@ConfigEntry(id = "mutativity_min", type = EntryType.INTEGER, translation = "config.agricraft.stats.mutativity_min")
	@ConfigOption.Range(min = 1, max = 10)
	@Comment("Minimum allowed value of the Mutativity stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int mutativityMin = 1;

	@ConfigEntry(id = "mutativity_max", type = EntryType.INTEGER, translation = "config.agricraft.stats.mutativity_max")
	@ConfigOption.Range(min = 1, max = 10)
	@Comment("Maximum allowed value of the Mutativity stat (setting min and max equal will freeze the stat to that value in crop breeding)")
	public static int mutativityMax = 10;

	@ConfigEntry(id = "mutativity_hidden", type = EntryType.BOOLEAN, translation = "config.agricraft.stats.mutativity_hidden")
	@Comment("Set to true to hide the Mutativity stat (hidden stats will not show up in tooltips or seed analysis). Setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
	public static boolean mutativityHidden = false;

}

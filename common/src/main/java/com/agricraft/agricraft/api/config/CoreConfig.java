package com.agricraft.agricraft.api.config;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.Comment;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType;

/**
 * Agricraft main configuration category.
 */
@Category("config.agricraft.core")
public final class CoreConfig {

	@ConfigEntry(id = "plant_off_crop_sticks", type = EntryType.BOOLEAN, translation = "config.agricraft.core.plant_off_crop_sticks")
	@Comment("Set to false to disable planting of (agricraft) seeds outside crop sticks")
	public static boolean plantOffCropSticks = true;

	@ConfigEntry(id = "crop_sticks_collide", type = EntryType.BOOLEAN, translation = "config.agricraft.core.crop_sticks_collide")
	@Comment("Set to false to disable collision boxes on crop sticks")
	public static boolean cropSticksCollide = true;

	@ConfigEntry(id = "only_fertile_crops_spread", type = EntryType.BOOLEAN, translation = "config.agricraft.core.only_fertile_crops_spread")
	@Comment("Set to true to allow only fertile plants to be able to cause, participate in, or contribute to a spreading / mutation action (note that this may cause issues with obtaining some specific plants)")
	public static boolean onlyFertileCropsSpread = false;

	@ConfigEntry(id = "allow_fertilizer_mutation", type = EntryType.BOOLEAN, translation = "config.agricraft.core.allow_fertilizer_mutation")
	@Comment("Set to false if to disable triggering of mutations by using fertilizers on a cross crop.")
	public static boolean allowFertilizerMutation = true;

	@ConfigEntry(id = "clone_mutations", type = EntryType.BOOLEAN, translation = "config.agricraft.core.clone_mutations")
	@Comment("Set to true to allow mutations on clone events (spreading from single crop).")
	public static boolean cloneMutations = false;

	@ConfigEntry(id = "override_vanilla_farming", type = EntryType.BOOLEAN, translation = "config.agricraft.core.override_vanilla_farming")
	@Comment("Set to true to override vanilla farming, meaning vanilla seeds will be converted to agricraft seeds on planting.")
	public static boolean overrideVanillaFarming = true;


	@ConfigEntry(id = "converts_seed_only_in_analyzer", type = EntryType.BOOLEAN, translation = "config.agricraft.core.converts_seed_only_in_analyzer")
	@Comment("Set to true to convert seeds only in the analyzer. Has no effect if \"Override vanilla farming\" is set to false.")
	public static boolean convertSeedsOnlyInAnalyzer = false;

	@ConfigEntry(id = "growth_multiplier", type = EntryType.DOUBLE, translation = "config.agricraft.core.growth_multiplier")
	@ConfigOption.Range(min = 0.0, max = 3.0)
	@Comment("Global growth rate multiplier for crops.")
	public static double growthMultiplier = 1.0;

	@ConfigEntry(id = "only_mature_seed_drops", type = EntryType.BOOLEAN, translation = "config.agricraft.core.only_mature_seed_drops")
	@Comment("Set this to true to make only mature crops drop seeds (to encourage trowel usage).")
	public static boolean onlyMatureSeedDrops = false;

	@ConfigEntry(id = "disable_weeds", type = EntryType.BOOLEAN, translation = "config.agricraft.core.disable_weeds")
	@Comment("Set to true to completely disable the spawning of weeds")
	public static boolean disableWeeds = false;

	@ConfigEntry(id = "mature_weeds_kill_plants", type = EntryType.BOOLEAN, translation = "config.agricraft.core.mature_weeds_kill_plants")
	@Comment("Set to false to disable mature weeds killing plants")
	public static boolean matureWeedsKillPlants = true;

	@ConfigEntry(id = "weeds_spreading", type = EntryType.BOOLEAN, translation = "config.agricraft.core.weeds_spreading")
	@Comment("Set to false to disable the spreading of weeds")
	public static boolean weedsSpreading = true;

	@ConfigEntry(id = "weeds_destroy_crop_sticks", type = EntryType.BOOLEAN, translation = "config.agricraft.core.weeds_destroy_crop_sticks")
	@Comment("Set this to true to have weeds destroy the crop sticks when they are broken with weeds (to encourage rake usage).")
	public static boolean weedsDestroyCropSticks = false;

	@ConfigEntry(id = "raking_drops_items", type = EntryType.BOOLEAN, translation = "config.agricraft.core.raking_drops_items")
	@Comment("Set to false if you wish to disable drops from raking weeds.")
	public static boolean rakingDropsItems = true;

	@ConfigEntry(id = "seed_compost_value", type = EntryType.FLOAT, translation = "config.agricraft.core.seed_compost_value")
	@ConfigOption.Range(min = 0F, max = 1F)
	@Comment("Defines the seed compost value, if set to zero, seeds will not be compostable")
	public static float seedCompostValue = 0.3F;

//	public static boolean animalAttraction = false;

	@ConfigEntry(id = "seed_bag_capactity", type = EntryType.INTEGER, translation = "config.agricraft.core.seed_bag_capactity")
	@ConfigOption.Range(min = 8, max = 256)
	@Comment("The amount of seeds one seed bag can hold.")
	public static int seedBagCapacity = 64;

	@ConfigEntry(id = "seed_bag_enchant_cost", type = EntryType.INTEGER, translation = "config.agricraft.core.seed_bag_enchant_cost")
	@ConfigOption.Range(min = 0, max = 30)
	@Comment("Enchantment cost in player levels to enchant the seed bag.")
	public static int seedBagEnchantCost = 10;

//	public static boolean allowGrassDropResets = false;

}

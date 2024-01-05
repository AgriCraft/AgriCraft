package com.agricraft.agricraft.api.config;

import com.teamresourceful.resourcefulconfig.common.annotations.Category;
import com.teamresourceful.resourcefulconfig.common.annotations.Comment;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.annotations.DoubleRange;
import com.teamresourceful.resourcefulconfig.common.annotations.FloatRange;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;

/**
 * Agricraft main configuration category.
 */
@Category(id = "core_config", translation = "agricraft.option.core_config")
public final class CoreConfig {

	@ConfigEntry(id = "plant_off_crop_sticks", type = EntryType.BOOLEAN, translation = "agricraft.option.core_config.plant_off_crop_sticks")
	@Comment("Set to false to disable planting of (agricraft) seeds outside crop sticks")
	public static boolean plantOffCropSticks = true;

	@ConfigEntry(id = "crop_sticks_collide", type = EntryType.BOOLEAN, translation = "agricraft.option.core_config.crop_sticks_collide")
	@Comment("Set to false to disable collision boxes on crop sticks")
	public static boolean cropSticksCollide = true;

	@ConfigEntry(id = "only_fertile_crops_spread", type = EntryType.BOOLEAN, translation = "agricraft.option.core_config.only_fertile_crops_spread")
	@Comment("Set to true to allow only fertile plants to be able to cause, participate in, or contribute to a spreading / mutation action (note that this may cause issues with obtaining some specific plants)")
	public static boolean onlyFertileCropsSpread = false;

//	public static boolean allowFertilizerMutation = true;

	@ConfigEntry(id = "clone_mutations", type = EntryType.BOOLEAN, translation = "agricraft.option.core_config.clone_mutations")
	@Comment("Set to true to allow mutations on clone events (spreading from single crop).")
	public static boolean cloneMutations = false;

	@ConfigEntry(id = "override_vanilla_farming", type = EntryType.BOOLEAN, translation = "agricraft.option.core_config.override_vanilla_farming")
	@Comment("Set to true to override vanilla farming, meaning vanilla seeds will be converted to agricraft seeds on planting.")
	public static boolean overrideVanillaFarming = true;


	@ConfigEntry(id = "converts_seed_only_in_analyzer", type = EntryType.BOOLEAN, translation = "agricraft.option.core_config.converts_seed_only_in_analyzer")
	@Comment("Set to true to convert seeds only in the analyzer. Has no effect if \"Override vanilla farming\" is set to false.")
	public static boolean convertSeedsOnlyInAnalyzer = false;

	@ConfigEntry(id = "growth_multiplier", type = EntryType.DOUBLE, translation = "agricraft.option.core_config.growth_multiplier")
	@DoubleRange(min = 0.0, max = 3.0)
	@Comment("Global growth rate multiplier for crops.")
	public static double growthMultiplier = 1.0;

	@ConfigEntry(id = "only_mature_seed_drops", type = EntryType.BOOLEAN, translation = "agricraft.option.core_config.only_mature_seed_drops")
	@Comment("Set this to true to make only mature crops drop seeds (to encourage trowel usage).")
	public static boolean onlyMatureSeedDrops = false;

//	public static boolean disableWeeds = false;
//	public static boolean weedsSpreading = false;
//	public static boolean weedsDestroyCropSticks = false;
//	public static boolean rakingDropsItems = false;

	@ConfigEntry(id = "seed_compost_value", type = EntryType.FLOAT, translation = "agricraft.option.core_config.seed_compost_value")
	@FloatRange(min = 0F, max = 1F)
	@Comment("Defines the seed compost value, if set to zero, seeds will not be compostable")
	public static float seedCompostValue = 0.3F;

//	public static boolean animalAttraction = false;
//	public static boolean seedBagEnchantCost = false;
//	public static boolean allowGrassDropResets = false;

}

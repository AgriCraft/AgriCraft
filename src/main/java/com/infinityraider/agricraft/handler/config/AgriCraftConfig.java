/*
 * AgriCraft Configuration File
 * 
 * This might not be *strictly* cleaner, but it is nicer.
 *
 */
package com.infinityraider.agricraft.handler.config;

import com.infinityraider.agricraft.reference.Constants;

/**
 * AgriCraft Configuration File.
 *
 * @author RlonRyan
 */
public class AgriCraftConfig {

	// Core
	@AgriCraftConfigurable(category = ConfigCategory.CORE, key = "Register crop products in the ore dict", comment = "Set to false to not register crop products to the ore dictionary if you are experiencing issues with GregTech (note that disabling this will have the side effect that seeds will no longer work with the Phytogenic Insulator).")
	public static boolean registerCropProductsToOreDict = true;
	@AgriCraftConfigurable(category = ConfigCategory.CORE, key = "Enable Resource Crops", comment = "Set to true if you wish to enable resource crops.")
	public static boolean resourcePlants = true;
	@AgriCraftConfigurable(category = ConfigCategory.CORE, key = "Clear Tall Grass Drops", comment = "Set to true to clear the list of items dropping from tall grass (Will run before adding seeds defined in the grass drops config).")
	public static boolean wipeTallGrassDrops = false;
	@AgriCraftConfigurable(category = ConfigCategory.CORE, key = "Render Book in Anaylzer", comment = "Set to false to not render the journal on the analyzer.")
	public static boolean renderBookInAnalyzer = true;
	@AgriCraftConfigurable(category = ConfigCategory.CORE, key = "Crops per Craft", min = "1", max = "4", comment = "The number of crops you get per crafting operation.")
	public static int cropsPerCraft = 4;
	@AgriCraftConfigurable(category = ConfigCategory.CORE, key = "Crop Stat Cap", min = "1", max = "10", comment = "The maximum attainable value of the stats on a crop.")
	public static int cropStatCap = 10;
	@AgriCraftConfigurable(category = ConfigCategory.CORE, key = "Enable Custom Crops", comment = "Set to true if you wish to create your own crops.")
	public static boolean customCrops = false;

	// Debug
	@AgriCraftConfigurable(category = ConfigCategory.DEBUG, key = "debug", comment = "Set to true to enable debug mode.")
	public static boolean debug = false;

	// Irrigation
	@AgriCraftConfigurable(category = ConfigCategory.IRRIGATION, key = "Disable Irrigation", comment = "Set to true if you want to disable irrigation systems.")
	public static boolean disableIrrigation = false;
	@AgriCraftConfigurable(category = ConfigCategory.IRRIGATION, key = "Spawn water after breaking tank", comment = "Set to false to disable placing a source block when breaking non-empty tanks.")
	public static boolean placeWater = false;
	@AgriCraftConfigurable(category = ConfigCategory.IRRIGATION, key = "Fill tank from flowing water", comment = "Set to true to let tanks fill up when water flows above them.")
	public static boolean fillFromFlowingWater = false;
	@AgriCraftConfigurable(category = ConfigCategory.IRRIGATION, key = "Channel Capacity", min = "100", max = "2000", comment = "The amount of water in mB that an irrigation channel can hold.")
	public static int channelCapacity = 500;

	// Sprinkler
	@AgriCraftConfigurable(category = ConfigCategory.IRRIGATION, key = "Sprinkler growth interval", min = "1", max = "300", comment = "Every x seconds each plant in sprinkler range has y chance to growth tick.")
	public static int sprinklerGrowthInterval = 5;
	@AgriCraftConfigurable(category = ConfigCategory.IRRIGATION, key = "Sprinkler growth chance", min = "0", max = "100", comment = "Every x seconds each plant in sprinkler range has this chance to growth tick")
	public static int sprinklerGrowthChance = 20;
	@AgriCraftConfigurable(category = ConfigCategory.IRRIGATION, key = "Sprinkler water usage", min = "0", max = "10000", comment = "Water usage of the sprinkler in mB per second")
	public static int sprinklerRatePerSecond = 10;
	public static int sprinklerRatePerHalfSecond = 5;
	public static float sprinklerGrowthChancePercent = 0.1f;
	public static int sprinklerGrowthIntervalTicks = 100;

	// Farming
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Bonemeal Mutations", comment = "Set to false if you wish to disable using bonemeal on a cross crop to force a mutation.")
	public static boolean bonemealMutation = false;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Disable Vanilla Farming", comment = "set to true to disable vanilla farming, meaning you can only grow plants on crops.")
	public static boolean disableVanillaFarming = false;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Hardcore stats", comment = "Set to true to enable hardcore mode for stat increasing: 1 parent: 3/4 decrement, 1/4 nothing.\n 2 parents: 2/4 decrement, 1/4 nothing, 1/4 increment.\n 3 parents: 1/4 decrement, 1/2 nothing, 1/4 increment.\n 4 parents: 1/4 decrement, 1/4 nothing, 1/2 increment.")
	public static boolean hardCoreStats = false;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Growth rate multiplier", min = "0", max = "2", comment = "This is a global growth rate multiplier.")
	public static float growthMultiplier = 1.0f;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Crop Stat Divisor", min = "1", max = "3", comment = "On a mutation the stats on the crop will be divided by this number.")
	public static int cropStatDivisor = 2;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Only mature crops drop seeds", comment = "Set this to true to make only mature crops drop seeds (to encourage trowel usage).")
	public static boolean onlyMatureDropSeeds = false;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Mod specific drops", comment = "Set to false to disable mod specific drops, this will (for instance) cause Natura berries to drop from Harvestcraft berry crops.")
	public static boolean modSpecificDrops = true;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Non parent crops affect stats negatively", comment = "True means any crop that is not considered a valid parent will affect stat gain negatively.")
	public static boolean otherCropsAffectStatsNegatively = true;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Mutation Chance", min = "0", max = "1", comment = "Define mutation chance (0.0 means no mutations, only spreading and 1.0 means only mutations no spreading.")
	public static float mutationChance = Constants.DEFAULT_MUTATION_CHANCE;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Valid parents", min = "1", max = "3", comment = "What are considered valid parents for stat increasing: 1 = Any. 2 = Mutation parents and identical crops. 3 = Only identical crops.")
	public static int validParents = 2;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Single spread stat increase", comment = "Set to true to allow crops that spread from one single crop to increase stats.")
	public static boolean singleSpreadsIncrement = false;

	// Weeds
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Enable weeds", comment = "Set to false if you wish to disable weeds")
	public static boolean enableWeeds = true;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Weeds Overtake Plants", comment = "Set to false if you don't want weeds to be able to overgrow other plants.")
	public static boolean weedsWipePlants = true;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Weeds destroy crop sticks", comment = "Set this to true to have weeds destroy the crop sticks when they are broken with weeds (to encourage rake usage).")
	public static boolean weedsDestroyCropSticks = false;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Weed Spawn Chance", min = "0.05", max = "0.95", comment = "The percent chance of weeds to spawn or spread. At 95% abandon all hope of farming.")
	public static float weedSpawnChance = 0.15f;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Weed Growth Rate", min = "10", max = "50", comment = "The average number of growth ticks for the weed to grow.")
	public static int weedGrowthRate = 50;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Weed Growth Multiplier", min = "1", max = "2", comment = "Ranges from 1-2 inclusive.")
	public static int weedGrowthMultiplier = 2;
	@AgriCraftConfigurable(category = ConfigCategory.FARMING, key = "Raking weeds drops items", comment = "Set to false if you wish to disable drops from raking weeds.")
	public static boolean rakingDrops = true;

	// World Generation
	//@AgriCraftConfigurable(category = ConfigCategory.WORLDGEN, key = "Disable World Gen", comment = "Set to true to disable world gen, no greenhouses will spawn in villages.")
	public static boolean disableWorldGen = true;
	//@AgriCraftConfigurable(category = ConfigCategory.WORLDGEN, key = "Greenhouse weight", min = "0", max = "100", comment = "The weight for a greenhouse to be generated in a village.")
	public static int greenhouseWeight = 10;
	//@AgriCraftConfigurable(category = ConfigCategory.WORLDGEN, key = "Greenhouse limit", min = "0", max = "2", comment = "The maximum number of greenhouses per village.")
	public static int greenhouseLimit = 1;
	//@AgriCraftConfigurable(category = ConfigCategory.WORLDGEN, key = "Irrigated greenhouse weight", min = "0", max = "100", comment = "The weight for an irrigated greenhouse to be generated in a village.")
	public static int greenhouseIrrigatedWeight = 2;
	//@AgriCraftConfigurable(category = ConfigCategory.WORLDGEN, key = "Irrigated greenhouse limit", min = "0", max = "2", comment = "The maximum number of irrigated greenhouses per village.")
	public static int greenhouseIrrigatedLimit = 1;
	//@AgriCraftConfigurable(category = ConfigCategory.WORLDGEN, key = "Enable villagers", comment = "Set to false if you wish to disable villagers spawning in the AgriCraft greenhouses.")
	public static boolean villagerEnabled = true;
	//@AgriCraftConfigurable(category = ConfigCategory.WORLDGEN, key = "Maximum crop tier", min = "1", max = "5", comment = "The maximum tier of plants that will spawn in greenhouses.")
	public static int greenHouseMaxTier = 3;

	// Client
	@AgriCraftConfigurable(category = ConfigCategory.CLIENT, key = "Condense custom wood blocks in NEI", comment = "Set to true to condense all entries for custom wood blocks into one entry in NEI.")
	public static boolean condenseCustomWoodInNei = true;
	@AgriCraftConfigurable(category = ConfigCategory.CLIENT, key = "Stat Display", comment = "This defines how to display the stats of plants. Possible settings are 'NUMBER': just display a simple number (ex: \"6\"), 'FRACTION': number/maximum (ex: \"6/10\"), 'CHARACTER-'char'': number of characters equal to the stats (ex: CHARACTER-� will give \"������\") and 'KEYWORD-'type'-'keyword'': keyword followed by the type and then the stat, type is any of the previous types (ex: KEYWORD-FRACTION-Rank will give \"Rank: 6/10\") . Invalid entries will default to NUMBER.")
	public static String statDisplay = "NUMBER";
	@AgriCraftConfigurable(category = ConfigCategory.CLIENT, key = "Disable particles", comment = "Set to true to disable particles for the sprinklers.")
	public static boolean disableParticles = false;
	@AgriCraftConfigurable(category = ConfigCategory.CLIENT, key = "Disable sounds", comment = "Set to true to disable sounds.")
	public static boolean disableSounds = false;

	//seed storage
	@AgriCraftConfigurable(category = ConfigCategory.STORAGE, key = "Disable seed storage system", comment = "Set to true to disable the seed storage systems.")
	public static boolean disableSeedStorage = false;
	@AgriCraftConfigurable(category = ConfigCategory.STORAGE, key = "Disable seed storage warehouses", comment = "Set to true to disable the seed storage warehouse blocks.")
	public static boolean disableSeedWarehouse = false;

	// Add to Configurator
	static {
		ConfigurationHandler.addConfigurable(AgriCraftConfig.class);
	}

}

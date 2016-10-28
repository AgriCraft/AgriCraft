/*
 * AgriCraft Configuration File
 *
 * This might not be *strictly* cleaner, but it is nicer.
 *
 */
package com.infinityraider.agricraft.config;

import net.minecraft.util.text.TextFormatting;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.reference.Constants;

/**
 * AgriCraft Configuration File.
 *
 *
 */
public class AgriCraftConfig {

    // Core
    @AgriConfigurable(category = AgriConfigCategory.CORE, key = "Register crop products in the ore dict", comment = "Set to false to not register crop products to the ore dictionary if you are experiencing issues with GregTech (note that disabling this will have the side effect that seeds will no longer work with the Phytogenic Insulator).")
    public static boolean registerCropProductsToOreDict = true;
    @AgriConfigurable(category = AgriConfigCategory.CORE, key = "Enable Resource Crops", comment = "Set to true if you wish to enable resource crops.")
    public static boolean resourcePlants = true;
    @AgriConfigurable(category = AgriConfigCategory.CORE, key = "Clear Tall Grass Drops", comment = "Set to true to clear the list of items dropping from tall grass (Will run before adding seeds defined in the grass drops config).")
    public static boolean wipeTallGrassDrops = false;
    @AgriConfigurable(category = AgriConfigCategory.CORE, key = "Render Book in Anaylzer", comment = "Set to false to not render the journal on the analyzer.")
    public static boolean renderBookInAnalyzer = true;
    @AgriConfigurable(category = AgriConfigCategory.CORE, key = "Crops per Craft", min = "1", max = "4", comment = "The number of crops you get per crafting operation.")
    public static int cropsPerCraft = 4;
    @AgriConfigurable(category = AgriConfigCategory.CORE, key = "Crop Stat Cap", min = "1", max = "10", comment = "The maximum attainable value of the stats on a crop.")
    public static int cropStatCap = 10;
    @AgriConfigurable(category = AgriConfigCategory.CORE, key = "Enable Custom Crops", comment = "Set to true if you wish to create your own crops.")
    public static boolean customCrops = false;

    // Debug
    @AgriConfigurable(category = AgriConfigCategory.DEBUG, key = "debug", comment = "Set to true to enable debug mode.")
    public static boolean debug = false;

    // Irrigation
    @AgriConfigurable(category = AgriConfigCategory.IRRIGATION, key = "Disable Irrigation", comment = "Set to true if you want to disable irrigation systems.")
    public static boolean disableIrrigation = false;
    @AgriConfigurable(category = AgriConfigCategory.IRRIGATION, key = "Spawn water after breaking tank", comment = "Set to false to disable placing a source block when breaking non-empty tanks.")
    public static boolean placeWater = false;
    @AgriConfigurable(category = AgriConfigCategory.IRRIGATION, key = "Fill tank from flowing water", comment = "Set to true to let tanks fill up when water flows above them.")
    public static boolean fillFromFlowingWater = false;
    @AgriConfigurable(category = AgriConfigCategory.IRRIGATION, key = "Channel Capacity", min = "100", max = "2000", comment = "The amount of water in mB that an irrigation channel can hold.")
    public static int channelCapacity = 500;

    // Sprinkler
    @AgriConfigurable(category = AgriConfigCategory.IRRIGATION, key = "Sprinkler growth interval", min = "1", max = "300", comment = "Every x seconds each plant in sprinkler range has y chance to growth tick.")
    public static int sprinklerGrowthInterval = 5;
    @AgriConfigurable(category = AgriConfigCategory.IRRIGATION, key = "Sprinkler growth chance", min = "0", max = "100", comment = "Every x seconds each plant in sprinkler range has this chance to growth tick")
    public static int sprinklerGrowthChance = 20;
    @AgriConfigurable(category = AgriConfigCategory.IRRIGATION, key = "Sprinkler water usage", min = "0", max = "10000", comment = "Water usage of the sprinkler in mB per second")
    public static int sprinklerRatePerSecond = 10;
    public static int sprinklerRatePerHalfSecond = 5;
    public static float sprinklerGrowthChancePercent = 0.1f;
    public static int sprinklerGrowthIntervalTicks = 100;

    // Farming
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Bonemeal Mutations", comment = "Set to false if you wish to disable using bonemeal on a cross crop to force a mutation.")
    public static boolean bonemealMutation = false;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Disable Vanilla Farming", comment = "set to true to disable vanilla farming, meaning you can only grow plants on crops.")
    public static boolean disableVanillaFarming = false;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Hardcore stats", comment = "Set to true to enable hardcore mode for stat increasing: 1 parent: 3/4 decrement, 1/4 nothing.\n 2 parents: 2/4 decrement, 1/4 nothing, 1/4 increment.\n 3 parents: 1/4 decrement, 1/2 nothing, 1/4 increment.\n 4 parents: 1/4 decrement, 1/4 nothing, 1/2 increment.")
    public static boolean hardCoreStats = false;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Growth rate multiplier", min = "0", max = "2", comment = "This is a global growth rate multiplier.")
    public static float growthMultiplier = 1.0f;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Crop Stat Divisor", min = "1", max = "3", comment = "On a mutation the stats on the crop will be divided by this number.")
    public static int cropStatDivisor = 2;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Only mature crops drop seeds", comment = "Set this to true to make only mature crops drop seeds (to encourage trowel usage).")
    public static boolean onlyMatureDropSeeds = false;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Mod specific drops", comment = "Set to false to disable mod specific drops, this will (for instance) cause Natura berries to drop from Harvestcraft berry crops.")
    public static boolean modSpecificDrops = true;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Non parent crops affect stats negatively", comment = "True means any crop that is not considered a valid parent will affect stat gain negatively.")
    public static boolean otherCropsAffectStatsNegatively = true;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Mutation Chance", min = "0", max = "1", comment = "Define mutation chance (0.0 means no mutations, only spreading and 1.0 means only mutations no spreading.")
    public static float mutationChance = Constants.DEFAULT_MUTATION_CHANCE;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Valid parents", min = "1", max = "3", comment = "What are considered valid parents for stat increasing: 1 = Any. 2 = Mutation parents and identical crops. 3 = Only identical crops.")
    public static int validParents = 2;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Single spread stat increase", comment = "Set to true to allow crops that spread from one single crop to increase stats.")
    public static boolean singleSpreadsIncrement = false;

    // Weeds
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Enable weeds", comment = "Set to false if you wish to disable weeds")
    public static boolean enableWeeds = true;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Weeds Overtake Plants", comment = "Set to false if you don't want weeds to be able to overgrow other plants.")
    public static boolean weedsWipePlants = true;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Weeds destroy crop sticks", comment = "Set this to true to have weeds destroy the crop sticks when they are broken with weeds (to encourage rake usage).")
    public static boolean weedsDestroyCropSticks = false;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Weed Spawn Chance", min = "0.05", max = "0.95", comment = "The percent chance of weeds to spawn or spread. At 95% abandon all hope of farming.")
    public static float weedSpawnChance = 0.15f;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Weed Growth Rate", min = "10", max = "50", comment = "The average number of growth ticks for the weed to grow.")
    public static int weedGrowthRate = 50;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Weed Growth Multiplier", min = "1", max = "2", comment = "Ranges from 1-2 inclusive.")
    public static int weedGrowthMultiplier = 2;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Raking weeds drops items", comment = "Set to false if you wish to disable drops from raking weeds.")
    public static boolean rakingDrops = true;

    // World Generation
    @AgriConfigurable(category = AgriConfigCategory.WORLD, key = "Disable World Gen", comment = "Set to true to disable world gen, no greenhouses will spawn in villages.")
    public static boolean disableWorldGen = false;
    @AgriConfigurable(category = AgriConfigCategory.WORLD, key = "Greenhouse weight", min = "0", max = "100", comment = "The weight for a greenhouse to be generated in a village.")
    public static int greenhouseWeight = 10;
    @AgriConfigurable(category = AgriConfigCategory.WORLD, key = "Greenhouse limit", min = "0", max = "2", comment = "The maximum number of greenhouses per village.")
    public static int greenhouseLimit = 1;
    @AgriConfigurable(category = AgriConfigCategory.WORLD, key = "Irrigated greenhouse weight", min = "0", max = "100", comment = "The weight for an irrigated greenhouse to be generated in a village.")
    public static int greenhouseIrrigatedWeight = 2;
    @AgriConfigurable(category = AgriConfigCategory.WORLD, key = "Irrigated greenhouse limit", min = "0", max = "2", comment = "The maximum number of irrigated greenhouses per village.")
    public static int greenhouseIrrigatedLimit = 1;
    @AgriConfigurable(category = AgriConfigCategory.WORLD, key = "Enable villagers", comment = "Set to false if you wish to disable villagers spawning in the AgriCraft greenhouses.")
    public static boolean villagerEnabled = true;
    @AgriConfigurable(category = AgriConfigCategory.WORLD, key = "Maximum crop tier", min = "1", max = "5", comment = "The maximum tier of plants that will spawn in greenhouses.")
    public static int greenHouseMaxTier = 3;

    // Client
    @AgriConfigurable(category = AgriConfigCategory.CLIENT, key = "Condense custom wood blocks in NEI", comment = "Set to true to condense all entries for custom wood blocks into one entry in NEI.")
    public static boolean condenseCustomWoodInNei = true;
    @AgriConfigurable(category = AgriConfigCategory.CLIENT, key = "Stat Display", comment = "This defines how to display the stats of plants.")
    public static String STAT_FORMAT = TextFormatting.GREEN + "- {0}: [{1}/{2}]";
    @AgriConfigurable(category = AgriConfigCategory.CLIENT, key = "Disable particles", comment = "Set to true to disable particles for the sprinklers.")
    public static boolean disableParticles = false;
    @AgriConfigurable(category = AgriConfigCategory.CLIENT, key = "Disable sounds", comment = "Set to true to disable sounds.")
    public static boolean disableSounds = false;

    //seed storage
    @AgriConfigurable(category = AgriConfigCategory.STORAGE, key = "Disable seed storage system", comment = "Set to true to disable the seed storage systems.")
    public static boolean disableSeedStorage = false;
    @AgriConfigurable(category = AgriConfigCategory.STORAGE, key = "Disable seed storage warehouses", comment = "Set to true to disable the seed storage warehouse blocks.")
    public static boolean disableSeedWarehouse = false;

    // Decoration
    @AgriConfigurable(category = AgriConfigCategory.DECORATION, key = "Enable Fences", comment = "Set to false to disable the decorative custom wood fences.")
    public static boolean enableFences = true;
    @AgriConfigurable(category = AgriConfigCategory.DECORATION, key = "Enable Grates", comment = "Set to false to disable the decorative custom wood grates.")
    public static boolean enableGrates = true;

    // Add to Configurator
    static {
        AgriCore.getConfig().addConfigurable(AgriCraftConfig.class);
    }

}

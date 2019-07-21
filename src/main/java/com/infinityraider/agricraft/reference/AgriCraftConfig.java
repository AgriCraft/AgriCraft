/*
 * AgriCraft Configuration File
 *
 * This might not be *strictly* cleaner, but it is nicer.
 *
 */
package com.infinityraider.agricraft.reference;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.agricraft.agricore.core.AgriCore;
import net.minecraft.util.text.TextFormatting;

/**
 * AgriCraft Configuration File.
 *
 *
 */
public class AgriCraftConfig {

    // Core
    @AgriConfigurable(category = AgriConfigCategory.CORE, key = "Crops per Craft", min = "1", max = "4", comment = "The number of crops you get per crafting operation.")
    public static int cropsPerCraft = 4;
    @AgriConfigurable(category = AgriConfigCategory.CORE, key = "Crop Stat Cap", min = "1", max = "10", comment = "The maximum attainable value of the stats on a crop.")
    public static int cropStatCap = 10;
    @AgriConfigurable(category = AgriConfigCategory.CORE, key = "Use boring alpha warning messages", comment = "Uses a boring one-line alpha warning message instead of the more interesting default ones.")
    public static boolean disableLinks = false;

    // Debug
    @AgriConfigurable(category = AgriConfigCategory.DEBUG, key = "debug", comment = "Set to true to enable debug mode.")
    public static boolean debug = false;
    @AgriConfigurable(category = AgriConfigCategory.DEBUG, key = "Display Registry ToolTips", comment = "Set to true to add Registry information to itemstack tooltips.")
    public static boolean enableRegistryTooltips = false;
    @AgriConfigurable(category = AgriConfigCategory.DEBUG, key = "Display OreDict Tooltips", comment = "Set to true to add OreDict info to itemstack tooltips.")
    public static boolean enableOreDictTooltips = false;
    @AgriConfigurable(category = AgriConfigCategory.DEBUG, key = "Display NBT Tooltips", comment = "Set to true to add NBT info to itemstack tooltips.")
    public static boolean enableNBTTooltips = false;

    // Irrigation
    @AgriConfigurable(category = AgriConfigCategory.IRRIGATION, key = "Enable Irrigation", comment = "Set to true if you want to enable irrigation systems.")
    public static boolean enableIrrigation = true;
    @AgriConfigurable(category = AgriConfigCategory.IRRIGATION, key = "Fill tank from flowing water", comment = "Set to true to let tanks fill up when water flows above them.")
    public static boolean fillFromFlowingWater = false;

    @AgriConfigurable(category = AgriConfigCategory.IRRIGATION, key = "Fill tank from rainfall", comment = "Set to true to let tanks fill up when water falls from the sky.")
    public static boolean fillFromRainfall = true;
    @AgriConfigurable(category = AgriConfigCategory.IRRIGATION, key = "Channel Capacity", min = "100", max = "2000", comment = "The amount of water in mB that an irrigation channel can hold.")
    public static int channelCapacity = 500;

    // Sprinkler
    @AgriConfigurable(category = AgriConfigCategory.IRRIGATION, key = "Sprinkler growth interval", min = "1", max = "1200", comment = "The minimum number of ticks between successive starts of irrigation. No effect if it's less than the number required to actually finish.")
    public static int sprinklerGrowthIntervalTicks = 100;
    @AgriConfigurable(category = AgriConfigCategory.IRRIGATION, key = "Sprinkler growth chance", min = "0", max = "100", comment = "Every loop, each unobscured plant in sprinkler range has this chance to get a growth tick from the sprinkler.")
    public static int sprinklerGrowthChance = 20;
    @AgriConfigurable(category = AgriConfigCategory.IRRIGATION, key = "Sprinkler water usage", min = "0", max = "10000", comment = "Defined in terms of mB per second. The irrigation loop progress will pause when there is insufficient water.")
    public static int sprinklerRatePerSecond = 10;

    // Farming
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Fertilizer Mutations", comment = "Set to false if you wish to disable using fertilizers on a cross crop to force a mutation.")
    public static boolean fertilizerMutation = false;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Allow other mods to apply bonemeal", comment = "For when a mod uses the IGrowable interface, instead of registering its own fertilizer with AgriCraft.\nNote: This does not restrict generic update ticks.")
    public static boolean allowIGrowableOnCrop = true;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Disable Vanilla Farming", comment = "Set to true to disable vanilla farming, meaning you can only grow plants on crops.")
    public static boolean disableVanillaFarming = false;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Show Disabled Vanilla Farming Warning", comment = "Set to true to warn that vanilla farming is disabled when trying to plant vanilla plant.")
    public static boolean showDisabledVanillaFarmingWarning = true;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Hardcore stats", comment = "Set to true to enable hardcore mode for stat increasing: 1 parent: 3/4 decrement, 1/4 nothing.\n 2 parents: 2/4 decrement, 1/4 nothing, 1/4 increment.\n 3 parents: 1/4 decrement, 1/2 nothing, 1/4 increment.\n 4 parents: 1/4 decrement, 1/4 nothing, 1/2 increment.")
    public static boolean hardCoreStats = false;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Growth rate multiplier", min = "0", max = "2", comment = "This is a global growth rate multiplier.")
    public static float growthMultiplier = 1.0f;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Crop Stat Divisor", min = "1", max = "3", comment = "On a mutation the stats on the crop will be divided by this number.")
    public static int cropStatDivisor = 2;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Only mature crops drop seeds", comment = "Set this to true to make only mature crops drop seeds (to encourage trowel usage).")
    public static boolean onlyMatureDropSeeds = false;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Non parent crops affect stats negatively", comment = "True means any crop that is not considered a valid parent will affect stat gain negatively.")
    public static boolean otherCropsAffectStatsNegatively = true;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Mutation Chance", min = "0", max = "1", comment = "Define mutation chance (0.0 means no mutations, only spreading and 1.0 means only mutations no spreading.")
    public static float mutationChance = 0.2f;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Valid parents", min = "1", max = "3", comment = "What are considered valid parents for stat increasing: 1 = Any. 2 = Mutation parents and identical crops. 3 = Only identical crops.")
    public static int validParents = 2;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Single spread stat increase", comment = "Set to true to allow crops that spread from one single crop to increase stats.")
    public static boolean singleSpreadsIncrement = false;
    @AgriConfigurable(key = "Wipe Grass Drops", category = AgriConfigCategory.FARMING, comment = "Determines if AgriCraft should completeley override grass drops with those confiured in the JSON files.")
    public static boolean wipeGrassDrops = false;

    // Weeds
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Weeds destroy crop sticks", comment = "Set this to true to have weeds destroy the crop sticks when they are broken with weeds (to encourage rake usage).")
    public static boolean weedsDestroyCropSticks = false;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Crossover Chance", min = "0.05", max = "0.95", comment = "The base chance for a crossover to occur during any given tick. Setting this 95% is of questionable morality.")
    public static float crossOverChance = 0.15f;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Weed Growth Rate", min = "10", max = "50", comment = "The average number of growth ticks for the weed to grow.")
    public static int defaultGrowthChance = 50;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Raking weeds drops items", comment = "Set to false if you wish to disable drops from raking weeds.")
    public static boolean enableRakingItemDrops = true;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Raking weeds drops seeds", comment = "Set to false if you wish to disable seed drops from raking weeds.")
    public static boolean enableRakingSeedDrops = false;
    @AgriConfigurable(category = AgriConfigCategory.FARMING, key = "Harvest drops seeds", comment = "Set to false if you wish to disable seed drops from harvest.")
    public static boolean enableHarvestSeedDrops = true;
    
    // World Generation
    /*
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
     */
    // Client
    //@AgriConfigurable(category = AgriConfigCategory.CLIENT, key = "Condense custom wood blocks in NEI", comment = "Set to true to condense all entries for custom wood blocks into one entry in NEI.")
    //public static boolean condenseCustomWoodInNei = true;
    @AgriConfigurable(category = AgriConfigCategory.CLIENT, key = "Stat Display", comment = "This defines how to display the stats of plants.")
    public static String STAT_FORMAT = TextFormatting.GREEN + "- {0}: [{1}/{2}]";
    // TODO: Instead follow the reduce particles setting from minecraft.
    @AgriConfigurable(category = AgriConfigCategory.CLIENT, key = "Disable particles", comment = "Set to true to disable particles for the sprinklers.")
    public static boolean disableParticles = false;

    //seed storage
    @AgriConfigurable(category = AgriConfigCategory.STORAGE, key = "Disable seed storage system", comment = "Set to true to disable the seed storage systems.")
    public static boolean disableSeedStorage = false;
    @AgriConfigurable(category = AgriConfigCategory.STORAGE, key = "Disable seed storage warehouses", comment = "Set to true to disable the seed storage warehouse blocks.")
    public static boolean disableSeedWarehouse = false;

    // Decoration
    @AgriConfigurable(category = AgriConfigCategory.DECORATION, key = "Enable Grates", comment = "Set to false to disable the decorative custom wood grates.")
    public static boolean enableGrates = true;

    // Add to Configurator
    static {
        AgriCore.getConfig().addConfigurable(AgriCraftConfig.class);
    }

}

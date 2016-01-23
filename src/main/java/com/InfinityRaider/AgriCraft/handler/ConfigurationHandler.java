package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class ConfigurationHandler {
    public static class Categories {
        public static final String CATEGORY_AGRICRAFT = "agricraft";
        public static final String CATEGORY_FARMING = "farming";
        public static final String CATEGORY_TOOLS = "tools";
        public static final String CATEGORY_DEBUG = "debug";
        public static final String CATEGORY_WORLDGEN = "world gen";
        public static final String CATEGORY_IRRIGATION = "irrigation";
        public static final String CATEGORY_STORAGE = "storage";
        public static final String CATEGORY_DECORATION = "decoration";
        public static final String CATEGORY_COMPATIBILITY = "compatibility";
        public static final String CATEGORY_CLIENT = "clientside config";
        public static final String CATEGORY_RENDERING = "rendering";
    }

    public static Configuration config;
    private static String directory;
    private static Property propGenerateDefaults = new Property("RegenDefaults","false", Property.Type.BOOLEAN);

    //COMMON
    //------
    //debug
    public static boolean debug;
    //agricraft
    public static boolean generateDefaults;
    public static boolean customCrops;
    public static int cropsPerCraft;
    public static int cropStatCap;
    public static boolean resourcePlants;
    public static boolean wipeTallGrassDrops;
    public static boolean renderBookInAnalyzer;
    public static boolean registerCropProductsToOreDict;
    //farming
    public static boolean disableVanillaFarming;
    public static double mutationChance;
    public static boolean singleSpreadsIncrement;
    public static int validParents;
    public static boolean otherCropsAffectStatsNegatively;
    public static boolean hardCoreStats;
    public static int cropStatDivisor;
    public static boolean enableWeeds;
    public static boolean weedsWipePlants;
    public static int weedGrowthMultiplier;
    public static int weedGrowthRate;
    public static float weedSpawnChance;
    public static boolean bonemealMutation;
    public static boolean onlyMatureDropSeeds;
    public static boolean weedsDestroyCropSticks;
    public static float growthMultiplier;
    public static boolean rakingDrops;
    public static boolean modSpecifDrops;
    //tools
    public static boolean enableHandRake;
    public static boolean enableTrowel;
    public static boolean enableMagnifyingGlass;
    public static boolean enableClipper;
    //world gen
    public static boolean disableWorldGen;
    public static int greenhouseWeight;
    public static int greenhouseLimit;
    public static int greenhouseIrrigatedWeight;
    public static int greenhouseIrrigatedLimit;
    public static boolean villagerEnabled;
    public static int greenHouseMaxTier;
    //seed storage
    public static boolean disableSeedStorage;
    public static boolean disableSeedWarehouse;
    //irrigation
    public static boolean disableIrrigation;
    public static int sprinklerRatePerSecond;
    public static int sprinklerRatePerHalfSecond;
    public static int sprinklerGrowthChance;
    public static float sprinklerGrowthChancePercent;
    public static int sprinklerGrowthInterval;
    public static int sprinklerGrowthIntervalTicks = 100;
    public static boolean placeWater;
    public static boolean fillFromFlowingWater;
    public static int channelCapacity;
    //decorative
    public static boolean disableFences;
    public static boolean disableGrates;
    //rendering
    public static boolean renderCropPlantsAsTESR;

    //CLIENT
    //------
    public static boolean condenseCustomWoodInNei;
    public static boolean disableParticles;
    public static String statDisplay;
    public static boolean disableSounds;

    public static void init(FMLPreInitializationEvent event) {
        checkAndCreateConfig(event);
        loadConfiguration();
        LogHelper.debug("Configuration Loaded");
    }

    private static void checkAndCreateConfig(FMLPreInitializationEvent event) {
        if(config == null) {
            directory = event.getModConfigurationDirectory().toString()+'/'+Reference.MOD_ID.toLowerCase()+'/';
            config = new Configuration(new File(directory, "Configuration.cfg"));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void initClientConfigs(FMLPreInitializationEvent event) {
        checkAndCreateConfig(event);

        condenseCustomWoodInNei = config.getBoolean("condense custom wood blocks in NEI", Categories.CATEGORY_CLIENT, true, "set to true to condense all entries for custom wood blocks into one entry in NEI");
        disableParticles = config.getBoolean("Disable particles", Categories.CATEGORY_CLIENT, false, "set to true to disable particles for the sprinklers");
        statDisplay = config.getString("Stat Display", Categories.CATEGORY_CLIENT, "NUMBER", "This defines how to display the stats of plants. Possible settings are 'NUMBER': just display a simple number (ex: \"6\"), 'FRACTION': number/maximum (ex: \"6/10\"), 'CHARACTER-'char'': number of characters equal to the stats (ex: CHARACTER-• will give \"••••••\") and 'KEYWORD-'type'-'keyword'': keyword followed by the type and then the stat, type is any of the previous types (ex: KEYWORD-FRACTION-Rank will give \"Rank: 6/10\") . Invalid entries will default to NUMBER ");
        disableSounds = config.getBoolean("Disable sounds", Categories.CATEGORY_CLIENT, false, "Set to true to disable sounds.");

        if(config.hasChanged()) {config.save();}
    }


    //read values from the config
    private static void loadConfiguration() {
        //agricraft
        resourcePlants = config.getBoolean("Resource Crops", Categories.CATEGORY_AGRICRAFT, true, "set to true if you wish to enable resource crops");
        cropsPerCraft = config.getInt("Crops per craft", Categories.CATEGORY_AGRICRAFT, 4, 1, 4, "The number of crops you get per crafting operation");
        cropStatCap = config.getInt("Crop stat cap", Categories.CATEGORY_AGRICRAFT, 10, 1, 10, "The maximum attainable value of the stats on a crop");
        propGenerateDefaults = config.get(Categories.CATEGORY_AGRICRAFT, "GenerateDefaults", false, "set to true to regenerate a default mutations file (will turn back to false afterwards)");
        generateDefaults = propGenerateDefaults.getBoolean();
        customCrops = config.getBoolean("Custom crops", Categories.CATEGORY_AGRICRAFT, false, "set to true if you wish to create your own crops");
        wipeTallGrassDrops = config.getBoolean("Clear tall grass drops", Categories.CATEGORY_AGRICRAFT, false, "set to true to clear the list of items dropping from tall grass (Will run before adding seeds defined in the grass drops config).");
        renderBookInAnalyzer = config.getBoolean("Render journal in analyzer", Categories.CATEGORY_AGRICRAFT, true, "set to false to not render the journal on the analyzer");
        registerCropProductsToOreDict = config.getBoolean("Register crop products in the ore dict", Categories.CATEGORY_AGRICRAFT, true, "set to false to not register crop products to the ore dictionary if you are experiencing issues with GregTech (note that disabling this will have the side effect that seeds will no longer work with the Phytogenic Insulator");
        //farming
        disableVanillaFarming = config.getBoolean("Disable Vanilla Farming", Categories.CATEGORY_FARMING, false, "set to true to disable vanilla farming, meaning you can only grow plants on crops");
        mutationChance = (double) config.getFloat("Mutation Chance", Categories.CATEGORY_FARMING, Constants.DEFAULT_MUTATION_CHANCE, 0, 1 , "Define mutation chance (0.0 means no mutations, only spreading and 1.0 means only mutations no spreading");
        singleSpreadsIncrement = config.getBoolean("Single spread stat increase", Categories.CATEGORY_FARMING, false, "Set to true to allow crops that spread from one single crop to increase stats");
        validParents = config.getInt("Valid parents", Categories.CATEGORY_FARMING, 2, 1, 3, "What are considered valid parents for stat increasing: 1 = Any. 2 = Mutation parents and identical crops. 3 = Only identical crops");
        otherCropsAffectStatsNegatively = config.getBoolean("Non parent crops affect stats negatively", Categories.CATEGORY_FARMING, true, "True means any crop that is not considered a valid parent will affect stat gain negatively");
        hardCoreStats = config.getBoolean("Hardcore stats", Categories.CATEGORY_FARMING, false, "Set to true to enable hardcore mode for stat increasing: 1 parent: 3/4 decrement, 1/4 nothing.\n 2 parents: 2/4 decrement, 1/4 nothing, 1/4 increment.\n 3 parents: 1/4 decrement, 1/2 nothing, 1/4 increment.\n 4 parents: 1/4 decrement, 1/4 nothing, 1/2 increment.");
        cropStatDivisor = config.getInt("Crop stat divisor", Categories.CATEGORY_FARMING, 2, 1, 3, "On a mutation the stats on the crop will be divided by this number");
        enableWeeds = config.getBoolean("Enable weeds", Categories.CATEGORY_FARMING, true, "set to false if you wish to disable weeds");
        weedGrowthMultiplier = config.getInt("Weed Growth Multiplier", Categories.CATEGORY_FARMING, 2, 1, 2, "Ranges from 1-2 inclusive.");
        weedGrowthRate = config.getInt("Weed Growth Rate", Categories.CATEGORY_FARMING, 50, 10, 50, "The average number of growth ticks for the weed to grow.");
        weedSpawnChance = config.getFloat("Weed Spawn Chance", Categories.CATEGORY_FARMING, 0.15f, 0.05f, 0.95f, "The percent chance of weeds to spawn or spread. At 95% abandon all hope of farming. Range 0.05-0.95.");
        weedsWipePlants = enableWeeds && config.getBoolean("Weeds can overtake plants", Categories.CATEGORY_FARMING,true,"set to false if you don't want weeds to be able to overgrow other plants");
        bonemealMutation = config.getBoolean("Bonemeal Mutations", Categories.CATEGORY_FARMING, false, "set to false if you wish to disable using bonemeal on a cross crop to force a mutation");
        onlyMatureDropSeeds = config.getBoolean("Only mature crops drop seeds", Categories.CATEGORY_FARMING, false, "set this to true to make only mature crops drop seeds (to encourage trowel usage)");
        weedsDestroyCropSticks = config.getBoolean("Weeds destroy crop sticks", Categories.CATEGORY_FARMING, false, "set this to true to have weeds destroy the crop sticks when they are broken with weeds (to encourage rake usage)");
        growthMultiplier = config.getFloat("Growth rate multiplier", Categories.CATEGORY_FARMING, 1.0F, 0.0F, 2.0F, "This is a global growth rate multiplier");
        rakingDrops = config.getBoolean("Raking weeds drops items", Categories.CATEGORY_FARMING, true, "set to false if you wish to disable drops from raking weeds");
        modSpecifDrops = config.getBoolean("Mod specific drops", Categories.CATEGORY_FARMING, true, "set to false to disable mod specific drops, this will (for instance) cause Natura berries to drop from Harvestcraft berry crops");
        //tools
        enableHandRake = config.getBoolean("Enable Hand Rake", Categories.CATEGORY_TOOLS, true, "Set to false to disable the Hand Rake");
        enableMagnifyingGlass = config.getBoolean("Enable Magnifying Glass", Categories.CATEGORY_TOOLS, true, "Set to false to disable the Magnifying Glass");
        enableTrowel = config.getBoolean("Enable Trowel", Categories.CATEGORY_TOOLS, true, "Set to false to disable the Trowel");
        enableClipper = config.getBoolean("Enable Clipper", Categories.CATEGORY_TOOLS, true, "Set to false to disable the Clipper");
        //world gen
        disableWorldGen = config.getBoolean("Disable World Gen", Categories.CATEGORY_WORLDGEN, false, "set to true to disable world gen, no greenhouses will spawn in villages");
        greenhouseWeight = config.getInt("Greenhouse weight", Categories.CATEGORY_WORLDGEN, 10, 0, 100, "The weight for a greenhouse to be generated in a village");
        greenhouseLimit = config.getInt("Greenhouse limit", Categories.CATEGORY_WORLDGEN, 1, 0, 2, "The maximum number of greenhouses per village");
        greenhouseIrrigatedWeight = config.getInt("Irrigated greenhouse weight", Categories.CATEGORY_WORLDGEN, 2, 0, 100, "The weight for an irrigated greenhouse to be generated in a village");
        greenhouseIrrigatedLimit = config.getInt("Irrigated greenhouse limit", Categories.CATEGORY_WORLDGEN, 1, 0, 2, "The maximum number of irrigated greenhouses per village");
        villagerEnabled = config.getBoolean("Enable villagers", Categories.CATEGORY_WORLDGEN, true, "Set to false if you wish to disable villagers spawning in the AgriCraft greenhouses");
        greenHouseMaxTier = config.getInt("Maximum crop tier", Categories.CATEGORY_WORLDGEN, 3, 1, 5, "The maximum tier of plants that will spawn in greenhouses");
        //storage
        disableSeedStorage = config.getBoolean("Disable seed storage system", Categories.CATEGORY_STORAGE, false, "set to true to disable the seed storage systems");
        disableSeedWarehouse = config.getBoolean("Disable seed storage warehouses", Categories.CATEGORY_STORAGE, false, "set to true to disable the seed storage warehouse blocks");
        //irrigation
        disableIrrigation = config.getBoolean("Disable Irrigation", Categories.CATEGORY_IRRIGATION, false, "set to true if you want to disable irrigation systems");
        sprinklerRatePerSecond = config.getInt("Sprinkler water usage", Categories.CATEGORY_IRRIGATION, 10, 0, 10000, "Water usage of the sprinkler in mB per second");
        sprinklerRatePerHalfSecond = Math.round(sprinklerRatePerSecond / 2);
        sprinklerGrowthChance = config.getInt("Sprinkler growth chance", Categories.CATEGORY_IRRIGATION, 20, 0, 100, "Every x seconds each plant in sprinkler range has this chance to growth tick");
        sprinklerGrowthChancePercent = sprinklerGrowthChance / 100F;
        sprinklerGrowthInterval = config.getInt("Sprinkler growth interval", Categories.CATEGORY_IRRIGATION, 5, 1, 300, "Every x seconds each plant in sprinkler range has y chance to growth tick");
        sprinklerGrowthIntervalTicks = sprinklerGrowthInterval * 20;
        placeWater = config.getBoolean("Spawn water after breaking tank", Categories.CATEGORY_IRRIGATION, true, "set to false to disable placing a source block when breaking non-empty tanks");
        fillFromFlowingWater = config.getBoolean("Fill tank from flowing water", Categories.CATEGORY_IRRIGATION, false, "set to true to let tanks fill up when water flows above them");
        channelCapacity = config.getInt("Channel capacity", Categories.CATEGORY_IRRIGATION, 500, 100, 2000, "The amount of water in mb that an irrigation channel can hold");
        //decoration
        disableFences = config.getBoolean("Disable fences", Categories.CATEGORY_DECORATION, false, "Set to true to disable the decorative custom wood fences");
        disableGrates = config.getBoolean("Disable grates", Categories.CATEGORY_DECORATION, false, "Set to true to disable the decorative custom wood grates");
        //debug mode
        debug = config.getBoolean("debug", Categories.CATEGORY_DEBUG,false,"Set to true if you wish to enable debug mode");
        //rendering
        renderCropPlantsAsTESR = config.getBoolean("Crop rendering setting", Categories.CATEGORY_RENDERING, false,
                "When rendering crops, the default (false) is that the plants will only be re-rendered whenever the chunk updates, " +
                "this basically means that whenever a crop grows it causes the chunk containing the plant to re-rendered.\n" +
                "For small farms this is the suggested approach, however for large farms, it is possible that a crop grows almost every tick, " +
                "resulting in  re-rendering the chunk every tick, possibly causing huge FPS drops.\n" +
                "When setting this to true, there will no longer be chunk updates when a crop grows, but the rendering will be different: " +
                "The plant will be rendered every tick (the sticks itself will still be rendered the default way), for small farms this is a bad approach," +
                "for large farms as well, but it might result in better FPS compared to the default.\n" +
                "I recommend leaving this on false, if you have FPS problems, set this to true and see for yourself if it is an improvement or not.\n" +
                "This config setting must match on server and client, the server should know if it should cause block updates and the client has to know how to render the crops"
        );


        if(config.hasChanged()) {config.save();}
    }

    public static boolean enableModCompatibility(String modId) {
        boolean flag = config.getBoolean(modId, Categories.CATEGORY_COMPATIBILITY, true, "set to false to disable compatibility for "+modId);
        if(config.hasChanged()) {
            config.save();
        }
        return flag;
    }

    public static String readGrassDrops() {
        return IOHelper.readOrWrite(directory, "GrassDrops", IOHelper.getGrassDrops());
    }

    public static String readCustomCrops() {
        return IOHelper.readOrWrite(directory, "CustomCrops", IOHelper.getCustomCropInstructions());
    }

    public static String readMutationData() {
        String data = IOHelper.readOrWrite(directory, "Mutations", IOHelper.getDefaultMutations(), generateDefaults);
        if(generateDefaults) {
            ConfigurationHandler.propGenerateDefaults.setToDefault();
            config.save();
        }
        return data;
    }

    public static String readSpreadChances() {
        return IOHelper.readOrWrite(directory, "SpreadChancesOverrides", IOHelper.getSpreadChancesOverridesInstructions());
    }

    public static String readSeedTiers() {
        return IOHelper.readOrWrite(directory, "SeedTiers", IOHelper.getSeedTierOverridesInstructions());
    }

    public static String readSeedBlackList() {
        return IOHelper.readOrWrite(directory, "SeedBlackList", IOHelper.getSeedBlackListInstructions());
    }

    public static String readVanillaOverrides() {
        return IOHelper.readOrWrite(directory, "VanillaPlantingExceptions", IOHelper.getPlantingExceptionsInstructions());
    }

    public static String readSoils() {
        return IOHelper.readOrWrite(directory, "SoilWhitelist", IOHelper.getSoilwhitelistData());
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(Reference.MOD_ID)) {
            loadConfiguration();
            LogHelper.debug("Configuration reloaded.");
        }
    }
}

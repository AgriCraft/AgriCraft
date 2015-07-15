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
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class ConfigurationHandler {

    public static final String CATEGORY_AGRICRAFT = "agricraft";
    public static final String CATEGORY_FARMING = "farming";
    public static final String CATEGORY_DEBUG = "debug";
    public static final String CATEGORY_WORLDGEN = "world gen";
    public static final String CATEGORY_IRRIGATION = "irrigation";
    public static final String CATEGORY_STORAGE = "storage";
    public static final String CATEGORY_COMPATIBILITY = "compatibility";

    public static Configuration config;
    private static String directory;
    private static Property propGenerateDefaults = new Property("RegenDefaults","false", Property.Type.BOOLEAN);

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
    //farming
    public static boolean disableVanillaFarming;
    public static double mutationChance;
    public static boolean singleSpreadsIncrement;
    public static int spreadingDifficulty;
    public static int cropStatDivisor;
    public static boolean enableWeeds;
    public static boolean weedsWipePlants;
    public static boolean enableHandRake;
    public static boolean bonemealMutation;
    public static boolean onlyMatureDropSeeds;
    public static boolean weedsDestroyCropSticks;
    public static float growthMultiplier;
    //world gen
    public static boolean disableWorldGen;
    public static int greenhouseWeight;
    public static int greenhouseLimit;
    public static int greenhouseIrrigatedWeight;
    public static int greenhouseIrrigatedLimit;
    public static boolean villagerEnabled;
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

    public static void init(FMLPreInitializationEvent event) {
        directory = event.getModConfigurationDirectory().toString()+'/'+Reference.MOD_ID.toLowerCase()+'/';

        if(config == null) {
            config = new Configuration(new File(directory, "Configuration.cfg"));
            loadConfiguration();
        }

        LogHelper.debug("Configuration Loaded");
    }

    @SideOnly(Side.CLIENT)
    public static void initClientConfigs(FMLPreInitializationEvent event) {

    }


    //read values from the config
    private static void loadConfiguration() {
        //agricraft
        resourcePlants = config.getBoolean("Resource Crops", CATEGORY_AGRICRAFT, true, "set to true if you wish to enable resource crops");
        cropsPerCraft = config.getInt("Crops per craft", CATEGORY_AGRICRAFT, 1, 1, 4, "The number of crops you get per crafting operation");
        cropStatCap = config.getInt("Crop stat cap", CATEGORY_AGRICRAFT, 10, 1, 10, "The maximum attainable value of the stats on a crop");
        propGenerateDefaults = config.get(CATEGORY_AGRICRAFT, "GenerateDefaults", false, "set to true to regenerate a default mutations file (will turn back to false afterwards)");
        generateDefaults = propGenerateDefaults.getBoolean();
        propGenerateDefaults.setToDefault();
        customCrops = config.getBoolean("Custom crops", CATEGORY_AGRICRAFT, false, "set to true if you wish to create your own crops");
        wipeTallGrassDrops = config.getBoolean("Clear tall grass drops", CATEGORY_AGRICRAFT, false, "set to true to clear the list of items dropping from tall grass (Will run before adding seeds defined in the grass drops config).");
        renderBookInAnalyzer = config.getBoolean("Render journal in analyzer", CATEGORY_AGRICRAFT, true, "set to false to not render the journal on the analyzer");
        //farming
        disableVanillaFarming = config.getBoolean("Disable Vanilla Farming", CATEGORY_FARMING, false, "set to true to disable vanilla farming, meaning you can only grow plants on crops");
        mutationChance = (double) config.getFloat("Mutation Chance",CATEGORY_FARMING, (float) Constants.defaultMutationChance, 0, 1 , "Define mutation chance (0.0 means no mutations, only spreading and 1.0 means only mutations no spreading");
        singleSpreadsIncrement = config.getBoolean("Single spread stat increase", CATEGORY_FARMING, false, "Set to true to allow crops that spread from one single crop to increase stats");
        spreadingDifficulty = config.getInt("Farming difficulty", CATEGORY_FARMING, 3, 1, 3, "Farming difficulty: 1 = Crops can inherit stats from any crop. 2 = Crops only inherit stats from parent and identical crops. 3 = Same as 2, but any other nearby crop will affect stats negatively.");
        cropStatDivisor = config.getInt("Crop stat divisor", CATEGORY_FARMING, 2, 1, 3, "On a mutation the stats on the crop will be divided by this number");
        enableWeeds = config.getBoolean("Enable weeds", CATEGORY_FARMING, true, "set to false if you wish to disable weeds");
        weedsWipePlants = enableWeeds && config.getBoolean("Weeds can overtake plants",CATEGORY_FARMING,true,"set to false if you don't want weeds to be able to overgrow other plants");
        enableHandRake = config.getBoolean("Enable Hand Rake", CATEGORY_FARMING, true, "When enabled, weeds can only be removed by using this Hand Rake tool");
        bonemealMutation = config.getBoolean("Bonemeal Mutations", CATEGORY_FARMING, false, "set to false if you wish to disable using bonemeal on a cross crop to force a mutation");
        onlyMatureDropSeeds = config.getBoolean("Only mature crops drop seeds", CATEGORY_FARMING, false, "set this to true to make only mature crops drop seeds (to encourage trowel usage)");
        weedsDestroyCropSticks = config.getBoolean("Weeds destroy crop sticks", CATEGORY_FARMING, false, "set this to true to have weeds destroy the crop sticks when they are broken with weeds (to encourage rake usage)");
        growthMultiplier = config.getFloat("Growth rate multiplier", CATEGORY_FARMING, 1.0F, 0.0F, 2.0F, "This is a global growth rate multiplier");
        //world gen
        disableWorldGen = config.getBoolean("Disable World Gen", CATEGORY_WORLDGEN, false, "set to true to disable world gen, no greenhouses will spawn in villages");
        greenhouseWeight = config.getInt("Greenhouse weight", CATEGORY_WORLDGEN, 10, 0, 100, "The weight for a greenhouse to be generated in a village");
        greenhouseLimit = config.getInt("Greenhouse limit", CATEGORY_WORLDGEN, 1, 0, 2, "The maximum number of greenhouses per village");
        greenhouseIrrigatedWeight = config.getInt("Irrigated greenhouse weight", CATEGORY_WORLDGEN, 2, 0, 100, "The weight for an irrigated greenhouse to be generated in a village");
        greenhouseIrrigatedLimit = config.getInt("Irrigated greenhouse limit", CATEGORY_WORLDGEN, 1, 0, 2, "The maximum number of irrigated greenhouses per village");
        villagerEnabled = config.getBoolean("Enable villagers", CATEGORY_WORLDGEN, true, "Set to false if you wish to disable villagers spawning in the ArgiCraft houses");
        //storage
        disableSeedStorage = config.getBoolean("Disable seed storage system", CATEGORY_STORAGE, false, "set to true to disable the seed storage systems");
        disableSeedWarehouse = config.getBoolean("Disable seed storage warehouses", CATEGORY_STORAGE, false, "set to true to disable the seed storage warehouse blocks");
        //irrigation
        disableIrrigation = config.getBoolean("Disable Irrigation", CATEGORY_IRRIGATION, false, "set to true if you want to disable irrigation systems");
        sprinklerRatePerSecond = config.getInt("Sprinkler water usage", CATEGORY_IRRIGATION, 10, 0, 10000, "Water usage of the sprinkler in mB per second");
        sprinklerRatePerHalfSecond = Math.round(sprinklerRatePerSecond / 2);
        sprinklerGrowthChance = config.getInt("Sprinkler growth chance", CATEGORY_IRRIGATION, 20, 0, 100, "Every x seconds each plant in sprinkler range has this chance to growth tick");
        sprinklerGrowthChancePercent = sprinklerGrowthChance / 100F;
        sprinklerGrowthInterval = config.getInt("Sprinkler growth interval", CATEGORY_IRRIGATION, 5, 1, 300, "Every x seconds each plant in sprinkler range has y chance to growth tick");
        sprinklerGrowthIntervalTicks = sprinklerGrowthInterval * 20;
        placeWater = config.getBoolean("Spawn water after breaking tank", CATEGORY_IRRIGATION, true, "set to false to disable placing a source block when breaking non-empty tanks");
        fillFromFlowingWater = config.getBoolean("Fill tank from flowing water", CATEGORY_IRRIGATION, false, "set to true to let tanks fill up when water flows above them");
        //debug mode
        debug = config.getBoolean("debug",CATEGORY_DEBUG,false,"Set to true if you wish to enable debug mode");

        if(config.hasChanged()) {config.save();}
    }

    public static boolean enableModCompatibility(String modId) {
        boolean flag = config.getBoolean(modId, CATEGORY_COMPATIBILITY, true, "set to false to disable compatibility for "+modId);
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
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(Reference.MOD_ID)) {
            loadConfiguration();
            LogHelper.debug("Configuration reloaded.");
        }
    }
}

package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class ConfigurationHandler {

    public static final String CATEGORY_AGRICRAFT = "agricraft";
    public static final String CATEGORY_DEBUG = "debug";
    public static final String CATEGORY_INTEGRATION = "integration";
    public static final String CATEGORY_IRRIGATION = "irrigation";

    public static Configuration config;
    private static String directory;
    private static Property propGenerateDefaults = new Property("RegenDefaults","false", Property.Type.BOOLEAN);

    public static boolean debug;
    public static boolean enableNEI;
    public static boolean generateDefaults;
    public static boolean customCrops;
    public static boolean resourcePlants;
    public static double mutationChance;
    public static int cropsPerCraft;
    public static boolean enableWeeds;
    public static boolean weedsWipePlants;
    public static boolean bonemealMutation;
    public static boolean disableWorldGen;
    public static boolean disableVanillaFarming;
    public static boolean wipeTallGrassDrops;
    public static boolean enableSeedStorage;

    public static boolean disableIrrigation;
    public static int sprinklerRatePerSecond;
    public static int sprinklerRatePerHalfSecond;
    public static boolean placeWater;
    public static boolean fillFromFlowingWater;

    public static boolean integration_HC;
    public static boolean integration_Nat;
    public static boolean integration_WeeeFlowers;
    public static boolean integration_PlantMegaPack;
    public static boolean integration_Chococraft;
    public static boolean integration_Botania;
    public static boolean integration_allowMagicFertiliser;
    public static boolean integration_instantMagicFertiliser;

    public static void init(FMLPreInitializationEvent event) {
        directory = event.getModConfigurationDirectory().toString()+'/'+Reference.MOD_ID.toLowerCase()+'/';

        if(config == null) {
            config = new Configuration(new File(directory, "Configuration.cfg"));
            loadConfiguration();
        }

        LogHelper.info("Configuration Loaded");
    }


    //read values from the config
    private static void loadConfiguration() {
        //agricraft settings
        resourcePlants = config.getBoolean("Resource Crops",CATEGORY_AGRICRAFT,false,"set to true if you wish to enable resource crops");
        mutationChance = (double) config.getFloat("Mutation Chance",CATEGORY_AGRICRAFT, (float) Constants.defaultMutationChance, 0, 1 , "Define mutation chance");
        cropsPerCraft = config.getInt("Crops per craft", CATEGORY_AGRICRAFT, 1, 1, 4, "The number of crops you get per crafting operation");
        enableWeeds = config.getBoolean("Enable weeds", CATEGORY_AGRICRAFT, true, "set to false if you wish to disable weeds");
        weedsWipePlants = enableWeeds && config.getBoolean("Weeds can overtake plants",CATEGORY_AGRICRAFT,true,"set to false if you don't want weeds to be able to overgrow other plants");
        bonemealMutation = config.getBoolean("Bonemeal Mutations",CATEGORY_AGRICRAFT, false, "set to false if you wish to disable using bonemeal on a cross crop to force a mutation");
        disableVanillaFarming = config.getBoolean("Disable Vanilla Farming", CATEGORY_AGRICRAFT, false, "set to true to disable vanilla farming, meaning you can only grow plants on crops");
        disableWorldGen = config.getBoolean("Disable World Gen", CATEGORY_AGRICRAFT, false, "set to true to disable world gen, no greenhouses will spawn in villages");
        enableNEI = config.getBoolean("Enable NEI", CATEGORY_AGRICRAFT, true, "set to false if you wish to disable mutation recipes in NEI");
        propGenerateDefaults = config.get(CATEGORY_AGRICRAFT, "GenerateDefaults", false, "set to true to regenerate a default mutations file (will turn back to false afterwards)");
        generateDefaults = propGenerateDefaults.getBoolean();
        customCrops = config.getBoolean("Custom crops", CATEGORY_AGRICRAFT, false, "set to true if you wish to create your own crops");
        wipeTallGrassDrops = config.getBoolean("Clear tall grass drops", CATEGORY_AGRICRAFT, false, "set to true to clear the list of items dropping from tall grass (Will run before adding seeds defined in the grass drops config).");
        enableSeedStorage = config.getBoolean("Enable seed storage system", CATEGORY_AGRICRAFT, true, "set to false to disable the seed storage system");

        disableIrrigation = config.getBoolean("Disable Irrigation", CATEGORY_IRRIGATION, false, "set to true if you want to disable irrigation systems");
        sprinklerRatePerSecond = config.getInt("Sprinkler water usage", CATEGORY_IRRIGATION, 10, 0, 10000, "Water usage of the sprinkler in mB per second");
        sprinklerRatePerHalfSecond = Math.round(sprinklerRatePerSecond / 2);
        placeWater = config.getBoolean("Spawn water after breaking tank", CATEGORY_IRRIGATION, true, "set to false to disable placing a source block when breaking non-empty tanks");
        fillFromFlowingWater = config.getBoolean("Fill tank from flowing water", CATEGORY_IRRIGATION, false, "set to true to let tanks fill up when water flows above them");

        //mod integration
        integration_HC = ModIntegration.LoadedMods.harvestcraft && config.getBoolean("HarvestCraft",CATEGORY_INTEGRATION, true,"Set to false to disable harvestCraft integration");
        integration_Nat = ModIntegration.LoadedMods.natura && config.getBoolean("Natura",CATEGORY_INTEGRATION, true,"Set to false to disable Natura Integration");
        integration_WeeeFlowers = ModIntegration.LoadedMods.weeeFlowers && config.getBoolean("Weee Flowers",CATEGORY_INTEGRATION, true,"Set to false to disable Weee Flowers Integration");
        integration_PlantMegaPack = ModIntegration.LoadedMods.plantMegaPack && config.getBoolean("Plant Mega Pack",CATEGORY_INTEGRATION, true,"Set to false to disable Plant Mega Pack Integration");
        integration_Chococraft = ModIntegration.LoadedMods.chococraft && config.getBoolean("ChocoCraft",CATEGORY_INTEGRATION, true,"Set to false to disable Chococraft Integration");
        integration_Botania = ModIntegration.LoadedMods.botania && config.getBoolean("Botania", CATEGORY_INTEGRATION, true, "Set to false to disable Chococraft Integration");
        integration_allowMagicFertiliser = ModIntegration.LoadedMods.magicalCrops && config.getBoolean("Magical Crops Fertiliser",CATEGORY_INTEGRATION,true,"Set to false to disable using magical fertiliser on crops");
        integration_instantMagicFertiliser = ModIntegration.LoadedMods.magicalCrops && config.getBoolean("Magical Crops Fertiliser Instant Growth", CATEGORY_INTEGRATION, false, "Set to true to insta-grow plants on which the magical fertiliser is used on");

        //toggle debug mode
        debug = config.getBoolean("debug",CATEGORY_DEBUG,false,"Set to true if you wish to enable debug mode");
        if(config.hasChanged()) {config.save();}
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

    public static String readSoils() {
        return IOHelper.readOrWrite(directory, "SoilWhitelist", IOHelper.getSoilwhitelistData());
    }

    @SubscribeEvent
    public void onCOnfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(Reference.MOD_ID)) {
            loadConfiguration();
            LogHelper.info("Configuration reloaded.");
        }
    }
}

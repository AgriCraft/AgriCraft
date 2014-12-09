package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.compatibility.LoadedMods;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.*;
import java.util.Arrays;

public class ConfigurationHandler {
    private static Configuration config;
    private static String directory;
    private static Property propGenerateDefaults = new Property("RegenDefaults","false", Property.Type.BOOLEAN);

    public static boolean debug;
    public static boolean enableNEI;
    public static boolean generateDefaults;
    public static boolean customCrops;
    public static boolean resourcePlants;
    public static double mutationChance;
    public static int cropsPerCraft;
    public static boolean bonemealMutation;
    public static boolean disableIrrigation;
    public static boolean disableVanillaFarming;

    public static boolean integration_HC;
    public static boolean integration_Nat;
    public static boolean integration_WeeeFlowers;
    public static boolean integration_PlantMegaPack;

    public static void init(FMLPreInitializationEvent event) {
        //specify the directory for the config files
        directory = event.getModConfigurationDirectory().toString()+'/'+Reference.MOD_ID.toLowerCase()+'/';
        //if the config file doesn't exist, make a new one
        if(config == null) {config = new Configuration(new File(directory,"Configuration.cfg"));}
        //load config file
        loadConfiguration();
        LogHelper.info("Configuration Loaded");
    }

    public static void loadConfiguration() {
        //read values from the config
        resourcePlants = config.getBoolean("Resource Crops","AGRICRAFT",false,"set to true if you wish to disable resource crops");
        mutationChance = (double) config.getFloat("Mutation Chance","AGRICRAFT", (float) Constants.defaultMutationChance, 0, 1 , "Define mutation chance");
        cropsPerCraft = config.getInt("Crops per craft", "AGRICRAFT", 1, 1, 4, "The number of crops you get per crafting operation");
        bonemealMutation = config.getBoolean("Bonemeal Mutations","AGRICRAFT", true, "set to false if you wish to disable using bonemeal on a cross crop to force a mutation");
        disableIrrigation = config.getBoolean("Disable Irrigation","AGRICRAFT", false, "set to true if you want to disable irrigation systems");
        disableVanillaFarming = config.getBoolean("Disable Vanilla Farming", "AGRICRAFT", false, "set to true to disable vanilla farming, meaning you can only grow plants on crops");
        enableNEI = config.getBoolean("Enable NEI", "AGRICRAFT", true, "set to false if you wish to disable mutation recipes in NEI");
        propGenerateDefaults = config.get("AGRICRAFT", "GenerateDefaults", false, "set to true to regenerate a default mutations file (will turn back to false afterwards)");
        generateDefaults = propGenerateDefaults.getBoolean();
        customCrops = config.getBoolean("Custom crops", "AGRICRAFT", false, "set to true if you wish to create your own crops");
        integration_HC = LoadedMods.harvestcraft && config.getBoolean("HarvestCraft","INTEGRATION",true,"Set to false to disable harvestCraft integration");
        integration_Nat = LoadedMods.natura && config.getBoolean("Natura","INTEGRATION",true,"Set to false to disable Natura Integration");
        integration_WeeeFlowers = LoadedMods.weeeFlowers && config.getBoolean("Weee Flowers","INTEGRATION",true,"Set to false to disable Weee Flowers Integration");
        integration_PlantMegaPack = LoadedMods.plantMegaPack && config.getBoolean("Plant Mega Pack","INTEGRATION",true,"Set to false to disable Plant Mega Pack Integration");
        debug = config.getBoolean("debug","DEBUG",false,"Set to true if you wish to enable debug mode");
        if(config.hasChanged()) {config.save();}
    }

    public static String readGrassDrops() {
        LogHelper.debug("Reading seeds dropped from grass");
        File file = new File(directory,"GrassDrops.txt");
        LogHelper.debug("File path: "+file.toString());
        if(file.exists() && !file.isDirectory()) {
            LogHelper.debug("File Found");
            try {
                FileInputStream inputStream = new FileInputStream(file);
                byte[] input = new byte[(int) file.length()];
                try {
                    inputStream.read(input);
                    inputStream.close();
                    return new String(input, "UTF-8");
                } catch (IOException e) {
                    LogHelper.error(Arrays.toString(e.getStackTrace()));
                }
            } catch(FileNotFoundException e) {
                LogHelper.error(Arrays.toString(e.getStackTrace()));
            }
        }
        else {
            LogHelper.debug("Generating new file");
            String defaultData = IOHelper.getGrassDrops();
            BufferedWriter writer;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                try {
                    writer.write(defaultData);
                    writer.close();
                    config.save();

                    return defaultData;
                }
                catch(IOException e) {
                    LogHelper.debug("Caught IOException");
                    LogHelper.error(Arrays.toString(e.getStackTrace()));
                }
            }
            catch(IOException e) {
                LogHelper.error(Arrays.toString(e.getStackTrace()));
            }
        }
        return null;
    }

    public static String readCustomCrops() {
        LogHelper.debug("Reading custom crops");
        File file = new File(directory,"CustomCrops.txt");
        LogHelper.debug("File path: "+file.toString());
        if(file.exists() && !file.isDirectory()) {
            LogHelper.debug("File Found");
            try {
                FileInputStream inputStream = new FileInputStream(file);
                byte[] input = new byte[(int) file.length()];
                try {
                    inputStream.read(input);
                    inputStream.close();
                    return new String(input, "UTF-8");
                } catch (IOException e) {
                    LogHelper.error(Arrays.toString(e.getStackTrace()));
                }
            } catch(FileNotFoundException e) {
                LogHelper.error(Arrays.toString(e.getStackTrace()));
            }
        }
        else {
            LogHelper.debug("Generating new file");
            String defaultData = IOHelper.getCustomCropInstructions();
            BufferedWriter writer;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                try {
                    writer.write(defaultData);
                    writer.close();
                    propGenerateDefaults.setToDefault();
                    config.save();

                    return defaultData;
                }
                catch(IOException e) {
                    LogHelper.debug("Caught IOException");
                    LogHelper.error(Arrays.toString(e.getStackTrace()));
                }
            }
            catch(IOException e) {
                LogHelper.error(Arrays.toString(e.getStackTrace()));
            }
        }
        return null;
    }

    public static String readMutationData() {
        LogHelper.debug("Getting mutations");
        File file = new File(directory,"Mutations.txt");
        LogHelper.debug("File path: "+file.toString());
        if(file.exists() && !file.isDirectory() && !generateDefaults) {
            LogHelper.debug("File Found");
            try {
                FileInputStream inputStream = new FileInputStream(file);
                byte[] input = new byte[(int) file.length()];
                try {
                    inputStream.read(input);
                    inputStream.close();
                    return new String(input, "UTF-8");
                } catch (IOException e) {
                    LogHelper.error(Arrays.toString(e.getStackTrace()));
                }
            } catch(FileNotFoundException e) {
                LogHelper.error(Arrays.toString(e.getStackTrace()));
            }
        }
        else {
            LogHelper.debug("Generating new file");
            String defaultData = IOHelper.getDefaultMutations();
            BufferedWriter writer;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                try {
                    writer.write(defaultData);
                    writer.close();
                    propGenerateDefaults.setToDefault();
                    config.save();

                    return defaultData;
                }
                catch(IOException e) {
                    LogHelper.debug("Caught IOException");
                    LogHelper.error(Arrays.toString(e.getStackTrace()));
                }
            }
            catch(IOException e) {
                LogHelper.error(Arrays.toString(e.getStackTrace()));
            }
        }
        return null;
    }


}

package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.items.ModItem;
import com.InfinityRaider.AgriCraft.reference.Data;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResourceCrops {
    //Resource crops
    public static ArrayList<BlockModPlant> vanillaCrops;
    public static ArrayList<ItemModSeed> vanillaSeeds;

    //Metal crops
    public static ArrayList<BlockModPlant> modCrops;
    public static ArrayList<ItemModSeed> modSeeds;

    public static void init() {
        if (ConfigurationHandler.resourcePlants) {
            //search oreDict
            OreDictHelper.getRegisteredOres();
            //vanilla resources
            initVanillaResources();
            //modded resources
            initModdedResources();
            LogHelper.debug("Resource crops registered");
        }
    }

    private static void initVanillaResources() {
        vanillaCrops = new ArrayList<BlockModPlant>();
        vanillaSeeds = new ArrayList<ItemModSeed>();
        for(Object[] data: Data.vanillaResources) {
            String name =(String) data[0];
            //create plant
            BlockModPlant plant = new BlockModPlant(data);
            vanillaCrops.add(plant);
            RegisterHelper.registerCrop(plant, name);
            //create seed
            ItemModSeed seed = new ItemModSeed(plant, "agricraft_journal."+Character.toLowerCase(name.charAt(0))+name.substring(1));
            vanillaSeeds.add(seed);
            RegisterHelper.registerSeed(seed, plant);
        }
        LogHelper.info("Crops registered");
    }

    public static void initModdedResources() {
        modCrops = new ArrayList<BlockModPlant>();
        modSeeds = new ArrayList<ItemModSeed>();
        for(String[] data:Data.modResources) {
            Block base = OreDictHelper.getOreBlockForName(data[0]);
            if(base!=null) {
                Object[] args = {data[1], OreDictHelper.getNuggetForName(data[0]), OreDictHelper.getNuggetMetaForName(data[0]), OreDictHelper.getOreBlockForName(data[0]), OreDictHelper.getOreMetaForName(data[0]), 0, 4, 6};
                String name =(String) args[0];
                //create plant
                BlockModPlant plant = new BlockModPlant(args);
                modCrops.add(plant);
                RegisterHelper.registerCrop(plant, name);
                //create seed
                ItemModSeed seed = new ItemModSeed(plant, "agricraft_journal."+Character.toLowerCase(name.charAt(0))+name.substring(1));
                modSeeds.add(seed);
                RegisterHelper.registerSeed(seed, plant);
            }
        }
    }
}
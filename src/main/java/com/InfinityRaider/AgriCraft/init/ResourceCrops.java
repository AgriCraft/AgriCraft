package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Data;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.ArrayList;

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
        Object[][] vanillaResources = {
                {"Aurigold", net.minecraft.init.Items.gold_nugget, 0, null, Blocks.gold_ore, 0, 4, 6},
                {"Ferranium", OreDictHelper.getNuggetForName("Iron"), OreDictHelper.getNuggetMetaForName("Iron"), null, Blocks.iron_ore, 0, 4, 1},
                {"Diamahlia", OreDictHelper.getNuggetForName("Diamond"), OreDictHelper.getNuggetMetaForName("Diamond"), null, Blocks.diamond_ore, 0, 5, 6},
                {"Lapender", net.minecraft.init.Items.dye, 4, null, Blocks.lapis_ore, 0, 3, 6},
                {"Emeryllis", OreDictHelper.getNuggetForName("Emerald"), OreDictHelper.getNuggetMetaForName("Emerald"), null, Blocks.emerald_ore, 0, 5, 6},
                {"Redstodendron", net.minecraft.init.Items.redstone, 0, null, Blocks.redstone_ore, 0, 3, 6},
                {"NitorWart", net.minecraft.init.Items.glowstone_dust, 0, Blocks.soul_sand, Blocks.glowstone, 0, 4, 6},
                {"Quartzanthemum", OreDictHelper.getNuggetForName("Quartz"), 0, Blocks.soul_sand, Blocks.quartz_ore, 0, 4, 6}
        };
        for(Object[] data: vanillaResources) {
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
                Object[] args = {data[1], OreDictHelper.getNuggetForName(data[0]), OreDictHelper.getNuggetMetaForName(data[0]), null, OreDictHelper.getOreBlockForName(data[0]), OreDictHelper.getOreMetaForName(data[0]), 4, 6};
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
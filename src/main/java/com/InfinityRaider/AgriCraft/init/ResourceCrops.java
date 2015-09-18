package com.InfinityRaider.AgriCraft.init;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.RenderMethod;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Data;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;

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
                {"Aurigold", new ItemStack(net.minecraft.init.Items.gold_nugget), new BlockWithMeta(Blocks.gold_ore, 0), 4, RenderMethod.HASHTAG},
                {"Ferranium", new ItemStack(OreDictHelper.getNuggetForName("Iron"), 1, OreDictHelper.getNuggetMetaForName("Iron")), new BlockWithMeta(Blocks.iron_ore, 0), 4, RenderMethod.HASHTAG},
                {"Diamahlia", new ItemStack(OreDictHelper.getNuggetForName("Diamond"), 1, OreDictHelper.getNuggetMetaForName("Diamond")), new BlockWithMeta(Blocks.diamond_ore, 0), 5, RenderMethod.HASHTAG},
                {"Lapender", new ItemStack(net.minecraft.init.Items.dye, 1, 4), new BlockWithMeta(Blocks.lapis_ore, 0), 3, RenderMethod.HASHTAG},
                {"Emeryllis", new ItemStack(OreDictHelper.getNuggetForName("Emerald"), 1, OreDictHelper.getNuggetMetaForName("Emerald")), new BlockWithMeta(Blocks.emerald_ore, 0), 5, RenderMethod.HASHTAG},
                {"Redstodendron", new ItemStack(net.minecraft.init.Items.redstone), new BlockWithMeta(Blocks.redstone_ore, 0), 3, RenderMethod.HASHTAG},
                {"NitorWart", new ItemStack(net.minecraft.init.Items.glowstone_dust), Blocks.soul_sand, new BlockWithMeta(Blocks.glowstone, 0), 4, RenderMethod.HASHTAG},
                {"Quartzanthemum", new ItemStack(OreDictHelper.getNuggetForName("Quartz")), Blocks.soul_sand, new BlockWithMeta(Blocks.quartz_ore, 0), 4, RenderMethod.HASHTAG}
        };
        for(Object[] data: vanillaResources) {
            BlockModPlant plant;
            try {
                plant = new BlockModPlant(data);
            } catch (Exception e) {
                if(ConfigurationHandler.debug) {
                    e.printStackTrace();
                }
                return;
            }
            vanillaCrops.add(plant);
            vanillaSeeds.add(plant.getSeed());
        }
    }

    public static void initModdedResources() {
        modCrops = new ArrayList<BlockModPlant>();
        modSeeds = new ArrayList<ItemModSeed>();
        for(String[] data:Data.modResources) {
            Block base = OreDictHelper.getOreBlockForName(data[0]);
            if(base!=null) {
                Object[] args = {data[1], new ItemStack(OreDictHelper.getNuggetForName(data[0]), 1, OreDictHelper.getNuggetMetaForName(data[0])), new BlockWithMeta(OreDictHelper.getOreBlockForName(data[0]), OreDictHelper.getOreMetaForName(data[0])), 4, RenderMethod.HASHTAG};
                BlockModPlant plant;
                try {
                    plant = new BlockModPlant(args);
                } catch (Exception e) {
                    if(ConfigurationHandler.debug) {
                    	LogHelper.printStackTrace(e);
                    }
                    return;
                }
                modCrops.add(plant);
                modSeeds.add(plant.getSeed());
            }
        }
    }
}
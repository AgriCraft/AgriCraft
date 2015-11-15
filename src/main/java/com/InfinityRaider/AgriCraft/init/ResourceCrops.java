package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.RenderMethod;
import com.InfinityRaider.AgriCraft.api.v1.RequirementType;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Data;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

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
                {"Aurigold", new ItemStack(net.minecraft.init.Items.gold_nugget), RequirementType.BELOW, new BlockWithMeta(Blocks.gold_ore, 0), 4, RenderMethod.HASHTAG},
                {"Ferranium", new ItemStack(OreDictHelper.getNuggetForName("Iron"), 1, OreDictHelper.getNuggetMetaForName("Iron")), RequirementType.BELOW, new BlockWithMeta(Blocks.iron_ore, 0), 4, RenderMethod.HASHTAG},
                {"Diamahlia", new ItemStack(OreDictHelper.getNuggetForName("Diamond"), 1, OreDictHelper.getNuggetMetaForName("Diamond")), RequirementType.BELOW, new BlockWithMeta(Blocks.diamond_ore, 0), 5, RenderMethod.HASHTAG},
                {"Lapender", new ItemStack(net.minecraft.init.Items.dye, 1, 4), RequirementType.BELOW, new BlockWithMeta(Blocks.lapis_ore, 4), 3, RenderMethod.HASHTAG},
                {"Emeryllis", new ItemStack(OreDictHelper.getNuggetForName("Emerald"), 1, OreDictHelper.getNuggetMetaForName("Emerald")), RequirementType.BELOW, new BlockWithMeta(Blocks.emerald_ore, 0), 5, RenderMethod.HASHTAG},
                {"Redstodendron", new ItemStack(net.minecraft.init.Items.redstone), RequirementType.BELOW, new BlockWithMeta(Blocks.redstone_ore, 0), 3, RenderMethod.HASHTAG},
                {"NitorWart", new ItemStack(net.minecraft.init.Items.glowstone_dust), new BlockWithMeta(Blocks.soul_sand), RequirementType.BELOW, new BlockWithMeta(Blocks.glowstone, 0), 4, RenderMethod.HASHTAG},
                {"Quartzanthemum", new ItemStack(OreDictHelper.getNuggetForName("Quartz")), new BlockWithMeta(Blocks.soul_sand), RequirementType.BELOW, new BlockWithMeta(Blocks.quartz_ore, 0), 4, RenderMethod.HASHTAG}
        };
        for(Object[] data: vanillaResources) {
            BlockModPlant plant;
            try {
                plant = new BlockModPlant(data);
                vanillaCrops.add(plant);
                vanillaSeeds.add(plant.getSeed());
            } catch (Exception e) {
                LogHelper.printStackTrace(e);
            }
        }
    }

    public static void initModdedResources() {
        modCrops = new ArrayList<BlockModPlant>();
        modSeeds = new ArrayList<ItemModSeed>();
        for(String[] data:Data.modResources) {
            Block base = OreDictHelper.getOreBlockForName(data[0]);
            if(base!=null) {
                Object[] args = {data[1], new ItemStack(OreDictHelper.getNuggetForName(data[0]), 1, OreDictHelper.getNuggetMetaForName(data[0])), RequirementType.BELOW, new BlockWithMeta(OreDictHelper.getOreBlockForName(data[0]), OreDictHelper.getOreMetaForName(data[0])), 4, RenderMethod.HASHTAG};
                BlockModPlant plant;
                try {
                    plant = new BlockModPlant(args);
                    modCrops.add(plant);
                    modSeeds.add(plant.getSeed());
                } catch (Exception e) {
                    LogHelper.printStackTrace(e);
                }
            }
        }
    }
}
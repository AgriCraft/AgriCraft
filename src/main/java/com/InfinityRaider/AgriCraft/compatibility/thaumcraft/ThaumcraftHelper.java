package com.InfinityRaider.AgriCraft.compatibility.thaumcraft;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.RenderMethod;
import com.InfinityRaider.AgriCraft.api.v1.RequirementType;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class ThaumcraftHelper extends ModHelper {
    public static ArrayList<BlockModPlant> thaumcraftCrops = new ArrayList<BlockModPlant>();
    public static ArrayList<ItemModSeed> thaumcraftSeeds = new ArrayList<ItemModSeed>();

    @Override
    protected void init() {
        FMLInterModComms.sendMessage(Names.Mods.thaumcraft, "harvestClickableCrop", new ItemStack(Blocks.blockCrop, 1, Constants.MATURE));
    }

    @Override
    protected void initPlants() {
        Item thaumcraftPlant = (Item) Item.itemRegistry.getObject("Thaumcraft:blockCustomPlant");
        Item thaumcraftTaintPlant = (Item) Item.itemRegistry.getObject("Thaumcraft:ItemResource");
        Block blockTaint = (Block) Block.blockRegistry.getObject("Thaumcraft:blockTaint");
        int cinderpearlMeta = 3;
        int shimmerleafMeta = 2;
        int vishroomMeta = 5;
        int taintPlantMeta = 12;

        //cinderpearl
        BlockModPlant cropCinderpearl = null;
        try {
            cropCinderpearl = new BlockModPlant("Cinderpearl", new ItemStack(thaumcraftPlant, 1, cinderpearlMeta), new BlockWithMeta(net.minecraft.init.Blocks.sand), 3, RenderMethod.CROSSED);
        } catch (Exception e) {
            LogHelper.printStackTrace(e);
        }
        if(cropCinderpearl != null) {
            thaumcraftCrops.add(cropCinderpearl);
            thaumcraftSeeds.add(cropCinderpearl.getSeed());
        }

        //shimmerleaf
        BlockModPlant cropShimmerleaf = null;
        try {
            cropShimmerleaf = new BlockModPlant("Shimmerleaf", new ItemStack(thaumcraftPlant, 1, shimmerleafMeta), 3, RenderMethod.CROSSED);
        } catch (Exception e) {
            LogHelper.printStackTrace(e);
        }
        if(cropShimmerleaf != null) {
            Block log = (Block) Block.blockRegistry.getObject("Thaumcraft:blockMagicalLog");
            if (log != null) {
                cropShimmerleaf.getGrowthRequirement().setRequiredBlock(new BlockWithMeta(log, 1), RequirementType.NEARBY, false);
            }
            thaumcraftCrops.add(cropShimmerleaf);
            thaumcraftSeeds.add(cropShimmerleaf.getSeed());
        }

        //vishroom
        BlockModPlant cropVishroom = null;
        try {
            cropVishroom = new BlockModPlant("Vishroom", new ItemStack(thaumcraftPlant, 1, vishroomMeta), new BlockWithMeta(net.minecraft.init.Blocks.mycelium), 3, RenderMethod.CROSSED);
        } catch (Exception e) {
            LogHelper.printStackTrace(e);
        }
        if(cropVishroom != null) {
            thaumcraftCrops.add(cropVishroom);
            thaumcraftSeeds.add(cropVishroom.getSeed());
            cropVishroom.getGrowthRequirement().setBrightnessRange(0, 8);
        }

        //tainted root
        BlockModPlant cropTaintedRoot = null;
        try {
            cropTaintedRoot = new BlockModPlant("TaintedRoot", new ItemStack(thaumcraftTaintPlant, 1, taintPlantMeta), new BlockWithMeta(blockTaint), RequirementType.BELOW, new BlockWithMeta(blockTaint, 0), 4, RenderMethod.CROSSED);
        } catch (Exception e) {
            if (ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        }
        if(cropTaintedRoot != null) {
            thaumcraftCrops.add(cropTaintedRoot);
            thaumcraftSeeds.add(cropTaintedRoot.getSeed());
            cropTaintedRoot.getGrowthRequirement().setSoil(new BlockWithMeta(blockTaint, 1));
        }
    }


    @Override
    protected void postTasks() {
        Aspects.registerAspects();
    }

    @Override
    protected String modId() {
        return Names.Mods.thaumcraft;
    }
}

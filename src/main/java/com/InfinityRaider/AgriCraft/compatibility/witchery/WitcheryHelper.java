package com.InfinityRaider.AgriCraft.compatibility.witchery;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public final class WitcheryHelper extends ModHelper {
    private Item sprig;
    private Block wispyCotton;
    private Item wormwoodSeed;

    @Override
    protected void onInit() {
        LogHelper.debug("Initializing Witchery Support");

        Item seedBelladonna = (Item) Item.itemRegistry.getObject("witchery:seedsbelladonna");
        Item seedMandrake = (Item) Item.itemRegistry.getObject("witchery:seedsmandrake");
        Item seedArtichoke = (Item) Item.itemRegistry.getObject("witchery:seedsartichoke");
        Item seedSnowbell = (Item) Item.itemRegistry.getObject("witchery:seedssnowbell");
        Item seedWolfsbane = (Item) Item.itemRegistry.getObject("witchery:seedswolfsbane");
        Item seedGarlic = (Item) Item.itemRegistry.getObject("witchery:garlic");
        wormwoodSeed = (Item) Item.itemRegistry.getObject("witchery:seedswormwood");

        Item itemGeneric = (Item) Item.itemRegistry.getObject("witchery:ingredient");

        OreDictionary.registerOre("seedBelladonna", seedBelladonna);
        OreDictionary.registerOre("seedMandrake", seedMandrake);
        OreDictionary.registerOre("seedWaterArtichoke", seedArtichoke);
        OreDictionary.registerOre("seedSnowbell", seedSnowbell);
        OreDictionary.registerOre("seedWolfsbane", seedWolfsbane);
        OreDictionary.registerOre("seedWitchGarlic", seedGarlic);
        OreDictionary.registerOre("seedWormwood", wormwoodSeed);

        OreDictionary.registerOre(Names.OreDict.listAllseed, seedBelladonna);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seedMandrake);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seedArtichoke);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seedSnowbell);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seedWolfsbane);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seedGarlic);
        OreDictionary.registerOre(Names.OreDict.listAllseed, wormwoodSeed);

        OreDictionary.registerOre("cropBelladonna", new ItemStack(itemGeneric, 1, 21));
        OreDictionary.registerOre("cropMandrake", new ItemStack(itemGeneric, 1, 22));
        OreDictionary.registerOre("cropWaterArtichoke", new ItemStack(itemGeneric, 1, 69));
        OreDictionary.registerOre("cropSnowbell", new ItemStack(itemGeneric, 1, 78));
        OreDictionary.registerOre("cropSnowbell", Items.snowball);
        OreDictionary.registerOre("cropWolfsbane", new ItemStack(itemGeneric, 1, 156));
        OreDictionary.registerOre("cropWitchGarlic", new ItemStack(seedGarlic));
        OreDictionary.registerOre("cropWormwood", new ItemStack(itemGeneric, 1, 111));
    }

    @Override
    protected void initPlants() {
        try {
            CropPlantHandler.registerPlant(new CropPlantWitchery((ItemSeeds) Item.itemRegistry.getObject("witchery:seedsbelladonna")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            CropPlantHandler.registerPlant(new CropPlantMandrake());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            CropPlantHandler.registerPlant(new CropPlantWitchery((ItemSeeds) Item.itemRegistry.getObject("witchery:seedsartichoke")));
            CropPlantHandler.getGrowthRequirement((Item) Item.itemRegistry.getObject("witchery:seedsartichoke"), 0).setSoil(new BlockWithMeta(Blocks.blockWaterPadFull));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            CropPlantHandler.registerPlant(new CropPlantWitchery((ItemSeeds) Item.itemRegistry.getObject("witchery:seedssnowbell")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            CropPlantHandler.registerPlant(new CropPlantWolfsbane());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            CropPlantHandler.registerPlant(new CropPlantWitchery((ItemSeeds) Item.itemRegistry.getObject("witchery:garlic")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            CropPlantHandler.registerPlant(new CropPlantWitchery((ItemSeeds) Item.itemRegistry.getObject("witchery:seedswormwood"), 4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onPostInit() {
        sprig = (Item) Item.itemRegistry.getObject("witchery:mutator");
        wispyCotton = (Block) Block.blockRegistry.getObject("witchery:somniancotton");
    }

    protected List<Item> getTools() {
        ArrayList<Item> tools = new ArrayList<Item>();
        tools.add(sprig);
        return tools;
    }

    protected boolean useTool(World world, int x, int y, int z, EntityPlayer player, ItemStack stack, BlockCrop block, TileEntityCrop crop) {
        if(stack.getItem() == sprig) {
            return tryCreateWormWood(world, x, y, z, player, stack, crop);
        }
        return false;
    }

    private boolean tryCreateWormWood(World world, int x, int y, int z, EntityPlayer player, ItemStack sprig, TileEntityCrop crop) {
        //check for wheat and check if mature
        if(!crop.hasPlant() || !crop.isMature() || crop.getPlant().getSeed().getItem() != Items.wheat_seeds) {
            return false;
        }
        //check for wispy cotton
        int cottonCount = 0;
        cottonCount = world.getBlock(x+1, y, z) == wispyCotton?cottonCount+1:cottonCount;
        cottonCount = world.getBlock(x-1, y, z) == wispyCotton?cottonCount+1:cottonCount;
        cottonCount = world.getBlock(x, y, z+1) == wispyCotton?cottonCount+1:cottonCount;
        cottonCount = world.getBlock(x, y, z-1) == wispyCotton?cottonCount+1:cottonCount;
        if(cottonCount<4) {
            return false;
        }
        //check for water
        int waterCount = 0;
        waterCount = world.getBlock(x+1, y-1, z+1) == net.minecraft.init.Blocks.water?waterCount+1:waterCount;
        waterCount = world.getBlock(x+1, y-1, z-1) == net.minecraft.init.Blocks.water?waterCount+1:waterCount;
        waterCount = world.getBlock(x-1, y-1, z+1) == net.minecraft.init.Blocks.water?waterCount+1:waterCount;
        waterCount = world.getBlock(x-1, y-1, z-1) == net.minecraft.init.Blocks.water?waterCount+1:waterCount;
        if(waterCount<4) {
            return false;
        }
        //create wormwood
        world.setBlockToAir(x+1, y, z);
        world.setBlockToAir(x-1, y, z);
        world.setBlockToAir(x, y, z+1);
        world.setBlockToAir(x, y, z-1);
        crop.clearPlant();
        crop.setPlant(1, 1, 1, false, CropPlantHandler.getPlantFromStack(new ItemStack(wormwoodSeed, 0)));
        if(!player.capabilities.isCreativeMode) {
            sprig.damageItem(1, player);
        }
        return true;
    }


    @Override
    protected String modId() {
        return Names.Mods.witchery;
    }
}

package com.InfinityRaider.AgriCraft.compatibility.witchery;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class WitcheryHelper extends ModHelper {
    @Override
    protected void init() {
        LogHelper.debug("Initializing Witchery Support");

        Item seedBelladonna = (Item) Item.itemRegistry.getObject("witchery:seedsbelladonna");
        Item seedMandrake = (Item) Item.itemRegistry.getObject("witchery:seedsmandrake");
        Item seedArtichoke = (Item) Item.itemRegistry.getObject("witchery:seedsartichoke");
        Item seedSnowbell = (Item) Item.itemRegistry.getObject("witchery:seedssnowbell");
        Item seedWolfsbane = (Item) Item.itemRegistry.getObject("witchery:seedswolfsbane");
        Item seedGarlic = (Item) Item.itemRegistry.getObject("witchery:garlic");
        Item seedWormwood = (Item) Item.itemRegistry.getObject("witchery:seedswormwood");

        Item itemGeneric = (Item) Item.itemRegistry.getObject("witchery:ingredient");

        OreDictionary.registerOre("seedBelladonna", seedBelladonna);
        OreDictionary.registerOre("seedMandrake", seedMandrake);
        OreDictionary.registerOre("seedWaterArtichoke", seedArtichoke);
        OreDictionary.registerOre("seedSnowbell", seedSnowbell);
        OreDictionary.registerOre("seedWolfsbane", seedWolfsbane);
        OreDictionary.registerOre("seedWitchGarlic", seedGarlic);
        OreDictionary.registerOre("seedWormwood", seedWormwood);

        OreDictionary.registerOre(Names.OreDict.listAllseed, seedBelladonna);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seedMandrake);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seedArtichoke);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seedSnowbell);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seedWolfsbane);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seedGarlic);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seedWormwood);

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
            GrowthRequirementHandler.getGrowthRequirement((Item) Item.itemRegistry.getObject("witchery:seedsartichoke"), 0).setSoil(new BlockWithMeta(Blocks.blockWaterPadFull));
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
            CropPlantHandler.registerPlant(new CropPlantWitchery((ItemSeeds) Item.itemRegistry.getObject("witchery:seedswormwood")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String modId() {
        return Names.Mods.witchery;
    }
}

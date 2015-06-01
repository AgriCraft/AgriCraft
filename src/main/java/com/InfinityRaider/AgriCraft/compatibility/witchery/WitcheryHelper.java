package com.InfinityRaider.AgriCraft.compatibility.witchery;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.emoniph.witchery.Witchery;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class WitcheryHelper extends ModHelper {
    @Override
    protected void init() {
        LogHelper.debug("Initializing Witchery Support");

        OreDictionary.registerOre("seedBelladonna", Witchery.Items.SEEDS_BELLADONNA);
        OreDictionary.registerOre("seedMandrake", Witchery.Items.SEEDS_MANDRAKE);
        OreDictionary.registerOre("seedWaterArtichoke", Witchery.Items.SEEDS_ARTICHOKE);
        OreDictionary.registerOre("seedSnowbell", Witchery.Items.SEEDS_SNOWBELL);
        OreDictionary.registerOre("seedWolfsbane", Witchery.Items.SEEDS_WOLFSBANE);
        OreDictionary.registerOre("seedWitchGarlic", Witchery.Items.SEEDS_GARLIC);
        OreDictionary.registerOre("seedWormwood", Witchery.Items.SEEDS_WORMWOOD);

        OreDictionary.registerOre(Names.OreDict.listAllseed, Witchery.Items.SEEDS_BELLADONNA);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Witchery.Items.SEEDS_MANDRAKE);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Witchery.Items.SEEDS_ARTICHOKE);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Witchery.Items.SEEDS_SNOWBELL);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Witchery.Items.SEEDS_WOLFSBANE);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Witchery.Items.SEEDS_GARLIC);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Witchery.Items.SEEDS_WORMWOOD);

        OreDictionary.registerOre("cropBelladonna", new ItemStack(Witchery.Items.GENERIC, 1, 21));
        OreDictionary.registerOre("cropMandrake", new ItemStack(Witchery.Items.GENERIC, 1, 22));
        OreDictionary.registerOre("cropWaterArtichoke", new ItemStack(Witchery.Items.GENERIC, 1, 69));
        OreDictionary.registerOre("cropSnowbell", new ItemStack(Witchery.Items.GENERIC, 1, 78));
        OreDictionary.registerOre("cropSnowbell", Items.snowball);
        OreDictionary.registerOre("cropWolfsbane", new ItemStack(Witchery.Items.GENERIC, 1, 156));
        OreDictionary.registerOre("cropWitchGarlic", new ItemStack(Witchery.Items.SEEDS_GARLIC));
        OreDictionary.registerOre("cropWormwood", new ItemStack(Witchery.Items.GENERIC, 1, 111));

    }

    @Override
    protected void initPlants() {
        try {
            CropPlantHandler.registerPlant(new CropPlantWitchery((ItemSeeds) Witchery.Items.SEEDS_BELLADONNA));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
        CropPlantHandler.registerPlant(new CropPlantMandrake());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
        CropPlantHandler.registerPlant(new CropPlantWitchery((ItemSeeds) Witchery.Items.SEEDS_ARTICHOKE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
        CropPlantHandler.registerPlant(new CropPlantWitchery((ItemSeeds) Witchery.Items.SEEDS_SNOWBELL));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            CropPlantHandler.registerPlant(new CropPlantWolfsbane());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            CropPlantHandler.registerPlant(new CropPlantWitchery((ItemSeeds) Witchery.Items.SEEDS_GARLIC));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            CropPlantHandler.registerPlant(new CropPlantWitchery((ItemSeeds) Witchery.Items.SEEDS_WORMWOOD));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String modId() {
        return Names.Mods.witchery;
    }
}

package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.LoadedMods;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Data;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import vazkii.botania.common.item.ModItems;

import java.util.ArrayList;

public class Crops {
    //AgriCraft crops
    public static ArrayList<BlockModPlant> defaultCrops;
    public static ArrayList<ItemModSeed> defaultSeeds;

    //Botania crops
    public static ArrayList<BlockModPlant> botaniaCrops;
    public static ArrayList<ItemModSeed> botaniaSeeds;

    public static void initDefaults() {
        defaultCrops = new ArrayList<BlockModPlant>();
        defaultSeeds = new ArrayList<ItemModSeed>();
        for(Object[] data: Data.defaults) {
            String name =(String) data[0];
            //create plant
            BlockModPlant plant = new BlockModPlant(data);
            defaultCrops.add(plant);
            RegisterHelper.registerCrop(plant, name);
            //create seed
            ItemModSeed seed = new ItemModSeed(plant, "agricraft_journal."+Character.toLowerCase(name.charAt(0))+name.substring(1));
            defaultSeeds.add(seed);
            RegisterHelper.registerSeed(seed, plant);
        }
        LogHelper.info("Crops registered");
    }

    public static void initBotaniaCrops() {
        if(LoadedMods.botania && ConfigurationHandler.integration_Botania) {
            botaniaCrops = new ArrayList<BlockModPlant>();
            botaniaSeeds = new ArrayList<ItemModSeed>();
            for(int i=0;i<16;i++) {
                Object[] args = {Data.botania[i], ModItems.petal, i, null, null, 0, 3, 1};
                String name =(String) args[0];
                //create plant
                BlockModPlant plant = new BlockModPlant(args);
                botaniaCrops.add(plant);
                RegisterHelper.registerCrop(plant, name);
                //create seed
                ItemModSeed seed = new ItemModSeed(plant, "agricraft_journal."+Character.toLowerCase(name.charAt(0))+name.substring(1));
                botaniaSeeds.add(seed);
                RegisterHelper.registerSeed(seed, plant);
            }
            LogHelper.info("Botania crops registered");
        }
    }
}
package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Data;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;

import java.util.ArrayList;

public class Crops {
    public static ArrayList<BlockModPlant> crops;
    public static ArrayList<ItemModSeed> seeds;

    public static void init() {
        crops = new ArrayList<BlockModPlant>();
        seeds = new ArrayList<ItemModSeed>();
        for(Object[] data: Data.defaults) {
            String name =(String) data[0];
            //create plant
            BlockModPlant plant = new BlockModPlant(data);
            crops.add(plant);
            RegisterHelper.registerCrop(plant, name);
            //create seed
            ItemModSeed seed = new ItemModSeed(plant, "agricraft_journal."+Character.toLowerCase(name.charAt(0))+name.substring(1));
            seeds.add(seed);
            RegisterHelper.registerSeed(seed, plant);
        }
        LogHelper.info("Crops registered");
    }
}
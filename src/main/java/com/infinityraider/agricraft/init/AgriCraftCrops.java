package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.blocks.BlockModPlant;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import com.infinityraider.agricraft.items.ItemModSeed;
import com.infinityraider.agricraft.reference.Data;
import com.infinityraider.agricraft.utility.LogHelper;

import java.util.ArrayList;

public class AgriCraftCrops {
    public static ArrayList<BlockModPlant> crops;
    public static ArrayList<ItemModSeed> seeds;

    public static void init() {
        crops = new ArrayList<>();
        seeds = new ArrayList<>();
        for(Object[] data: Data.defaults) {
            BlockModPlant plant;
            try {
                plant = new BlockModPlant(data);
            } catch (Exception e) {
                if(AgriCraftConfig.debug) {
                    LogHelper.printStackTrace(e);
                }
                return;
            }
            crops.add(plant);
            seeds.add(plant.getSeed());
        }
        LogHelper.info("Crops registered");
    }
}
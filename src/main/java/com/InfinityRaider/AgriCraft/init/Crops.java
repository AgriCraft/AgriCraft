package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Data;
import com.InfinityRaider.AgriCraft.utility.LogHelper;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class Crops {
    public static ArrayList<BlockModPlant> crops;
    public static ArrayList<ItemStack> seeds;

    public static void init() {
        crops = new ArrayList<>();
        seeds = new ArrayList<>();
        for(Object[] data: Data.defaults) {
            BlockModPlant plant;
            try {
                plant = new BlockModPlant(data);
            } catch (Exception e) {
                if(ConfigurationHandler.debug) {
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
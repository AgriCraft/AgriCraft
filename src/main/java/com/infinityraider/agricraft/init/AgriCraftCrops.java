package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.blocks.BlockModPlant;
import com.infinityraider.agricraft.items.ItemModSeed;
import com.agricraft.agricore.core.AgriCore;

import java.util.ArrayList;

public class AgriCraftCrops {
    public static ArrayList<BlockModPlant> crops;
    public static ArrayList<ItemModSeed> seeds;

    public static void init() {
        crops = new ArrayList<>();
        seeds = new ArrayList<>();
        AgriCore.getLogger("AgriCraft").info("Crop Handler Initialized!");
    }
}
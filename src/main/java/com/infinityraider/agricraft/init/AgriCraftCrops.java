package com.infinityraider.agricraft.init;

import com.agricraft.agricore.core.AgriCore;

import java.util.ArrayList;
import com.infinityraider.agricraft.api.v3.core.IAgriPlant;

public class AgriCraftCrops {
    public static ArrayList<IAgriPlant> crops;

    public static void init() {
        crops = new ArrayList<>();
        AgriCore.getLogger("AgriCraft").info("Crop Handler Initialized!");
    }
}
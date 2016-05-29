package com.infinityraider.agricraft.init;

import com.agricraft.agricore.core.AgriCore;

import java.util.ArrayList;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;

public class AgriCraftCrops {
    public static ArrayList<IAgriCraftPlant> crops;

    public static void init() {
        crops = new ArrayList<>();
        AgriCore.getLogger("AgriCraft").info("Crop Handler Initialized!");
    }
}
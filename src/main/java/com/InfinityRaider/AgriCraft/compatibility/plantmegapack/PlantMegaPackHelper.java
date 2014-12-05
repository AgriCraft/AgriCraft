package com.InfinityRaider.AgriCraft.compatibility.plantmegapack;

import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;

public class PlantMegaPackHelper {
    public static int getTextureIndex(ItemSeeds seed, int meta) {
        if(Item.itemRegistry.getNameForObject(seed).equalsIgnoreCase("plantmegapack:seedCorn")) {
            return meta;
        }
        return (int) Math.ceil(((float)meta)/2.0F);
    }
}

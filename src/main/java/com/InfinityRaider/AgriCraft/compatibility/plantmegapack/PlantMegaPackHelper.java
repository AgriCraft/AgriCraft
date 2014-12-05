package com.InfinityRaider.AgriCraft.compatibility.plantmegapack;

public class PlantMegaPackHelper {
    public static int getTextureIndex(int meta) {
        return (int) Math.ceil(((float)meta)/2.0F);
    }
}

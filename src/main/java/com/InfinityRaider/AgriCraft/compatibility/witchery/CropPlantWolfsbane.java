package com.InfinityRaider.AgriCraft.compatibility.witchery;

import net.minecraft.item.ItemSeeds;

public class CropPlantWolfsbane extends CropPlantWitchery {
    public CropPlantWolfsbane(ItemSeeds seed) {
        super(seed);
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }
}

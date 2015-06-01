package com.InfinityRaider.AgriCraft.compatibility.witchery;

import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;

public class CropPlantWolfsbane extends CropPlantWitchery {
    public CropPlantWolfsbane() {
        super((ItemSeeds) Item.itemRegistry.getObject("witchery:seedswolfsbane"));
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }
}

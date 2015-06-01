package com.InfinityRaider.AgriCraft.compatibility.witchery;

import com.emoniph.witchery.Witchery;
import net.minecraft.item.ItemSeeds;

public class CropPlantWolfsbane extends CropPlantWitchery {
    public CropPlantWolfsbane() {
        super((ItemSeeds)Witchery.Items.SEEDS_WOLFSBANE);
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }
}

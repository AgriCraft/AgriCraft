package com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant;

import net.minecraft.item.ItemSeeds;

public class CropPlantOreDict extends CropPlantGeneric {
    public CropPlantOreDict(ItemSeeds seed) {
        super(seed);
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }

    @Override
    public boolean renderAsFlower() {
        return false;
    }

    @Override
    public String getInformation() {
        return "agricraft_journal."+getSeed().getUnlocalizedName();
    }
}

package com.InfinityRaider.AgriCraft.compatibility.adventofascension;

import net.minecraft.item.ItemSeeds;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantGeneric;

public class CropPlantAoA extends CropPlantGeneric {
    private final String name;

    public CropPlantAoA(ItemSeeds seed, String name) {
        super(seed);
        this.name = name;
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }

    @Override
    public boolean renderAsFlower() {
        return true;
    }

    @Override
    public String getInformation() {
        return "agricraft_journal.aoa_"+name;
    }
}

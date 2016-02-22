package com.InfinityRaider.AgriCraft.compatibility.harderwildlife;

import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantGeneric;
import net.minecraft.item.ItemSeeds;

public class CropPlantHarderWildLife extends CropPlantGeneric {
    private final String name;

    public CropPlantHarderWildLife(ItemSeeds seed, String name) {
        super(seed);
        this.name = name;
    }

    @Override
    protected boolean modSpecificFruits() {
        return false;
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
        return "agricraft_journal.harderWildLife_" + name;
    }
}

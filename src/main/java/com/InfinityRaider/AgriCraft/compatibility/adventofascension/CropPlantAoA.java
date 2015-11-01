package com.InfinityRaider.AgriCraft.compatibility.adventofascension;

import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantGeneric;
import net.minecraft.item.ItemSeeds;

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

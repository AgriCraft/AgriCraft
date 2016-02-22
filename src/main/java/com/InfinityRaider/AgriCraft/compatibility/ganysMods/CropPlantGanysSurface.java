package com.InfinityRaider.AgriCraft.compatibility.ganysMods;

import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantGeneric;
import net.minecraft.item.ItemSeeds;

public class CropPlantGanysSurface extends CropPlantGeneric {
    private final String name;

    protected CropPlantGanysSurface(ItemSeeds seed, String name) {
        super(seed);
        this.name = name;
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
        return "agricraft_journal." + name;
    }
}

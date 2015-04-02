package com.InfinityRaider.AgriCraft.compatibility.harvestcraft;

import com.InfinityRaider.AgriCraft.farming.CropPlantGeneric;
import net.minecraft.item.ItemSeeds;

public class CropPlantHarvestCraft extends CropPlantGeneric {

    public CropPlantHarvestCraft(ItemSeeds seed) {
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
        return "agricraft_journal.hc_"+getSeed().getUnlocalizedName();
    }
}

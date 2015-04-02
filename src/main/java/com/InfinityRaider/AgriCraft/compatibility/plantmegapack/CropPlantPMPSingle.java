package com.InfinityRaider.AgriCraft.compatibility.plantmegapack;

import com.InfinityRaider.AgriCraft.farming.CropPlantGeneric;
import net.minecraft.item.ItemSeeds;

public class CropPlantPMPSingle extends CropPlantGeneric {
    public CropPlantPMPSingle(ItemSeeds seed) {
        super(seed);
    }

    @Override
    public int transformMeta(int growthStage) {
        return (int) Math.ceil(((float)growthStage)/2.0F);
    }

    @Override
    public boolean renderAsFlower() {
        return false;
    }

    @Override
    public String getInformation() {
        return "agricraft_journal.pmp_"+getSeed().getUnlocalizedName();
    }
}

package com.InfinityRaider.AgriCraft.compatibility.witchery;

import com.InfinityRaider.AgriCraft.farming.CropPlantGeneric;
import net.minecraft.item.ItemSeeds;

public class CropPlantWitchery extends CropPlantGeneric {
    public CropPlantWitchery(ItemSeeds seed) {
        super(seed);
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage==7?4:(int) (((float) growthStage)/2.0F);
    }

    @Override
    public boolean renderAsFlower() {
        return false;
    }

    @Override
    public String getInformation() {
        return "agricraft_journal.wi_"+getSeed().getUnlocalizedName();
    }
}

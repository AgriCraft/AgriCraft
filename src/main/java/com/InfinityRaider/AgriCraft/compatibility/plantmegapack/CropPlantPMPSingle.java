package com.InfinityRaider.AgriCraft.compatibility.plantmegapack;

import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantGeneric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        return "agricraft_journal.pmp_"+getSeed().getUnlocalizedName();
    }
}

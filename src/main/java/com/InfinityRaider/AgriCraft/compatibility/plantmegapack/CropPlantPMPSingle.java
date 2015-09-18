package com.InfinityRaider.AgriCraft.compatibility.plantmegapack;

import net.minecraft.item.ItemSeeds;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantGeneric;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CropPlantPMPSingle extends CropPlantGeneric {
    public CropPlantPMPSingle(ItemSeeds seed) {
        super(seed);
    }

    @Override
    public int transformMeta(int growthStage) {
        return (int) Math.ceil(((float)growthStage)/2.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        String name = getSeed().getUnlocalizedName();
        name = name.substring(name.indexOf("seed")+"seed".length());
        return "agricraft_journal.pmp_"+name;
    }
}

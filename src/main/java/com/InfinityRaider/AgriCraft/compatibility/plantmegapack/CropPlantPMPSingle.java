package com.InfinityRaider.AgriCraft.compatibility.plantmegapack;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.AgriCraftPlantGeneric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemSeeds;

public class CropPlantPMPSingle extends AgriCraftPlantGeneric {
    public CropPlantPMPSingle(ItemSeeds seed) {
        super(seed, 2);
    }

    @Override
    public int transformMeta(int growthStage) {
        return (int) Math.ceil((growthStage)/2.0F);
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

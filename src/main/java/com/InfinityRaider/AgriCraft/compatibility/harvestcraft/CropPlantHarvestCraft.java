package com.InfinityRaider.AgriCraft.compatibility.harvestcraft;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantGeneric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        String name = getSeed().getUnlocalizedName();
        int start = Math.max(0, name.indexOf('.')+1);
        int stop = name.indexOf("seedItem");
        name = name.substring(start, stop<0?name.length():stop);
        return "agricraft_journal.hc_"+Character.toUpperCase(name.charAt(0))+name.substring(1);
    }
}

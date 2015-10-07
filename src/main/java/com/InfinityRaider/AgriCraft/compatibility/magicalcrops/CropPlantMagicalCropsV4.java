package com.InfinityRaider.AgriCraft.compatibility.magicalcrops;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.AgriCraftPlantGeneric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;

public class CropPlantMagicalCropsV4 extends AgriCraftPlantGeneric {
    public CropPlantMagicalCropsV4(ItemSeeds seed, Item drop) {
        super(seed, 4);
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
        name = name.substring(name.indexOf("Seeds")+"Seeds".length());
        return "agricraft_journal.mc_"+name;
    }
}

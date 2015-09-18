package com.InfinityRaider.AgriCraft.compatibility.witchery;

import net.minecraft.item.ItemSeeds;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantGeneric;
import com.emoniph.witchery.Witchery;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CropPlantWitchery extends CropPlantGeneric {
    private final int tier;

    public CropPlantWitchery(ItemSeeds seed) {
        this(seed, 3);
    }

    public CropPlantWitchery(ItemSeeds seed, int tier) {
        super(seed);
        this.tier = tier;
    }

    @Override
    public int tier() {
        return tier;
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage==7?4:(int) (((float) growthStage)/2.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return getSeed().getItem()!= Witchery.Items.SEEDS_BELLADONNA;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        String name = getSeed().getUnlocalizedName();
        name = name.substring(name.indexOf(':')+1);
        int index = name.indexOf("seeds");
        name = index<0?name:name.substring(index+"seeds".length());
        return "agricraft_journal.wi_"+Character.toUpperCase(name.charAt(0))+name.substring(1);
    }
}

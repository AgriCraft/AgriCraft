package com.InfinityRaider.AgriCraft.compatibility.magicalcrops;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantGeneric;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CropPlantMagicalCropsV4 extends CropPlantGeneric {
    private int meta = 0;
    private Item drop;

    public CropPlantMagicalCropsV4(ItemSeeds seed, Item drop) {
        super(seed);
        this.drop = drop;
        //getDropMeta();
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }

    private boolean highTier() {
        //return getPlant() instanceof BlockMagicalCrops;
        return true;
    }

    @Override
    public int tier() {
        return highTier()?3:4;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        if(highTier()) {
            list.add(new ItemStack(drop, 1, meta));
        } else {
            list.addAll(OreDictHelper.getFruitsFromOreDict(getSeed()));
        }
        return list;
    }

    @Override
    public boolean canBonemeal() {
        return !highTier();
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

package com.InfinityRaider.AgriCraft.compatibility.weeeflowers;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantGeneric;
import com.pam.weeeflowers.BlockPamFlowerCrop;
import com.pam.weeeflowers.weeeflowers;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class CropPlantWeeeFlower extends CropPlantGeneric {
    public CropPlantWeeeFlower(ItemSeeds seed) {
        super(seed);
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> fruits = new ArrayList<ItemStack>();
        BlockPamFlowerCrop crop = (BlockPamFlowerCrop) ((ItemSeeds) getSeed().getItem()).getPlant(null, 0, 0, 0);
        fruits.add(new ItemStack(weeeflowers.pamFlower, 1, crop.func_149692_a(7)));
        return fruits;
    }

    @Override
    public boolean renderAsFlower() {
        return false;
    }

    @Override
    public String getInformation() {
        String name = getSeed().getUnlocalizedName();
        int start = name.indexOf('.')+1;
        int stop = name.indexOf("seedItem");
        name = name.substring(start, stop);
        return "agricraft_journal.wf_"+Character.toUpperCase(name.charAt(0))+name.substring(1);
    }
}

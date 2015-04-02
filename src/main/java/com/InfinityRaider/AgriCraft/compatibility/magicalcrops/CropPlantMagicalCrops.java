package com.InfinityRaider.AgriCraft.compatibility.magicalcrops;

import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantGeneric;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import com.mark719.magicalcrops.crops.BlockMagicalCrops;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class CropPlantMagicalCrops extends CropPlantGeneric {
    public CropPlantMagicalCrops(ItemSeeds seed) {
        super(seed);
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }

    private boolean highTier() {
        return getPlant() instanceof BlockMagicalCrops;
    }

    private Block getPlant() {
        return ((ItemSeeds) getSeed().getItem()).getPlant(null, 0, 0, 0);
    }

    @Override
    public int tier() {
        return highTier()?3:4;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        Block plant = getPlant();
        if(highTier()) {
            BlockMagicalCrops magicPlant = (BlockMagicalCrops) plant;
            list.add(new ItemStack(magicPlant.func_149650_a(7, null, 0)));
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
        return "agricraft_journal.mc_"+getSeed().getUnlocalizedName();
    }
}

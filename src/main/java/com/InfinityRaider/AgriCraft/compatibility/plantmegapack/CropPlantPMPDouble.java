package com.InfinityRaider.AgriCraft.compatibility.plantmegapack;

import com.InfinityRaider.AgriCraft.farming.CropPlantDouble;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CropPlantPMPDouble extends CropPlantDouble {
    @Override
    public int getTier() {
        return 0;
    }

    @Override
    public ItemStack getSeed() {
        return null;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        return null;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return null;
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, int strength, Random rand) {
        return null;
    }

    @Override
    public boolean canBonemeal() {
        return false;
    }

    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public IIcon getSeedIcon() {
        return null;
    }

    @Override
    public IIcon getPlantIcon(int growthStage) {
        return null;
    }

    @Override
    public boolean renderAsFlower() {
        return false;
    }

    @Override
    public String getInformation() {
        return null;
    }
}

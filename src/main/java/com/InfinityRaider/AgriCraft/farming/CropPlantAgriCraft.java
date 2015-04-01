package com.InfinityRaider.AgriCraft.farming;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CropPlantAgriCraft extends CropPlant {
    IAgriCraftPlant plant;

    public CropPlantAgriCraft(IAgriCraftPlant plant) {
       this.plant = plant;
    }

    @Override
    public int getTier() {
        return plant.getSeed().tier();
    }

    @Override
    public ItemStack getSeed() {
        return plant.getSeedStack(1);
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        return plant.getAllFruits();
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return plant.getRandomFruit(rand);
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount =  (int) (Math.ceil((gain + 0.00) / 3));
        return plant.getFruit(amount, rand);
    }

    @Override
    public boolean canBonemeal() {
        return getTier()<4;
    }

    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return false;
    }

    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        return plant.getGrowthRequirement().canGrow(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getSeedIcon() {
        return plant.getSeed().getIcon(getSeed());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        return plant.getIcon(growthStage);
    }

    @Override
    public boolean renderAsFlower() {
        return plant.renderAsFlower();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        return plant.getSeed().getInformation();
    }
}

package com.InfinityRaider.AgriCraft.farming.cropplant;

import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftPlant;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.reference.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

/**
 * Implementation of the CropPlant class for an IAgriCraftPlant object
 */
public class CropPlantAgriCraft extends CropPlant {
    IAgriCraftPlant plant;

    public CropPlantAgriCraft(IAgriCraftPlant plant) {
        super();
        this.plant = plant;
        this.setTier(plant.getSeed().tier());
        this.setGrowthRequirement(plant.getGrowthRequirement());
        this.setSpreadChance(100/getTier());
    }

    @Override
    public int tier() {
        return 1;
    }

    @Override
    public ItemStack getSeed() {
        return plant.getSeedStack(1);
    }

    @Override
    public Block getBlock() {
        return plant.getBlock();
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
    protected IGrowthRequirement initGrowthRequirement() {
        return null;
    }

    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return Constants.UNIT*13;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        return plant.getIcon(growthStage);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return plant.renderAsFlower();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        return plant.getSeed().getInformation();
    }
}

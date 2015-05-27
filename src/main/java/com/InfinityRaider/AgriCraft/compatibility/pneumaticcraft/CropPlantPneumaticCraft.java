package com.InfinityRaider.AgriCraft.compatibility.pneumaticcraft;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.reference.Constants;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CropPlantPneumaticCraft extends CropPlant {
    private int meta;
    private Block plant;
    private static final Item seed = (Item) Item.itemRegistry.getObject("PneumaticCraft:plasticPlant");

    public CropPlantPneumaticCraft(int meta, Block plant) {
        this.meta = meta;
        this.plant = plant;
    }

    @Override
    public int tier() {
        return 3;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(seed, 1, meta);
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(new ItemStack(seed, 1, meta));
        return list;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return new ItemStack(seed, 1, meta);
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount = (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        while(amount>0) {
            list.add(new ItemStack(seed, 1, meta));
            amount--;
        }
        return list;
    }

    @Override
    public boolean canBonemeal() {
        return true;
    }

    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return true;
    }

    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public float getHeight(int meta) {
        return Constants.unit*13;
    }

    @Override
    public IIcon getPlantIcon(int growthStage) {
        return plant.getIcon(0, Math.max(0, growthStage - 1));
    }

    @Override
    public boolean renderAsFlower() {
        return true;
    }

    @Override
    public String getInformation() {
        return "agricraft.journal_PC.plant"+meta;
    }
}

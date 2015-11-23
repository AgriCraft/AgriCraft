package com.InfinityRaider.AgriCraft.compatibility.resourcefulcrops;

import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.reference.Constants;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CropPlantResourcefulCrops extends CropPlant {
    private static ResourcefulCropsAPIwrapper api;

    private final int meta;
    private final int tier;
    private final ArrayList<ItemStack> fruits;

    protected CropPlantResourcefulCrops(int meta) {
        if(api==null) {
            api = ResourcefulCropsAPIwrapper.getInstance();
        }
        this.meta = meta;
        this.fruits = api.getAllFruits(meta);
        this.tier = api.getTier(meta);
    }

    @Override
    public int tier() {
        return tier;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(api.getSeed(), 1, meta);
    }

    @Override
    public Block getBlock() {
        return api.getPlantBlock();
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        return new ArrayList<ItemStack>(fruits);
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        if(fruits.size()==0) {
            return null;
        }
        return fruits.get(rand.nextInt(fruits.size())).copy();
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        if(fruits.size()>0) {
            int amount = (int) (Math.ceil((gain + 0.00) / 3));
            while (amount > 0) {
                list.add(getRandomFruit(rand));
                amount--;
            }
        }
        return list;
    }

    @Override
    public boolean canBonemeal() {
        return getTier()<4;
    }

    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return true;
    }

    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        return GrowthRequirementHandler.getGrowthRequirement(api.getSeed(), meta).canGrow(world, x, y, z);
    }

    @Override
    public float getHeight(int meta) {
        return Constants.UNIT*13;
    }

    @Override
    public IIcon getPlantIcon(int growthStage) {
        return getBlock().getIcon(0, growthStage);
    }

    @Override
    public boolean renderAsFlower() {
        return false;
    }

    @Override
    public String getInformation() {
        return "";
    }
}

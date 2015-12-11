package com.InfinityRaider.AgriCraft.compatibility.lordoftherings;

import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CropPlantLotR extends CropPlant {
    private final Item seed;
    private final Item fruit;
    private final Block plant;
    private final boolean crossed;

    public CropPlantLotR(Item seed, Item fruit, Block plant, boolean crossed) {
        this.seed = seed;
        this.fruit = fruit;
        this.plant = plant;
        this.crossed = crossed;
    }

    @Override
    public int tier() {
        return 2;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(seed);
    }

    @Override
    public Block getBlock() {
        return plant;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(new ItemStack(fruit));
        return list;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return new ItemStack(fruit);
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount = (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        while(amount>0) {
            list.add(getRandomFruit(rand));
            amount--;
        }
        return list;
    }

    @Override
    public boolean canBonemeal() {
        return true;
    }

    @Override
    protected IGrowthRequirement initGrowthRequirement() {
        return GrowthRequirementHandler.getNewBuilder().build();
    }

    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return true;
    }
    @Override
    public float getHeight(int meta) {
        return Constants.UNIT * 13;
    }

    @Override
    public IIcon getPlantIcon(int growthStage) {
        return plant.getIcon(0, growthStage);
    }

    @Override
    public boolean renderAsFlower() {
        return crossed;
    }

    @Override
    public String getInformation() {
        String name = seed.getUnlocalizedName();
        int index = name.indexOf(":");
        name = index>0?name.substring(index+1):name;
        return "agricraft_journal.lotr_"+name;
    }
}

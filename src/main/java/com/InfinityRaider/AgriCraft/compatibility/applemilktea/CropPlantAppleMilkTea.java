package com.InfinityRaider.AgriCraft.compatibility.applemilktea;

import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CropPlantAppleMilkTea extends CropPlant {
    Item seed;
    int seedMeta;

    Item fruit;
    int fruitMeta;

    Block plant;

    public CropPlantAppleMilkTea(ItemStack seed, ItemStack fruit, Block plant) {
        this.seed = seed.getItem();
        this.seedMeta = seed.getItemDamage();
        this.fruit = fruit.getItem();
        this.fruitMeta = fruit.getItemDamage();
        this.plant = plant;
    }

    @Override
    public int tier() {
        return 2;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(seed, 1, seedMeta);
    }

    @Override
    public Block getBlock() {
        return plant;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> fruits = new ArrayList<ItemStack>();
        fruits.add(new ItemStack(fruit, 1, fruitMeta));
        return fruits;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return new ItemStack(fruit, 1, fruitMeta);
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

    @SideOnly(Side.CLIENT)
    @Override
    public float getHeight(int meta) {
        return Constants.UNIT*13;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getPlantIcon(int growthStage) {
        int meta = 1;
        if(growthStage==0 || growthStage==1) {
            meta = 0;
        } else if(growthStage==5 || growthStage==6) {
            meta = 2;
        } else if(growthStage==7) {
            meta = 3;
        }
        return plant.getIcon(0, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean renderAsFlower() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getInformation() {
        return "agricraft.journal_AMT."+seed.getUnlocalizedName()+":"+seedMeta;
    }
}

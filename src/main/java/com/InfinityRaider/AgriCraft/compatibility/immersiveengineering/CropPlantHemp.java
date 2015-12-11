package com.InfinityRaider.AgriCraft.compatibility.immersiveengineering;

import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantTall;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.ArrayList;
import java.util.Random;

public class CropPlantHemp extends CropPlantTall {
    Block plant = (Block) Block.blockRegistry.getObject("ImmersiveEngineering:hemp");
    Item seed = (Item) Item.itemRegistry.getObject("ImmersiveEngineering:seed");
    Item fruit = (Item) Item.itemRegistry.getObject("ImmersiveEngineering:material");

    @Override
    public int maxMetaBottomBlock() {
        return 6;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getBottomIcon(int growthStage) {
        if(growthStage<=maxMetaBottomBlock()) {
            return getPlantIcon(growthStage);
        } else {
            return plant.getIcon(0, 4);
        }
    }

    @Override
    public int tier() {
        return 3;
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
        ArrayList<ItemStack> fruits = new ArrayList<ItemStack>();
        fruits.add(new ItemStack(fruit, 1, 3));
        return fruits;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return new ItemStack(fruit, 1, 3);
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

    @SideOnly(Side.CLIENT)
    @Override
    public float getHeight(int meta) {
        return meta<=maxMetaBottomBlock()? Constants.UNIT*13:Constants.UNIT*32;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getPlantIcon(int growthStage) {
        if(growthStage>maxMetaBottomBlock()) {return plant.getIcon(0, 5);}
        if(growthStage>4) {return plant.getIcon(0, 3);}
        if(growthStage>2) {return plant.getIcon(0, 2);}
        if(growthStage>0) {return plant.getIcon(0, 1);}
        return plant.getIcon(0, 0);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean renderAsFlower() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getInformation() {
        return "agricraft_journal.ie_hemp";
    }
}

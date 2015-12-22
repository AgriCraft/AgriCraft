package com.InfinityRaider.AgriCraft.farming.cropplant;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

public class CropPlantNetherWart extends CropPlant {
    @Override
    public int tier() {
        return 2;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(Items.nether_wart);
    }

    @Override
    public Block getBlock() {
        return Blocks.nether_wart;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(Items.nether_wart));
        return list;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return new ItemStack(Items.nether_wart);
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount = (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> list = new ArrayList<>();
        while (amount > 0) {
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
        return GrowthRequirementHandler.getNewBuilder().soil(new BlockWithMeta(Blocks.soul_sand)).brightnessRange(0, 8).build();
    }

    @Override
    public void onAllowedGrowthTick(World world, BlockPos pos, int oldGrowthStage) {}

    @Override
    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return Constants.UNIT * 13;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        int meta = 1;
        if(growthStage>=7) {
            meta = 3;
        }
        else if(growthStage<4) {
            meta = 0;
        }
        return Blocks.nether_wart.getIcon(0, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        return "agricraft_journal." + "nether_wart";
    }
}

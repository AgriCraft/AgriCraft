package com.InfinityRaider.AgriCraft.farming.cropplant;

import com.InfinityRaider.AgriCraft.api.v1.ICropPlant;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v1.IAdditionalCropData;
import com.InfinityRaider.AgriCraft.api.v1.ICrop;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("deprecation")
public class CropPlantAPIv1 extends CropPlant {
    protected ICropPlant plant;

    public CropPlantAPIv1(ICropPlant plant) {
        this.plant = plant;
    }

    @Override
    public int tier() {
        return plant.tier();
    }

    @Override
    public ItemStack getSeed() {
        return plant.getSeed();
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
        return plant.getFruitsOnHarvest(gain, rand);
    }

    @Override
    public boolean onHarvest(World world, BlockPos pos, EntityPlayer player) {
        return plant.onHarvest(world, pos, player);
    }

    @Override
    public void onSeedPlanted(World world, BlockPos pos) {
        plant.onSeedPlanted(world, pos);
    }

    @Override
    public void onPlantRemoved(World world, BlockPos pos) {
        plant.onPlantRemoved(world, pos);
    }

    @Override
    public boolean canBonemeal() {
        return plant.canBonemeal();
    }

    @Override
    public IAdditionalCropData getInitialCropData(World world, BlockPos pos, ICrop crop) {
        return plant.getInitialCropData(world, pos, crop);
    }

    @Override
    public IAdditionalCropData readCropDataFromNBT(NBTTagCompound tag) {
        return plant.readCropDataFromNBT(tag);
    }

    @Override
    protected IGrowthRequirement initGrowthRequirement() {
        return plant.getGrowthRequirement();
    }
    
    @Override
    public boolean onAllowedGrowthTick(World world, BlockPos pos, int oldGrowthStage) {
        return plant.onAllowedGrowthTick(world, pos, oldGrowthStage);
    }

    @Override
    public float getHeight(int meta) {
        return plant.getHeight(meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        return plant.getPlantIcon(growthStage);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return plant.renderAsFlower();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        return plant.getInformation();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, BlockPos pos, RenderBlocks renderer) {
        if(plant.overrideRendering()) {
            plant.renderPlantInCrop(world, pos, renderer);
        } else {
            PlantRenderer.renderPlantLayer(pos, renderer, renderAsFlower() ? 1 : 6, getPlantIcon(world.getBlockMetadata(pos)), 0);
        }
    }

    @Override
    public void onValidate(World world, BlockPos pos, ICrop crop) {
        plant.onValidate(world, pos, crop);
    }

    @Override
    public void onInvalidate(World world, BlockPos pos, ICrop crop) {
        plant.onInvalidate(world, pos, crop);}

    @Override
    public void onChunkUnload(World world, BlockPos pos, ICrop crop) {
        plant.onChunkUnload(world, pos, crop);}
}

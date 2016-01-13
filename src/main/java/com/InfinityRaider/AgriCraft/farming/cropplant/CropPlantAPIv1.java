package com.InfinityRaider.AgriCraft.farming.cropplant;

import com.InfinityRaider.AgriCraft.api.v1.ICropPlant;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v2.IAdditionalCropData;
import com.InfinityRaider.AgriCraft.api.v2.ICrop;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("deprecation")
public class CropPlantAPIv1 extends CropPlant {
    protected ICropPlant plant;

    public CropPlantAPIv1(ICropPlant plant) {
        this.plant = plant;
        this.setTier(plant.tier());
    }

    @Override
    public int tier() {
        return plant == null ? 1 : plant.tier();
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
    public boolean onHarvest(World world, int x, int y, int z, EntityPlayer player) {
        return plant.onHarvest(world, x, y, z, player);
    }

    @Override
    public void onSeedPlanted(World world, int x, int y, int z) {
        plant.onSeedPlanted(world, x, y, z);
    }

    @Override
    public void onPlantRemoved(World world, int x, int y, int z) {
        plant.onPlantRemoved(world, x, y, z);
    }

    @Override
    public boolean canBonemeal() {
        return plant.canBonemeal();
    }

    @Override
    public IAdditionalCropData getInitialCropData(World world, int x, int y, int z, ICrop crop) {
        return null;
    }

    @Override
    public IAdditionalCropData readCropDataFromNBT(NBTTagCompound tag) {
        return null;
    }

    @Override
    protected IGrowthRequirement initGrowthRequirement() {
        return GrowthRequirementHandler.getNewBuilder().build();
    }
    
    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return plant.onAllowedGrowthTick(world, x, y, z, oldGrowthStage);
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

    @SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        if(plant.overrideRendering()) {
            plant.renderPlantInCrop(world, x, y, z, renderer);
        } else {
            PlantRenderer.renderPlantLayer(world, x, y, z, renderAsFlower() ? 1 : 6, getPlantIcon(world.getBlockMetadata(x, y, z)), 0);
        }
    }
}

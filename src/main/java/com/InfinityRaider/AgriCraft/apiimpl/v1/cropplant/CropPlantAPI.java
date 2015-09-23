package com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant;

import com.InfinityRaider.AgriCraft.api.v1.ICropPlant;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CropPlantAPI extends CropPlant {
    private ICropPlant plant;

    public CropPlantAPI(ICropPlant plant) {
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

    public boolean onHarvest(World world, int x, int y, int z, EntityPlayer player) {
        return plant.onHarvest(world, x, y, z, player);
    }

    public void onSeedPlanted(World world, int x, int y, int z) {
        plant.onSeedPlanted(world, x, y, z);
    }

    public void onPlantRemoved(World world, int x, int y, int z) {
        plant.onPlantRemoved(world, x, y, z);
    }

    @Override
    public boolean canBonemeal() {
        return plant.canBonemeal();
    }

    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return plant.onAllowedGrowthTick(world, x, y, z, oldGrowthStage);
    }

    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        return plant.isFertile(world, x, y, z);
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
            PlantRenderer.renderPlantLayer(x, y, z, renderer, renderAsFlower() ? 1 : 6, getPlantIcon(world.getBlockMetadata(x, y, z)), 0);
        }
    }
}

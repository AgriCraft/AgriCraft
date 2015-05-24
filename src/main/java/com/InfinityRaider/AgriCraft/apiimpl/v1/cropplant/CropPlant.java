package com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

/** main class used by TileEntityCrop to perform its functionality, only make one object of this per seed object and register it using CropPlantHandler.registerPlant(CropPlant plant) */
public abstract class CropPlant {
    public final int getGrowthRate() {
        switch(getTier()) {
            case 1: return Constants.growthTier1;
            case 2: return Constants.growthTier2;
            case 3: return Constants.growthTier3;
            case 4: return Constants.growthTier4;
            case 5: return Constants.growthTier5;
            default: return 0;
        }
    }

    public final int getTier() {
        int seedTierOverride = SeedHelper.getSeedTierOverride(getSeed());
        if(seedTierOverride>0) {
            return seedTierOverride;
        }
        return tier();
    }

    public abstract int tier();

    public abstract ItemStack getSeed();

    public abstract ArrayList<ItemStack> getAllFruits();

    public abstract ItemStack getRandomFruit(Random rand);

    public abstract ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand);

    public boolean onHarvest(World world, int x, int y, int z) {
        return true;
    }

    public void onSeedPlanted(World world, int x, int y, int z) {}

    public void onPlantRemoved(World world, int x, int y, int z) {}

    public abstract boolean canBonemeal();

    public abstract boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage);

    public abstract boolean isFertile(World world, int x, int y, int z);

    public boolean isMature(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z)>=7;
    }

    @SideOnly(Side.CLIENT)
    public abstract float getHeight(int meta);

    @SideOnly(Side.CLIENT)
    public abstract IIcon getPlantIcon(int growthStage);

    @SideOnly(Side.CLIENT)
    public abstract boolean renderAsFlower();

    @SideOnly(Side.CLIENT)
    public abstract String getInformation();

    @SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        PlantRenderer.renderPlantLayer(x, y, z, renderer, renderAsFlower() ? 1 : 6, getPlantIcon(world.getBlockMetadata(x, y, z)), 0);
    }
}

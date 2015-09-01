package com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

/** main class used by TileEntityCrop to perform its functionality, only make one object of this per seed object and register it using CropPlantHandler.registerPlant(CropPlant plant) */
public abstract class CropPlant {
    public final int getGrowthRate() {
    	int tier = getTier();
    	
    	if (tier > 0 && tier <= Constants.GROWTH_TIER.length) { //Worst-case two comparisons instead of five.
    		return Constants.GROWTH_TIER[tier];
    	} else {
    		return Constants.GROWTH_TIER[0];
    	}
    }

    /** This is called to get the actual tier of a seed */
    public final int getTier() {
        int seedTierOverride = SeedHelper.getSeedTierOverride(getSeed());
        if(seedTierOverride>0) {
            return seedTierOverride;
        }
        return tier();
    }

    /** Gets the tier of this plant, can be overridden trough the configs */
    public abstract int tier();

    /** Gets a stack of the seed for this plant */
    public abstract ItemStack getSeed();

    /** Gets an arraylist of all possible fruit drops from this plant */
    public abstract ArrayList<ItemStack> getAllFruits();

    /** Returns a random fruit for this plant */
    public abstract ItemStack getRandomFruit(Random rand);

    /** Returns an ArrayList with amount of random fruit stacks for this plant */
    public abstract ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand);

    /** Gets called right before a harvest attempt, return false to prevent further processing, player may be null if harvested by automation */
    public boolean onHarvest(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    /** This is called right after this plant is planted on a crop, either trough planting, mutation or spreading */
    public void onSeedPlanted(World world, int x, int y, int z) {}

    /** This is called right after this plant is removed from a crop or a crop holding this plant is broken */
    public void onPlantRemoved(World world, int x, int y, int z) {}

    /** Allow this plant to be bonemealed or not */
    public abstract boolean canBonemeal();

    /** When a growth thick is allowed for this plant, return true to re-render the crop clientside */
    public abstract boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage);

    /** Checks if the plant can grow on this position */
    public abstract boolean isFertile(World world, int x, int y, int z);

    /** Checks if the plant is mature */
    public boolean isMature(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) >= Constants.MATURE; //Seven was a magic number.
    }

    /** Gets the height of the crop */
    @SideOnly(Side.CLIENT)
    public abstract float getHeight(int meta);

    /** Gets the icon for the plant, growth stage goes from 0 to 7 (both inclusive, 0 is sprout and 7 is mature) */
    @SideOnly(Side.CLIENT)
    public abstract IIcon getPlantIcon(int growthStage);

    /** Determines how the plant is rendered, return false to render as wheat (#), true to render as a flower (X) */
    @SideOnly(Side.CLIENT)
    public abstract boolean renderAsFlower();

    /** Gets some information about the plant for the journal */
    @SideOnly(Side.CLIENT)
    public abstract String getInformation();

    /** This is called when the plant is rendered */
    @SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        PlantRenderer.renderPlantLayer(x, y, z, renderer, renderAsFlower() ? 1 : 6, getPlantIcon(world.getBlockMetadata(x, y, z)), 0);
    }
}

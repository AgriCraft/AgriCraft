package com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant;

import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
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

/**
 * The main class used by TileEntityCrop.
 * Only make one object of this per seed object, and register using 
 * {@link CropPlantHandler#registerPlant(CropPlant plant)}
 */
public abstract class CropPlant {
    public final int getGrowthRate() {
    	int tier = getTier();
    	
    	if (tier > 0 && tier <= Constants.GROWTH_TIER.length) {
    		return Constants.GROWTH_TIER[tier];
    	} else {
    		return Constants.GROWTH_TIER[0];
    	}
    }

    /**
     * Returns the tier of the seed as represented as an integer value, or the overriding value.
     * The overriding value may be set in the configuration files.
     * 
     * Does not always have same output as {@link #tier()}.
     * 
     * Should fall within the range of {@link Constants#GROWTH_TIER}.
     * 
     * @return the tier of the seed.
     */
    public final int getTier() {
        int seedTierOverride = SeedHelper.getSeedTierOverride(getSeed());
        if(seedTierOverride>0) {
            return seedTierOverride;
        }
        return tier();
    }

    /**
     * Returns the tier of this plant, called by {@link #getTier()}.
     * 
     * Does not always have same output as {@link #getTier()}.
     * 
     * Should fall within the range of {@link Constants#GROWTH_TIER}.
     * 
     * @return the tier of the seed.
     */
    protected abstract int tier();

    /**
     * Gets a stack of the seed for this plant.
     * 
     * @return a stack of the plant's seeds.
     */
    public abstract ItemStack getSeed();

    /**
     * Gets a list of all possible fruit drops from this plant.
     * 
     * @return a list containing of all possible fruit drops.
     */
    public abstract ArrayList<ItemStack> getAllFruits();

    /**
     * Returns a random fruit for this plant.
     * 
     * @param rand a random for choosing the drop.
     * @return a random fruit dropped by the plant.
     */
    public abstract ItemStack getRandomFruit(Random rand);

    /**
     * Returns an ArrayList with amount of random fruit stacks for this plant.
     * Gain is passed to allow for different fruits for higher gain levels
     *
     * @param gain the gain, as in number of drops.
     * @param rand a random for choosing the drop.
     * @return a list containing random fruit drops from this plant.
     */
    public abstract ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand);

    /**
     * Gets called right before a harvest attempt, player may be null if harvested by automation.
     * Should return false to prevent further processing.
     * World object and coordinates are passed in the case you would want to keep track of all planted crops and their status
     * 
     * @param world the current world of the plant being harvested.
     * @param x the x-coordinate of the plant being harvested.
     * @param y the y-coordinate of the plant being harvested.
     * @param z the z-coordinate of the plant being harvested.
     * @param player the player attempting to harvest the plant.
     * @return true, by default.
     */
    public boolean onHarvest(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    /**
     * Called right after the plant is added to a crop through planting, mutation, or spreading.
     * 
     * @param world the world the plant is in.
     * @param x the x-coordinate of the plant.
     * @param y the y-coordinate of the plant.
     * @param z the z-coordinate of the plant.
     */
    public void onSeedPlanted(World world, int x, int y, int z) {}

    /**
     * Called right after the plant is removed from a crop, or a crop holding the plant is broken.
     * 
     * @param world the world the plant is in.
     * @param x the x-coordinate of the plant.
     * @param y the y-coordinate of the plant.
     * @param z the z-coordinate of the plant.
     */
    public void onPlantRemoved(World world, int x, int y, int z) {}

    /**
     * Determines if the plant may be grown with Bonemeal.
     * 
     * @return if the plant may be bonemealed.
     */
    public abstract boolean canBonemeal();

    /**
     * Attempts to apply a growth tick to the plant, if allowed.
     * Should return true to re-render the crop clientside.
     * 
     * @param world the world the plant is in.
     * @param x the x-coordinate of the plant.
     * @param y the y-coordinate of the plant.
     * @param z the z-coordinate of the plant.
     * @param oldGrowthStage the current/old growth stage of the plant.
     * @return if the plant should be rendered again client side (e.g. if the next growth stage has a different icon)
     */
    public abstract boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage);

    /**
     * Determines if the plant can grow at a location.
     * 
     * @param world the world the plant is in.
     * @param x the x-coordinate of the plant.
     * @param y the y-coordinate of the plant.
     * @param z the z-coordinate of the plant.
     * @return if the growth location for the plant is fertile.
     */
    public abstract boolean isFertile(World world, int x, int y, int z);

    /**
     * Determines if the plant is mature. That is, the plant's metadata matches {@link Constants#MATURE}.
     * 
     * @param world the world the plant is in.
     * @param x the x-coordinate of the plant.
     * @param y the y-coordinate of the plant.
     * @param z the z-coordinate of the plant.
     * @return if the plant is mature.
     */
    public boolean isMature(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) >= Constants.MATURE;
    }

    /**
     * Determines the height of the crop in float precision, as a function of the plant's metadata(growth stage).
     * Used to render and define the height of the selection bounding box
     * 
     * @param meta the growth stage of the plant (may range from 0 to {@link Constants#MATURE}).
     * @return the current height of the plant.
     */
    @SideOnly(Side.CLIENT)
    public abstract float getHeight(int meta);

    /**
     * Gets the icon for the plant, as a function of the plant's growth stage.
     *
     * @param growthStage the growthstage of the plant may range from 0 to {@link Constants#MATURE}.
     * @return the current icon for the plant.
     */
    @SideOnly(Side.CLIENT)
    public abstract IIcon getPlantIcon(int growthStage);

    /**
     * Determines how the plant is rendered.
     * 
     * @return false to render the plant as wheat (#), true to render as a flower (X).
     */
    @SideOnly(Side.CLIENT)
    public abstract boolean renderAsFlower();

    /**
     * Retrieves information about the plant for the seed journal.
     * It's possible to pass an unlocalized String, the returned value will be localized if possible.
     * 
     * @return a string describing the plant for use by the seed journal.
     */
    @SideOnly(Side.CLIENT)
    public abstract String getInformation();

    /**
     * A function to render the crop. Called when the plant is rendered.
     * 
     * @param world the world the plant is in.
     * @param x the x-coordinate of the plant.
     * @param y the y-coordinate of the plant.
     * @param z the z-coordinate of the plant.
     * @param renderer the renderer to use in the rendering of the plant.
     */
    @SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        PlantRenderer.renderPlantLayer(x, y, z, renderer, renderAsFlower() ? 1 : 6, getPlantIcon(world.getBlockMetadata(x, y, z)), 0);
    }
}

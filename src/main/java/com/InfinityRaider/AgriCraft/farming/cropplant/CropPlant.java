package com.InfinityRaider.AgriCraft.farming.cropplant;

import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v2.IAdditionalCropData;
import com.InfinityRaider.AgriCraft.api.v2.ICrop;
import com.InfinityRaider.AgriCraft.api.v2.ICropPlant;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
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

/**
 * The main class used by TileEntityCrop.
 * Only make one object of this per seed object, and register using {@link CropPlantHandler#registerPlant(CropPlant plant)}
 * ICropPlant is implemented to be able to read data from this class from the API
 */
public abstract class CropPlant implements ICropPlant {
    private IGrowthRequirement growthRequirement;
    private int tier;
    private int spreadChance;
    private boolean blackListed;
    private boolean ignoreVanillaPlantingRule;

    public CropPlant() {
        this.growthRequirement = initGrowthRequirement();
        growthRequirement = growthRequirement == null ? GrowthRequirementHandler.getNewBuilder().build() : growthRequirement;
        this.setTier(tier());
        this.blackListed = false;
        this.ignoreVanillaPlantingRule = false;
    }

    /**
     * GLOBAL CROPPLANT METHODS
     */

    /**
     * Gets the growth rate for this CropPlant, used in calculations on growth tick
     * @return the growth rate
     */
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
        return tier;
    }

    /**
     * Sets the tier for this crop plant
     */
    public final void setTier(int tier) {
        tier = tier >= Constants.GROWTH_TIER.length ? Constants.GROWTH_TIER.length-1 : tier;
        tier = tier <= 0 ? 1 : tier;
        this.tier = tier;
        this.spreadChance = 100/tier;
    }

    /** Gets the spread chance in percent for this plant */
    public final int getSpreadChance() {
        return spreadChance;
    }

    /** Sets the spread chance in percent for this plant */
    public final void setSpreadChance(int spreadChance) {
        this.spreadChance = spreadChance;
    }

    /**
     * @return if the plant is blacklisted
     */
    public final boolean isBlackListed() {
        return blackListed;
    }

    /**
     * Sets the blacklist status for this plant
     * @param status true if it should be blacklisted, false if not
     */
    public final void setBlackListStatus(boolean status) {
        this.blackListed = status;
    }

    /**
     * Checks if this plant ignores the rule to disabled vanilla planting
     * true means that the seed for this plant can still be planted even though vanilla planting is disabled
     * @return if this ignores the vanilla planting rule or not
     */
    public final boolean ingoresVanillaPlantingRule() {
        return ignoreVanillaPlantingRule;
    }

    /**
     * Sets if this plant should ignore the rule to disabled vanilla planting
     * true means that the seed for this plant can still be planted even though vanilla planting is disabled
     * @param value if this ignores the vanilla planting rule or not
     */
    public final void setIgnoreVanillaPlantingRule(boolean value) {
        this.ignoreVanillaPlantingRule = value;
    }

    /**
     * ICROPPLANT METHODS
     */

    /**
     * Returns the tier of this plant, called by {@link #getTier()}.
     * 
     * Does not always have same output as {@link #getTier()}.
     * 
     * Should fall within the range of {@link Constants#GROWTH_TIER}.
     * 
     * @return the tier of the seed.
     */
    @Override
    public abstract int tier();

    /**
     * Gets a stack of the seed for this plant.
     * 
     * @return a stack of the plant's seeds.
     */
    @Override
    public abstract ItemStack getSeed();

    /**
     * Gets the block instance for this plant.
     *
     * @return the Block object for this plant.
     */
    @Override
    public abstract Block getBlock();

    /**
     * Gets a list of all possible fruit drops from this plant.
     * 
     * @return a list containing of all possible fruit drops.
     */
    @Override
    public abstract ArrayList<ItemStack> getAllFruits();

    /**
     * Returns a random fruit for this plant.
     * 
     * @param rand a random for choosing the drop.
     * @return a random fruit dropped by the plant.
     */
    @Override
    public abstract ItemStack getRandomFruit(Random rand);

    /**
     * Returns an ArrayList with amount of random fruit stacks for this plant.
     * Gain is passed to allow for different fruits for higher gain levels
     *
     * @param gain the gain, as in number of drops.
     * @param rand a random for choosing the drop.
     * @return a list containing random fruit drops from this plant.
     */
    @Override
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
    @Override
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
    @Override
    public void onSeedPlanted(World world, int x, int y, int z) {}

    /**
     * Called right after the plant is removed from a crop, or a crop holding the plant is broken.
     * 
     * @param world the world the plant is in.
     * @param x the x-coordinate of the plant.
     * @param y the y-coordinate of the plant.
     * @param z the z-coordinate of the plant.
     */
    @Override
    public void onPlantRemoved(World world, int x, int y, int z) {}

    /**
     * Determines if the plant may be grown with Bonemeal.
     * 
     * @return if the plant may be bonemealed.
     */
    @Override
    public abstract boolean canBonemeal();

    @Override
    public IAdditionalCropData getInitialCropData(World world, int x, int y, int z, ICrop crop) {
        return null;
    }

    @Override
    public IAdditionalCropData readCropDataFromNBT(NBTTagCompound tag) {
        return null;
    }

    @Override
    public void onValidate(World world, int x, int y, int z, ICrop crop) {}

    @Override
    public void onInvalidate(World world, int x, int y, int z, ICrop crop) {}

    @Override
    public void onChunkUnload(World world, int x, int y, int z, ICrop crop) {}

    public final void setGrowthRequirement(IGrowthRequirement growthRequirement) {
        this.growthRequirement = growthRequirement;
    }

    @Override
    public final IGrowthRequirement getGrowthRequirement() {
        return growthRequirement;
    }

    protected abstract IGrowthRequirement initGrowthRequirement();

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
    @Override
    public abstract boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage);

    /**
     * Determines if the plant can grow at a location.
     * This is an override of a deprecated method for the sake of backwards API compatibility and should not be called anymore,
     * To check if a plant is fertile, use getGrowthRequirement().canGrow(world, x, y, z)
     * 
     * @param world the world the plant is in.
     * @param x the x-coordinate of the plant.
     * @param y the y-coordinate of the plant.
     * @param z the z-coordinate of the plant.
     * @return if the growth location for the plant is fertile.
     */
    @Override
    @SuppressWarnings("deprecation")
    public final boolean isFertile(World world, int x, int y, int z) {
        return getGrowthRequirement().canGrow(world, x, y, z);
    }

    /**
     * Determines if the plant is mature. That is, the plant's metadata matches {@link Constants#MATURE}.
     * 
     * @param world the world the plant is in.
     * @param x the x-coordinate of the plant.
     * @param y the y-coordinate of the plant.
     * @param z the z-coordinate of the plant.
     * @return if the plant is mature.
     */
    @Override
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
    @Override
    @SideOnly(Side.CLIENT)
    public abstract float getHeight(int meta);

    /**
     * Gets the icon for the plant, as a function of the plant's growth stage.
     *
     * @param growthStage the growthstage of the plant may range from 0 to {@link Constants#MATURE}.
     * @return the current icon for the plant.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public abstract IIcon getPlantIcon(int growthStage);

    /**
     * Determines how the plant is rendered.
     * 
     * @return false to render the plant as wheat (#), true to render as a flower (X).
     */
    @Override
    @SideOnly(Side.CLIENT)
    public abstract boolean renderAsFlower();

    /**
     * Retrieves information about the plant for the seed journal.
     * It's possible to pass an unlocalized String, the returned value will be localized if possible.
     * 
     * @return a string describing the plant for use by the seed journal.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public abstract String getInformation();


    @Override
    @SideOnly(Side.CLIENT)
    public boolean overrideRendering() {
        return false;
    }

    /**
     * A function to render the crop. Called when the plant is rendered.
     * 
     * @param world the world the plant is in.
     * @param x the x-coordinate of the plant.
     * @param y the y-coordinate of the plant.
     * @param z the z-coordinate of the plant.
     * @param renderer the renderer to use in the rendering of the plant.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        PlantRenderer.renderPlantLayer(world, x, y, z, renderAsFlower() ? 1 : 6, getPlantIcon(world.getBlockMetadata(x, y, z)), 0);
    }
}

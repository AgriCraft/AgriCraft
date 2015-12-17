package com.InfinityRaider.AgriCraft.api.v2;

import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
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
 * This interface is used both for you to read the AgriCraft CropPlants as well as coding your own.
 * If you register your own ICropPlant object, it will be wrapped by the api.
 * Meaning if you query the ICropPlant object you registered, it will return a different object.
 */
@SuppressWarnings("deprecation")
public interface ICropPlant extends com.InfinityRaider.AgriCraft.api.v1.ICropPlant {
    /** Gets the tier of this plant, can be overridden trough the configs */
    @Override
    int tier();

    /** Gets a stack of the seed for this plant */
    @Override
    ItemStack getSeed();

    /** Gets a block instance of the crop */
    @Override
    Block getBlock();

    /** Gets an ArrayList of all possible fruit drops from this plant */
    @Override
    ArrayList<ItemStack> getAllFruits();

    /** Returns a random fruit for this plant */
    @Override
    ItemStack getRandomFruit(Random rand);

    /** Returns an ArrayList with random fruit stacks for this plant */
    @Override
    ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand);

    /** Gets called right before a harvest attempt, return false to prevent further processing, player may be null if harvested by automation */
    @Override
    boolean onHarvest(World world, int x, int y, int z, EntityPlayer player);

    /** This is called right after this plant is planted on a crop, either trough planting, mutation or spreading */
    @Override
    void onSeedPlanted(World world, int x, int y, int z);

    /** This is called right after this plant is removed from a crop or a crop holding this plant is broken */
    @Override
    void onPlantRemoved(World world, int x, int y, int z);

    /** Allow this plant to be bonemealed or not */
    @Override
    boolean canBonemeal();

    /**
     * If you want your crop to have additional data, this is called when the plant is first applied to crop sticks, either trough planting, spreading or mutation
     * @param world the world object for the crop
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @param crop the crop where this plant is planted on
     * @return initial IAdditionalCropData object (can be null if you don't need additional data)
     */
    IAdditionalCropData getInitialCropData(World world, int x, int y, int z, ICrop crop);

    /**
     * If this CropPlant should track additional data, this method will be called when the crop containing such a CropPlant is reading from NBT
     * @param tag the same tag returned from the IAdditionalCropData.writeToNBT() method
     * @return an object holding the data
     */
    IAdditionalCropData readCropDataFromNBT(NBTTagCompound tag);

    /**
     * Called when the TileEntity with this plant has its validate() method called
     * @param world the World object for the TileEntity
     * @param x the x-coordinate for the TileEntity
     * @param y the x-coordinate for the TileEntity
     * @param z the x-coordinate for the TileEntity
     * @param crop the ICrop instance of the TileEntity (is the same object as the TileEntity, but is for convenience)
     */
    void onValidate(World world, int x, int y, int z, ICrop crop);

    /**
     * Called when the TileEntity with this plant has its invalidate() method called
     * @param world the World object for the TileEntity
     * @param x the x-coordinate for the TileEntity
     * @param y the x-coordinate for the TileEntity
     * @param z the x-coordinate for the TileEntity
     * @param crop the ICrop instance of the TileEntity (is the same object as the TileEntity, but is for convenience)
     */
    void onInvalidate(World world, int x, int y, int z, ICrop crop);

    /**
     * Called when the TileEntity with this plant has its onChunkUnload() method called
     * @param world the World object for the TileEntity
     * @param x the x-coordinate for the TileEntity
     * @param y the x-coordinate for the TileEntity
     * @param z the x-coordinate for the TileEntity
     * @param crop the ICrop instance of the TileEntity (is the same object as the TileEntity, but is for convenience)
     */
    void onChunkUnload(World world, int x, int y, int z, ICrop crop);

    /**
     * Gets the growth requirement for this plant, this is used to check if the plant can be planted or grow in certain locations
     *
     * If you don't want to create your own class for this, you can use APIv2.getGrowthRequirementBuilder() to get a Builder object to build IGrowthRequirements
     * If you just want to have vanilla crop behaviour, you can use APIv2.getDefaultGrowthRequirement() to get a growth requirement with default behaviour
     */
    IGrowthRequirement getGrowthRequirement();

    /** When a growth thick is allowed for this plant, return true to re-render the crop client side */
    @Override
    boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage);

    /** Checks if the plant is mature */
    @Override
    boolean isMature(IBlockAccess world, int x, int y, int z);

    /** Gets the height of the crop */
    @Override
    float getHeight(int meta);

    /** Gets the icon for the plant, growth stage goes from 0 to 7 (both inclusive, 0 is sprout and 7 is mature) */
    @Override
    IIcon getPlantIcon(int growthStage);

    /** Determines how the plant is rendered, return false to render as wheat (#), true to render as a flower (X) */
    @Override
    boolean renderAsFlower();

    /** Gets some information about the plant for the journal */
    @Override
    String getInformation();

    /** Return true if you want to render the plant yourself, else agricraft will render the plant based on the data returned by the getIcon and renderAsFlower methods */
    @Override
    @SideOnly(Side.CLIENT)
    boolean overrideRendering();

    /** This is called when the plant is rendered, this is never called if returned false on overrideRendering */
    @SideOnly(Side.CLIENT)
    void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer);
}

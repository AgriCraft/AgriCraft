package com.infinityraider.agricraft.api.v1;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This interface is used both for you to read the AgriCraft CropPlants as well as coding your own.
 * If you register your own ICropPlant object, it will be wrapped by the api.
 * Meaning if you query the ICropPlant object you registered, it will return a different object.
 */
public interface ICropPlant {
    /** Gets the tier of this plant, can be overridden trough the configs */
    int tier();

    /** Gets a stack of the seed for this plant */
    ItemStack getSeed();

    /** Gets a block instance of the crop */
    Block getBlock();

    /** Gets an ArrayList of all possible fruit drops from this plant */
    ArrayList<ItemStack> getAllFruits();

    /** Returns a random fruit for this plant */
    ItemStack getRandomFruit(Random rand);

    /** Returns an ArrayList with random fruit stacks for this plant */
    ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand);

    /** Gets called right before a harvest attempt, return false to prevent further processing, player may be null if harvested by automation */
    boolean onHarvest(World world,BlockPos pos, IBlockState state, EntityPlayer player);

    /** This is called right after this plant is planted on a crop, either trough planting, mutation or spreading */
    void onSeedPlanted(World world, BlockPos pos);

    /** This is called right after this plant is removed from a crop or a crop holding this plant is broken */
    void onPlantRemoved(World world, BlockPos pos);

    /** Allow this plant to be bonemealed or not */
    boolean canBonemeal();

    /**
     * If you want your crop to have additional data, this is called when the plant is first applied to crop sticks, either trough planting, spreading or mutation
     * @param world the world object for the crop
     * @param pos the block position
     * @param crop the crop where this plant is planted on
     * @return initial IAdditionalCropData object (can be null if you don't need additional data)
     */
    IAdditionalCropData getInitialCropData(World world,BlockPos pos, ICrop crop);

    /**
     * If this CropPlant should track additional data, this method will be called when the crop containing such a CropPlant is reading from NBT
     * @param tag the same tag returned from the IAdditionalCropData.writeToNBT() method
     * @return an object holding the data
     */
    IAdditionalCropData readCropDataFromNBT(NBTTagCompound tag);

    /**
     * Called when the TileEntity with this plant has its validate() method called
     * @param world the World object for the TileEntity
     * @param pos the block position
     * @param crop the ICrop instance of the TileEntity (is the same object as the TileEntity, but is for convenience)
     */
    void onValidate(World world,BlockPos pos, ICrop crop);

    /**
     * Called when the TileEntity with this plant has its invalidate() method called
     * @param world the World object for the TileEntity
     * @param pos the block position
     * @param crop the ICrop instance of the TileEntity (is the same object as the TileEntity, but is for convenience)
     */
    void onInvalidate(World world, BlockPos pos, ICrop crop);

    /**
     * Called when the TileEntity with this plant has its onChunkUnload() method called
     * @param world the World object for the TileEntity
     * @param pos the block position
     * @param crop the ICrop instance of the TileEntity (is the same object as the TileEntity, but is for convenience)
     */
    void onChunkUnload(World world, BlockPos pos, ICrop crop);

    /**
     * Gets the growth requirement for this plant, this is used to check if the plant can be planted or grow in certain locations
     *
     * If you don't want to create your own class for this, you can use APIv2.getGrowthRequirementBuilder() to get a Builder object to build IGrowthRequirements
     * If you just want to have vanilla crop behaviour, you can use APIv2.getDefaultGrowthRequirement() to get a growth requirement with default behaviour
     */
    IGrowthRequirement getGrowthRequirement();

    List<IMutation> getDefaultMutations();

    /** When a growth thick is allowed for this plant */
    void onAllowedGrowthTick(World world, BlockPos pos, int oldGrowthStage);

    /** Checks if the plant is mature */
    boolean isMature(IBlockAccess world, BlockPos pos, IBlockState state);

    /** Gets the height of the crop */
    float getHeight(int meta);

    /** Determines how the plant is rendered, return false to render as wheat (#), true to render as a flower (X) */
    @SideOnly(Side.CLIENT)
    RenderMethod getRenderMethod();

    /**
     * Gets the primary texture to render this plant with as a TextureAtlasSprite, note that this must be on the locationBlocksTextureMap
     * See {@link RenderMethod} for what primary texture should be returned
     */
    @SideOnly(Side.CLIENT)
    TextureAtlasSprite getPrimaryPlantTexture(int growthStage);

    /**
     * Gets the secondary texture to render this plant with as a TextureAtlasSprite, note that this must be on the locationBlocksTextureMap
     * See {@link RenderMethod}  for what secondary texture should be returned
     */
    @SideOnly(Side.CLIENT)
    TextureAtlasSprite getSecondaryPlantTexture(int growthStage);

    /** Gets some information about the plant for the journal */
    @SideOnly(Side.CLIENT)
    String getInformation();

    /** This is called when the plant is rendered, this is never called if returned false on overrideRendering */
    @SideOnly(Side.CLIENT)
    void renderPlantInCrop(WorldRenderer renderer, IBlockAccess world, BlockPos pos, IBlockState state, int growthStage);
}

package com.InfinityRaider.AgriCraft.api.v2;

import com.InfinityRaider.AgriCraft.api.v1.IFertiliser;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.Random;

/** Interface to interact with AgriCraft's crops
 * use API.getCrop(World world, int x, int y, int z) to retrieve the ICrop instance
 */
public interface ICrop {
    /**
     * @return if this crop has a plant
     */
    boolean hasPlant();

    /**
     * @return the ICropPlant instance planted on this crop
     */
    ICropPlant getPlant();

    /**
     * @return the stats for this crop
     */
    ISeedStats getStats();

    /**
     * @return the growth stat
     */
    short getGrowth();

    /**
     * @return the gain stat
     */
    short getGain();

    /**
     * @return the strength stat
     */
    short getStrength();

    /**
     * @return if this crop is analyzed
     */
    boolean isAnalyzed();

    /**
     * @return if there are weeds on this crop
     */
    boolean hasWeed();

    /**
     * @return if this crop is a crosscrop
     */
    boolean isCrossCrop();

    /**
     * Converts this crop to a crosscrop or a regular crop
     * @param status true for crosscrop, false for regular crop
     */
    void setCrossCrop(boolean status);

    /**
     * @return if a plant can be planted here, meaning the crop is empty and is not a cross crop
     */
    boolean canPlant();

    /**
     * Sets the plant onto this crop
     * @param growth the growth stat for the plant
     * @param gain the gain stat for the plant
     * @param strength the strength stat for the plant
     * @param analyzed if the plant is analyzed
     * @param seed the seed representing the plant
     * @param seedMeta the metadata for the seed
     */
    void setPlant(int growth, int gain, int strength, boolean analyzed, Item seed, int seedMeta);

    /**
     * Clears the plant from this crop
     */
    void clearPlant();

    /**
     * @return if this crop is fertile and thus can grow
     */
    boolean isFertile();

    /**
     * @return if bonemeal can be applied to this crop
     */
    boolean canBonemeal();

    /**
     * @return if this crop is fully grown
     */
    boolean isMature();

    /**
     * @return an ItemStack containing the seed planted on this crop
     */
    ItemStack getSeedStack();

    /**
     * @return the Block for the plant currently planted on this crop
     */
    Block getPlantBlock();

    /**
     * Spawns weeds on this crop
     */
    void spawnWeed();

    /**
     * Attempts to spread weeds to neighbouring crops
     */
    void spreadWeed();

    /**
     * Updates the growthstage of the weeds on this crop
     * @param growthStage the growth stage to be applied, should be in [0, 8[. 0 means clearing weeds
     */
    void updateWeed(int growthStage);

    /**
     * Clears weeds from this crop
     */
    void clearWeed();

    /**
     * Checks if a certain fertilizer may be applied to this crop
     * @param fertiliser the fertilizer to be checked
     * @return if the fertilizer may be applied
     */
    boolean allowFertiliser(IFertiliser fertiliser);

    /**
     * Apply fertilizer to this crop
     * @param fertiliser the fertilizer to be applied
     * @param rand a Random object
     */
    void applyFertiliser(IFertiliser fertiliser, Random rand);

    /**
     * Harvests this crop
     * @param player the player which harvests the crop, may be null if it is harvested by automation
     * @return if the harvest was successful
     */
    boolean harvest(@Nullable EntityPlayer player);

    /**
     * Utility method to get access to the TileEntity fields and methods for the crop
     * @return the TileEntity implementing ICrop
     */
    TileEntity getTileEntity();

    /**
     * @return Any additional data this crop might hold
     */
    IAdditionalCropData getAdditionalCropData();
}

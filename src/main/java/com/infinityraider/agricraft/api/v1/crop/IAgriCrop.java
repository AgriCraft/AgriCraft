package com.infinityraider.agricraft.api.v1.crop;

import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizable;
import com.infinityraider.agricraft.api.v1.misc.IAgriHarvestable;
import com.infinityraider.agricraft.api.v1.misc.IAgriRakeable;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.seed.IAgriSeedAcceptor;
import com.infinityraider.agricraft.api.v1.seed.IAgriSeedProvider;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import com.infinityraider.agricraft.api.v1.util.MethodResult;
import java.util.Optional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface to interact with AgriCraft's crops.
 *
 * To retrieve the ICrop instance use:
 * {@code API.getCrop(World world, int x, int y, int z)}
 */
public interface IAgriCrop extends IAgriSeedProvider, IAgriSeedAcceptor, IAgriFertilizable, IAgriHarvestable, IAgriRakeable {

    /**
     * Retrieves the location of the crop instance.
     *
     * @return the crop's position.
     */
    BlockPos getPos();

    /**
     * Retrieves the world that the crop is in.
     *
     * @return The world in which the crop is located.
     */
    World getWorld();

    /**
     * @return The growth stage of the crop, between 0 and 7 (both inclusive).
     */
    int getGrowthStage();

    /**
     * Sets the growth stage for this crop
     *
     * @param stage the growth stage, between 0 and 7 (both inclusive).
     */
    void setGrowthStage(int stage);

    /**
     * @return if this crop is a crosscrop
     */
    boolean isCrossCrop();

    /**
     * Converts this crop to a crosscrop or a regular crop
     *
     * @param status true for crosscrop, false for regular crop
     * 
     * @return if the cross crop was successfully set.
     */
    boolean setCrossCrop(boolean status);

    /**
     * @return if this crop is fertile and thus can grow
     */
    default boolean isFertile() {
        return this.hasSeed() && this.isFertile(this.getSeed());
    }

    default boolean isFertile(AgriSeed seed) {
        return (seed != null) && this.isFertile(seed.getPlant());
    }

    boolean isFertile(IAgriPlant plant);

    /**
     * @return if this crop is fully grown
     */
    boolean isMature();

    Optional<IAgriSoil> getSoil();

    // =========================================================================
    // IHarvestable Defaults
    // =========================================================================
    @Override
    default boolean canBeHarvested() {
        return hasSeed() && isMature();
    }

    // =========================================================================
    // IRakeable Defaults
    // =========================================================================
    @Override
    default boolean canBeRaked() {
        return hasSeed();
    }

    // =========================================================================
    // Event Methods
    // =========================================================================
    public MethodResult onGrowthTick();

    public MethodResult onApplyCrops(EntityPlayer player);

    public MethodResult onApplySeeds(EntityPlayer player, AgriSeed seed);

    public MethodResult onBroken(EntityPlayer player);

}

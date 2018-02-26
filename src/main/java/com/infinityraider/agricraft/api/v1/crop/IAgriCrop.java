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
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface to interact with AgriCraft's crops.
 *
 * To retrieve the ICrop instance use: {@code API.getCrop(World world, int x, int y, int z)}
 */
public interface IAgriCrop extends IAgriSeedProvider, IAgriSeedAcceptor, IAgriFertilizable, IAgriHarvestable, IAgriRakeable {

    /**
     * Retrieves the location of the crop instance.
     *
     * @return the crop's position.
     */
    BlockPos getCropPos();

    /**
     * Retrieves the world that the crop is in.
     *
     * @return The world in which the crop is located.
     */
    World getCropWorld();

    /**
     * @return The growth stage of the crop, between 0 and 7 (both inclusive).
     */
    int getGrowthStage();

    /**
     * Sets the growth stage for this crop, normalized to the range of valid values for the plant.
     * If there is no plant, this will set the stage to zero, and log a warning if the input was
     * non-zero. If growthStage changes, this will call markForUpdate and return true to inform the
     * caller. Otherwise this will return false to indicate that growthStage didn't change and
     * didn't do an update.
     *
     * @param stage The new value, from 0 (inclusive) up to the plant's stage amount (exclusive), if
     * this has a plant.
     * @return true if this changed the value and markForUpdate was called.
     */
    boolean setGrowthStage(int stage);

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

    default boolean isFertile(@Nullable AgriSeed seed) {
        return (seed != null) && this.isFertile(seed.getPlant());
    }

    boolean isFertile(@Nullable IAgriPlant plant);

    /**
     * @return if this crop is fully grown
     */
    boolean isMature();

    @Nonnull
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
    @Nonnull
    public MethodResult onGrowthTick();

    @Nonnull
    public MethodResult onApplyCrops(@Nullable EntityPlayer player);

    @Nonnull
    public MethodResult onApplySeeds(@Nonnull AgriSeed seed, @Nullable EntityPlayer player);

    @Nonnull
    public MethodResult onBroken(@Nonnull Consumer<ItemStack> consumer, @Nullable EntityPlayer player);

}

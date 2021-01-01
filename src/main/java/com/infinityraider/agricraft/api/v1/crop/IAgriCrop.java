package com.infinityraider.agricraft.api.v1.crop;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizable;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneCarrier;
import com.infinityraider.agricraft.api.v1.misc.IAgriHarvestable;
import com.infinityraider.agricraft.api.v1.misc.IAgriRakeable;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlantAcceptor;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlantProvider;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeedSpawnable;
import com.infinityraider.agricraft.api.v1.seed.IAgriSeedAcceptor;
import com.infinityraider.agricraft.api.v1.seed.IAgriSeedProvider;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * Interface to interact with AgriCraft's crops.
 *
 * To retrieve use AgriApi.getCrop()
 */
public interface IAgriCrop extends IAgriPlantProvider, IAgriPlantAcceptor, IAgriSeedProvider, IAgriSeedAcceptor,
        IAgriGeneCarrier, IAgriFertilizable, IAgriHarvestable, IAgriWeedSpawnable, IAgriRakeable {
    /**
     * @return true if this object represents a valid IAgriCrop, can return false if the world has changed and there is no longer a crop
     */
    boolean isValid();

    /**
     * @return The World in which this crop exists
     */
    World getWorld();

    /**
     * @return The position of this crop in the world
     */
    BlockPos getPosition();

    /**
     * @return The growth stage of the crop.
     */
    IAgriGrowthStage getGrowthStage();

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
    boolean setGrowthStage(IAgriGrowthStage stage);

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
    boolean isFertile();

    /**
     * @return if this crop is fully grown
     */
    boolean isMature();

    @Nonnull
    Optional<IAgriSoil> getSoil();

    void breakCrop();

    void applyGrowthTick();

    default Stream<IAgriCrop> streamNeighbours() {
        return Direction.Plane.HORIZONTAL.getDirectionValues()
                .map(dir -> AgriApi.getCrop(this.getWorld(), this.getPosition().offset(dir)))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    default Stream<IAgriCrop> streamNeighbours(Predicate<IAgriCrop> filter) {
        return this.streamNeighbours().filter(filter);
    }

    default List<IAgriCrop> getNeighbours() {
        return this.streamNeighbours().collect(Collectors.toList());
    }

    default List<IAgriCrop> getNeighbours(Predicate<IAgriCrop> filter) {
        return this.streamNeighbours(filter).collect(Collectors.toList());
    }
}

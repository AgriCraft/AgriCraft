package com.agricraft.agricraft.api.crop;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.content.items.IAgriCropStickItem;
import com.agricraft.agricraft.api.fertilizer.IAgriFertilizable;
import com.agricraft.agricraft.api.genetics.IAgriGenomeAcceptor;
import com.agricraft.agricraft.api.genetics.IAgriGenomeProvider;
import com.agricraft.agricraft.api.plant.IAgriPlantProvider;
import com.agricraft.agricraft.api.plant.IAgriWeedSpawnable;
import com.agricraft.agricraft.api.requirement.IAgriGrowthResponse;
import com.agricraft.agricraft.api.requirement.IAgriSoil;
import com.agricraft.agricraft.api.stat.IAgriStatProvider;
import com.agricraft.agricraft.api.util.IAgriDisplayable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Interface to interact with AgriCraft's crops.
 * <p>
 * To retrieve use AgriApi.getCrop()
 */
public interface IAgriCrop  extends IAgriPlantProvider, IAgriGenomeProvider, IAgriGenomeAcceptor, IAgriStatProvider,
		IAgriFertilizable, IAgriHarvestable, IAgriWeedSpawnable, IAgriRakeable, IAgriDisplayable {

	/**
	 * @return an Optional IAgriCrop from the world
	 */
	@SuppressWarnings("unused")
	static Optional<IAgriCrop> fetchFromWorld(BlockGetter world, BlockPos pos) {
		return AgriApi.getCrop(world, pos);
	}

	/**
	 * @return true if this object represents a valid IAgriCrop, can return false if the world has changed and there is no longer a crop
	 */
	boolean isValid();

	/**
	 * @return The position of this crop in the world
	 */
	@NotNull
	BlockPos getPosition();

	/**
	 * @return The fluid state of this crop in the world
	 */
	@NotNull
	BlockState getBlockState();

	/**
	 * @return The fluid state of the crop in the world
	 */
	@NotNull
	FluidState getFluidState();

	/**
	 * @return The growth stage of the crop.
	 */
	@NotNull
	IAgriGrowthStage getGrowthStage();

	/**
	 * @return The growth percentage of the crop.
	 */
	default double getGrowthPercentage() {
		return this.getGrowthStage().growthPercentage();
	}

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
	boolean setGrowthStage(@NotNull IAgriGrowthStage stage);

	/**
	 * @return if this crop has crop sticks
	 */
	boolean hasCropSticks();

	/**
	 * @return if this crop is a cross crop
	 */
	boolean isCrossCrop();

	/**
	 * @return the crop sticks variant if the crop has crop sticks
	 */
	Optional<IAgriCropStickItem.Variant> getCropStickVariant();

	/**
	 * Tries to apply crop sticks of a certain variant to the crop sticks
	 *
	 * @param variant the crop stick variant
	 *
	 * @return if the crop sticks were successfully applied.
	 */
	boolean applyCropStick(IAgriCropStickItem.Variant variant);

	Optional<IAgriCropStickItem.Variant> removeCropStick();

	/**
	 * @return if this crop is fertile and thus can grow
	 */
	default boolean isFertile() {
		return this.getFertilityResponse().isFertile();
	}

	/**
	 * @return the current fertility response for this crop
	 */
	IAgriGrowthResponse getFertilityResponse();

	/**
	 * @return if this crop can be harvested
	 */
	boolean isMature();

	/**
	 * @return true if this crop is fully grown (has no more growth stages)
	 */
	boolean isFullyGrown();

	/**
	 * @return the soil this crop is planted on
	 */
	@NotNull
	Optional<IAgriSoil> getSoil();

	/**
	 * Breaks the crop and performs the related actions (like firing events and dropping items)
	 * @param entity the entity responsible for breaking the crop
	 */
	void breakCrop(@Nullable LivingEntity entity);

	/**
	 * @return a Stream of all neighbouring crops this crop can interact with
	 */
	@NotNull
	Stream<IAgriCrop> streamNeighbours();

	/**
	 * Same as streamNeighbours(), but pre-applies a Predicate
	 * @param filter the Predicate
	 * @return Stream of all neighbouring crops this crop can interact with
	 */
	@NotNull
	default Stream<IAgriCrop> streamNeighbours(Predicate<IAgriCrop> filter) {
		return this.streamNeighbours().filter(filter);
	}

	/**
	 * @return an immutable list of all neighbouring crops this crop can interact with
	 */
	@NotNull
	default List<IAgriCrop> getNeighbours() {
		return this.streamNeighbours().collect(Collectors.toList());
	}

	/**
	 * Same as getNeighbours(), but applies a filter according to the Predicate
	 * @param filter the Predicate
	 * @return Stream of all neighbouring crops this crop can interact with
	 */
	@NotNull
	default List<IAgriCrop> getNeighbours(Predicate<IAgriCrop> filter) {
		return this.streamNeighbours(filter).collect(Collectors.toList());
	}

	/**
	 * @return The World in which this crop exists (may be null if the tile is invalid)
	 */
	@Nullable
	default Level world() {
		return this.asTile().getLevel();
	}

	/**
	 * @return this, but cast to a TileEntity
	 */
	default BlockEntity asTile() {
		return (BlockEntity) this;
	}

	void dropItem(ItemStack item);

}

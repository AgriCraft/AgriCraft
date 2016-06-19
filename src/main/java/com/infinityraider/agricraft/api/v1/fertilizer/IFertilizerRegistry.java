/*
 */
package com.infinityraider.agricraft.api.v1.fertilizer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * An interface for managing fertilizers supported by AgriCraft.
 *
 * @author The AgriCraft Team.
 */
public interface IFertilizerRegistry {

	/**
	 * Checks if the given item is any form of supported fertilizer (e.g. bone
	 * meal).
	 *
	 * <p>
	 * Note: A "fertilizer" is any item that can be applied to a growing plant,
	 * regardless of the effect.
	 * </p>
	 *
	 * @param fertilizer Any item.
	 * @return True if AgriCraft knows how to handle the given item as
	 * fertilizer.
	 */
	boolean isSupportedFertilizer(ItemStack fertilizer);

	/**
	 * Checks if the given fertilizer is valid for the plant in the crop at the
	 * given position.
	 *
	 * @param world World object
	 * @param pos the block position
	 * @param fertilizer Any item, preferable one that is a fertilizer.
	 * @return True if the item is a valid fertilizer and can be applied, false
	 * otherwise.
	 */
	boolean isValidFertilizer(World world, BlockPos pos, ItemStack fertilizer);

	/**
	 * Tries to apply the given fertilizer to the plant in the crop at the given
	 * position. On success the item stack's size will be decreased (and may be
	 * 0). All world interaction will be handled by this method.
	 *
	 * <p>
	 * Note: The return value does not state if the plant was effected by the
	 * fertilizer, only if it was applied.
	 * </p>
	 *
	 * @param world World object
	 * @param pos the block position
	 * @param state the block state
	 * @param fertilizer Any item, preferable one that is a fertilizer. Will be
	 * modified on success!
	 * @return True if the fertilizer was applied successfully, false otherwise.
	 */
	boolean applyFertilizer(World world, BlockPos pos, IBlockState state, ItemStack fertilizer);

}

/*
 */
package com.infinityraider.agricraft.api.v3.fertiliser;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * An interface for managing fertilisers supported by AgriCraft.
 *
 * @author The AgriCraft Team.
 */
public interface IFertiliserRegistry {

	/**
	 * Checks if the given item is any form of supported fertiliser (e.g. bone
	 * meal).
	 *
	 * <p>
	 * Note: A "fertiliser" is any item that can be applied to a growing plant,
	 * regardless of the effect.
	 * </p>
	 *
	 * @param fertiliser Any item.
	 * @return True if AgriCraft knows how to handle the given item as
	 * fertiliser.
	 */
	boolean isSupportedFertiliser(ItemStack fertiliser);

	/**
	 * Checks if the given fertiliser is valid for the plant in the crop at the
	 * given position.
	 *
	 * @param world World object
	 * @param pos the block position
	 * @param fertiliser Any item, preferable one that is a fertiliser.
	 * @return True if the item is a valid fertiliser and can be applied, false
	 * otherwise.
	 */
	boolean isValidFertiliser(World world, BlockPos pos, ItemStack fertiliser);

	/**
	 * Tries to apply the given fertiliser to the plant in the crop at the given
	 * position. On success the item stack's size will be decreased (and may be
	 * 0). All world interaction will be handled by this method.
	 *
	 * <p>
	 * Note: The return value does not state if the plant was effected by the
	 * fertiliser, only if it was applied.
	 * </p>
	 *
	 * @param world World object
	 * @param pos the block position
	 * @param state the block state
	 * @param fertiliser Any item, preferable one that is a fertiliser. Will be
	 * modified on success!
	 * @return True if the fertiliser was applied successfully, false otherwise.
	 */
	boolean applyFertiliser(World world, BlockPos pos, IBlockState state, ItemStack fertiliser);

}

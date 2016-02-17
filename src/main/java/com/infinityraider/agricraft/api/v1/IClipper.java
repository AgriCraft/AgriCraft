package com.infinityraider.agricraft.api.v1;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * Implement this interface in Item classes which you want to have clipping behaviour
 */
public interface IClipper {
    /**
     * Method provided to allow more things to happen when the clipper is used, default behaviour will always happen, even if this method does nothing
     * @param world the World object for the crop being clipped
     * @param pos the block position
     * @param player the player using the clipper
     */
    void onClipperUsed(World world, BlockPos pos, IBlockState state, EntityPlayer player);
}

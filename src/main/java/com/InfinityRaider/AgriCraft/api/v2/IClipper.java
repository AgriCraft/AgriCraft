package com.InfinityRaider.AgriCraft.api.v2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Implement this interface in Item classes which you want to have clipping behaviour
 */
public interface IClipper {
    /**
     * Method provided to allow more things to happen when the clipper is used, default behaviour will always happen, even if this method does nothing
     * @param world the World object for the crop being clipped
     * @param x the x-coordinate for the crop being clipped
     * @param y the y-coordinate object for the crop being clipped
     * @param z the z-coordinate object for the crop being clipped
     * @param player the player using the clipper
     */
    void onClipperUsed(World world, int x, int y, int z, EntityPlayer player);
}

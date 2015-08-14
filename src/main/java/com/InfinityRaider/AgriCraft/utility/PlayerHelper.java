package com.InfinityRaider.AgriCraft.utility;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class PlayerHelper {
    public static float getPlayerYaw(EntityPlayer player) {
        float yaw = player.rotationYaw + player.cameraYaw;
        while(yaw<0) {
            yaw = yaw + 360;
        }
        return yaw;
    }

    public static ForgeDirection getPlayerFacing(EntityPlayer player) {
        float yaw = getPlayerYaw(player);
        if(yaw>45 && yaw<=135) {
            return ForgeDirection.WEST;
        }
        if(yaw>135 && yaw<=225) {
            return ForgeDirection.NORTH;
        }
        if(yaw>225 && yaw<=315) {
            return ForgeDirection.EAST;
        }
        return ForgeDirection.SOUTH;
    }
}

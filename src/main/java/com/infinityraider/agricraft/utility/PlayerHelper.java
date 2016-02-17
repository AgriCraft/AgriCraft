package com.infinityraider.agricraft.utility;

import net.minecraft.entity.player.EntityPlayer;

public abstract class PlayerHelper {
    public static float getPlayerYaw(EntityPlayer player) {
        float yaw = player.rotationYaw + player.cameraYaw;
        while(yaw<0) {
            yaw = yaw + 360;
        }
        return yaw;
    }

    public static AgriForgeDirection getPlayerFacing(EntityPlayer player) {
        float yaw = getPlayerYaw(player);
        if(yaw>45 && yaw<=135) {
            return AgriForgeDirection.WEST;
        }
        if(yaw>135 && yaw<=225) {
            return AgriForgeDirection.NORTH;
        }
        if(yaw>225 && yaw<=315) {
            return AgriForgeDirection.EAST;
        }
        return AgriForgeDirection.SOUTH;
    }
}

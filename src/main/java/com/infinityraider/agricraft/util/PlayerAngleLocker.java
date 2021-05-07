package com.infinityraider.agricraft.util;

import com.infinityraider.agricraft.AgriCraft;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerAngleLocker {

    /** Player orientation trackers */
    private static float yaw;
    private static float yawCamera;
    private static float yawOffset;
    private static float yawHead;
    private static float pitch;

    public static void storePlayerAngles() {
        PlayerEntity player = AgriCraft.instance.getClientPlayer();
        yaw = player.rotationYaw;
        yawCamera = player.cameraYaw;
        yawOffset = player.renderYawOffset;
        yawHead = player.rotationYawHead;
        pitch = player.rotationPitch;
    }

    public static void forcePlayerAngles() {
        PlayerEntity player = AgriCraft.instance.getClientPlayer();
        player.rotationYaw = yaw;
        player.prevRotationYaw = yaw;
        player.cameraYaw = yawCamera;
        player.prevCameraYaw = yawCamera;
        player.renderYawOffset = yawOffset;
        player.prevRenderYawOffset = yawOffset;
        player.rotationYawHead = yawHead;
        player.prevRotationYawHead = yawHead;
        player.rotationPitch = pitch;
        player.prevRotationPitch = pitch;
    }
}

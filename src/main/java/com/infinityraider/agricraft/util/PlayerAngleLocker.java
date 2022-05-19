package com.infinityraider.agricraft.util;

import com.infinityraider.agricraft.AgriCraft;
import net.minecraft.world.entity.player.Player;

public class PlayerAngleLocker {
    /** Player orientation trackers */
    private static float yaw;
    private static float bob;
    private static float bodyYawY;
    private static float yawHead;
    private static float pitch;

    public static void storePlayerAngles() {
        Player player = AgriCraft.instance.getClientPlayer();
        pitch = player.getXRot();
        yaw = player.getYRot();
        bob = player.bob;
        bodyYawY = player.yBodyRot;
        yawHead = player.yHeadRot;
    }

    public static void forcePlayerAngles() {
        Player player = AgriCraft.instance.getClientPlayer();
        player.yRotO = player.getYRot();
        player.setYRot(yaw);
        player.xRotO = player.getXRot();
        player.setXRot(pitch);
        player.oBob = player.bob;
        player.bob = bob;
        player.yBodyRotO = player.yBodyRot;
        player.yBodyRot = bodyYawY;
        player.yHeadRotO = player.yHeadRot;
        player.yHeadRot = yawHead;
    }
}

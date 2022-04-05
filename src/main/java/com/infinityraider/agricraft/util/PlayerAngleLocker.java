package com.infinityraider.agricraft.util;

import com.infinityraider.agricraft.AgriCraft;
import net.minecraft.world.entity.player.Player;

public class PlayerAngleLocker {

    /** Player orientation trackers */
    private static float yaw;
    private static float bob;
    private static float bodyYawy;
    private static float yawHead;
    private static float pitch;

    public static void storePlayerAngles() {
        Player player = AgriCraft.instance.getClientPlayer();
        yaw = player.getYRot();
        bob = player.bob;
        bodyYawy = player.yBodyRot;
        yawHead = player.yHeadRot;
        pitch = player.getXRot();
    }

    public static void forcePlayerAngles() {
        Player player = AgriCraft.instance.getClientPlayer();
        player.setYRot(yaw);
        player.yRotO = yaw;
        player.bob = bob;
        player.oBob = bob;
        player.yBodyRot = bodyYawy;
        player.yBodyRotO = bodyYawy;
        player.yHeadRot = yawHead;
        player.yHeadRotO = yawHead;
        player.setYRot(pitch);
        player.yRotO = pitch;
    }
}

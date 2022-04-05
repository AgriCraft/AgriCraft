package com.infinityraider.agricraft.util;

import com.google.common.base.Preconditions;
import com.infinityraider.infinitylib.utility.MessageUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author Ryan
 */
public final class LightHelper {

    public static final String LIGHT_METHOD_NAMES[] = {
        "getLight(pos)",
        "getLightValue(pos)",
        "getLightFromNeighborsFor(SKY  , pos)",
        "getLightFromNeighborsFor(BLOCK, pos)"
    };

    public static final int LIGHT_METHOD_COUNT = LIGHT_METHOD_NAMES.length;

    @Nonnull
    public static byte[] getLightData(@Nonnull Level world, @Nonnull BlockPos pos) {
        // Validate
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(pos);

        // Create the array.
        final byte[] lightData = new byte[LIGHT_METHOD_COUNT];

        // Fill the array.
        lightData[0] = (byte) world.getBrightness(pos);
        lightData[1] = (byte) world.getLightEmission(pos);
        lightData[2] = (byte) world.getBrightness(LightLayer.SKY, pos);
        lightData[3] = (byte) world.getBrightness(LightLayer.BLOCK, pos);
        // Return the array.
        return lightData;
    }

    public static void messageLightData(@Nullable Player player, @Nonnull byte[] clientLightData, @Nonnull byte[] serverLightData) {
        // Validate
        Preconditions.checkNotNull(clientLightData);
        Preconditions.checkNotNull(serverLightData);
        Preconditions.checkArgument(clientLightData.length == LIGHT_METHOD_COUNT);
        Preconditions.checkArgument(serverLightData.length == LIGHT_METHOD_COUNT);

        // Send message.
        MessageUtil.messagePlayer(player, "`7==================================================`r");
        MessageUtil.messagePlayer(player, "       `eLight Level`r");
        MessageUtil.messagePlayer(player, "   Client   `7|`r   Server   `7|`r Method Name");
        MessageUtil.messagePlayer(player, "`7--------------------------------------------------`r");
        for (int i = 0; i < LIGHT_METHOD_COUNT; i++) {
            MessageUtil.messagePlayer(player, "     {0}     `7|`r     {1}     `7|`r {2}", clientLightData[i], serverLightData[i], LIGHT_METHOD_NAMES[i]);
        }
        MessageUtil.messagePlayer(player, "`7==================================================`r");
    }

}

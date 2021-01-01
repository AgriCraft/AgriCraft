package com.infinityraider.agricraft.utility;

import com.google.common.base.Preconditions;
import com.infinityraider.infinitylib.utility.MessageUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

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
    public static byte[] getLightData(@Nonnull World world, @Nonnull BlockPos pos) {
        // Validate
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(pos);

        // Create the array.
        final byte[] lightData = new byte[LIGHT_METHOD_COUNT];

        // Fill the array.
        lightData[0] = (byte) world.getLight(pos);
        lightData[1] = (byte) world.getLightValue(pos);
        lightData[2] = (byte) world.getLightFor(LightType.SKY, pos);
        lightData[3] = (byte) world.getLightFor(LightType.BLOCK, pos);
        // Return the array.
        return lightData;
    }

    public static void messageLightData(@Nullable PlayerEntity player, @Nonnull byte[] clientLightData, @Nonnull byte[] serverLightData) {
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

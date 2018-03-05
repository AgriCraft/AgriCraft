/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.infinityraider.agricraft.utility;

import com.google.common.base.Preconditions;
import com.infinityraider.infinitylib.utility.MessageUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

/**
 *
 * @author Ryan
 */
public final class LightHelper {

    public static final String LIGHT_METHOD_NAMES[] = {
        "getLight(pos)",
        "getLight(pos, false)",
        "getLight(pos, true )",
        "getLightFor(SKY  , pos)",
        "getLightFor(BLOCK, pos)",
        "getLightFromNeighbors(pos)",
        "getLightFromNeighborsFor(SKY  , pos)",
        "getLightFromNeighborsFor(BLOCK, pos)",
        "getLightBrightness(pos)"
    };

    public static final int LIGHT_METHOD_COUNT = LIGHT_METHOD_NAMES.length;

    @Nonnull
    public static byte[] getLightData(@Nonnull World world, @Nonnull BlockPos pos) {
        // Validate
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(pos);

        // Create the array.
        final byte lightData[] = new byte[LIGHT_METHOD_COUNT];

        // Fill the array.
        lightData[0] = (byte) world.getLight(pos);
        lightData[1] = (byte) world.getLight(pos, false);
        lightData[2] = (byte) world.getLight(pos, true);
        lightData[3] = (byte) world.getLightFor(EnumSkyBlock.SKY, pos);
        lightData[4] = (byte) world.getLightFor(EnumSkyBlock.BLOCK, pos);
        lightData[5] = (byte) world.getLightFromNeighbors(pos);
        if (world.isRemote) {
            lightData[6] = (byte) world.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos);
            lightData[7] = (byte) world.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos);
        } else {
            lightData[6] = (byte) 0;
            lightData[7] = (byte) 0;
        }
        lightData[8] = (byte) world.getLightBrightness(pos);

        // Return the array.
        return lightData;
    }

    public static void messageLightData(@Nullable EntityPlayer player, @Nonnull byte[] clientLightData, @Nonnull byte[] serverLightData) {
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

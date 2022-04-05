package com.infinityraider.agricraft.network;

import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.util.LightHelper;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class MessageCompareLight extends MessageBase {
    private ResourceKey<Level> dimId;
    private BlockPos pos;
    private byte[] clientLightData;

    public MessageCompareLight() {}

    @OnlyIn(Dist.CLIENT)
    public MessageCompareLight(net.minecraft.world.level.Level world, BlockPos pos) {
        // Validate
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(pos);
        
        // Set
        this.dimId = world.dimension();
        this.pos = pos;
        this.clientLightData = LightHelper.getLightData(world, pos);
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_SERVER;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        // Get world.
        final Level world = AgriCraft.instance.proxy().getWorldFromDimension(this.dimId);

        // Get player.
        final Player player = ctx.getSender();

        // Get server light data.
        final byte[] serverLightData = LightHelper.getLightData(world, this.pos);

        // Message the light data to the player.
        LightHelper.messageLightData(player, clientLightData, serverLightData);
    }
}

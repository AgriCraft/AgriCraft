package com.infinityraider.agricraft.network;

import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.util.LightHelper;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageCompareLight extends MessageBase {
    private RegistryKey<World> dimId;
    private BlockPos pos;
    private byte[] clientLightData;

    public MessageCompareLight() {}

    @OnlyIn(Dist.CLIENT)
    public MessageCompareLight(World world, BlockPos pos) {
        // Validate
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(pos);
        
        // Set
        this.dimId = world.getDimensionKey();
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
        final World world = AgriCraft.instance.proxy().getWorldFromDimension(this.dimId);

        // Get player.
        final PlayerEntity player = ctx.getSender();

        // Get server light data.
        final byte[] serverLightData = LightHelper.getLightData(world, this.pos);

        // Message the light data to the player.
        LightHelper.messageLightData(player, clientLightData, serverLightData);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.infinityraider.agricraft.network;

import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.utility.LightHelper;
import com.infinityraider.infinitylib.network.MessageBase;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author Ryan
 */
public class MessageCompareLight extends MessageBase<IMessage> {
    
    private String playerId;
    private int dimId;
    private BlockPos pos;
    private byte[] clientLightData;

    public MessageCompareLight() {
    }

    @SideOnly(Side.CLIENT)
    public MessageCompareLight(EntityPlayer player, World world, BlockPos pos) {
        // Validate
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(pos);
        
        // Set
        this.playerId = player.getUniqueID().toString();
        this.dimId = world.provider.getDimension();
        this.pos = pos;
        this.clientLightData = LightHelper.getLightData(world, pos);
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.SERVER;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        // Get world.
        final World world = AgriCraft.proxy.getWorldByDimensionId(this.dimId);
        
        // Get player.
        final EntityPlayer player = world.getPlayerEntityByUUID(UUID.fromString(this.playerId));
        
        // Get server light data.
        final byte serverLightData[] = LightHelper.getLightData(world, this.pos);
        
        // Message the light data to the player.
        LightHelper.messageLightData(player, clientLightData, serverLightData);
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        // No need to reply to client, as just returning chat messages.
        return null;
    }
    
}

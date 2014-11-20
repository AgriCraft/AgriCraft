package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityChannel;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MessageTileEntityChannel implements IMessage, IMessageHandler<MessageTileEntityChannel, IMessage> {
    public int fluidLevel;
    public int x;
    public int y;
    public int z;

    public MessageTileEntityChannel() {
    }

    public MessageTileEntityChannel(TileEntityChannel channel) {
        this.fluidLevel = channel.getFluidLevel();
        this.x = channel.xCoord;
        this.y = channel.yCoord;
        this.z = channel.zCoord;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.fluidLevel = buf.readInt();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(fluidLevel);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public IMessage onMessage(MessageTileEntityChannel message, MessageContext ctx) {
        if(FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x,message.y,message.z) instanceof TileEntityChannel) {
            TileEntityChannel channel = (TileEntityChannel) FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x,message.y,message.z);
            channel.setFluidLevel(message.fluidLevel);
        }
        return null;
    }
}
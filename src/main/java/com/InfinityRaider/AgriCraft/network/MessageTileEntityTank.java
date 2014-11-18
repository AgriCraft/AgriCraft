package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityTank;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MessageTileEntityTank implements IMessage, IMessageHandler<MessageTileEntityTank, IMessage> {
    public int connectedTanks;
    public int fluidLevel;
    public int x;
    public int y;
    public int z;

    public MessageTileEntityTank() {
    }

    public MessageTileEntityTank(TileEntityTank tank) {
        this.connectedTanks = tank.getConnectedTanks();
        this.fluidLevel = tank.getFluidLevel();
        this.x = tank.xCoord;
        this.y = tank.yCoord;
        this.z = tank.zCoord;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.connectedTanks = buf.readInt();
        this.fluidLevel = buf.readInt();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(connectedTanks);
        buf.writeInt(fluidLevel);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public IMessage onMessage(MessageTileEntityTank message, MessageContext ctx) {
        if(FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x,message.y,message.z) instanceof TileEntityTank) {
            TileEntityTank tank = (TileEntityTank) FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x,message.y,message.z);
            tank.setConnectedTanks(message.connectedTanks);
            tank.setFluidLevel(message.fluidLevel);
        }
        return null;
    }
}

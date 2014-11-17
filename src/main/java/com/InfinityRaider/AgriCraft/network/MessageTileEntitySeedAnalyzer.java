package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class MessageTileEntitySeedAnalyzer implements IMessage, IMessageHandler<MessageTileEntitySeedAnalyzer, IMessage> {
    public int progress;
    public byte direction;
    public int x;
    public int y;
    public int z;

    public MessageTileEntitySeedAnalyzer() {
    }

    public MessageTileEntitySeedAnalyzer(TileEntitySeedAnalyzer analyzer) {
        this.progress = analyzer.progress;
        this.direction = analyzer.direction==null?(byte)ForgeDirection.NORTH.ordinal():(byte)analyzer.direction.ordinal();
        this.x = analyzer.xCoord;
        this.y = analyzer.yCoord;
        this.z = analyzer.zCoord;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.progress = buf.readInt();
        this.direction = buf.readByte();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.progress);
        buf.writeByte(this.direction);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }

    @Override
    public IMessage onMessage(MessageTileEntitySeedAnalyzer message, MessageContext ctx) {
        if(FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x,message.y,message.z) instanceof TileEntitySeedAnalyzer) {
            TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x,message.y,message.z);
            analyzer.progress = message.progress;
            analyzer.setDirection(message.direction);
        }
        return null;
    }
}

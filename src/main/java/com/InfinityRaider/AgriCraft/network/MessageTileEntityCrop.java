package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MessageTileEntityCrop implements IMessage, IMessageHandler<MessageTileEntityCrop, IMessage> {
    public short gain;
    public short growth;
    public short strength;
    public boolean crossCrop;
    public String seed;
    public short seedMeta;
    public int x;
    public int y;
    public int z;

    public MessageTileEntityCrop() {
    }

    public MessageTileEntityCrop(TileEntityCrop crop) {
        this.gain = (short) crop.gain;
        this.growth = (short) crop.growth;
        this.strength = (short) crop.strength;
        this.crossCrop = crop.crossCrop;
        this.seed = crop.getSeedString();
        this.seedMeta = (short) crop.seedMeta;
        this.x = crop.xCoord;
        this.y = crop.yCoord;
        this.z = crop.zCoord;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.gain = buf.readShort();
        this.growth = buf.readShort();
        this.strength = buf.readShort();
        this.crossCrop = buf.readBoolean();
        int stringLength =buf.readInt();
        this.seed = new String(buf.readBytes(stringLength).array());
        this.seedMeta = buf.readShort();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeShort(gain);
        buf.writeShort(growth);
        buf.writeShort(strength);
        buf.writeBoolean(crossCrop);
        buf.writeInt(seed.length());
        buf.writeBytes(seed.getBytes());
        buf.writeShort(seedMeta);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public IMessage onMessage(MessageTileEntityCrop message, MessageContext ctx) {
        if(FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x,message.y,message.z) instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x,message.y,message.z);
            crop.gain = message.gain;
            crop.growth = message.growth;
            crop.strength = message.strength;
            crop.crossCrop = message.crossCrop;
            crop.setSeed(message.seed);
            crop.seedMeta = message.seedMeta;
        }
        return null;
    }
}

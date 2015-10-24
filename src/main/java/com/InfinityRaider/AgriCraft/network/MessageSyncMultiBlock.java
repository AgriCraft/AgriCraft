package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.utility.multiblock.IMultiBlockComponent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MessageSyncMultiBlock extends MessageAgriCraft {
    private int xCoord;
    private int yCoord;
    private int zCoord;
    private int xSize;
    private int ySize;
    private int zSize;

    public MessageSyncMultiBlock() {}

    public MessageSyncMultiBlock(int xCoord, int yCoord, int zCoord, int xSize, int ySize, int zSize) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.xCoord = buf.readInt();
        this.yCoord = buf.readInt();
        this.zCoord = buf.readInt();
        this.xSize = buf.readInt();
        this.ySize = buf.readInt();
        this.zSize = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.xCoord);
        buf.writeInt(this.yCoord);
        buf.writeInt(this.zCoord);
        buf.writeInt(this.xSize);
        buf.writeInt(this.ySize);
        buf.writeInt(this.zSize);
    }

    public static class MessageHandler implements IMessageHandler<MessageSyncMultiBlock, IMessage> {
        @Override
        public IMessage onMessage(MessageSyncMultiBlock msg, MessageContext ctx) {
            World world = AgriCraft.proxy.getClientWorld();
            if(world != null) {
                TileEntity te = world.getTileEntity(msg.xCoord, msg.yCoord, msg.zCoord);
                if(te != null && te instanceof IMultiBlockComponent) {
                    IMultiBlockComponent component = (IMultiBlockComponent) te;
                    component.getMultiBlockManager().createMultiBlock(world, msg.xCoord, msg.yCoord, msg.zCoord, msg.xCoord+msg.xSize, msg.yCoord+msg.ySize, msg.zCoord+msg.zSize);
                }
            }
            return null;
        }
    }
}

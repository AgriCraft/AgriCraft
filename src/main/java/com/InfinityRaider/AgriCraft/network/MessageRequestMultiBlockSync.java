package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.utility.multiblock.IMultiBlockComponent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MessageRequestMultiBlockSync extends MessageAgriCraft {
    private int x;
    private int y;
    private int z;
    private int dim;

    public MessageRequestMultiBlockSync() {}

    public MessageRequestMultiBlockSync(World world, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = world.provider.dimensionId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.dim = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(dim);
    }

    public static class MessageHandler implements IMessageHandler<MessageRequestMultiBlockSync, IMessage> {
        @Override
        public MessageSyncMultiBlock onMessage(MessageRequestMultiBlockSync message, MessageContext ctx) {
            World world = AgriCraft.proxy.getWorldByDimensionId(message.dim);
            TileEntity tile = world.getTileEntity(message.x, message.y, message.z);
            if(tile != null && tile instanceof IMultiBlockComponent) {
                ((IMultiBlockComponent) tile).syncMultiBlockToClient();
            }
            return null;
        }
    }
}

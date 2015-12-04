package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.IIrrigationComponent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MessageSyncFluidLevel extends MessageAgriCraft {
    int lvl;
    int x;
    int y;
    int z;

    @SuppressWarnings("unused")
    public MessageSyncFluidLevel() {}

    public MessageSyncFluidLevel(int lvl, int x, int y, int z) {
        this.lvl = lvl;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.lvl = buf.readInt();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(lvl);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public static class MessageHandler implements IMessageHandler<MessageSyncFluidLevel, IMessage> {
        @Override
        public IMessage onMessage(MessageSyncFluidLevel message, MessageContext ctx) {
            World world = AgriCraft.proxy.getClientWorld();
            if(world != null) {
                TileEntity tile = world.getTileEntity(message.x, message.y, message.z);
                if(tile!=null && (tile instanceof IIrrigationComponent)) {
                    ((IIrrigationComponent) tile).setFluidLevel(message.lvl);
                }
            }
            return null;
        }
    }
}

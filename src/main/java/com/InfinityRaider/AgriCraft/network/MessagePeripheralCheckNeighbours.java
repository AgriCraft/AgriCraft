package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MessagePeripheralCheckNeighbours extends MessageAgriCraft {
    private int x;
    private int y;
    private int z;

    @SuppressWarnings("unused")
    public MessagePeripheralCheckNeighbours() {}

    public MessagePeripheralCheckNeighbours(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }

    public static class MessageHandler implements IMessageHandler<MessagePeripheralCheckNeighbours, IMessage> {
        @Override
        public IMessage onMessage(MessagePeripheralCheckNeighbours message, MessageContext ctx) {
            World world = AgriCraft.proxy.getClientWorld();
            if(world != null) {
                TileEntity te = world.getTileEntity(message.x, message.y, message.z);
                if(te != null && te instanceof TileEntityPeripheral) {
                    ((TileEntityPeripheral) te).checkSides();
                }
            }
            return null;
        }
    }
}

package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePeripheralCheckNeighbours extends MessageAgriCraft {
    private BlockPos pos;

    @SuppressWarnings("unused")
    public MessagePeripheralCheckNeighbours() {}

    public MessagePeripheralCheckNeighbours(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = readBlockPosFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writeBlockPosToByteBuf(buf, pos);
    }

    public static class MessageHandler implements IMessageHandler<MessagePeripheralCheckNeighbours, IMessage> {
        @Override
        public IMessage onMessage(MessagePeripheralCheckNeighbours message, MessageContext ctx) {
            World world = AgriCraft.proxy.getClientWorld();
            if(world != null) {
                TileEntity te = world.getTileEntity(message.pos);
                if(te != null && te instanceof TileEntityPeripheral) {
                    ((TileEntityPeripheral) te).checkSides();
                }
            }
            return null;
        }
    }
}

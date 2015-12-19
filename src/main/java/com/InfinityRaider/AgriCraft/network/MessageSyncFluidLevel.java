package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.IIrrigationComponent;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSyncFluidLevel extends MessageAgriCraft {
    private int lvl;
    private BlockPos pos;

    @SuppressWarnings("unused")
    public MessageSyncFluidLevel() {}

    public MessageSyncFluidLevel(int lvl, BlockPos pos) {
        this.lvl = lvl;
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.lvl = buf.readInt();
        this.pos = readBlockPosFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(lvl);
        this.writeBlockPosToByteBuf(buf, pos);
    }

    public static class MessageHandler implements IMessageHandler<MessageSyncFluidLevel, IMessage> {
        @Override
        public IMessage onMessage(MessageSyncFluidLevel message, MessageContext ctx) {
            World world = AgriCraft.proxy.getClientWorld();
            if(world != null) {
                TileEntity tile = world.getTileEntity(message.pos);
                if(tile!=null && (tile instanceof IIrrigationComponent)) {
                    ((IIrrigationComponent) tile).setFluidLevel(message.lvl);
                }
            }
            return null;
        }
    }
}

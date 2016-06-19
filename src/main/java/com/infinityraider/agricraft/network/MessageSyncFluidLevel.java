package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IIrrigationComponent;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSyncFluidLevel extends MessageBase {
    private int lvl;
    private BlockPos pos;

    @SuppressWarnings("unused")
    public MessageSyncFluidLevel() {}

    public MessageSyncFluidLevel(int lvl, BlockPos pos) {
        this.lvl = lvl;
        this.pos = pos;
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        World world = AgriCraft.proxy.getClientWorld();
        if(world != null) {
            TileEntity tile = world.getTileEntity(this.pos);
            if(tile!=null && (tile instanceof IIrrigationComponent)) {
                ((IIrrigationComponent) tile).setFluidLevel(this.lvl);
            }
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
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
}

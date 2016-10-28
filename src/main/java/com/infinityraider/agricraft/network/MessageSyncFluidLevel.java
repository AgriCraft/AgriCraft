package com.infinityraider.agricraft.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.irrigation.IIrrigationComponent;
import com.infinityraider.agricraft.utility.AgriWorldHelper;
import com.infinityraider.infinitylib.network.MessageBase;
import io.netty.buffer.ByteBuf;

public class MessageSyncFluidLevel extends MessageBase<IMessage> {
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
            AgriWorldHelper.getTile(world, pos, IIrrigationComponent.class).ifPresent(c -> c.setFluidLevel(this.lvl));
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

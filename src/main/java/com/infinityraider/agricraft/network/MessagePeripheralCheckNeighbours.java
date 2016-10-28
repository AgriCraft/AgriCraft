package com.infinityraider.agricraft.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.compat.computer.tiles.TileEntityPeripheral;
import com.infinityraider.agricraft.utility.AgriWorldHelper;
import com.infinityraider.infinitylib.network.MessageBase;

import io.netty.buffer.ByteBuf;

public class MessagePeripheralCheckNeighbours extends MessageBase<IMessage> {
    private BlockPos pos;

    @SuppressWarnings("unused")
    public MessagePeripheralCheckNeighbours() {}

    public MessagePeripheralCheckNeighbours(BlockPos pos) {
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
            AgriWorldHelper.getTile(world, pos, TileEntityPeripheral.class).ifPresent(te -> te.checkSides());
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = readBlockPosFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writeBlockPosToByteBuf(buf, pos);
    }
}

package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.tileentity.peripheral.TileEntityPeripheral;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessagePeripheralCheckNeighbours extends MessageBase {
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
            TileEntity te = world.getTileEntity(this.pos);
            if(te != null && te instanceof TileEntityPeripheral) {
                ((TileEntityPeripheral) te).checkSides();
            }
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

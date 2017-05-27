package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.compat.computer.tiles.TileEntityPeripheral;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.utility.WorldHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessagePeripheralCheckNeighbours extends MessageBase<IMessage> {

    private BlockPos pos;

    public MessagePeripheralCheckNeighbours() {
    }

    public MessagePeripheralCheckNeighbours(BlockPos pos) {
        this();
        this.pos = pos;
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        World world = AgriCraft.proxy.getClientWorld();
        if (world != null) {
            WorldHelper.getTile(world, pos, TileEntityPeripheral.class).ifPresent(TileEntityPeripheral::checkSides);
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}

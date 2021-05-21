package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationComponent;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageIrrigationNeighbourUpdate extends MessageBase {
    private BlockPos pos;
    private Direction dir;

    public MessageIrrigationNeighbourUpdate() {
        super();
    }

    public MessageIrrigationNeighbourUpdate(BlockPos pos, Direction dir) {
        this();
        this.pos = pos;
        this.dir = dir;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        World world = AgriCraft.instance.getClientWorld();
        if(world != null && this.pos != null && this.dir != null) {
            TileEntity tile = world.getTileEntity(this.pos);
            if(tile instanceof TileEntityIrrigationComponent) {
                ((TileEntityIrrigationComponent) tile).onNeighbourUpdate(this.dir);
            }
        }
    }
}

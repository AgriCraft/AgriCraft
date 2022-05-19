package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationComponent;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

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
        Level world = AgriCraft.instance.getClientWorld();
        if(world != null && this.pos != null && this.dir != null) {
            BlockEntity tile = world.getBlockEntity(this.pos);
            if(tile instanceof TileEntityIrrigationComponent) {
                ((TileEntityIrrigationComponent) tile).onNeighbourUpdate(this.dir);
            }
        }
    }
}

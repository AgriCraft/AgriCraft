package com.infinityraider.agricraft.network;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSyncFluidAmount extends MessageBase {
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private RegistryKey<World> dimId;
    private BlockPos pos;
    private int fluidAmount;

    public MessageSyncFluidAmount() {
        final int count = COUNTER.addAndGet(1);
        if (count % 100 == 0) {
            AgriCore.getLogger("agricraft").debug("Created another hundred fluid update packets!");
        }
    }

    public MessageSyncFluidAmount(World world, BlockPos pos, int fluidAmount) {
        this();
        this.dimId = world.getDimensionKey();
        this.pos = pos;
        this.fluidAmount = fluidAmount;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        // Get world.
        final World world = AgriCraft.instance.getWorldFromDimension(this.dimId);

        // If world is null abort.
        if (world == null) {
            // TODO: Perhaps log this?
            return;
        }

        // Get component.
        final IAgriFluidComponentSyncable component = WorldHelper.getTile(world, pos, IAgriFluidComponentSyncable.class).orElse(null);

        // If tile is null abort.
        if (component == null) {
            // TODO: Perhaps log this?
            return;
        }

        // Now update the actual fluid level.
        component.setFluidAmount(fluidAmount);
    }
}

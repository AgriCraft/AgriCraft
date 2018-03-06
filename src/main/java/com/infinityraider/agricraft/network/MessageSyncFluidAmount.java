package com.infinityraider.agricraft.network;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.utility.IAgriFluidComponentSyncable;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSyncFluidAmount extends MessageBase<IMessage> {
    
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private int dimId;
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
        this.dimId = world.provider.getDimension();
        this.pos = pos;
        this.fluidAmount = fluidAmount;
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        // Get world.
        final World world = AgriCraft.proxy.getWorldByDimensionId(this.dimId);
        
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

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}

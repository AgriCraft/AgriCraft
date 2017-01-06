package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.irrigation.IIrrigationComponent;
import com.infinityraider.infinitylib.utility.WorldHelper;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSyncFluidLevel extends MessageBase<IMessage> {
    private int lvl;
    private BlockPos pos;

    public MessageSyncFluidLevel() {}

    public MessageSyncFluidLevel(int lvl, BlockPos pos) {
        this();
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
            WorldHelper.getTile(world, pos, IIrrigationComponent.class).ifPresent(c -> c.setFluidLevel(this.lvl));
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}

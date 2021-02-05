package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.core.TileEntitySeedAnalyzer;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSeedAnalyzerUpdate extends MessageBase {
    private BlockPos pos;

    public MessageSeedAnalyzerUpdate() {}

    public MessageSeedAnalyzerUpdate(TileEntitySeedAnalyzer tile) {
        this();
        this.pos = tile.getPos();
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.pos != null) {
            AgriCraft.instance.proxy().updateSeedAnalyzerOverlay(this.pos);
        }
    }
}

package com.infinityraider.agricraft.network.json;

import com.infinityraider.agricraft.impl.v1.CoreHandler;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

/**
 * Message to tell the CoreHandler that sync from server has been completed
 */
public class MessageNotifySyncComplete extends MessageBase {
    public MessageNotifySyncComplete() {
        super();
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        CoreHandler.onSyncComplete();
    }
}

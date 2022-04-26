package com.infinityraider.agricraft.network.json;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.json.AgriSaver;
import com.agricraft.agricore.templates.AgriPlant;
import com.agricraft.agricore.templates.AgriWeed;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.impl.v1.CoreHandler;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.nio.file.Path;
import java.util.List;

public class MessageSyncWeedJson extends MessageBase {
    private AgriWeed weed;
    private int index;
    private int count;

    @SuppressWarnings("unused")
    public MessageSyncWeedJson() {}

    public MessageSyncWeedJson(AgriWeed weed, int index, int count) {
        this.weed = weed;
        this.index = index;
        this.count = count;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if (this.index == 0) {
            AgriCore.getWeeds().clearElements();
        }

        // Add the weed
        AgriCore.getWeeds().addWeed(weed);
        AgriCore.getLogger("agricraft-net").debug("Received Weed {0} ({1} of {2}).", weed.getId(), index + 1, count);

        if (this.index == this.count - 1) {
            final Path worldDir = CoreHandler.getJsonDir().resolve(this.getServerId());
            AgriSaver.saveElements(worldDir, AgriCore.getWeeds().getAllElements());
            AgriCore.getWeeds().getAllElements().stream()
                    .map(weed -> AgriCraft.instance.proxy().jsonObjectFactory().createWeed(weed))
                    .forEach(AgriApi.getWeedRegistry()::add);
        }
    }

    @Override
    protected List<IMessageSerializer> getNecessarySerializers() {
        return ImmutableList.of(new JsonSerializer<AgriPlant>());
    }
}

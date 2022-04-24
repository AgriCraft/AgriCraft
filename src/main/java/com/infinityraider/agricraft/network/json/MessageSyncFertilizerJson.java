package com.infinityraider.agricraft.network.json;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.json.AgriSaver;
import com.agricraft.agricore.plant.AgriFertilizer;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.impl.v1.CoreHandler;
import com.infinityraider.agricraft.impl.v1.fertilizer.JsonFertilizer;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.nio.file.Path;
import java.util.List;

public class MessageSyncFertilizerJson extends MessageBase {
    private AgriFertilizer fertilizer;
    private int index;
    private int count;

    public MessageSyncFertilizerJson() {
    }

    public MessageSyncFertilizerJson(AgriFertilizer fertilizer, int index, int count) {
        this.fertilizer = fertilizer;
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
            AgriCore.getFertilizers().clearElements();
        }

        // Add the fertilizer
        AgriCore.getFertilizers().addFertilizer(fertilizer);
        AgriCore.getLogger("agricraft-net").debug("Received Fertilizer {0} ({1} of {2}).", fertilizer.getLangKey(), index + 1, count);

        if (this.index == this.count - 1) {
            final Path worldDir = CoreHandler.getJsonDir().resolve(this.getServerId());
            AgriSaver.saveElements(worldDir, AgriCore.getFertilizers().getAll());
            AgriCore.getFertilizers().getAll().stream()
                    .map(JsonFertilizer::new)
                    .forEach(AgriApi.getFertilizerAdapterizer()::registerAdapter);
        }
    }

    @Override
    protected List<IMessageSerializer> getNecessarySerializers() {
        return ImmutableList.of(new JsonSerializer<AgriFertilizer>());
    }
}

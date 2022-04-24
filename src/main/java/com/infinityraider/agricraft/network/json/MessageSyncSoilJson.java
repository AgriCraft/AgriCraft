package com.infinityraider.agricraft.network.json;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.json.AgriSaver;
import com.agricraft.agricore.templates.AgriSoil;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.impl.v1.CoreHandler;
import com.infinityraider.agricraft.impl.v1.requirement.JsonSoil;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.nio.file.Path;
import java.util.List;

public class MessageSyncSoilJson extends MessageBase {
    private AgriSoil soil;
    private int index;
    private int count;

    @SuppressWarnings("unused")
    public MessageSyncSoilJson() {}

    public MessageSyncSoilJson(AgriSoil soil, int index, int count) {
        this.soil = soil;
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
            AgriCore.getSoils().clearElements();
        }

        // Add the soil
        AgriCore.getSoils().addSoil(soil);
        AgriCore.getLogger("agricraft-net").debug("Received Soil {0} ({1} of {2}).", soil.getLangKey(), index + 1, count);

        if (this.index == this.count - 1) {
            final Path worldDir = CoreHandler.getJsonDir().resolve(this.getServerId());
            AgriSaver.saveElements(worldDir, AgriCore.getSoils().getAll());
            AgriCore.getSoils().getAll().stream()
                    .map(JsonSoil::new)
                    .forEach(AgriApi.getSoilRegistry()::add);
        }
    }

    @Override
    protected List<IMessageSerializer> getNecessarySerializers() {
        return ImmutableList.of(new JsonSerializer<AgriSoil>());
    }
}

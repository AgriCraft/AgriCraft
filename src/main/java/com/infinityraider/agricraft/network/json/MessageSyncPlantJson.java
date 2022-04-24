package com.infinityraider.agricraft.network.json;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.json.AgriSaver;
import com.agricraft.agricore.templates.AgriPlant;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.impl.v1.CoreHandler;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlant;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.nio.file.Path;
import java.util.List;

public class MessageSyncPlantJson extends MessageBase {
    private AgriPlant plant;
    private int index;
    private int count;

    @SuppressWarnings("unused")
    public MessageSyncPlantJson() {}

    public MessageSyncPlantJson(AgriPlant plant, int index, int count) {
        this.plant = plant;
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
            AgriCore.getPlants().clearElements();
        }

        // Add the plant
        AgriCore.getPlants().addPlant(plant);
        AgriCore.getLogger("agricraft-net").debug("Received Plant {0} ({1} of {2}).", plant.getId(), index + 1, count);

        if (this.index == this.count - 1) {
            final Path worldDir = CoreHandler.getJsonDir().resolve(this.getServerId());
            AgriSaver.saveElements(worldDir, AgriCore.getPlants().getAllElements());
            AgriCore.getPlants().getAllElements().stream()
                    .map(JsonPlant::new)
                    .forEach(AgriApi.getPlantRegistry()::add);
        }
    }

    @Override
    protected List<IMessageSerializer> getNecessarySerializers() {
        return ImmutableList.of(new JsonSerializer<AgriPlant>());
    }
}

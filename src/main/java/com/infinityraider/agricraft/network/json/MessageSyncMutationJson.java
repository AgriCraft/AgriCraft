package com.infinityraider.agricraft.network.json;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.json.AgriSaver;
import com.agricraft.agricore.plant.AgriMutation;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.core.CoreHandler;
import com.infinityraider.agricraft.core.JsonHelper;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSyncMutationJson extends MessageBase {
    private AgriMutation plant;
    private int index;
    private int count;

    @SuppressWarnings("unused")
    public MessageSyncMutationJson() {
    }

    public MessageSyncMutationJson(AgriMutation plant, int index, int count) {
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
            AgriCore.getMutations().clearElements();
        }

        // Add the soil
        AgriCore.getMutations().addMutation(plant);
        AgriCore.getLogger("agricraft-net").debug("Recieved Mutation ({0} of {1}).", index + 1, count);

        if (this.index == this.count - 1) {
            final Path worldDir = CoreHandler.getJsonDir().resolve(this.getServerId());
            AgriSaver.saveElements(worldDir, AgriCore.getMutations().getAll());
            AgriCore.getMutations().getAll().stream()
                    .map(JsonHelper::wrap)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(AgriApi.getMutationRegistry()::add);
        }
    }

    @Override
    protected List<IMessageSerializer> getNecessarySerializers() {
        return ImmutableList.of(new JsonSerializer<AgriMutation>());
    }
}

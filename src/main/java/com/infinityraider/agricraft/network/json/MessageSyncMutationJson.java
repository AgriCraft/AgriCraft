package com.infinityraider.agricraft.network.json;

import java.nio.file.Path;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.json.AgriSaver;
import com.agricraft.agricore.plant.AgriMutation;
import com.google.gson.Gson;
import com.infinityraider.agricraft.apiimpl.MutationRegistry;
import com.infinityraider.agricraft.core.CoreHandler;
import com.infinityraider.agricraft.core.JsonMutation;

public class MessageSyncMutationJson extends MessageSyncElement<AgriMutation> {

    private static final Gson gson = new Gson();

    public MessageSyncMutationJson() {
    }

    public MessageSyncMutationJson(AgriMutation mutation, int index, int count) {
        super(mutation, index, count);
    }

    @Override
    protected String toString(AgriMutation element) {
        return gson.toJson(element);
    }

    @Override
    protected AgriMutation fromString(String element) {
        return gson.fromJson(element, AgriMutation.class);
    }

    @Override
    public void onSyncStart(MessageContext ctx) {
        AgriCore.getMutations().clearElements();
    }

    @Override
    protected void onMessage(MessageContext ctx) {
        AgriCore.getMutations().addMutation(this.element);
    }

    @Override
    public void onFinishSync(MessageContext ctx) {
        final Path worldDir = CoreHandler.getJsonDir().resolve(this.getServerId());
        AgriSaver.saveElements(worldDir, AgriCore.getMutations().getAll());
        AgriCore.getMutations().getAll().stream()
                .map(JsonMutation::new)
                .forEach(MutationRegistry.getInstance()::addMutation);
    }

}

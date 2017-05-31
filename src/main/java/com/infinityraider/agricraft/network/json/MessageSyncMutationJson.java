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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageSyncMutationJson extends MessageBase<IMessage> {

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
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }

    @Override
    protected void processMessage(MessageContext ctx) {

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

    @SideOnly(Side.CLIENT)
    public final String getServerId() {
        final ServerData data = Minecraft.getMinecraft().getCurrentServerData();
        return "server_" + data.serverIP.replaceAll("\\.", "-").replaceAll(":", "_");
    }

}

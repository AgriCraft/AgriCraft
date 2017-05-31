package com.infinityraider.agricraft.network.json;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.json.AgriSaver;
import com.agricraft.agricore.plant.AgriPlant;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.core.CoreHandler;
import com.infinityraider.agricraft.core.JsonPlant;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageSyncPlantJson extends MessageBase<IMessage> {

    private AgriPlant plant;
    private int index;
    private int count;

    @SuppressWarnings("unused")
    public MessageSyncPlantJson() {
    }

    public MessageSyncPlantJson(AgriPlant plant, int index, int count) {
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
            AgriCore.getPlants().clearElements();
        }

        // Add the soil
        AgriCore.getPlants().addPlant(plant);
        AgriCore.getLogger("agricraft-net").debug("Recieved Plant {0} ({1} of {2}).", plant.getPlantName(), index + 1, count);

        if (this.index == this.count - 1) {
            final Path worldDir = CoreHandler.getJsonDir().resolve(this.getServerId());
            AgriSaver.saveElements(worldDir, AgriCore.getPlants().getAll());
            AgriCore.getPlants().getAll().stream()
                    .map(JsonPlant::new)
                    .forEach(AgriApi.getPlantRegistry()::add);
        }
    }

    @Override
    protected List<IMessageSerializer> getNecessarySerializers() {
        return ImmutableList.of(new JsonSerializer<AgriPlant>());
    }

    @SideOnly(Side.CLIENT)
    public final String getServerId() {
        final ServerData data = Minecraft.getMinecraft().getCurrentServerData();
        return "server_" + data.serverIP.replaceAll("\\.", "-").replaceAll(":", "_");
    }

}

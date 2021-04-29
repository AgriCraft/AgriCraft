package com.infinityraider.agricraft.network;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.capability.CapabilityResearchedPlants;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.network.serialization.IMessageReader;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import com.infinityraider.infinitylib.network.serialization.IMessageWriter;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;

public class MessagePlantResearched extends MessageBase {
    private IAgriPlant plant;

    public MessagePlantResearched() {
        super();
    }

    public MessagePlantResearched(IAgriPlant plant) {
        this();
        this.plant = plant;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_SERVER;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        CapabilityResearchedPlants.getInstance().researchPlant(ctx.getSender(), this.plant);
        CapabilityResearchedPlants.getInstance().configureJei();
    }

    @Override
    protected List<IMessageSerializer> getNecessarySerializers() {
        return ImmutableList.of(SERIALIZER);
    }

    private static final IMessageSerializer<IAgriPlant> SERIALIZER = new IMessageSerializer<IAgriPlant>() {
        private final IMessageWriter<IAgriPlant> writer = (buf, plant) ->
                buf.writeString(plant.getId());

        private final IMessageReader<IAgriPlant> reader = (buf) ->
                AgriApi.getPlantRegistry().get(buf.readString()).orElse(AgriApi.getPlantRegistry().getNoPlant());

        @Override
        public boolean accepts(Class<IAgriPlant> clazz) {
            return IAgriPlant.class.isAssignableFrom(clazz);
        }

        @Override
        public IMessageWriter<IAgriPlant> getWriter(Class<IAgriPlant> clazz) {
            return this.writer;
        }

        @Override
        public IMessageReader<IAgriPlant> getReader(Class<IAgriPlant> clazz) {
            return this.reader;
        }
    };
}

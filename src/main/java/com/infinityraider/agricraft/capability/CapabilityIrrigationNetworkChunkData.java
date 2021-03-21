package com.infinityraider.agricraft.capability;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationConnection;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationNetworkPart;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class CapabilityIrrigationNetworkChunkData implements IInfSerializableCapabilityImplementation<Chunk, CapabilityIrrigationNetworkChunkData.Impl> {
    private static final CapabilityIrrigationNetworkChunkData INSTANCE = new CapabilityIrrigationNetworkChunkData();

    public static CapabilityIrrigationNetworkChunkData getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(
            AgriCraft.instance.getModId().toLowerCase(), Names.Objects.IRRIGATION_NETWORK_DATA);

    @CapabilityInject(CapabilityIrrigationNetworkChunkData.Impl.class)
    public static final Capability<CapabilityIrrigationNetworkChunkData.Impl> CAPABILITY = null;

    public boolean registerPart(IrrigationNetworkPart part) {
        if(part.isValid()) {
            return part.getChunk().getCapability(this.getCapability())
                    .map(impl -> {
                        impl.registerPart(part);
                        return true;
                    }).orElse(false);
        }
        return false;
    }

    public IrrigationNetworkPart getPart(Chunk chunk, int id) {
        return chunk.getCapability(this.getCapability())
                .map(impl -> impl.getPart(id)).orElse(IrrigationNetworkPart.createEmpty(chunk));
    }

    @Override
    public Class<CapabilityIrrigationNetworkChunkData.Impl> getCapabilityClass() {
        return CapabilityIrrigationNetworkChunkData.Impl.class;
    }

    @Override
    public Capability<CapabilityIrrigationNetworkChunkData.Impl> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(Chunk carrier) {
        return true;
    }

    @Override
    public CapabilityIrrigationNetworkChunkData.Impl createNewValue(Chunk chunk) {
        return new CapabilityIrrigationNetworkChunkData.Impl(chunk);
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<Chunk> getCarrierClass() {
        return Chunk.class;
    }

    public static class Impl implements ISerializable {
        private final Chunk chunk;

        private final Map<Integer, IrrigationNetworkPart> parts;
        private final Map<Integer, IrrigationNetworkPart.Loader> loaders;

        private Impl(Chunk chunk) {
            this.chunk = chunk;
            this.parts = Maps.newHashMap();
            this.loaders = Maps.newHashMap();
        }

        public Chunk getChunk() {
            return this.chunk;
        }

        public IrrigationNetworkPart getPart(int id) {
            return this.parts.get(id);
        }

        private void registerPart(IrrigationNetworkPart part) {
            this.parts.put(part.getId(), part);
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            this.parts.clear();
            this.loaders.clear();
            if(!tag.contains(AgriNBT.ENTRIES)) {
                return;
            }
            AgriNBT.stream(tag.getList(AgriNBT.ENTRIES, 10)).forEach(partTag -> {
                int id = partTag.getInt(AgriNBT.NETWORK);
                int nodes = partTag.getInt(AgriNBT.ENTRIES);
                Consumer<IrrigationNetworkPart> finalizer = (part) -> {
                    this.registerPart(part);
                    this.loaders.remove(part.getId());
                };
                IrrigationNetworkPart.Loader loader = IrrigationNetworkPart.createLoader(id, this.getChunk(), nodes, finalizer);
                // TODO
                this.loaders.put(id, loader);
            });
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            ListNBT partTags = new ListNBT();
            this.parts.values().forEach(part -> {
                // Create tag for the part
                CompoundNBT partTag = new CompoundNBT();
                // Write network id
                partTag.putInt(AgriNBT.NETWORK, part.getId());
                // Write the number of nodes
                partTag.putInt(AgriNBT.ENTRIES, part.getConnections().size());
                // Write internal connections
                ListNBT connectionsList = new ListNBT();
                part.getConnections().values().stream().flatMap(Set::stream).forEach(connection -> {
                    // Add connection to the list
                    connectionsList.add(this.writeConnectionToNBT(connection));
                });
                partTag.put(AgriNBT.CONNECTIONS, connectionsList);
                // Write chunk connections
                ListNBT chunkConnectionsList = new ListNBT();
                part.getCrossChunkConnections().forEach((pos, connections) -> connections.forEach(connection -> {
                    // Create connection tag
                    CompoundNBT connectionTag = this.writeConnectionToNBT(connection);
                    // Write chunk data
                    connectionTag.putInt(AgriNBT.U1, pos.x);
                    connectionTag.putInt(AgriNBT.V1, pos.z);
                    // Add connection to the list
                    chunkConnectionsList.add(connectionTag);
                }));
                partTag.put(AgriNBT.CHUNK, chunkConnectionsList);
                // Add part list to the tag list
                partTags.add(partTag);
            });
            tag.put(AgriNBT.ENTRIES, partTags);
            return tag;
        }

        protected CompoundNBT writeConnectionToNBT(IAgriIrrigationConnection connection) {
            // Create tag
            CompoundNBT tag = new CompoundNBT();
            // Write from pos
            tag.putInt(AgriNBT.X1, connection.fromPos().getX());
            tag.putInt(AgriNBT.Y1, connection.fromPos().getY());
            tag.putInt(AgriNBT.Z1, connection.fromPos().getZ());
            // Write to pos
            tag.putInt(AgriNBT.X2, connection.toPos().getX());
            tag.putInt(AgriNBT.Y2, connection.toPos().getY());
            tag.putInt(AgriNBT.Z2, connection.toPos().getZ());
            // Write direction
            tag.putInt(AgriNBT.DIRECTION, connection.direction().ordinal());
            // Return the tag
            return tag;
        }
    }
}

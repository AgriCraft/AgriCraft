package com.infinityraider.agricraft.impl.v1.irrigation;

import com.google.common.collect.*;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationConnection;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import com.infinityraider.agricraft.capability.CapabilityIrrigationNetworkData;
import com.infinityraider.agricraft.capability.CapabilityIrrigationNetworkManager;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class IrrigationNetwork implements IAgriIrrigationNetwork {
    public static IAgriIrrigationNetwork createNewNetwork(IAgriIrrigationComponent component) {
        World world = component.castToTile().getWorld();
        if(world == null) {
            return IrrigationNetworkInvalid.getInstance();
        }
        return new Builder(world).build(component);
    }

    public static IrrigationNetwork readFromNbt(World world, int id, CompoundNBT tag) {
        IrrigationNetwork network = new IrrigationNetwork(world, (nw) -> id, Maps.newIdentityHashMap());
        network.readFromNBT(tag);
        return network;
    }

    private final World world;
    private final int id;

    // Per chunk network parts
    private final Map<ChunkPos, IrrigationNetworkPart> parts;

    // Caches
    private Set<IAgriIrrigationNode> nodeCache;
    private Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connectionCache;

    // Fluid data
    private final List<IrrigationNetworkLayer> layers;
    private int capacity;
    private int contents;

    private IrrigationNetwork(@Nonnull World world, Function<IrrigationNetwork, Integer> idDefinition,
                              Map<Chunk, Function<IrrigationNetwork, IrrigationNetworkPart>> partFactories) {
        this.world = world;
        this.id = idDefinition.apply(this);
        this.parts = Maps.newHashMap();
        Maps.transformValues(partFactories, (factory) -> Objects.requireNonNull(factory).apply(this))
                .forEach((key, value) -> this.parts.put(key.getPos(), value));
        this.layers = this.compileLayers();
    }

    public final int getId() {
        return this.id;
    }

    /**
     * --------------------------
     * NETWORK MANAGEMENT METHODS
     * --------------------------
     */

    protected List<IrrigationNetworkLayer> compileLayers() {
        double[] limits = this.parts.values().stream()
                .flatMap(part -> part.getLayers().stream())
                .flatMap(layer -> Stream.of(layer.getMin(), layer.getMax()))
                .mapToDouble(Double::doubleValue)
                .sorted()
                .distinct()
                .toArray();
        List<IrrigationNetworkLayer> layers = Lists.newArrayList();
        int capacity = 0;
        for(int i = 0; i < limits.length - 1; i++) {
            double y1 = limits[i];
            double y2 = limits[1 + 1];
            int volume = this.parts.values().stream().mapToInt(part -> part.getCapacity(y1, y2)).sum();
            capacity += volume;
            layers.add(new IrrigationNetworkLayer(y2, y1, volume));
        }
        this.capacity = capacity;
        return layers;
    }

    public void onChunkLoaded(Chunk chunk) {
        if(this.parts.containsKey(chunk.getPos())) {
            IrrigationNetworkPart part = CapabilityIrrigationNetworkData.getInstance().getPart(chunk, this.getId());
            this.parts.put(chunk.getPos(), part);
            this.parts.values().forEach(aPart -> aPart.onChunkLoaded(chunk));
            this.nodeCache = null;
            this.connectionCache = null;
        }
    }

    public void onChunkUnloaded(Chunk chunk) {
        if(this.parts.containsKey(chunk.getPos())) {
            this.parts.put(chunk.getPos(), null);
            this.parts.values().forEach(aPart -> aPart.onChunkUnloaded(chunk));
            this.nodeCache = null;
            this.connectionCache = null;
        }
    }

    public CompoundNBT writeToNBT() {
        // Create new tag
        CompoundNBT tag = new CompoundNBT();
        // Write Chunk positions
        ListNBT chunkTags = new ListNBT();
        this.parts.keySet().forEach(pos -> {
            CompoundNBT chunkTag = new CompoundNBT();
            chunkTag.putInt(AgriNBT.X1, pos.x);
            chunkTag.putInt(AgriNBT.Z1, pos.z);
            chunkTags.add(chunkTag);
        });
        tag.put(AgriNBT.ENTRIES, chunkTags);
        // Write layers
        ListNBT layerTags = new ListNBT();
        this.layers.forEach(layer -> layerTags.add(layer.writeToTag()));
        tag.put(AgriNBT.LAYERS, layerTags);
        // Write capacity and contents
        tag.putInt(AgriNBT.CAPACITY, this.capacity);
        tag.putInt(AgriNBT.LEVEL, this.contents);
        // Return tag
        return tag;
    }

    public boolean readFromNBT(CompoundNBT tag) {
        boolean valid = true;
        this.parts.clear();
        // Read Chunk positions
        if(this.getWorld() != null && tag.contains(AgriNBT.ENTRIES)) {
            ListNBT chunkTags = tag.getList(AgriNBT.ENTRIES, 10);
            for(int i = 0; i < chunkTags.size(); i++) {
                CompoundNBT chunkTag = chunkTags.getCompound(i);
                if(chunkTag.contains(AgriNBT.X1) && chunkTag.contains(AgriNBT.Z1)) {
                    ChunkPos pos = new ChunkPos(chunkTag.getInt(AgriNBT.X1), chunkTag.getInt(AgriNBT.Z1));
                    this.parts.put(pos, null);
                } else {
                    valid = false;
                }
            }
        } else {
            valid = false;
        }
        // Read layers
        this.layers.clear();
        if(tag.contains(AgriNBT.LAYERS)) {
            ListNBT layerTags = tag.getList(AgriNBT.LAYERS, 10);
            for(int i = 0; i < layerTags.size(); i++) {
                IrrigationNetworkLayer layer = new IrrigationNetworkLayer(layerTags.getCompound(i));
                this.layers.add(i, layer);
                if(layer.getVolume() <= 0 || layer.getMin() >= layer.getMax()) {
                    valid = false;
                }
            }
        }
        // Read capacity and contents
        this.capacity = tag.contains(AgriNBT.CAPACITY) ? tag.getInt(AgriNBT.CAPACITY) : 0;
        this.contents = tag.contains(AgriNBT.LEVEL) ? tag.getInt(AgriNBT.LEVEL) : 0;
        if(this.capacity <= 0) {
            valid = false;
        }
        return valid;
    }

    /**
     * -----------
     * API METHODS
     * -----------
     */

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Nonnull
    @Override
    public Set<IAgriIrrigationNode> nodes() {
        if(this.nodeCache == null) {
            ImmutableSet.Builder<IAgriIrrigationNode> nodes = new ImmutableSet.Builder<>();
            this.parts.values().stream().flatMap(part -> part.getConnections().keySet().stream()).forEach(nodes::add);
            this.nodeCache = nodes.build();
        }
        return this.nodeCache;
    }

    @Nonnull
    @Override
    public Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections() {
        if(this.connectionCache == null) {
            Map<IAgriIrrigationNode, ImmutableSet.Builder<IAgriIrrigationConnection>> connections= Maps.newIdentityHashMap();
            this.nodes().forEach(node -> connections.put(node, new ImmutableSet.Builder<>()));
            this.parts.values().forEach(part -> {
                part.getConnections().values().stream()
                        .flatMap(Set::stream)
                        .forEach(connection -> connections.get(connection.from()).add(connection));
                part.getCrossChunkConnections().values().stream()
                        .flatMap(Collection::stream)
                        .filter(IrrigationNetworkCrossChunkConnection::isTargetChunkLoaded)
                        .forEach(connection -> connections.get(connection.from()).add(connection));
            });
            this.connectionCache = ImmutableMap.copyOf(Maps.transformValues(connections, ImmutableSet.Builder::build));
        }
        return this.connectionCache;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public double fluidHeight() {
        int remaining = this.capacity();
        IrrigationNetworkLayer last = null;
        for(IrrigationNetworkLayer layer : this.layers) {
            if(remaining <= layer.getVolume()) {
                return layer.getHeight(remaining);
            }
            remaining -= layer.getVolume();
            last = layer;
        }
        return last == null ? 0 : last.getMax();
    }

    @Override
    public int contents() {
        return this.contents;
    }

    @Override
    public void setContents(int value) {
        this.contents = Math.min(this.capacity(), Math.max(0, value));
    }

    @Nonnull
    @Override
    public FluidStack contentAsFluidStack() {
        return this.contents() > 0 ? new FluidStack(Fluids.WATER, this.contents()) : FluidStack.EMPTY;
    }

    /**
     * -------
     * BUILDER
     * -------
     */

    protected static class Builder {
        private final World world;

        private final Map<Chunk, IrrigationNetworkPart.Builder> partBuilders;
        private final Map<Chunk, Set<IrrigationNetworkCrossChunkConnection>> crossChunkConnections;
        private final Map<Chunk, Set<PotentialNode>> potentialConnections;

        private Builder(World world) {
            this.world = world;
            this.partBuilders = Maps.newIdentityHashMap();
            this.crossChunkConnections = Maps.newIdentityHashMap();
            this.potentialConnections = Maps.newIdentityHashMap();
        }

        protected IrrigationNetwork build(IAgriIrrigationComponent component) {
            // Initialize the first part builder
            Chunk chunk = this.getWorld().getChunkAt(component.castToTile().getPos());
            IrrigationNetworkPart.Builder partBuilder = new IrrigationNetworkPart.Builder(this, chunk);
            this.partBuilders.put(chunk, partBuilder);
            // Add the node to the first part builder and start iterating
            partBuilder.addNodeAndIterate(component.getNode());
            // Iterations are finished, compile the network
            return new IrrigationNetwork(
                    this.getWorld(),
                    (network) -> CapabilityIrrigationNetworkManager.getInstance().addNetworkToWorld(network),
                    Maps.transformEntries(this.partBuilders, (aChunk, builder) -> {
                        Map<ChunkPos, Set<IrrigationNetworkCrossChunkConnection>> connectionMap = Maps.newHashMap();
                        this.crossChunkConnections.values().stream()
                                .flatMap(Collection::stream)
                                .forEach(connection ->
                                        connectionMap.computeIfAbsent(connection.getToChunkPos(), (pos) -> Sets.newHashSet())
                                                .add(connection));
                        return (network) -> Objects.requireNonNull(builder).build(network, connectionMap);
                    })
            );
        }

        public World getWorld() {
            return this.world;
        }

        protected void handleCrossChunkConnection(Chunk fromChunk, IAgriIrrigationNode from, Chunk toChunk, PotentialNode to) {
            if (this.potentialConnections.containsKey(fromChunk)) {
                // If the target chunk has been visited, we create a new cross-chunk connection and update the part in the chunk
                Set<PotentialNode> potentialNodes = this.potentialConnections.get(fromChunk);
                potentialNodes.stream()
                        .filter(node -> from == node.getNode())
                        .findAny()
                        .ifPresent(node -> {
                            potentialNodes.remove(node);
                            boolean flag = false;
                            if (from.canConnect(to.getNode(), to.getDirection())) {
                                this.crossChunkConnections.computeIfAbsent(fromChunk, chunk -> Sets.newIdentityHashSet())
                                        .add(new IrrigationNetworkCrossChunkConnection(
                                                from, to.getNode(), to.getFromPos(),  to.getDirection(), fromChunk.getPos()));
                                flag = true;
                            }
                            if (to.getNode().canConnect(from, to.getDirection().getOpposite())) {
                                this.crossChunkConnections.computeIfAbsent(toChunk, chunk -> Sets.newIdentityHashSet())
                                        .add(new IrrigationNetworkCrossChunkConnection(
                                                to.getNode(), from, to.getToPos(), to.getDirection().getOpposite(), toChunk.getPos()));
                                flag = true;
                            }
                            if(flag) {
                                this.partBuilders.computeIfAbsent(toChunk, chunk -> new IrrigationNetworkPart.Builder(this, chunk))
                                        .addNodeAndIterate(to.getNode());
                            }
                        });
            } else {
                // If the target chunk has not yet been visited, add it to the 'to-do' list
                this.potentialConnections.computeIfAbsent(toChunk, chunk -> Sets.newIdentityHashSet()).add(to);
            }
        }

    }

}

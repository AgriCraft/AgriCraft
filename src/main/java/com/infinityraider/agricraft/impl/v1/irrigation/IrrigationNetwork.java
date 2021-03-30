package com.infinityraider.agricraft.impl.v1.irrigation;

import com.google.common.collect.*;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationConnection;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import com.infinityraider.agricraft.capability.CapabilityIrrigationNetworkChunkData;
import com.infinityraider.agricraft.capability.CapabilityIrrigationNetworkManager;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This is the main implementation for IAgriIrrigationNetwork interface
 * The network is split up in different parts, where each part is within a Chunk.
 *
 * Parts are loaded and unloaded when their chunk is loaded and unloaded respectively.
 *
 * Alternative implementations, defined as inner classes are SingleNode and Invalid,
 * these represent networks consisting of a single node or are always invalid respectively.
 */
public class IrrigationNetwork extends IrrigationNetworkJoinable {
    public static IAgriIrrigationNetwork createSingleNodeNetwork(@Nonnull IAgriIrrigationComponent component, @Nonnull IAgriIrrigationNode node) {
        return new SingleNode(component, node);
    }

    public static IAgriIrrigationNetwork getInvalid() {
        return Invalid.getInstance();
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

    private IrrigationNetwork(SingleNode first, SingleNode second, Direction dir) {
        this.world = Objects.requireNonNull(first.getWorld(), "Can not initialize an irrigation network while the world is null");
        this.id = CapabilityIrrigationNetworkManager.getInstance().addNetworkToWorld(this);
        this.parts = Maps.newHashMap();
        // Populate parts map
        Chunk firstChunk = this.world.getChunkAt(first.getPos());
        Chunk secondChunk = this.world.getChunkAt(second.getPos());
        if(firstChunk == secondChunk) {
            // components are in the same chunk
            Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections = Maps.newIdentityHashMap();
            connections.put(first.getNode(), Sets.newIdentityHashSet());
            connections.get(first.getNode()).add(new IrrigationNetworkConnection(first.getNode(), second.getNode(), first.getPos(), dir));
            connections.put(second.getNode(), Sets.newIdentityHashSet());
            connections.get(second.getNode()).add(new IrrigationNetworkConnection(second.getNode(), first.getNode(), second.getPos(), dir.getOpposite()));
            IrrigationNetworkPart part = new IrrigationNetworkPart(this.getId(), firstChunk, connections, Maps.newHashMap(), Lists.newArrayList());
            this.parts.put(firstChunk.getPos(), part);
        } else {
            // Initialize first part
            Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> firstConnections = Maps.newIdentityHashMap();
            firstConnections.put(first.getNode(), Sets.newIdentityHashSet());
            Map<ChunkPos, Set<IrrigationNetworkConnection.CrossChunk>> firstChunkConnections = Maps.newHashMap();
            firstChunkConnections.put(secondChunk.getPos(), Sets.newIdentityHashSet());
            IrrigationNetworkConnection.CrossChunk firstConnection = new IrrigationNetworkConnection.CrossChunk(
                    first.getNode(), second.getNode(), first.getPos(), dir, secondChunk.getPos());
            firstChunkConnections.get(secondChunk.getPos()).add(firstConnection);
            IrrigationNetworkPart firstPart = new IrrigationNetworkPart(this.getId(), firstChunk, firstConnections, firstChunkConnections, Lists.newArrayList());
            // Initialize second part
            Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> secondConnections = Maps.newIdentityHashMap();
            secondConnections.put(second.getNode(), Sets.newIdentityHashSet());
            Map<ChunkPos, Set<IrrigationNetworkConnection.CrossChunk>> secondChunkConnections = Maps.newHashMap();
            secondChunkConnections.put(firstChunk.getPos(), Sets.newIdentityHashSet());
            IrrigationNetworkConnection.CrossChunk secondConnection = new IrrigationNetworkConnection.CrossChunk(
                    second.getNode(), first.getNode(), second.getPos(), dir.getOpposite(), firstChunk.getPos());
            secondChunkConnections.get(firstChunk.getPos()).add(secondConnection);
            IrrigationNetworkPart secondPart = new IrrigationNetworkPart(this.getId(), secondChunk, secondConnections, secondChunkConnections, Lists.newArrayList());
            // Add the parts
            this.parts.put(firstChunk.getPos(), firstPart);
            this.parts.put(secondChunk.getPos(), secondPart);
        }
        this.layers = this.compileLayers();
    }

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
    @Override
    protected Optional<IAgriIrrigationNetwork> joinComponent(
            @Nonnull IAgriIrrigationNode from,
            @Nonnull IAgriIrrigationNode to,
            @Nonnull IAgriIrrigationNetwork other,
            @Nonnull IAgriIrrigationComponent component,
            @Nonnull Direction dir) {
        // World null check, should always pass
        if(this.getWorld() == null) {
            return Optional.empty();
        }
        // Fetch positions and chunks
        BlockPos toPos = component.getTile().getPos();
        BlockPos fromPos = component.getTile().getPos().offset(dir.getOpposite());
        Chunk toChunk = this.getWorld().getChunkAt(toPos);
        Chunk fromChunk = this.getWorld().getChunkAt(fromPos);
        // Gather nodes and connections of the joining network
        Map<Chunk, Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>>> connectionMap = Maps.newIdentityHashMap();
        Map<ChunkPos, Map<ChunkPos, Set<IrrigationNetworkConnection.CrossChunk>>> chunkConnectionMap = Maps.newHashMap();
        other.connections().forEach((node, connections) -> connections.forEach(connection -> {
            // Fetch chunk
            Chunk chunk = this.getWorld().getChunkAt(connection.fromPos());
            // Ensure the node is in the connection map
            connectionMap.computeIfAbsent(chunk, (pos) -> Maps.newIdentityHashMap()).computeIfAbsent(node, (aNode) -> Sets.newIdentityHashSet());
            // Insert the connection in the correct connection map
            if(connection instanceof IrrigationNetworkConnection.CrossChunk) {
                IrrigationNetworkConnection.CrossChunk chunkConnection = (IrrigationNetworkConnection.CrossChunk) connection;
                chunkConnectionMap.computeIfAbsent(chunk.getPos(), (pos) -> Maps.newHashMap())
                        .computeIfAbsent(chunkConnection.getToChunkPos(), (pos) -> Sets.newIdentityHashSet())
                        .add(chunkConnection);
            } else {
                connectionMap.get(chunk).get(node).add(connection);
            }
        }));
        // Create new connection between this network and the new node
        if(fromChunk == toChunk) {
            connectionMap.computeIfAbsent(fromChunk, (pos) -> Maps.newIdentityHashMap())
                    .computeIfAbsent(from, node -> Sets.newIdentityHashSet())
                    .add(new IrrigationNetworkConnection(from, to, fromPos, dir));
            connectionMap.computeIfAbsent(toChunk, (pos) -> Maps.newIdentityHashMap())
                    .computeIfAbsent(to, node -> Sets.newIdentityHashSet())
                    .add(new IrrigationNetworkConnection(to, from, toPos, dir.getOpposite()));
        } else {
            chunkConnectionMap.computeIfAbsent(fromChunk.getPos(), (pos) -> Maps.newHashMap())
                    .computeIfAbsent(toChunk.getPos(), (pos) -> Sets.newIdentityHashSet())
                    .add(new IrrigationNetworkConnection.CrossChunk(from, to, fromPos, dir, toChunk.getPos()));
            chunkConnectionMap.computeIfAbsent(toChunk.getPos(), (pos) -> Maps.newHashMap())
                    .computeIfAbsent(fromChunk.getPos(), (pos) -> Sets.newIdentityHashSet())
                    .add(new IrrigationNetworkConnection.CrossChunk(to, from, toPos, dir.getOpposite(), fromChunk.getPos()));
        }
        // Populate parts
        connectionMap.forEach((chunk, connections) -> {
            // Fetch or create part
            IrrigationNetworkPart part = this.parts.computeIfAbsent(chunk.getPos(), (pos) ->
                    new IrrigationNetworkPart(this.getId(), chunk, Maps.newIdentityHashMap(), Maps.newHashMap(), Lists.newArrayList()));
            // Add connections
            part.addConnections(connections);
            // Add cross-chunk connections
            part.addCrossChunkConnections(chunkConnectionMap.getOrDefault(chunk.getPos(), Collections.emptyMap()));
            // Update ids
            part.setComponentIds();
        });
        // Return this
        return Optional.of(this);
    }

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
            IrrigationNetworkPart part = CapabilityIrrigationNetworkChunkData.getInstance().getPart(chunk, this.getId());
            this.parts.put(chunk.getPos(), part);   // TODO: make sure this is called after the part is deserialized
            this.parts.values().forEach(aPart -> aPart.onChunkLoaded(chunk));
            // Reset caches
            this.nodeCache = null;
            this.connectionCache = null;
        }
    }

    public void onChunkUnloaded(Chunk chunk) {
        if(this.parts.containsKey(chunk.getPos())) {
            this.parts.put(chunk.getPos(), null);
            this.parts.values().forEach(aPart -> aPart.onChunkUnloaded(chunk));
            // Reset caches
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
                        .filter(IrrigationNetworkConnection.CrossChunk::isTargetChunkLoaded)
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

    private static class SingleNode extends IrrigationNetworkJoinable {
        private final IAgriIrrigationComponent component;
        private final IAgriIrrigationNode node;

        private final Set<IAgriIrrigationNode> nodes;
        private final Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections;

        private SingleNode(IAgriIrrigationComponent component, IAgriIrrigationNode node) {
            this.component = component;
            this.node = node;
            this.nodes = ImmutableSet.of(this.getNode());
            this.connections = new ImmutableMap.Builder<IAgriIrrigationNode, Set<IAgriIrrigationConnection>>().put(this.getNode(), ImmutableSet.of()).build();
        }

        public IAgriIrrigationComponent getComponent() {
            return this.component;
        }

        public BlockPos getPos() {
            return this.getComponent().getTile().getPos();
        }

        public IAgriIrrigationNode getNode() {
            return this.node;
        }

        @Nullable
        @Override
        public World getWorld() {
            return this.getComponent().getTile().getWorld();
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Nonnull
        @Override
        public Set<IAgriIrrigationNode> nodes() {
            return this.nodes;
        }

        @Nonnull
        @Override
        public Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections() {
            return this.connections;
        }

        @Override
        public int capacity() {
            return this.getNode().getFluidCapacity();
        }

        @Override
        public double fluidHeight() {
            return this.getNode().getFluidHeight();
        }

        @Override
        public int contents() {
            return this.getNode().getFluidContents();
        }

        @Override
        public void setContents(int value) {
            this.getNode().setFluidContents(value);

        }

        @Nonnull
        @Override
        public FluidStack contentAsFluidStack() {
            return this.contents() <= 0 ? FluidStack.EMPTY : new FluidStack(Fluids.WATER, this.contents());
        }

        @Override
        protected Optional<IAgriIrrigationNetwork> joinComponent(
                @Nonnull IAgriIrrigationNode from,
                @Nonnull IAgriIrrigationNode to,
                @Nonnull IAgriIrrigationNetwork other,
                @Nonnull IAgriIrrigationComponent component,
                @Nonnull Direction dir) {
            if(other instanceof SingleNode) {
                return Optional.of(new IrrigationNetwork(this, (SingleNode) other, dir));
            } else {
                if(other instanceof IrrigationNetwork) {
                    return ((IrrigationNetwork) other).joinComponent(to, from, this, this.getComponent(), dir.getOpposite());
                } else {
                    return other.tryJoinComponent(to, this.getComponent(), dir.getOpposite());
                }
            }
        }
    }

    private static class Invalid implements IAgriIrrigationNetwork {
        private static final Invalid INSTANCE = new Invalid();

        private static IAgriIrrigationNetwork getInstance() {
            return INSTANCE;
        }

        private Invalid() {}

        @Nullable
        @Override
        public World getWorld() {
            return null;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Nonnull
        @Override
        public Set<IAgriIrrigationNode> nodes() {
            return ImmutableSet.of();
        }

        @Nonnull
        @Override
        public Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections() {
            return ImmutableMap.of();
        }

        @Nonnull
        @Override
        public Optional<IAgriIrrigationNetwork> tryJoinComponent(
                @Nonnull IAgriIrrigationNode node,
                @Nonnull IAgriIrrigationComponent component,
                @Nonnull Direction dir) {
            return Optional.empty();
        }

        @Override
        public int capacity() {
            return 0;
        }

        @Override
        public double fluidHeight() {
            return 0;
        }

        @Override
        public int contents() {
            return 0;
        }

        @Override
        public void setContents(int value) {}

        @Nonnull
        @Override
        public FluidStack contentAsFluidStack() {
            return FluidStack.EMPTY;
        }
    }
}

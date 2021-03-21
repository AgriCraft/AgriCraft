package com.infinityraider.agricraft.impl.v1.irrigation;

import com.google.common.collect.*;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationConnection;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import com.infinityraider.agricraft.capability.CapabilityIrrigationComponent;
import com.infinityraider.agricraft.capability.CapabilityIrrigationNetworkReference;
import com.infinityraider.agricraft.capability.CapabilityIrrigationNetworkChunkData;
import com.infinityraider.agricraft.capability.CapabilityIrrigationNetworkManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IrrigationNetworkPart implements IAgriIrrigationNetwork {
    public static IrrigationNetworkPart createEmpty(Chunk chunk) {
        return new IrrigationNetworkPart(-1, chunk,
                Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList());
    }
    private final Chunk chunk;
    private final int networkId;

    private final Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections;
    private final Map<ChunkPos, Set<IrrigationNetworkCrossChunkConnection>> crossChunkConnections;
    private final List<IrrigationNetworkLayer> layers;

    private IrrigationNetworkPart(int networkId, Chunk chunk,
                                  Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections,
                                  Map<ChunkPos, Set<IrrigationNetworkCrossChunkConnection>> crossChunkConnections,
                                  List<IrrigationNetworkLayer> layers) {
        // Set all fields
        this.chunk = chunk;
        this.networkId = networkId;
        this.connections = connections;
        this.crossChunkConnections = crossChunkConnections;
        this.layers = layers;
        // Register the part in the Chunk data
        CapabilityIrrigationNetworkChunkData.getInstance().registerPart(this);
        // Set the network for all components
        Stream.concat(
                this.connections.values().stream().flatMap(Set::stream),
                this.crossChunkConnections.values().stream().flatMap(Set::stream)
        ).forEach(connection ->
            connection.from().getComponents().forEach(component ->
                    CapabilityIrrigationNetworkReference.getInstance().setIrrigationNetwork(
                            component, connection.direction(), this.networkId)));
    }

    public final Chunk getChunk() {
        return this.chunk;
    }

    public final IAgriIrrigationNetwork getNetwork() {
        return CapabilityIrrigationNetworkManager.getInstance().getNetwork(this.getWorld(), this.getId());
    }

    public final int getId() {
        return this.networkId;
    }

    protected Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> getConnections() {
        return this.connections;
    }

    protected Map<ChunkPos, Set<IrrigationNetworkCrossChunkConnection>> getCrossChunkConnections() {
        return this.crossChunkConnections;
    }

    public List<IrrigationNetworkLayer> getLayers() {
        return this.layers;
    }

    public int getCapacity(double height) {
        return this.getLayers().stream().mapToInt(layer -> layer.getCapacity(height)).sum();
    }

    public int getCapacity(double height1, double height2) {
        if(height2 == height1) {
            return 0;
        }
        if(height2 > height1) {
            return this.getCapacity(height2, height1);
        }
        return this.getCapacity(height1) - this.getCapacity(height2);
    }

    public void onChunkUnloaded(Chunk chunk) {
        this.getCrossChunkConnections().getOrDefault(chunk.getPos(), Collections.emptySet())
                .forEach(IrrigationNetworkCrossChunkConnection::onChunkUnloaded);
    }

    public void onChunkLoaded(Chunk chunk) {
        this.getCrossChunkConnections().getOrDefault(chunk.getPos(), Collections.emptySet())
                .forEach(connection -> {
                    Direction dir = connection.direction().getOpposite();
                    CapabilityIrrigationComponent.getInstance().acceptForNode(
                            chunk.getTileEntity(connection.toPos()), dir, connection::onChunkLoaded);
                });
    }

    @Nonnull
    @Override
    public World getWorld() {
        return this.getChunk().getWorld();
    }

    @Override
    public boolean isValid() {
        return this.getNetwork().isValid();
    }

    @Nonnull
    @Override
    public Set<IAgriIrrigationNode> nodes() {
        return this.getNetwork().nodes();
    }

    @Nonnull
    @Override
    public Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections() {
        return this.getNetwork().connections();
    }

    @Nonnull
    @Override
    public FluidStack contentAsFluidStack() {
        return this.getNetwork().contentAsFluidStack();
    }

    @Override
    public int capacity() {
        return this.getNetwork().capacity();
    }

    @Override
    public double fluidHeight() {
        return this.getNetwork().fluidHeight();
    }

    @Override
    public int contents() {
        return this.getNetwork().contents();
    }

    @Override
    public void setContents(int value) {
        this.getNetwork().setContents(value);
    }

    protected static class Builder {
        private final IrrigationNetwork.Builder parent;
        private final Chunk chunk;
        private final List<Double> limits;

        private final Map<IAgriIrrigationNode, List<PotentialNode>> toVisit;
        private final Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections;

        protected Builder(@Nonnull IrrigationNetwork.Builder parent, @Nonnull Chunk chunk) {
            this.parent = parent;
            this.chunk = chunk;
            this.limits = Lists.newArrayList();
            this.toVisit = Maps.newIdentityHashMap();
            this.connections = Maps.newIdentityHashMap();
        }

        public IrrigationNetwork.Builder getParent() {
            return this.parent;
        }

        public Chunk getChunk() {
            return this.chunk;
        }

        public World getWorld() {
            return this.getChunk().getWorld();
        }

        protected void addNodeAndIterate(IAgriIrrigationNode node) {
            if(this.addNode(node)) {
                this.iterate();
            }
        }

        protected IrrigationNetworkPart build(IrrigationNetwork network,
                                              Map<ChunkPos, Set<IrrigationNetworkCrossChunkConnection>> crossChunkConnections) {
            List<IrrigationNetworkLayer> layers = Lists.newArrayList();
            for(int i = 0; i < this.limits.size() - 1; i++) {
                double min = this.limits.get(i);
                double max = this.limits.get(i + 1);
                int volume = this.toVisit.keySet().stream()
                        .filter(node -> node.getMinFluidHeight() >= min)
                        .filter(node -> node.getMaxFluidHeight() <= max)
                        .mapToInt(node -> node.getFluidVolume(max) - node.getFluidVolume(min))
                        .sum();
                layers.add(new IrrigationNetworkLayer(min, max, volume));
            }
            return new IrrigationNetworkPart(network.getId(), this.getChunk(),
                    this.connections, crossChunkConnections, layers);
        }

        private void iterate() {
            boolean iterate = this.toVisit.entrySet().stream()
                    .filter(entry -> entry.getValue().size() > 0)
                    .findFirst()
                    .map(entry -> {
                        PotentialNode site = entry.getValue().remove(0);
                        this.checkConnection(entry.getKey(), site);
                        // A new node was treated, therefore we need to continue to iterate
                        return true;
                    })
                    // No new node was treated, therefore we can stop iterating
                    .orElse(false);
            if (iterate) {
                this.iterate();
            }
        }

        private boolean addNode(IAgriIrrigationNode node) {
            if (this.connections.containsKey(node)) {
                return false;
            }
            // Add limits
            this.addLimit(node.getMinFluidHeight());
            this.addLimit(node.getMaxFluidHeight());
            // Initialize entry in the connections map
            this.connections.put(node, Sets.newIdentityHashSet());
            // Find connections
            this.toVisit.put(node, node.getPotentialConnections().stream()
                    .map(tuple ->
                            CapabilityIrrigationComponent.getInstance().applyForComponent(
                                    this.getWorld().getTileEntity(tuple.getB()),
                                    tuple.getA().getOpposite(),
                                    component -> new PotentialNode(component, tuple.getA())
                            ))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList())
            );
            return true;
        }

        private void checkConnection(IAgriIrrigationNode node, PotentialNode site) {
            Chunk chunk = this.getWorld().getChunkAt(site.getToPos());
            if(this.getChunk() == chunk) {
                // Fetch the new node
                site.getNode().ifPresent(newNode -> {
                    // Check if the node can connect to the network via the visiting node
                    boolean forward = node.canConnect(newNode, site.getDirection());
                    boolean backward = newNode.canConnect(node, site.getDirection().getOpposite());
                    // Add the connections
                    if (forward) {
                        this.connections.get(node).add(new IrrigationNetworkConnection(
                                node, newNode, site.getFromPos(), site.getDirection()));
                    }
                    if (backward) {
                        this.connections.get(newNode).add(new IrrigationNetworkConnection(
                                newNode, node, site.getToPos(), site.getDirection().getOpposite()));
                    }
                    // Add the node to the network if it isn't there yet
                    if (!this.toVisit.containsKey(newNode)) {
                        if (forward || backward) {
                            this.addNode(newNode);
                        }
                    }
                });
            } else {
                this.getParent().handleCrossChunkConnection(this.getChunk(), node, chunk, site);
            }
        }

        private void addLimit(double limit) {
            int index;
            for(index = 0; index < this.limits.size(); index++) {
                if(limit < this.limits.get(index)) {
                    break;
                }
            }
            if(index == 0 || limit > this.limits.get(index - 1)) {
                this.limits.add(index, limit);
            }
        }
    }
}

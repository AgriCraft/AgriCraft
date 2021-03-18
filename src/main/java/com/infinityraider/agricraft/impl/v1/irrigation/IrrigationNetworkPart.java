package com.infinityraider.agricraft.impl.v1.irrigation;

import com.google.common.collect.*;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationConnection;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class IrrigationNetworkPart implements IAgriIrrigationNetwork {
    private final Chunk chunk;
    private final IrrigationNetwork network;

    private final Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections;
    private final Map<Direction, Set<IAgriIrrigationConnection>> crossChunkConnections;
    private final List<IrrigationNetworkLayer> layers;

    private IrrigationNetworkPart(IrrigationNetwork network, Chunk chunk,
                                  Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections,
                                  Map<Direction, Set<IAgriIrrigationConnection>> crossChunkConnections,
                                  List<IrrigationNetworkLayer> layers) {
        this.chunk = chunk;
        this.network = network;
        this.connections = connections;
        this.crossChunkConnections = crossChunkConnections;
        this.layers = layers;
    }

    public final Chunk getChunk() {
        return this.chunk;
    }

    public final IrrigationNetwork getNetwork() {
        return this.network;
    }

    public final int getId() {
        return this.getNetwork().getId();
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

    @Nullable
    @Override
    public World getWorld() {
        return this.getNetwork().getWorld();
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
            this.addNode(node);
            this.iterate();
        }

        protected IrrigationNetworkPart build(IrrigationNetwork network, Map<Direction,
                Set<IAgriIrrigationConnection>> crossChunkConnections) {
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
            return new IrrigationNetworkPart(network, this.getChunk(),
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

        private void addNode(IAgriIrrigationNode node) {
            // Add limits
            this.addLimit(node.getMinFluidHeight());
            this.addLimit(node.getMaxFluidHeight());
            // Initialize entry in the connections map
            this.connections.put(node, Sets.newIdentityHashSet());
            // Find connections
            this.toVisit.put(node, node.getPotentialConnections().stream()
                    .map(tuple -> {
                        TileEntity tile = this.getWorld().getTileEntity(tuple.getB());
                        if(tile instanceof IAgriIrrigationComponent) {
                            return new PotentialNode((IAgriIrrigationComponent) tile, tuple.getA());
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList())
            );
        }

        private void checkConnection(IAgriIrrigationNode node, PotentialNode site) {
            Chunk chunk = this.getWorld().getChunkAt(site.getPos());
            if(this.getChunk() == chunk) {
                // Fetch the new node
                IAgriIrrigationNode newNode = site.getNode();
                // Check if the node can connect to the network via the visiting node
                boolean forward = node.canConnect(newNode, site.getDirection());
                boolean backward = newNode.canConnect(node, site.getDirection().getOpposite());
                // Add the connections
                if (forward) {
                    this.connections.get(node).add(new IrrigationNetworkConnection(node, newNode, site.getDirection()));
                }
                if (backward) {
                    this.connections.get(newNode).add(new IrrigationNetworkConnection(newNode, node, site.getDirection().getOpposite()));
                }
                // Add the node to the network if it isn't there yet
                if (!this.toVisit.containsKey(newNode)) {
                    if (forward || backward) {
                        this.addNode(newNode);
                    }
                }
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

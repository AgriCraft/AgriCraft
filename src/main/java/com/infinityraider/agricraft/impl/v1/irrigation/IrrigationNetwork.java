package com.infinityraider.agricraft.impl.v1.irrigation;

import com.google.common.collect.*;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import com.infinityraider.agricraft.capability.CapabilityIrrigationNetworkData;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class IrrigationNetwork implements IAgriIrrigationNetwork {
    public static IAgriIrrigationNetwork buildNetwork(@Nonnull IAgriIrrigationComponent start) {
        TileEntity tile = start.castToTile();
        if(tile.getWorld() == null) {
            return new Invalid(start.getNode());
        }
        return new Builder(tile.getWorld()).build(start);
    }

    private final World world;
    private final int id;

    private final Map<IAgriIrrigationNode, Set<IAgriIrrigationNode>> connections;
    private final Set<IAgriIrrigationNode> nodesViewer;
    private final Map<IAgriIrrigationNode, Set<IAgriIrrigationNode>> connectionsViewer;

    private final List<Layer> layers;
    private int capacity;

    private int content;
    private double height;

    public IrrigationNetwork(@Nonnull World world, Map<IAgriIrrigationNode, Set<IAgriIrrigationNode>> connections, List<Layer> layers, int capacity) {
        this.world = world;
        this.id = CapabilityIrrigationNetworkData.getInstance().addNetworkToWorld(this);
        this.connections = connections;
        this.nodesViewer = Collections.unmodifiableSet(this.connections.keySet());
        this.connectionsViewer = Collections.unmodifiableMap(this.connections);
        this.layers = layers;
        this.capacity = capacity;
    }

    @Nonnull
    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public boolean isValid() {
        return this.id >= 0;
    }

    @Nonnull
    @Override
    public Set<IAgriIrrigationNode> nodes() {
        return this.nodesViewer;
    }

    @Nonnull
    @Override
    public Map<IAgriIrrigationNode, Set<IAgriIrrigationNode>> connections() {
        return this.connectionsViewer;
    }

    @Nonnull
    @Override
    public FluidStack contentAsFluidStack() {
        return this.contents() <= 0 ? FluidStack.EMPTY : new FluidStack(Fluids.WATER, this.contents());
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public double fluidHeight() {
        return this.height;
    }

    @Override
    public int contents() {
        return this.content;
    }

    @Override
    public void setContents(int value) {
        value = Math.min(this.capacity(), Math.max(0, value));
        if(value != this.contents()) {
            this.content = value;

        }
    }

    protected void calculateHeight() {
        int remaining = this.contents();
        for(Layer layer : this.layers) {
            if(remaining > layer.getVolume()) {
                remaining -= layer.getVolume();
            } else {
                this.height = layer.getHeight(remaining);
                break;
            }
        }
    }

    private static class Builder {
        private final World world;
        private final List<Double> limits;

        private final Map<IAgriIrrigationNode, List<IAgriIrrigationComponent>> toVisit;
        private final Map<IAgriIrrigationNode, Set<IAgriIrrigationNode>> connections;

        private Builder(@Nonnull World world) {
            this.world = world;
            this.limits = Lists.newArrayList();
            this.toVisit = Maps.newIdentityHashMap();
            this.connections = Maps.newIdentityHashMap();
        }

        public World getWorld() {
            return this.world;
        }

        public IrrigationNetwork build(IAgriIrrigationComponent init) {
            // Initialize
            this.init(init);
            // Iterate
            this.iterate();
            // Compile
            return this.compile();
        }

        private void init(IAgriIrrigationComponent init) {
            // Fetch and add first node
            this.addNode(init.getNode());
        }

        private void iterate() {
            boolean iterate = this.toVisit.entrySet().stream()
                    .filter(entry -> entry.getValue().size() > 0)
                    .findFirst()
                    .map(entry -> {
                        IAgriIrrigationComponent component = entry.getValue().remove(0);
                        this.checkConnection(entry.getKey(), component);
                        // A new node was treated, therefore we need to continue to iterate
                        return true;
                    })
                    // No new node was treated, therefore we can stop iterating
                    .orElse(false);
            if (iterate) {
                this.iterate();
            }
        }

        private IrrigationNetwork compile() {
            Set<IAgriIrrigationNode> nodes = ImmutableSet.copyOf(this.toVisit.keySet());
            ImmutableList.Builder<Layer> layerBuilder = new ImmutableList.Builder<>();
            int capacity = 0;
            for(int i = 0; i < this.limits.size() - 1; i++) {
                double min = this.limits.get(i);
                double max = this.limits.get(i + 1);
                int volume = nodes.stream()
                        .filter(node -> node.getMinFluidHeight() >= min)
                        .filter(node -> node.getMaxFluidHeight() <= max)
                        .mapToInt(node -> node.getFluidVolume(max) - node.getFluidVolume(min))
                        .sum();
                layerBuilder.add(new Layer(min, max, volume));
                capacity += volume;
            }
            return new IrrigationNetwork(this.getWorld(), this.connections, layerBuilder.build(), capacity);
        }

        private void addNode(IAgriIrrigationNode node) {
            // Add limits
            this.addLimit(node.getMinFluidHeight());
            this.addLimit(node.getMaxFluidHeight());
            // Initialize entry in the connections map
            this.connections.put(node, Sets.newIdentityHashSet());
            // Find connections
            this.toVisit.put(node, node.getPotentialNeighbours().stream()
                    .map(pos -> this.getWorld().getTileEntity(pos))
                    .filter(tile -> tile instanceof IAgriIrrigationComponent)
                    .map(tile -> (IAgriIrrigationComponent) tile)
                    .collect(Collectors.toList())
            );
        }

        private void checkConnection(IAgriIrrigationNode node, IAgriIrrigationComponent component) {
            // Fetch the new node
            IAgriIrrigationNode newNode = component.getNode();
            // Check if the node has already been visited
            if(this.toVisit.containsKey(newNode)) {
                return;
            }
            // Check if the node can connect to the network via the visiting node
            boolean forward = node.canConnect(newNode);
            boolean backward = newNode.canConnect(node);
            // Add the node to the network
            if(forward || backward) {
                this.addNode(newNode);
            }
            // Add the connections
            if(forward) {
                this.connections.get(node).add(newNode);
            }
            if(backward) {
                this.connections.get(newNode).add(node);
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

    private static class Layer {
        private final double min;
        private final double max;
        private final int volume;

        public Layer(double min, double max, int volume) {
            this.min = min;
            this.max = max;
            this.volume = volume;
        }

        public double getMin() {
            return this.min;
        }

        public double getMax() {
            return this.max;
        }

        public int getVolume() {
            return this.volume;
        }

        public double getHeight(int content) {
            if(content <= 0) {
                return this.getMin();
            }
            if(content >= this.getVolume()) {
                return this.getMax();
            }
            double f = (content + 0.0D)/this.getVolume();
            return MathHelper.lerp(f, this.getMin(), this.getMax());
        }
    }

    private static class Invalid implements IAgriIrrigationNetwork {
        private final IAgriIrrigationNode node;
        private final Set<IAgriIrrigationNode> nodes;
        private final Map<IAgriIrrigationNode, Set<IAgriIrrigationNode>> connections;

        private int contents;

        private Invalid(IAgriIrrigationNode node) {
            this.node = node;
            this.nodes = ImmutableSet.of(node);
            ImmutableMap.Builder<IAgriIrrigationNode, Set<IAgriIrrigationNode>> mapBuilder = new ImmutableMap.Builder<>();
            this.connections = mapBuilder.put(node, ImmutableSet.of()).build();
        }

        @Override
        public World getWorld() {
            return null;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public Set<IAgriIrrigationNode> nodes() {
            return this.nodes;
        }

        @Override
        public Map<IAgriIrrigationNode, Set<IAgriIrrigationNode>> connections() {
            return this.connections;
        }

        @Override
        public int capacity() {
            return this.node.getFluidCapacity();
        }

        @Override
        public double fluidHeight() {
            return this.node.getFluidHeight(this.contents());
        }

        @Override
        public int contents() {
            return this.contents;
        }

        @Override
        public void setContents(int value) {
            this.contents = value;
        }

        @Override
        public FluidStack contentAsFluidStack() {
            return new FluidStack(Fluids.WATER, this.contents());
        }
    }
}

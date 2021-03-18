package com.infinityraider.agricraft.impl.v1.irrigation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationConnection;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import com.infinityraider.agricraft.capability.CapabilityIrrigationNetworkManager;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class IrrigationNetwork implements IAgriIrrigationNetwork {

    private final World world;
    private final int id;

    // Per chunk network parts
    private final Map<Chunk, IrrigationNetworkPart> parts;

    // Cross chunk connections
    private final Map<Chunk, Set<IrrigationNetworkConnection>> crossChunkConnections;

    private IrrigationNetwork(@Nonnull World world,
                              Map<Chunk, Function<IrrigationNetwork, IrrigationNetworkPart>> partFactories,
                              Map<Chunk, Set<IrrigationNetworkConnection>> crossChunkConnections
                              ) {
        this.world = world;
        this.id = CapabilityIrrigationNetworkManager.getInstance().addNetworkToWorld(this);
        this.parts = Maps.transformValues(partFactories, factory -> Objects.requireNonNull(factory).apply(this));
        this.crossChunkConnections = crossChunkConnections;
    }

    public final int getId() {
        return this.id;
    }

    /**
     * --------------------------
     * NETWORK MANAGEMENT METHODS
     * --------------------------
     */

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
        return false;
    }

    @Override
    public Set<IAgriIrrigationNode> nodes() {
        return null;
    }

    @Override
    public Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections() {
        return null;
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
    public void setContents(int value) {

    }

    @Nonnull
    @Override
    public FluidStack contentAsFluidStack() {
        return null;
    }

    protected static class Builder {
        private final World world;

        private final Map<Chunk, IrrigationNetworkPart.Builder> partBuilders;
        private final Map<Chunk, Set<IrrigationNetworkConnection>> crossChunkConnections;
        private final Map<Chunk, Set<PotentialNode>> potentialConnections;

        private Builder(World world) {
            this.world = world;
            this.partBuilders = Maps.newIdentityHashMap();
            this.crossChunkConnections = Maps.newIdentityHashMap();
            this.potentialConnections = Maps.newIdentityHashMap();
        }

        protected IrrigationNetwork build(IAgriIrrigationComponent part) {
            // Initialize the first part builder
            Chunk chunk = this.getWorld().getChunkAt(part.castToTile().getPos());
            IrrigationNetworkPart.Builder partBuilder = new IrrigationNetworkPart.Builder(this, chunk);
            this.partBuilders.put(chunk, partBuilder);
            // Add the node to the first part builder and start iterating
            partBuilder.addNodeAndIterate(part.getNode());
            // Iterations are finished, compile the network
            return new IrrigationNetwork(
                    this.getWorld(),
                    Maps.transformValues(this.partBuilders, builder -> (Objects.requireNonNull(builder)::build)),
                    this.crossChunkConnections
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
                                        .add(new IrrigationNetworkConnection(from, to.getNode(), to.getDirection()));
                                flag = true;
                            }
                            if (to.getNode().canConnect(from, to.getDirection().getOpposite())) {
                                this.crossChunkConnections.computeIfAbsent(toChunk, chunk -> Sets.newIdentityHashSet())
                                        .add(new IrrigationNetworkConnection(to.getNode(), from, to.getDirection().getOpposite()));
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

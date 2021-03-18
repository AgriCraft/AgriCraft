package com.infinityraider.agricraft.impl.v1.irrigation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationConnection;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

class IrrigationNetworkInvalid implements IAgriIrrigationNetwork {
    private final IAgriIrrigationNode node;
    private final Set<IAgriIrrigationNode> nodes;
    private final Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections;

    private int contents;

    public IrrigationNetworkInvalid(IAgriIrrigationNode node) {
        this.node = node;
        this.nodes = ImmutableSet.of(node);
        ImmutableMap.Builder<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> mapBuilder = new ImmutableMap.Builder<>();
        this.connections = mapBuilder.put(node, ImmutableSet.of()).build();
    }

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
        return this.nodes;
    }

    @Nonnull
    @Override
    public Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections() {
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

    @Nonnull
    @Override
    public FluidStack contentAsFluidStack() {
        return new FluidStack(Fluids.WATER, this.contents());
    }
}

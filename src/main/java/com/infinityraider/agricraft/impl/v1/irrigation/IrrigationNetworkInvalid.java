package com.infinityraider.agricraft.impl.v1.irrigation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationConnection;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class IrrigationNetworkInvalid implements IAgriIrrigationNetwork {
    private static final IrrigationNetworkInvalid INSTANCE = new IrrigationNetworkInvalid();

    public static IAgriIrrigationNetwork getInstance() {
        return INSTANCE;
    }

    private IrrigationNetworkInvalid() {}

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

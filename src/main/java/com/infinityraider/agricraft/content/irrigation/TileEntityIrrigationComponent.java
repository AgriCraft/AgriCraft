package com.infinityraider.agricraft.content.irrigation;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import com.infinityraider.agricraft.capability.CapabilityIrrigationComponent;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class TileEntityIrrigationComponent extends TileEntityDynamicTexture implements IAgriIrrigationComponent, IAgriIrrigationNode {
    private final int capacity;
    private final double minY;
    private final double maxY;

    private final AutoSyncedField<Integer> contents;
    private final AutoSyncedField<Double> waterLevel;

    private final ImmutableSet<IAgriIrrigationComponent> component;
    private final LazyOptional<IAgriIrrigationComponent> irrigationCapability;

    private Set<Tuple<Direction, BlockPos>> connections;


    public TileEntityIrrigationComponent(TileEntityType<?> type, int capacity, double minY, double maxY) {
        super(type);
        this.capacity = capacity;
        this.minY = minY;
        this.maxY = maxY;
        this.contents = this.getAutoSyncedFieldBuilder(0).build();
        this.waterLevel = this.getAutoSyncedFieldBuilder(this.minY).build();
        this.component = ImmutableSet.of(this);
        this.irrigationCapability = LazyOptional.of(() -> this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (side != null && side.getAxis().isHorizontal()) {
            if(cap == CapabilityIrrigationComponent.getInstance().getCapability()) {
                return this.irrigationCapability.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public void onNetworkContentsChanged(Direction side) {
        double h = this.getNetwork(side).fluidHeight();
        h = Math.min(this.maxY, h);
        if(h <= this.minY) {
            this.waterLevel.set(0.0);
        } else {
            this.waterLevel.set(h);
        }
    }

    @Override
    public ImmutableSet<IAgriIrrigationComponent> getComponents() {
        return this.component;
    }

    @Override
    public double getMinFluidHeight() {
        return this.getPos().getY() + this.minY;
    }

    @Override
    public double getMaxFluidHeight() {
        return this.getPos().getY() + this.maxY;
    }

    @Override
    public int getFluidCapacity() {
        return this.capacity;
    }

    @Override
    public int getFluidContents() {
        return this.contents.get();
    }

    @Override
    public void setFluidContents(int volume) {
        this.contents.set(Math.min(this.getFluidCapacity(), Math.max(0, volume)));
    }

    @Override
    public Set<Tuple<Direction, BlockPos>> getPotentialConnections() {
        if(this.connections == null) {
            this.connections = ImmutableSet.of(
                    new Tuple<>(Direction.NORTH, this.getPos().offset(Direction.NORTH)),
                    new Tuple<>(Direction.EAST, this.getPos().offset(Direction.EAST)),
                    new Tuple<>(Direction.SOUTH, this.getPos().offset(Direction.SOUTH)),
                    new Tuple<>(Direction.WEST, this.getPos().offset(Direction.WEST))
            );
        }
        return this.connections;
    }
}

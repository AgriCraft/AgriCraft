package com.infinityraider.agricraft.content.irrigation;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class TileEntityIrrigationComponent extends TileEntityDynamicTexture implements IAgriIrrigationComponent, IAgriIrrigationNode {
    private final int capacity;
    private final double minY;
    private final double maxY;

    private Set<Tuple<Direction, BlockPos>> connections;

    public TileEntityIrrigationComponent(TileEntityType<?> type, int capacity, double minY, double maxY) {
        super(type);
        this.capacity = capacity;
        this.minY = minY;
        this.maxY = maxY;
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

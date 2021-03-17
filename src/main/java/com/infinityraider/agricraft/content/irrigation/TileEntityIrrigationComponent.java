package com.infinityraider.agricraft.content.irrigation;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class TileEntityIrrigationComponent extends TileEntityDynamicTexture implements IAgriIrrigationComponent, IAgriIrrigationNode {
    private final int capacity;
    private final double minY;
    private final double maxY;

    private Set<BlockPos> neighbours;

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

    protected abstract int getSingleCapacity();

    @Override
    public boolean canConnect(@Nonnull IAgriIrrigationNode other) {
        if(other instanceof TileEntityIrrigationComponent) {
            return this.isSameMaterial((TileEntityIrrigationComponent) other);
        }
        return false;
    }

    @Override
    public Collection<BlockPos> getPotentialNeighbours() {
        if(this.neighbours == null) {
            this.neighbours = ImmutableSet.of(
                    this.getPos().offset(Direction.NORTH),
                    this.getPos().offset(Direction.EAST),
                    this.getPos().offset(Direction.SOUTH),
                    this.getPos().offset(Direction.WEST)
            );
        }
        return this.neighbours;
    }
}

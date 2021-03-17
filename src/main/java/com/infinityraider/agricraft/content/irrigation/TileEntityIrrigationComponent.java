package com.infinityraider.agricraft.content.irrigation;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class TileEntityIrrigationComponent extends TileEntityDynamicTexture implements IAgriIrrigationComponent, IAgriIrrigationNode {
    private final double minY;
    private final double maxY;

    private Set<BlockPos> neighbours;

    public TileEntityIrrigationComponent(TileEntityType<?> type, double minY, double maxY) {
        super(type);
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
    public int getFluidVolume(double fluidHeight) {
        return (int) MathHelper.lerp(
                (fluidHeight - this.getMinFluidHeight()) / (this.getMaxFluidHeight() - this.getMinFluidHeight()),
                0, this.getSingleCapacity()
        );
    }

    protected abstract int getSingleCapacity();

    @Override
    public boolean canConnect(@Nonnull IAgriIrrigationComponent other) {
        if(other instanceof TileEntityIrrigationComponent) {
            return this.isSameMaterial(other.castToTile());
        }
        return true;
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

    @Override
    public CompoundNBT serializeData() {
        //TODO: serialize water level
        return new CompoundNBT();
    }

    @Override
    public void deserializeData(CompoundNBT tag) {
        //TODO: deserialize water level
    }
}

package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IrrigationComponentCapability;
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
public abstract class TileEntityIrrigationComponent extends TileEntityDynamicTexture implements IAgriIrrigationComponent {
    private final int capacity;
    private final double minY;
    private final double maxY;

    private final AutoSyncedField<Integer> contents;
    private final AutoSyncedField<Double> waterLevel;

    private Set<Tuple<Direction, BlockPos>> connections;

    private final LazyOptional<IAgriIrrigationComponent> capability;

    public TileEntityIrrigationComponent(TileEntityType<?> type, int capacity, double minY, double maxY) {
        super(type);
        this.capacity = capacity;
        this.minY = minY;
        this.maxY = maxY;
        this.contents = this.getAutoSyncedFieldBuilder(0).build();
        this.waterLevel = this.getAutoSyncedFieldBuilder(this.minY).build();
        this.capability = LazyOptional.of(() -> this);
    }

    @Override
    public double getMinFluidHeight() {
        return this.minY;
    }

    @Override
    public double getMaxFluidHeight() {
        return this.maxY;
    }

    @Override
    public int getFluidCapacity() {
        return this.capacity;
    }

    @Override
    public TileEntity asTile() {
        return this;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == IrrigationComponentCapability.getCapability()) {
            return this.capability.cast();
        }
        return super.getCapability(cap, side);
    }
}

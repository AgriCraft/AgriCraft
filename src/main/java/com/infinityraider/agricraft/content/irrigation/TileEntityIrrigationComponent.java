package com.infinityraider.agricraft.content.irrigation;

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
public abstract class TileEntityIrrigationComponent extends TileEntityDynamicTexture {
    private final int capacity;
    private final double minY;
    private final double maxY;

    private final AutoSyncedField<Integer> contents;
    private final AutoSyncedField<Double> waterLevel;

    private Set<Tuple<Direction, BlockPos>> connections;


    public TileEntityIrrigationComponent(TileEntityType<?> type, int capacity, double minY, double maxY) {
        super(type);
        this.capacity = capacity;
        this.minY = minY;
        this.maxY = maxY;
        this.contents = this.getAutoSyncedFieldBuilder(0).build();
        this.waterLevel = this.getAutoSyncedFieldBuilder(this.minY).build();
    }
}

package com.infinityraider.agricraft.impl.v1.irrigation;

import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class IrrigationNetworkCrossChunkConnection extends IrrigationNetworkConnection {
    private final ChunkPos toChunk;

    private IAgriIrrigationNode to;

    public IrrigationNetworkCrossChunkConnection(IAgriIrrigationNode from, IAgriIrrigationNode to,
                                                 BlockPos fromPos, Direction direction, ChunkPos toChunk) {
        super(from, null, fromPos, direction);
        this.toChunk = toChunk;
        this.to = to;
    }

    public ChunkPos getToChunkPos() {
        return this.toChunk;
    }

    @Override
    public IAgriIrrigationNode to() {
        return this.to;
    }

    public boolean isTargetChunkLoaded() {
        return this.to() != null;
    }

    public void onChunkUnloaded() {
        this.to = null;
    }

    public void onChunkLoaded(IAgriIrrigationNode to) {
        this.to = to;
    }
}

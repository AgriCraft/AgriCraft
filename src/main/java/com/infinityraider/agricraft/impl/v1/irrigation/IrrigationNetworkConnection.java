package com.infinityraider.agricraft.impl.v1.irrigation;

import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationConnection;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class IrrigationNetworkConnection implements IAgriIrrigationConnection {
    private final IAgriIrrigationNode from;
    private final IAgriIrrigationNode to;
    private final BlockPos pos;
    private final Direction direction;

    public IrrigationNetworkConnection(IAgriIrrigationNode from, IAgriIrrigationNode to, BlockPos pos, Direction direction) {
        this.from = from;
        this.to = to;
        this.pos = pos;
        this.direction = direction;
    }

    @Override
    public IAgriIrrigationNode from() {
        return this.from;
    }

    @Override
    public IAgriIrrigationNode to() {
        return this.to;
    }

    @Override
    public BlockPos fromPos() {
        return this.pos;
    }

    @Override
    public Direction direction() {
        return this.direction;
    }

    public static class CrossChunk extends IrrigationNetworkConnection {
        private final ChunkPos toChunk;

        private IAgriIrrigationNode to;

        public CrossChunk(IAgriIrrigationNode from, IAgriIrrigationNode to,
                          BlockPos fromPos, Direction direction, ChunkPos toChunk) {
            super(from, null, fromPos, direction);
            this.toChunk = toChunk;
            this.to = to;
        }

        public CrossChunk(IAgriIrrigationNode from,
                          BlockPos fromPos, Direction direction, ChunkPos toChunk) {
            this(from, null, fromPos, direction, toChunk);
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
}

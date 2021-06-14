package com.infinityraider.agricraft.content.world;

import com.google.common.collect.Maps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.stream.Collectors;

public class GreenHouse {
    private final Map<ChunkPos, Part> parts;
    private final BlockPos min;
    private final BlockPos max;

    public GreenHouse(Map<BlockPos, GreenHouse.BlockType> blocks) {
        BlockPos.Mutable min = new BlockPos.Mutable(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        BlockPos.Mutable max = new BlockPos.Mutable(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        this.parts = blocks.entrySet().stream().collect(Collectors.toMap(
                entry -> new ChunkPos(entry.getKey()),
                entry -> handleMapEntry(entry, min, max),
                GreenHouse::mergeMaps
        )).entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                (entry) -> new Part(this, entry.getKey(), entry.getValue())
        ));
        this.min = min.toImmutable();
        this.max = max.toImmutable();
    }

    public BlockPos getMin() {
        return this.min;
    }

    public BlockPos getMax() {
        return this.max;
    }

    public Part getPart(BlockPos pos) {
        return this.getPart(new ChunkPos(pos));
    }

    public Part getPart(ChunkPos pos) {
        return this.parts.get(pos);
    }

    public BlockType getType(BlockPos pos) {
        if(this.isInRange(pos)) {
            return this.getPart(pos).getType(pos);
        }
        return BlockType.EXTERIOR;
    }

    public boolean isInside(BlockPos pos) {
        return this.getType(pos).isInterior();
    }

    protected boolean isInRange(BlockPos pos) {
        return pos.getX() >= this.getMin().getX() && pos.getX() <= this.getMax().getX()
                && pos.getY() >= this.getMin().getY() && pos.getY() <= this.getMax().getY()
                && pos.getZ() >= this.getMin().getZ() && pos.getZ() <= this.getMax().getZ();
    }

    public static class Part {
        private final GreenHouse greenHouse;
        private final ChunkPos chunk;
        private final Map<BlockPos, Block> blocks;

        protected Part(GreenHouse greenHouse, ChunkPos chunk, Map<BlockPos, GreenHouse.Block> blocks) {
            this.blocks = blocks;
            this.greenHouse = greenHouse;
            this.chunk = chunk;
        }

        public GreenHouse getGreenHouse() {
            return this.greenHouse;
        }

        public ChunkPos getChunk() {
            return this.chunk;
        }

        public BlockType getType(BlockPos pos) {
            Block block = this.getBlock(pos);
            return block == null ? BlockType.EXTERIOR : block.getType();
        }

        @Nullable
        public Block getBlock(BlockPos pos) {
            return this.blocks.getOrDefault(pos, null);
        }
    }

    protected static class Block {
        private final BlockPos pos;
        private final BlockType type;

        protected Block(BlockPos pos, BlockType type) {
            this.pos = pos;
            this.type = type;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public BlockType getType() {
            return this.type;
        }
    }

    public enum BlockType {
        INTERIOR_AIR(true),
        INTERIOR_OTHER(true),
        BOUNDARY(false),
        EXTERIOR(false);

        private final boolean interior;

        BlockType(boolean interior) {
            this.interior = interior;
        }

        public boolean isInterior() {
            return this.interior;
        }

        public boolean isBoundary() {
            return this == BOUNDARY;
        }
    }

    private static Map<BlockPos, GreenHouse.Block> handleMapEntry(Map.Entry<BlockPos, GreenHouse.BlockType> entry,
                                                                  BlockPos.Mutable min, BlockPos.Mutable max) {
        Map<BlockPos, GreenHouse.Block> map = Maps.newHashMap();
        map.put(entry.getKey(), new Block(entry.getKey(), entry.getValue()));
        min.setPos(
                Math.min(entry.getKey().getX(), min.getX()),
                Math.min(entry.getKey().getY(), min.getY()),
                Math.min(entry.getKey().getZ(), min.getZ())
        );
        max.setPos(
                Math.max(entry.getKey().getX(), max.getX()),
                Math.max(entry.getKey().getY(), max.getY()),
                Math.max(entry.getKey().getZ(), max.getZ())
        );
        return map;
    }

    private static Map<BlockPos, GreenHouse.Block> mergeMaps(Map<BlockPos, GreenHouse.Block> a, Map<BlockPos, GreenHouse.Block> b) {
        a.putAll(b);
        return a;
    }
}

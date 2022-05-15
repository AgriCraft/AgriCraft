package com.infinityraider.agricraft.content.world;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GreenHouse {
    private final Map<ChunkPos, Part> parts;
    private final Properties properties;

    public GreenHouse(Map<BlockPos, GreenHouse.BlockType> blocks) {
        BlockPos.MutableBlockPos min = new BlockPos.MutableBlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        BlockPos.MutableBlockPos max = new BlockPos.MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        MutableInt interiorCounter = new MutableInt(0);
        MutableInt ceilingCounter = new MutableInt(0);
        MutableInt ceilingGlassCounter = new MutableInt(0);
        this.parts = blocks.entrySet().stream().collect(Collectors.toMap(
                entry -> new ChunkPos(entry.getKey()),
                entry -> handleMapEntry(entry, isCeiling(entry.getKey(), blocks), min, max, interiorCounter, ceilingCounter, ceilingGlassCounter),
                GreenHouse::mergeMaps
        )).entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                (entry) -> new Part(this, entry.getKey(), entry.getValue())
        ));
        this.properties = new Properties(min, max, interiorCounter.getValue(), ceilingCounter.getValue(), ceilingGlassCounter.getValue());
    }

    public boolean isValid() {
        return this.parts.values().stream().noneMatch(Part::hasGaps)
                && this.getCeilingGlassFraction() >= AgriCraft.instance.getConfig().greenHouseCeilingGlassFraction();
    }

    protected Properties getProperties() {
        return this.properties;
    }

    public BlockPos getMin() {
        return this.getProperties().getMin();
    }

    public BlockPos getMax() {
        return this.getProperties().getMax();
    }

    public double getCeilingGlassFraction() {
        return (this.getProperties().getCeilingGlassCount() + 0.0) / this.getProperties().getCeilingCount();
    }

    public Set<ChunkPos> getChunks() {
        return this.parts.keySet();
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

    public void convertAirBlocks(Level world) {
        this.parts.values().forEach(part -> part.replaceAirBlocks(world));
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
        private final Set<BlockPos> gaps;

        protected Part(GreenHouse greenHouse, ChunkPos chunk, Map<BlockPos, GreenHouse.Block> blocks) {
            this.blocks = blocks;
            this.greenHouse = greenHouse;
            this.chunk = chunk;
            this.gaps = Sets.newHashSet();
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

        public boolean hasGaps() {
            return this.gaps.size() > 0;
        }

        protected void replaceAirBlocks(Level world) {
            BlockState air = AgriApi.getAgriContent().getBlocks().getGreenHouseAirBlock().defaultBlockState();
            this.blocks.values().stream()
                    .filter(block -> block.getType().isAir())
                    .map(Block::getPos)
                    .forEach(pos -> world.setBlock(pos,air, 3));
        }

        public CompoundTag writeToTag() {
            CompoundTag tag = new CompoundTag();
            //TODO
            return tag;
        }
    }

    protected static class Block {
        private final BlockPos pos;
        private final BlockType type;
        private final boolean ceiling;

        protected Block(BlockPos pos, BlockType type, boolean ceiling) {
            this.pos = pos;
            this.type = type;
            this.ceiling = ceiling;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public BlockType getType() {
            return this.type;
        }

        public boolean isCeiling() {
            return this.ceiling;
        }
    }

    protected static class Properties {
        private BlockPos min;
        private BlockPos max;

        private int interiorCount;
        private int ceilingCount;
        private int ceilingGlassCount;

        public Properties(BlockPos min, BlockPos max, int interiorCount, int ceilingCount, int ceilingGlassCount) {
            this.min = min.immutable();
            this.max = max.immutable();
            this.interiorCount = interiorCount;
            this.ceilingCount = ceilingCount;
            this.ceilingGlassCount = ceilingGlassCount;
        }

        public BlockPos getMin() {
            return this.min;
        }

        public BlockPos getMax() {
            return this.max;
        }

        public int getInteriorCount() {
            return this.interiorCount;
        }

        public int getCeilingCount() {
            return this.ceilingCount;
        }

        public int getCeilingGlassCount() {
            return this.ceilingGlassCount;
        }
    }

    public enum BlockType {
        INTERIOR_AIR(true, false),
        INTERIOR_OTHER(true, false),
        BOUNDARY(false, true),
        GLASS(false, true),
        EXTERIOR(false, false);

        private final boolean interior;
        private final boolean boundary;

        BlockType(boolean interior, boolean boundary) {
            this.interior = interior;
            this.boundary = boundary;
        }

        public boolean isAir() {
            return this == INTERIOR_AIR;
        }

        public boolean isInterior() {
            return this.interior;
        }

        public boolean isBoundary() {
            return this.boundary;
        }

        public boolean isGlass() {
            return this == GLASS;
        }

        public boolean isExterior() {
            return this == EXTERIOR;
        }
    }

    private static Map<BlockPos, GreenHouse.Block> handleMapEntry(
            Map.Entry<BlockPos, GreenHouse.BlockType> entry, boolean ceiling, BlockPos.MutableBlockPos min, BlockPos.MutableBlockPos max,
            MutableInt interiorCounter, MutableInt ceilingCounter, MutableInt ceilingGlassCounter) {
        Map<BlockPos, GreenHouse.Block> map = Maps.newHashMap();
        map.put(entry.getKey(), new Block(entry.getKey(), entry.getValue(), ceiling));
        if(entry.getValue().isInterior()) {
            interiorCounter.increment();
        }
        if(ceiling) {
            ceilingCounter.increment();
            if(entry.getValue().isGlass()) {
                ceilingGlassCounter.increment();
            }
        }
        min.set(
                Math.min(entry.getKey().getX(), min.getX()),
                Math.min(entry.getKey().getY(), min.getY()),
                Math.min(entry.getKey().getZ(), min.getZ())
        );
        max.set(
                Math.max(entry.getKey().getX(), max.getX()),
                Math.max(entry.getKey().getY(), max.getY()),
                Math.max(entry.getKey().getZ(), max.getZ())
        );
        return map;
    }

    private static boolean isCeiling(BlockPos pos, Map<BlockPos, GreenHouse.BlockType> blocks) {
        if(blocks.containsKey(pos.above())) {
            return false;
        }
        BlockPos below = pos.below();
        return Optional.ofNullable(blocks.get(below))
                .map(BlockType::isInterior)
                .orElse(false);
    }

    private static Map<BlockPos, GreenHouse.Block> mergeMaps(Map<BlockPos, GreenHouse.Block> a, Map<BlockPos, GreenHouse.Block> b) {
        a.putAll(b);
        return a;
    }
}

package com.infinityraider.agricraft.content.world;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.capability.CapabilityGreenHouseParts;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GreenHouse {
    private int id;

    private final Map<ChunkPos, GreenHousePartHolder> parts;
    private Properties properties;

    public GreenHouse(CompoundTag tag) {
        this.id = tag.getInt(AgriNBT.KEY);
        this.parts = Maps.newHashMap();
        tag.getList(AgriNBT.ENTRIES, Tag.TAG_COMPOUND).stream()
                .filter(chunkTag -> chunkTag instanceof CompoundTag)
                .map(chunkTag -> (CompoundTag) chunkTag)
                .forEach(chunkTag -> {
                    ChunkPos pos = new ChunkPos(chunkTag.getInt(AgriNBT.X1), chunkTag.getInt(AgriNBT.Z1));
                    this.parts.put(pos, new GreenHousePartHolder(pos));
                });
    }

    public GreenHouse(Map<BlockPos, GreenHouse.BlockType> blocks) {
        this.id = -1;
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
                (entry) -> new GreenHousePartHolder(entry.getKey(), entry.getValue())
        ));
        this.properties = new Properties(min, max, interiorCounter.getValue(), ceilingCounter.getValue(), ceilingGlassCounter.getValue());
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isValid() {
        return /*this.parts.values().stream().noneMatch(GreenHousePart::hasGaps)
                && */this.getCeilingGlassFraction() >= AgriCraft.instance.getConfig().greenHouseCeilingGlassFraction();
    }

    public void onChunkLoaded(Level world, ChunkPos pos) {
        // Put a reference to the greenhouse part in the map
        LevelChunk chunk = world.getChunk(pos.x, pos.z);
        if(chunk != null) {
            CapabilityGreenHouseParts.getInstance().getCapability(chunk)
                    .map(o -> o)
                    .ifPresent(impl -> this.parts.put(pos, impl.getPartHolder(this.getId())));
        }
    }

    public void onChunkUnloaded(ChunkPos pos) {
        GreenHousePartHolder part = this.parts.get(pos);
        if(part != null) {
            part.unload();
        }
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

    public GreenHousePartHolder getPart(BlockPos pos) {
        return this.getPart(new ChunkPos(pos));
    }

    public GreenHousePartHolder getPart(ChunkPos pos) {
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

    public CompoundTag writeToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(AgriNBT.KEY, this.id);
        ListTag chunks = new ListTag();
        this.parts.keySet().forEach(pos -> {
            CompoundTag chunkTag = new CompoundTag();
            chunkTag.putInt(AgriNBT.X1, pos.x);
            chunkTag.putInt(AgriNBT.Z1, pos.z);
        });
        tag.put(AgriNBT.ENTRIES, chunks);
        return tag;
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

    private static boolean isCeiling(BlockPos pos, Map<BlockPos, GreenHouse.BlockType> blocks) {
        if(blocks.containsKey(pos.above())) {
            return false;
        }
        BlockPos below = pos.below();
        return Optional.ofNullable(blocks.get(below))
                .map(BlockType::isInterior)
                .orElse(false);
    }

    private static Map<BlockPos, GreenHousePartHolder.Block> handleMapEntry(
            Map.Entry<BlockPos, GreenHouse.BlockType> entry, boolean ceiling, BlockPos.MutableBlockPos min, BlockPos.MutableBlockPos max,
            MutableInt interiorCounter, MutableInt ceilingCounter, MutableInt ceilingGlassCounter) {
        Map<BlockPos, GreenHousePartHolder.Block> map = Maps.newHashMap();
        map.put(entry.getKey(), new GreenHousePartHolder.Block(entry.getKey(), entry.getValue(), ceiling));
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

    private static Map<BlockPos, GreenHousePartHolder.Block> mergeMaps(Map<BlockPos, GreenHousePartHolder.Block> a, Map<BlockPos, GreenHousePartHolder.Block> b) {
        a.putAll(b);
        return a;
    }
}

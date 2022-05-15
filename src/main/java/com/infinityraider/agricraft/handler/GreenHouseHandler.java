package com.infinityraider.agricraft.handler;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.AgriTags;
import com.infinityraider.agricraft.capability.CapabilityGreenHouse;
import com.infinityraider.agricraft.content.world.greenhouse.GreenHouseBlockType;
import com.infinityraider.agricraft.content.world.greenhouse.GreenHouseConfiguration;
import com.infinityraider.agricraft.content.world.greenhouse.GreenHousePart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.*;
import java.util.stream.Collectors;

public class GreenHouseHandler {
    private static final GreenHouseHandler INSTANCE = new GreenHouseHandler();

    public static GreenHouseHandler getInstance() {
        return INSTANCE;
    }

    private GreenHouseHandler() {}

    public void checkAndFormGreenHouse(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!state.isAir()) {
            return;
        }
        new GreenHouseFormer(world, pos).getGreenHouseConfig()
                .flatMap(config -> CapabilityGreenHouse.addGreenHouse(world, config))
                .ifPresent(greenHouse -> greenHouse.convertAirBlocks(world));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onChunkLoad(ChunkEvent.Load event) {
        if(event.getWorld() instanceof Level) {
            CapabilityGreenHouse.onChunkLoad((Level) event.getWorld(), event.getChunk().getPos());
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onChunkUnload(ChunkEvent.Unload event) {
        if(event.getWorld() instanceof Level) {
            CapabilityGreenHouse.onChunkUnload((Level) event.getWorld(), event.getChunk().getPos());
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onChunkWrite(ChunkDataEvent.Save event) {
        if(event.getWorld() instanceof Level) {

        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onChunkRead(ChunkDataEvent.Load event) {
        if(event.getWorld() instanceof Level) {

        }
    }

    public static class GreenHouseFormer {
        private final Level world;

        private final Set<BlockPos> toVisit;
        private final Map<BlockPos, GreenHouseBlockType> visited;

        private int interiorCount;
        private boolean valid;
        private GreenHouseConfiguration greenHouse;

        protected GreenHouseFormer(Level world, BlockPos start) {
            this.world = world;
            this.toVisit = Sets.newConcurrentHashSet();
            this.visited = Maps.newHashMap();
            toVisit.add(start);
            this.valid = true;
        }

        public Optional<GreenHouseConfiguration> getGreenHouseConfig() {
            if(this.greenHouse == null) {
                this.run();
            }
            return Optional.ofNullable(this.greenHouse);
        }

        public Level getWorld() {
            return this.world;
        }

        public boolean isValid() {
            return this.valid;
        }

        public int getBlockLimit() {
            return AgriCraft.instance.getConfig().getGreenHouseBlockSizeLimit();
        }

        public boolean canContinue() {
            return this.interiorCount < this.getBlockLimit() && this.isValid();
        }

        public boolean hasVisited(BlockPos pos) {
            return this.visited.containsKey(pos);
        }

        public void recordVisit(BlockPos pos, GreenHouseBlockType type) {
            this.visited.put(pos, type);
        }

        protected void run() {
            Iterator<BlockPos> iterator = toVisit.iterator();
            while(iterator.hasNext() && this.canContinue()) {
                BlockPos pos = iterator.next();
                iterator.remove();
                this.checkPosition(pos);
            }
            if(this.toVisit.isEmpty()) {
                if(this.isValid()) {
                    this.defineConfiguration();
                }
            } else {
                if(this.canContinue()) {
                    this.run();
                } else {
                    // block limit reached
                    this.valid = false;
                }
            }
        }

        protected void defineConfiguration() {
            BlockPos.MutableBlockPos min = new BlockPos.MutableBlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
            BlockPos.MutableBlockPos max = new BlockPos.MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
            MutableInt interiorCounter = new MutableInt(0);
            MutableInt ceilingCounter = new MutableInt(0);
            MutableInt ceilingGlassCounter = new MutableInt(0);
            Set<Tuple<ChunkPos, Map<BlockPos, GreenHousePart.Block>>> parts = this.visited.entrySet().stream().collect(Collectors.toMap(
                    entry -> new ChunkPos(entry.getKey()),
                    entry -> handleMapEntry(entry, this.isCeiling(entry.getKey()), min, max, interiorCounter, ceilingCounter, ceilingGlassCounter),
                    GreenHouseHandler.GreenHouseFormer::mergeMaps
            )).entrySet().stream().map(entry -> new Tuple<>(entry.getKey(), entry.getValue())).collect(Collectors.toSet());
            this.greenHouse = new GreenHouseConfiguration(parts, min, max, interiorCounter.getValue(), ceilingCounter.getValue(), ceilingGlassCounter.getValue());
        }

        protected void checkPosition(BlockPos pos) {
            if(!this.hasVisited(pos)) {
                BlockState state = this.getWorld().getBlockState(pos);
                if(state.isAir()) {
                    this.recordVisit(pos, GreenHouseBlockType.INTERIOR_AIR);
                } else {
                    // initial block is not air
                    this.valid = false;
                }
            }
            Arrays.stream(Direction.values()).forEach(dir -> {
                BlockPos checkPos = pos.relative(dir);
                BlockState state = this.getWorld().getBlockState(checkPos);
                if(this.visited.containsKey(checkPos)) {
                    // If we already visited the position, check if it was a boundary, it might not be from another direction
                    GreenHouseBlockType type = this.visited.get(checkPos);
                    if (type.isBoundary()) {
                        if(!this.isSolidBlock(state, checkPos, dir)) {
                            // was a boundary when visited from another direction, but from the current direction it is not, update is needed
                            this.recordVisit(checkPos, GreenHouseBlockType.INTERIOR_OTHER);
                            this.toVisit.add(checkPos);
                            this.interiorCount++;
                        }
                    }
                } else {
                    // Block is not yet visited, categorize it
                    if(state.isAir()) {
                        this.recordVisit(checkPos, GreenHouseBlockType.INTERIOR_AIR);
                        this.toVisit.add(checkPos);
                        this.interiorCount++;
                    } else if(this.isGreenHouseGlass(state)) {
                        this.recordVisit(checkPos, GreenHouseBlockType.GLASS);
                    } else if(this.isSolidBlock(state, checkPos, dir)) {
                        this.recordVisit(checkPos, GreenHouseBlockType.BOUNDARY);
                    } else {
                        this.recordVisit(checkPos, GreenHouseBlockType.INTERIOR_OTHER);
                        this.toVisit.add(checkPos);
                        this.interiorCount++;
                    }
                }
            });
        }

        protected boolean isSolidBlock(BlockState state, BlockPos pos, Direction dir) {
            return state.getBlock() instanceof DoorBlock || state.isFaceSturdy(this.getWorld(), pos, dir) || state.isFaceSturdy(this.getWorld(), pos, dir.getOpposite());
        }

        protected boolean isGreenHouseGlass(BlockState state) {
            return state.is(Tags.Blocks.GLASS) || state.is(AgriTags.Blocks.GREENHOUSE_GLASS);
        }

        protected boolean isCeiling(BlockPos pos) {
            if(this.visited.containsKey(pos.above())) {
                return false;
            }
            BlockPos below = pos.below();
            return Optional.ofNullable(this.visited.get(below))
                    .map(GreenHouseBlockType::isInterior)
                    .orElse(false);
        }

        private static Map<BlockPos, GreenHousePart.Block> handleMapEntry(
                Map.Entry<BlockPos, GreenHouseBlockType> entry, boolean ceiling, BlockPos.MutableBlockPos min, BlockPos.MutableBlockPos max,
                MutableInt interiorCounter, MutableInt ceilingCounter, MutableInt ceilingGlassCounter) {
            Map<BlockPos, GreenHousePart.Block> map = Maps.newHashMap();
            map.put(entry.getKey(), new GreenHousePart.Block(entry.getKey(), entry.getValue(), ceiling));
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

        private static Map<BlockPos, GreenHousePart.Block> mergeMaps(Map<BlockPos, GreenHousePart.Block> a, Map<BlockPos, GreenHousePart.Block> b) {
            a.putAll(b);
            return a;
        }
    }
}

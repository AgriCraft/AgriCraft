package com.infinityraider.agricraft.handler;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.AgriTags;
import com.infinityraider.agricraft.api.v1.content.world.GreenHouseEvent;
import com.infinityraider.agricraft.api.v1.content.world.IAgriGreenHouse;
import com.infinityraider.agricraft.capability.CapabilityGreenHouse;
import com.infinityraider.agricraft.content.world.greenhouse.GreenHouseBlock;
import com.infinityraider.agricraft.content.world.greenhouse.GreenHouseConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.VanillaGameEvent;
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

    private final Map<ResourceKey<Level>, Set<Runnable>> tasks;

    private GreenHouseHandler() {
        this.tasks = Maps.newConcurrentMap();
    }

    public Optional<IAgriGreenHouse> checkAndFormGreenHouse(Level world, BlockPos pos) {
        if(world.isClientSide()) {
            return Optional.empty();
        }
        BlockState state = world.getBlockState(pos);
        if (!state.isAir()) {
            return Optional.empty();
        }
        return new GreenHouseFormer(world, pos).getGreenHouseConfig()
                .flatMap(config -> CapabilityGreenHouse.addGreenHouse(world, config))
                .map(greenHouse -> {
                    greenHouse.convertAirBlocks(world);
                    MinecraftForge.EVENT_BUS.post(new GreenHouseEvent.Created(greenHouse));
                    return greenHouse;
                });
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onChunkLoad(ChunkEvent.Load event) {
        if(event.getWorld() instanceof Level && !event.getWorld().isClientSide()) {
            final Level world = (Level) event.getWorld();
            final ChunkPos pos = event.getChunk().getPos();
            // we must make certain the world is fully loaded, or else a deadlock is caused
            this.tasks.computeIfAbsent(world.dimension(), key -> Sets.newConcurrentHashSet()).add(() -> CapabilityGreenHouse.onChunkLoad(world, pos));
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onServerTick(TickEvent.WorldTickEvent event) {
        // if there are no tasks, return
        if(this.tasks.isEmpty()) {
            return;
        }
        // if the world is client side, return
        if(event.world.isClientSide()) {
            return;
        }
        // if the event is not the end of a tick, return
        if(event.phase != TickEvent.Phase.END) {
            return;
        }
        // remove the tasks from the queue
        Set<Runnable> tasks = this.tasks.remove(event.world.dimension());
        // if there are no tasks for this world, return
        if(tasks == null) {
            return;
        }
        // run the tasks
        tasks.forEach(Runnable::run);
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
    public void onGameEvent(VanillaGameEvent event) {
        GameEvent type = event.getVanillaEvent();
        final Level world = event.getLevel();
        if (world.isClientSide() || !(world instanceof ServerLevel)) {
            return;
        }
        if (type == GameEvent.BLOCK_PLACE
                || type == GameEvent.BLOCK_CHANGE
                || type == GameEvent.BLOCK_DESTROY
                || type == GameEvent.BLOCK_ATTACH
                || type == GameEvent.BLOCK_DETACH) {
            // schedule task for the next tick such that the event has certainly passed and the block has been modified
            final BlockPos pos = event.getEventPosition();
            AgriCraft.instance.proxy().queueTask(() -> CapabilityGreenHouse.onBlockUpdated(world, pos));
        }
    }

    public static class GreenHouseFormer {
        private final Level world;

        private final Set<BlockPos> toVisit;
        private final Map<BlockPos, GreenHouseBlock.Type> visited;

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

        public void recordVisit(BlockPos pos, GreenHouseBlock.Type type) {
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
            MutableInt ceilingCounter = new MutableInt(0);
            MutableInt ceilingGlassCounter = new MutableInt(0);
            Set<Tuple<ChunkPos, Map<BlockPos, GreenHouseBlock>>> parts = this.visited.entrySet().stream().collect(Collectors.toMap(
                    entry -> new ChunkPos(entry.getKey()),
                    entry -> handleMapEntry(entry, this.isCeiling(entry.getKey()), min, max, ceilingCounter, ceilingGlassCounter),
                    GreenHouseHandler.GreenHouseFormer::mergeMaps
            )).entrySet().stream().map(entry -> new Tuple<>(entry.getKey(), entry.getValue())).collect(Collectors.toSet());
            this.greenHouse = new GreenHouseConfiguration(parts, min, max, ceilingCounter.getValue(), ceilingGlassCounter.getValue());
        }

        protected void checkPosition(BlockPos pos) {
            if(!this.hasVisited(pos)) {
                BlockState state = this.getWorld().getBlockState(pos);
                if(state.isAir()) {
                    this.recordVisit(pos, GreenHouseBlock.Type.INTERIOR_AIR);
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
                    GreenHouseBlock.Type type = this.visited.get(checkPos);
                    if (type.isBoundary()) {
                        if(!isSolidBlock(this.getWorld(), state, checkPos, dir)) {
                            // was a boundary when visited from another direction, but from the current direction it is not, update is needed
                            this.recordVisit(checkPos, GreenHouseBlock.Type.INTERIOR_OTHER);
                            this.toVisit.add(checkPos);
                            this.interiorCount++;
                        }
                    }
                } else {
                    // Block is not yet visited, categorize it
                    if(state.isAir()) {
                        this.recordVisit(checkPos, GreenHouseBlock.Type.INTERIOR_AIR);
                        this.toVisit.add(checkPos);
                        this.interiorCount++;
                    } else if(isGreenHouseGlass(state)) {
                        this.recordVisit(checkPos, GreenHouseBlock.Type.GLASS);
                    } else if(isSolidBlock(this.getWorld(), state, checkPos, dir)) {
                        this.recordVisit(checkPos, GreenHouseBlock.Type.BOUNDARY);
                    } else {
                        this.recordVisit(checkPos, GreenHouseBlock.Type.INTERIOR_OTHER);
                        this.toVisit.add(checkPos);
                        this.interiorCount++;
                    }
                }
            });
        }

        protected boolean isCeiling(BlockPos pos) {
            if(this.visited.containsKey(pos.above())) {
                return false;
            }
            BlockPos below = pos.below();
            return Optional.ofNullable(this.visited.get(below))
                    .map(GreenHouseBlock.Type::isInterior)
                    .orElse(false);
        }

        private static Map<BlockPos, GreenHouseBlock> handleMapEntry(
                Map.Entry<BlockPos, GreenHouseBlock.Type> entry, boolean ceiling, BlockPos.MutableBlockPos min, BlockPos.MutableBlockPos max,
                MutableInt ceilingCounter, MutableInt ceilingGlassCounter) {
            Map<BlockPos, GreenHouseBlock> map = Maps.newHashMap();
            map.put(entry.getKey(), new GreenHouseBlock(entry.getKey(), entry.getValue(), ceiling));
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

        private static Map<BlockPos, GreenHouseBlock> mergeMaps(Map<BlockPos, GreenHouseBlock> a, Map<BlockPos, GreenHouseBlock> b) {
            a.putAll(b);
            return a;
        }
    }

    public static boolean isSolidBlock(Level world, BlockState state, BlockPos pos, Direction dir) {
        return state.getBlock() instanceof DoorBlock || state.isFaceSturdy(world, pos, dir) || state.isFaceSturdy(world, pos, dir.getOpposite());
    }

    public static boolean isGreenHouseGlass(BlockState state) {
        return state.is(Tags.Blocks.GLASS) || state.is(AgriTags.Blocks.GREENHOUSE_GLASS);
    }
}

package com.infinityraider.agricraft.handler;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.world.GreenHouse;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GreenHouseHandler {
    private static final GreenHouseHandler INSTANCE = new GreenHouseHandler();

    public static GreenHouseHandler getInstance() {
        return INSTANCE;
    }

    private GreenHouseHandler() {}

    public void checkAndFormGreenHouse(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if(!state.getBlock().isAir(state, world, pos)) {
            return;
        }
        GreenHouseFormer former = new GreenHouseFormer(world, pos);
        former.run();
        if(former.isValid()) {
            // TODO: construct greenhouse chunk capability and store greenhouse instances in chunks
            former.convertAirBlocks();
        }
    }

    public static class GreenHouseFormer {
        private final World world;

        private final Set<BlockPos> toVisit;
        private final Map<BlockPos, GreenHouse.BlockType> visited;

        private BlockPos.Mutable min;
        private BlockPos.Mutable max;
        private int interiorCount;
        private boolean valid;

        protected GreenHouseFormer(World world, BlockPos start) {
            this.world = world;
            this.toVisit = Sets.newConcurrentHashSet();
            this.visited = Maps.newHashMap();
            toVisit.add(start);
            this.min = start.toMutable();
            this.max = start.toMutable();
            this.valid = true;
        }

        public World getWorld() {
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

        public void recordVisit(BlockPos pos, GreenHouse.BlockType type) {
            this.visited.put(pos, type);
            if(type.isInterior()) {
                this.interiorCount++;
            }
            this.min.setPos(
                    Math.min(pos.getX(), this.min.getX()),
                    Math.min(pos.getY(), this.min.getY()),
                    Math.min(pos.getZ(), this.min.getZ())
            );
            this.max.setPos(
                    Math.max(pos.getX(), this.max.getX()),
                    Math.max(pos.getY(), this.max.getY()),
                    Math.max(pos.getZ(), this.max.getZ())
            );
        }

        public void run() {
            Iterator<BlockPos> iterator = toVisit.iterator();
            while(iterator.hasNext() && this.canContinue()) {
                BlockPos pos = iterator.next();
                iterator.remove();
                this.checkPosition(pos);
            }
            if(!this.toVisit.isEmpty()) {
                if(this.canContinue()) {
                    this.run();
                } else {
                    // block limit reached
                    this.valid = false;
                }
            }
        }

        protected void checkPosition(BlockPos pos) {
            if(!this.hasVisited(pos)) {
                BlockState state = this.getWorld().getBlockState(pos);
                if(state.getBlock().isAir(state, this.getWorld(), pos)) {
                    this.recordVisit(pos, GreenHouse.BlockType.INTERIOR_AIR);
                } else {
                    // initial block is not air
                    this.valid = false;
                }
            }
            Arrays.stream(Direction.values()).forEach(dir -> {
                BlockPos checkPos = pos.offset(dir);
                BlockState state = this.getWorld().getBlockState(checkPos);
                if(this.visited.containsKey(checkPos)) {
                    // If we already visited the position, check if it was a boundary, it might not be from another direction
                    GreenHouse.BlockType type = this.visited.get(checkPos);
                    if (type.isBoundary() && !this.isSolidBlock(state, checkPos, dir)) {
                        // was a boundary when visited from another direction, but from the current direction it is not, update is needed
                        this.recordVisit(checkPos, GreenHouse.BlockType.INTERIOR_OTHER);
                        this.toVisit.add(checkPos);
                    }
                } else {
                    // Block is not yet visited, categorize it
                    if(isAirBlock(state, checkPos)) {
                        this.recordVisit(checkPos, GreenHouse.BlockType.INTERIOR_AIR);
                        this.toVisit.add(checkPos);
                    } else if(this.isSolidBlock(state, checkPos, dir)) {
                        this.recordVisit(checkPos, GreenHouse.BlockType.BOUNDARY);
                    }else {
                        this.recordVisit(checkPos, GreenHouse.BlockType.INTERIOR_OTHER);
                        this.toVisit.add(checkPos);
                    }
                }
            });
        }

        protected boolean isAirBlock(BlockState state, BlockPos pos) {
            return state.getBlock().isAir(state, this.getWorld(), pos);
        }

        protected boolean isSolidBlock(BlockState state, BlockPos pos, Direction dir) {
            return state.isSolidSide(this.getWorld(), pos, dir) || state.isSolidSide(this.getWorld(), pos, dir.getOpposite());
        }

        public void convertAirBlocks() {
            BlockState air = AgriCraft.instance.getModBlockRegistry().greenhouse_air.getDefaultState();
            this.visited.entrySet().stream()
                    .filter(entry -> entry.getValue() == GreenHouse.BlockType.INTERIOR_AIR)
                    .map(Map.Entry::getKey)
                    .forEach(pos -> this.getWorld().setBlockState(pos,air));
        }
    }
}

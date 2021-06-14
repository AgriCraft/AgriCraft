package com.infinityraider.agricraft.handler;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.capability.CapabilityGreenHouseData;
import com.infinityraider.agricraft.content.world.GreenHouse;
import com.infinityraider.agricraft.util.GreenHouseHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

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
        new GreenHouseFormer(world, pos).getGreenHouse().ifPresent(greenHouse -> {
            if(greenHouse.isValid()) {
                CapabilityGreenHouseData.getInstance().addGreenHouse(world, greenHouse);
                greenHouse.convertAirBlocks(world);
            }
        });
    }

    public static class GreenHouseFormer {
        private final World world;

        private final Set<BlockPos> toVisit;
        private final Map<BlockPos, GreenHouse.BlockType> visited;

        private int interiorCount;
        private boolean valid;
        private GreenHouse greenHouse;

        protected GreenHouseFormer(World world, BlockPos start) {
            this.world = world;
            this.toVisit = Sets.newConcurrentHashSet();
            this.visited = Maps.newHashMap();
            toVisit.add(start);
            this.valid = true;
        }

        public Optional<GreenHouse> getGreenHouse() {
            if(this.greenHouse == null) {
                this.run();
            }
            return Optional.ofNullable(this.greenHouse);
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
                    this.greenHouse = new GreenHouse(this.visited);
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
                    if (type.isBoundary()) {
                        if(!this.isSolidBlock(state, checkPos, dir)) {
                            // was a boundary when visited from another direction, but from the current direction it is not, update is needed
                            this.recordVisit(checkPos, GreenHouse.BlockType.INTERIOR_OTHER);
                            this.toVisit.add(checkPos);
                            this.interiorCount++;
                        }
                    }
                } else {
                    // Block is not yet visited, categorize it
                    if(isAirBlock(state, checkPos)) {
                        this.recordVisit(checkPos, GreenHouse.BlockType.INTERIOR_AIR);
                        this.toVisit.add(checkPos);
                        this.interiorCount++;
                    } else if(this.isGreenHouseGlass(state)) {
                        this.recordVisit(checkPos, GreenHouse.BlockType.GLASS);
                    } else if(this.isSolidBlock(state, checkPos, dir)) {
                        this.recordVisit(checkPos, GreenHouse.BlockType.BOUNDARY);
                    } else {
                        this.recordVisit(checkPos, GreenHouse.BlockType.INTERIOR_OTHER);
                        this.toVisit.add(checkPos);
                        this.interiorCount++;
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

        protected boolean isGreenHouseGlass(BlockState state) {
            return GreenHouseHelper.isGreenHouseGlass(state);
        }
    }
}

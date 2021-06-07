package com.infinityraider.agricraft.handler;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

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
        // TODO
    }

    public static class GreenHouseFormer {
        private final World world;

        private List<BlockPos> boundaries;
        private int minX;
        private int minY;
        private int minZ;
        private int maxX;
        private int maxY;
        private int maxZ;

        protected GreenHouseFormer(World world) {
            this.world = world;
        }

        public World getWorld() {
            return this.world;
        }

        public void findBoundaries(BlockPos pos) {

        }

        protected void findBoundaries(BlockPos.Mutable pos, Direction direction) {
            BlockState state = this.getWorld().getBlockState(pos);
            if(state.getBlock().isAir(state, this.getWorld(), pos) || !state.isSolid()) {
                this.findBoundaries(pos.offset(direction).toMutable(), direction);
            } else {
                this.boundaries.add(pos.toImmutable());
            }
        }

        protected void addBoundary(BlockPos pos) {
            this.boundaries.add(pos.toImmutable());
            this.minX = Math.min(this.minX, pos.getX());
            this.minY = Math.min(this.minY, pos.getY());
            this.minZ = Math.min(this.minZ, pos.getZ());
            this.maxX = Math.max(this.maxX, pos.getX());
            this.maxY = Math.max(this.maxY, pos.getY());
            this.maxZ = Math.max(this.maxZ, pos.getZ());

        }

        protected static class ScanVolume {
            private ScanResult[][][] volume;

            private final int minX;
            private final int minY;
            private final int minZ;
            private final int maxX;
            private final int maxY;
            private final int maxZ;

            protected ScanVolume(BlockPos min, BlockPos max) {
                this(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
            }

            protected ScanVolume(int x1, int y1, int z1, int x2, int y2, int z2) {
                this.minX = Math.min(x1, x2);
                this.minY = Math.min(y1, y2);
                this.minZ = Math.min(z1, z2);
                this.maxX = Math.max(x1, x2);
                this.maxY = Math.max(y1, y2);
                this.maxZ = Math.max(z1, z2);
                this.volume = new ScanResult[this.getWidth()][this.getHeight()][this.getWidth()];
                for(int x = 0; x < this.getWidth(); x++) {
                    for(int y = 0; y < this.getHeight(); y++) {
                        for (int z = 0; z < this.getDepth(); z++) {
                            this.volume[x][y][z] = ScanResult.TODO;
                        }
                    }
                }
            }

            public int getWidth() {
                return this.maxX - this.minX + 1;
            }

            public int getHeight() {
                return this.maxY - this.minY + 1;
            }

            public int getDepth() {
                return this.maxZ - this.minZ + 1;
            }

            public void scan(World world) {
                BlockPos.Mutable pos = new BlockPos.Mutable(0, 0, 0);
                for(int x = 0; x < this.getWidth(); x++) {
                    for(int y = 0; y < this.getHeight(); y++) {
                        for (int z = 0; z < this.getDepth(); z++) {
                            pos.setPos(this.minX + x, this.minY + y, this.minZ + z);
                            BlockState state = world.getBlockState(pos);
                            this.volume[x][y][z] = ScanResult.fromState(state, world, pos);
                        }
                    }
                }
            }

            public ScanVolume expand(BlockPos pos) {
                if (pos.getX() <= this.maxX && pos.getX() >= this.minX
                        && pos.getY() <= this.maxY && pos.getY() >= this.minY
                        && pos.getZ() <= this.maxZ && pos.getZ() >= this.minY) {
                    // Position is inside the current volume
                    return this;
                }
                // Form new volume
                ScanVolume newVolume = new ScanVolume(
                        Math.min(this.minX, pos.getX()), Math.min(this.minY, pos.getY()), Math.min(this.minZ, pos.getZ()),
                        Math.max(this.maxX, pos.getX()), Math.max(this.maxY, pos.getY()), Math.max(this.maxZ, pos.getZ())
                );
                // Copy over checked positions
                int dx = this.minX - newVolume.minX;
                int dy = this.minY - newVolume.minY;
                int dz = this.minZ - newVolume.minZ;
                for(int x = 0; x < getWidth(); x++) {
                    for(int y = 0; y < getHeight(); y++) {
                        System.arraycopy(this.volume[x][y], this.minZ, newVolume.volume[x + dx][y + dy], this.minZ + dz, this.maxZ + 1 - this.minZ);
                    }
                }
                // return the new volume
                return newVolume;
            }

            public ScanResult getResult(int x, int y, int z) {
                return this.volume[x][y][z];
            }
        }

        protected static class ScanFace {
            private final Direction direction;

            // Indicates the depth of the boundary
            private int[][] boundary;

            public ScanFace(ScanVolume volume, Direction dir) {
                this.direction = dir;
                this.boundary = new int[this.getFirstDimension(volume)][this.getSecondDimension(volume)];
                int iterationDepth = this.getIterationDepth(volume);
                for(int a = 0; a < this.boundary.length; a++) {
                    for(int b = 0; b < this.boundary[a].length; b++) {
                        this.boundary[a][b] = -1;
                        if(this.isIterationInverted()) {
                            for(int i = iterationDepth - 1; i >= 0; i--) {
                                if(this.checkPosition(volume, a, b, i)) {
                                    this.boundary[a][b] = i;
                                    break;
                                }
                            }
                        } else {
                            for(int i = 0; i < iterationDepth; i++) {
                                if(this.checkPosition(volume, a, b, i)) {
                                    this.boundary[a][b] = i;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            public Direction getDirection() {
                return this.direction;
            }

            protected int getFirstDimension(ScanVolume volume) {
                if(this.getDirection().getAxis() == Direction.Axis.X) {
                    return volume.getHeight();
                } else {
                    return volume.getWidth();
                }
            }

            protected int getSecondDimension(ScanVolume volume) {
                if(this.getDirection().getAxis() == Direction.Axis.Z) {
                    return volume.getHeight();
                } else {
                    return volume.getDepth();
                }
            }

            protected int getIterationDepth(ScanVolume volume) {
                if(this.getDirection().getAxis() == Direction.Axis.X) {
                    return volume.getWidth();
                } else if(this.getDirection().getAxis() == Direction.Axis.Y) {
                    return volume.getHeight();
                } else {
                    return volume.getDepth();
                }
            }

            public boolean isIterationInverted() {
                return this.getDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            }

            protected boolean checkPosition(ScanVolume volume, int a, int b, int i) {
                int x = this.getXIndex(a, b, i);
                int y = this.getYIndex(a, b, i);
                int z = this.getZIndex(a, b, i);
                ScanResult result = volume.getResult(x, y, z);
                return result == ScanResult.SOLID;
            }

            protected int getXIndex(int a, int b, int i) {
                if(this.getDirection().getAxis() == Direction.Axis.X) {
                    return i;
                } else {
                    return a;
                }
            }

            protected int getYIndex(int a, int b, int i) {
                if(this.getDirection().getAxis() == Direction.Axis.X) {
                    return a;
                } else if(this.getDirection().getAxis() == Direction.Axis.Y){
                    return i;
                } else {
                    return b;
                }
            }

            protected int getZIndex(int a, int b, int i) {
                if(this.getDirection().getAxis() == Direction.Axis.Z) {
                    return i;
                } else {
                    return b;
                }
            }
        }

        public enum ScanResult {
            AIR,
            SOLID,
            NON_SOLID,
            TODO;

            public static ScanResult fromState(BlockState state, World world, BlockPos pos) {
                if(state.getBlock().isAir(state, world, pos)) {
                    return ScanResult.AIR;
                } else if(state.isSolid()) {
                    return ScanResult.SOLID;
                } else {
                    return ScanResult.NON_SOLID;
                }
            }
        }
    }
}

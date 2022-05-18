package com.infinityraider.agricraft.content.world.greenhouse;

import com.infinityraider.agricraft.handler.GreenHouseHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.function.Function;

public class GreenHouseBlock {
    private final BlockPos pos;
    private final Type type;
    private final boolean ceiling;

    public GreenHouseBlock(BlockPos pos, Type type, boolean ceiling) {
        this.pos = pos;
        this.type = type;
        this.ceiling = ceiling;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Type getType() {
        return this.type;
    }

    public boolean isInterior() {
        return this.getType().isInterior();
    }

    public boolean isExterior() {
        return this.getType().isExterior();
    }

    public boolean isBoundary() {
        return this.getType().isBoundary();
    }

    public boolean isGlass() {
        return this.getType().isGlass();
    }

    public boolean isAir() {
        return this.getType().isAir();
    }

    public boolean isGap() {
        return this.getType().isGap();
    }

    public boolean isCeiling() {
        return this.ceiling;
    }

    public enum Type {
        INTERIOR_AIR(true, false, INewStateFunction.INTERIOR),
        INTERIOR_OTHER(true, false, INewStateFunction.INTERIOR),
        BOUNDARY(false, true, INewStateFunction.BOUNDARY),
        GLASS(false, true, INewStateFunction.BOUNDARY),
        GAP(false, true, INewStateFunction.BOUNDARY),
        EXTERIOR(false, false, INewStateFunction.EXTERIOR);

        private final boolean interior;
        private final boolean boundary;
        private final INewStateFunction newStateFunction;

        Type(boolean interior, boolean boundary, INewStateFunction newStateFunction) {
            this.interior = interior;
            this.boundary = boundary;
            this.newStateFunction = newStateFunction;
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

        public boolean isGap() {
            return this == GAP;
        }

        public boolean isExterior() {
            return this == EXTERIOR;
        }

        public Type getNewTypeForState(Level world, BlockPos pos, BlockState newState, Function<BlockPos, GreenHouseBlock> blockFunc)  {
            return this.newStateFunction.getNewTypeForState(world, pos, newState, blockFunc);
        }
    }

    @FunctionalInterface
    private interface INewStateFunction {
        Type getNewTypeForState(Level world, BlockPos pos, BlockState newState, Function<BlockPos, GreenHouseBlock> blockFunc);

        INewStateFunction INTERIOR = (world, pos, newState, blockFunc) -> {
            if(newState.isAir()) {
                return Type.INTERIOR_AIR;
            } else {
                return Type.INTERIOR_OTHER;
            }
        };

        INewStateFunction BOUNDARY = (world, pos, newState, blockFunc) -> {
            if(GreenHouseHandler.isGreenHouseGlass(newState)) {
                return Type.GLASS;
            }
            if(checkSolidness(world, pos, newState, blockFunc)) {
                return Type.BOUNDARY;
            }
            return Type.GAP;
        };

        INewStateFunction EXTERIOR = (world, pos, newState, blockFunc) -> Type.EXTERIOR;

        static boolean checkSolidness(Level world, BlockPos pos, BlockState state, Function<BlockPos, GreenHouseBlock> blockFunc) {
            return Arrays.stream(Direction.values()).allMatch(dir -> {
                GreenHouseBlock block = blockFunc.apply(pos.relative(dir));
                if(block == null || block.isExterior()) {
                    // edges facing the exterior must be solid
                    return GreenHouseHandler.isSolidBlock(world, state, pos, dir);
                }
                return block.isBoundary() || block.isInterior();
            });
        }
    }
}

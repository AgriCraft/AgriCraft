package com.infinityraider.agricraft.content.world.greenhouse;

import com.infinityraider.agricraft.content.world.greenhouse.GreenHousePart.GreenHouseBlock;
import com.infinityraider.agricraft.handler.GreenHouseHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.function.Function;

public enum GreenHouseBlockType {
    INTERIOR_AIR(true, false, INewStateFunction.INTERIOR),
    INTERIOR_OTHER(true, false, INewStateFunction.INTERIOR),
    BOUNDARY(false, true, INewStateFunction.BOUNDARY),
    GLASS(false, true, INewStateFunction.BOUNDARY),
    GAP(false, true, INewStateFunction.BOUNDARY),
    EXTERIOR(false, false, INewStateFunction.EXTERIOR);

    private final boolean interior;
    private final boolean boundary;
    private final INewStateFunction newStateFunction;

    GreenHouseBlockType(boolean interior, boolean boundary, INewStateFunction newStateFunction) {
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

    public GreenHouseBlockType getNewTypeForState(Level world, BlockPos pos, BlockState newState, Function<BlockPos, GreenHouseBlock> blockFunc)  {
        return this.newStateFunction.getNewTypeForState(world, pos, newState, blockFunc);
    }

    @FunctionalInterface
    private interface INewStateFunction {
        GreenHouseBlockType getNewTypeForState(Level world, BlockPos pos, BlockState newState, Function<BlockPos, GreenHouseBlock> blockFunc);

        INewStateFunction INTERIOR = (world, pos, newState, blockFunc) -> {
            if(newState.isAir()) {
                return GreenHouseBlockType.INTERIOR_AIR;
            } else {
                return GreenHouseBlockType.INTERIOR_OTHER;
            }
        };

        INewStateFunction BOUNDARY = (world, pos, newState, blockFunc) -> {
            if(GreenHouseHandler.isGreenHouseGlass(newState)) {
                return GreenHouseBlockType.GLASS;
            }
            if(checkSolidness(world, pos, newState, blockFunc)) {
                return GreenHouseBlockType.BOUNDARY;
            }
            return GreenHouseBlockType.GAP;
        };

        INewStateFunction EXTERIOR = (world, pos, newState, blockFunc) -> GreenHouseBlockType.EXTERIOR;

        static boolean checkSolidness(Level world, BlockPos pos, BlockState state, Function<BlockPos, GreenHouseBlock> blockFunc) {
            return Arrays.stream(Direction.values()).allMatch(dir -> {
                GreenHouseBlock block = blockFunc.apply(pos.relative(dir));
                if(block == null) {
                    return true;
                }
                if(block.isBoundary() || block.isInterior()) {
                    return true;
                }
                if(block.isExterior()) {
                    // edges facing the exterior must be solid
                    return GreenHouseHandler.isSolidBlock(world, state, pos, dir);
                }
                return false;
            });
        }
    }
}

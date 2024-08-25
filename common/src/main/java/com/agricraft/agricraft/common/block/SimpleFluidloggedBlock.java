package com.agricraft.agricraft.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.Optional;

public interface SimpleFluidloggedBlock extends BucketPickup, LiquidBlockContainer {
    BooleanProperty LAVALOGGED = BooleanProperty.create("agricraft_lavalogged");
    BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    @Override
    default boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return fluid == Fluids.LAVA || fluid == Fluids.WATER;
    }

    @Override
    default boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!(state.getValue(LAVALOGGED) || state.getValue(WATERLOGGED))) {
            if (fluidState.getType() == Fluids.LAVA) {
                if (!level.isClientSide()) {
                    level.setBlock(pos, state.setValue(LAVALOGGED, true), 3);
                    level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
                }

                return true;
            } else if (fluidState.getType() == Fluids.WATER) {
                if (!level.isClientSide()) {
                    level.setBlock(pos, state.setValue(WATERLOGGED, true), 3);
                    level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
                }

                return true;
            }
        }
        return false;
    }

    @Override
    default ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        if (state.getValue(WATERLOGGED)) {
            level.setBlock(pos, state.setValue(WATERLOGGED, false), 3);
            if (!state.canSurvive(level, pos)) {
                level.destroyBlock(pos, true);
            }

            return new ItemStack(Items.WATER_BUCKET);
        } else if (state.getValue(LAVALOGGED)) {
            level.setBlock(pos, state.setValue(LAVALOGGED, false), 3);
            if (!state.canSurvive(level, pos)) {
                level.destroyBlock(pos, true);
            }

            return new ItemStack(Items.LAVA_BUCKET);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    default Optional<SoundEvent> getPickupSound() {
        // TODO: @Ketheroth wrap CropBlock in loader specific block to have the blockstate passed in
//		if (state.getValue(WATERLOGGED)) {
//			return Fluids.WATER.getPickupSound()
//		}
        return Fluids.WATER.getPickupSound();
    }
}

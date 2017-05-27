/*
 */
package com.infinityraider.agricraft.utility;

import java.util.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 *
 * @author Ryan
 */
public final class FluidHandlerBlockWrapper implements IFluidHandler {

    private final IFluidHandlerBlock block;
    private final World world;
    private final BlockPos pos;

    public FluidHandlerBlockWrapper(IFluidHandlerBlock block, World world, BlockPos pos) {
        this.block = Objects.requireNonNull(block);
        this.world = Objects.requireNonNull(world);
        this.pos = Objects.requireNonNull(pos);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return block.getTankProperties(world, pos, world.getBlockState(pos));
    }

    @Override
    public int fill(FluidStack fluid, boolean doFill) {
        return block.fill(world, pos, world.getBlockState(pos), fluid, doFill);
    }

    @Override
    public FluidStack drain(FluidStack fluid, boolean doDrain) {
        return block.drain(world, pos, world.getBlockState(pos), fluid, doDrain);
    }

    @Override
    public FluidStack drain(int amount, boolean doDrain) {
        return block.drain(world, pos, world.getBlockState(pos), amount, doDrain);
    }

}

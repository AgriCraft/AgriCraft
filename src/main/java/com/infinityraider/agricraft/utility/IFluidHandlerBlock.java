/*
 */
package com.infinityraider.agricraft.utility;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 *
 * @author Ryan
 */
public interface IFluidHandlerBlock {

    /**
     * Gets an array containing all the properties associated with the given tank.
     *
     * @param world the world that the tank is in.
     * @param pos the position that the tank is at in the given world.
     * @param state the the state of the tank.
     * @return the tank's property array.
     */
    public IFluidTankProperties[] getTankProperties(World world, BlockPos pos, IBlockState state);

    /**
     * Attempts to fill the tank with the given fluid.
     *
     * @param world the world the tank is in.
     * @param pos the position of the tank in the given world.
     * @param state the state of the thank.
     * @param fluid the fluid to fill the tank with.
     * @param doFill if the action should actually occur or just be simulated.
     * @return the amount of the fluid that was or would be consumed by the operation.
     */
    public int fill(World world, BlockPos pos, IBlockState state, FluidStack fluid, boolean doFill);

    /**
     * Attempts to drain the tank.
     *
     * @param world the world the tank is in.
     * @param pos the position of the tank in the given world.
     * @param state the state of the thank.
     * @param fluid the max amount of fluid to drain from the tank.
     * @param doDrain if the action should actually occur or just be simulated.
     * @return the fluid that was drained from the tank.
     */
    @Nullable
    public FluidStack drain(World world, BlockPos pos, IBlockState state, FluidStack fluid, boolean doDrain);

    /**
     * Attempts to drain the tank.
     *
     * @param world the world the tank is in.
     * @param pos the position of the tank in the given world.
     * @param state the state of the thank.
     * @param amount the max amount of fluid to drain from the tank.
     * @param doDrain if the action should actually occur or just be simulated.
     * @return the fluid that was drained from the tank.
     */
    @Nullable
    public FluidStack drain(World world, BlockPos pos, IBlockState state, int amount, boolean doDrain);

}

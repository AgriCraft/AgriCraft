package com.infinityraider.agricraft.api.v1.irrigation;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface indicating irrigation components
 */
public interface IAgriIrrigationComponent extends IFluidHandler {
    /**
     * @return the world y-coordinate corresponding to the minimum fluid height (empty fluid contents)
     */
    double getMinFluidHeight();

    /**
     * @return the world y-coordinate corresponding to the maximum fluid height (full fluid contents)
     */
    double getMaxFluidHeight();

    /**
     * @return the fluid capacity of this component in mB
     */
    int getFluidCapacity();

    /**
     * @return the TileEntity implementation of the component (either direct cast, or as a capability
     */
    TileEntity asTile();

    /**
     * @return the World this component is in
     */
    @Nullable
    default World world() {
        return this.asTile().getWorld();
    }

    /**
     * @return the position this component is in the world
     */
    default BlockPos position() {
        return this.asTile().getPos();
    }

    /**
     * @return the state of the block of this component
     */
    default BlockState BlockState() {
        return this.asTile().getBlockState();
    }

    /**
     * @return the IFluidHandler attached to this component (do not override)
     */
    default IFluidHandler getFluidHandler() {
        return AgriApi.getIrrigationComponentFluidHandler(this);
    }

    /**
     * IFluidHandler override (do not override)
     */
    @Override
    default int getTanks() {
        return this.getFluidHandler().getTanks();
    }

    /**
     * IFluidHandler override (do not override)
     */
    @Nonnull
    @Override
    default FluidStack getFluidInTank(int tank) {
        return this.getFluidHandler().getFluidInTank(tank);
    }

    /**
     * IFluidHandler override (do not override)
     */
    @Override
    default int getTankCapacity(int tank) {
        return this.getFluidHandler().getTankCapacity(tank);
    }

    /**
     * IFluidHandler override (do not override)
     */
    @Override
    default boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return this.getFluidHandler().isFluidValid(tank, stack);
    }

    /**
     * IFluidHandler override (do not override)
     */
    @Override
    default int fill(FluidStack resource, FluidAction action) {
        return this.getFluidHandler().fill(resource, action);
    }

    /**
     * IFluidHandler override (do not override)
     */
    @Nonnull
    @Override
    default FluidStack drain(FluidStack resource, FluidAction action) {
        return this.getFluidHandler().drain(resource, action);
    }

    /**
     * IFluidHandler override (do not override)
     */
    @Nonnull
    @Override
    default FluidStack drain(int maxDrain, FluidAction action) {
        return this.getFluidHandler().drain(maxDrain, action);
    }
}

package com.infinityraider.agricraft.api.v1.irrigation;

import mezz.jei.api.MethodsReturnNonnullByDefault;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Set;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface IAgriIrrigationNetwork extends IFluidHandler {
    @Nullable
    World getWorld();

    boolean isValid();

    Set<IAgriIrrigationNode> nodes();

    Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections();

    int capacity();

    double fluidHeight();

    int contents();

    void setContents(int value);

    FluidStack contentAsFluidStack();

    @Override
    default int getTanks() {
        return 1;
    }

    @Override
    default FluidStack getFluidInTank(int tank) {
        return this.contentAsFluidStack();
    }

    @Override
    default int getTankCapacity(int tank) {
        return this.capacity();
    }

    @Override
    default boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return stack.getFluid().isEquivalentTo(Fluids.WATER);
    }

    @Override
    default int fill(FluidStack resource, FluidAction action) {
        if(resource.getFluid().isEquivalentTo(Fluids.WATER)) {
            int fill = Math.min(this.capacity() - this.contents(), resource.getAmount());
            if(action.execute()) {
                this.setContents(this.contents() + fill);
            }
            return fill;
        }
        return 0;
    }

    @Nonnull
    @Override
    default FluidStack drain(FluidStack resource, FluidAction action) {
        if(resource.getFluid().isEquivalentTo(Fluids.WATER)) {
            return this.drain(resource.getAmount(), action);
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    default FluidStack drain(int maxDrain, FluidAction action) {
        int drain = Math.min(maxDrain, this.contents());
        if(action.execute()) {
            this.setContents(this.contents() - drain);
        }
        return new FluidStack(Fluids.WATER, drain);
    }
}

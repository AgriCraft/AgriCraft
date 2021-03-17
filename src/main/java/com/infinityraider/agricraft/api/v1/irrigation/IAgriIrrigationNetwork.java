package com.infinityraider.agricraft.api.v1.irrigation;

import mezz.jei.api.MethodsReturnNonnullByDefault;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface IAgriIrrigationNetwork extends IFluidHandler {
    Set<IAgriIrrigationNode> nodes();

    Set<Tuple<IAgriIrrigationNode, IAgriIrrigationNode>> connections();

    boolean attachNode(IAgriIrrigationNode newNode, IAgriIrrigationNode existingNode);

    boolean connectNodes(IAgriIrrigationNode first, IAgriIrrigationNode second);

    Set<IAgriIrrigationNetwork> removeNode(IAgriIrrigationNode node);

    FluidStack contents();

    int volume();

    @Override
    default int getTanks() {
        return 1;
    }


    @Override
    default FluidStack getFluidInTank(int tank) {
        return this.contents();
    }

    @Override
    default int getTankCapacity(int tank) {
        return this.volume();
    }

    @Override
    default boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return stack.getFluid().equals(Fluids.WATER);
    }
}

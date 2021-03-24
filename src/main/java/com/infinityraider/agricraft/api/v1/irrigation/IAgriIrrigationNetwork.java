package com.infinityraider.agricraft.api.v1.irrigation;

import mezz.jei.api.MethodsReturnNonnullByDefault;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Interface representing an AgriCraft Irrigation Network.
 * This interface exists for the purpose of interaction, and should not be implemented.
 *
 * Irrigation networks are made up of nodes and connections between these nodes.
 * The nodes are defined by Irrigation Components in the World, which consist of TileEntities.
 *
 * An irrigation network is gravitationally based, where each node has a certain volume held between
 * its maximum and minimum height levels (minimum = empty, maximum = full).
 *
 * To obtain an Irrigation Network instance, call getNetwork(Direction) on an IAgriIrrigationComponent
 *
 * To implement blocks which act as nodes, one must first implement IAgriIrrigationComponent,
 * either directly on the TileEntity class, or as a capability.
 * Secondly, one must also implement the IAgriIrrigationNode(s) that this TileEntity can provide to the network.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface IAgriIrrigationNetwork extends IFluidHandler {
    /*
     * --------------------------
     * Irrigation network methods
     * --------------------------
     */

    /**
     * @return the world this irrigation network is in
     */
    @Nullable
    World getWorld();

    /**
     * Checks the validity of an irrigation network.
     * An irrigation network might become invalid if one of its blocks is destroyed
     *
     * References to invalid irrigation networks must be discarded in order to prevent memory leaks
     *
     * @return true if this irrigation network is still valid
     */
    boolean isValid();

    /**
     * @return a non-modifiable set of all nodes in the network
     */
    Set<IAgriIrrigationNode> nodes();

    /**
     * Fetches a non-modifiable map of all connections from a certain node
     * @param node the node
     * @return a set containing the connections
     */
    default Set<IAgriIrrigationConnection> getConnectionsFrom(IAgriIrrigationNode node) {
        return this.connections().getOrDefault(node, Collections.emptySet());
    }

    /**
     * @return a map non-modifiable map of all connections, mapped per node in the network
     */
    Map<IAgriIrrigationNode, Set<IAgriIrrigationConnection>> connections();

    /**
     * @return the total fluid capacity of the network in mB
     */
    int capacity();

    /**
     * @return the current fluid height of the network, in absolute world y-coordinate
     */
    double fluidHeight();

    /**
     * @return the current total volume of fluid in the network in mB
     */
    int contents();

    /**
     * Sets the current total volume of fluid in the network
     * @param value the volume in mB
     */
    void setContents(int value);

    /**
     * @return the current network fluid content as a new fluid stack
     */
    FluidStack contentAsFluidStack();


    /*
     * ------------------------------------------------
     * Default implementations of IFluidHandler methods
     * ------------------------------------------------
     */

     //TODO: Correctly implement the tanks and gravitational flow, as sometimes a certain tank needs to be filled to a certain level before water can flow to lower levels

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

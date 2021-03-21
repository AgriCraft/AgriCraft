package com.infinityraider.agricraft.api.v1.irrigation;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Interface representing TileEntities which can be part of an AgriCraft irrigation network.
 *
 * The interface can either by implemented directly in a TileEntity class, or added as a Capability (See the inner class Cap)
 *
 * An IAgriIrrigationComponent is not directly part of an irrigation network, but instead provides nodes which can.
 * This allows one Node to consist of multiple Components (for instance a MultiBlock), or a Component to have multiple Nodes.
 */
public interface IAgriIrrigationComponent {

    /**
     * Retrieves the Irrigation node for the given side, if there is one.
     * @param side the side
     * @return Optional holding the node, or empty if no node is available from the given side
     */
    Optional<IAgriIrrigationNode> getNode(@Nullable Direction side);

    /**
     * @return the TileEntity representing this Component
     */
    TileEntity getTile();

    /**
     * Callback for when the fluid content of the network changes
     *
     * Note that this could be called multiple times for the same content change
     * in case this component is joined to the same network on multiple sides
     *
     * @param side the side for which the network contents changed
     */
    void onNetworkContentsChanged(Direction side);

    /**
     * Default implementation to fetch the network this component is a part of, for a given side.
     *
     * DO NOT OVERRIDE
     *
     * AgriCraft handles all required logic and operations internally to guarantee each component is aware of the networks
     * it is connected to
     *
     * @param side the side
     * @return the network this component is currently in
     */
    default IAgriIrrigationNetwork getNetwork(@Nullable Direction side) {
        return AgriApi.getIrrigationNetwork(this, side);
    }

    /** Static inner class holding the Capability key and instance for IAgriIrrigationComponents */
    final class Cap {
        /** The Capabililty key  */
        public static final ResourceLocation KEY = new ResourceLocation("agricraft", "irrigation_component");

        /** The Capability instance */
        @CapabilityInject(IAgriIrrigationComponent.class)
        public static final Capability<IAgriIrrigationComponent> INSTANCE = null;

        /** Private constructor to prevent instantiation */
        private Cap() {}
    }
}

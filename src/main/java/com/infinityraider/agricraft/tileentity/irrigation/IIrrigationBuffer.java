package com.InfinityRaider.AgriCraft.tileentity.irrigation;

public interface IIrrigationBuffer extends IIrrigationComponent {
    /**
     * @return if this component can provide fluid to a network
     */
    boolean canProvideFluid();

    /**
     * @return the total fluid capacity for this component in mB
     */
    int totalCapacity();

    /**
     * Returns the total fluid capacity above this height level
     * @param height the reference height from the bottom of the block where 1 unit is 1/16th of a block
     * @return the amount of fluid that can be stored above the height level in mB
     */
    int capacityAboveHeight(float height);

    /**
     * Returns the total fluid volume above this height level
     * @param height the reference height from the bottom of the block where 1 unit is 1/16th of a block
     * @return the amount of fluid currently in the buffer above the height level in mB
     */
    int fluidVolumeAboveHeight(float height);
}

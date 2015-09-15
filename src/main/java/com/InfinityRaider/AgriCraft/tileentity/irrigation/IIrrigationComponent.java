package com.InfinityRaider.AgriCraft.tileentity.irrigation;

public interface IIrrigationComponent {
    void setFluidLevel(int lvl);

    boolean canConnectTo(IIrrigationComponent component);
}

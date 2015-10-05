package com.InfinityRaider.AgriCraft.utility.multiblock;

import net.minecraftforge.common.util.ForgeDirection;

public interface IMultiBlockComponent<L extends MultiBlockLogic> {
    L getMultiBLockLogic();

    void setMultiBlockLogic(L logic, boolean syncToClient);

    boolean hasNeighbour(ForgeDirection dir);

    boolean isValidComponent(IMultiBlockComponent component);
}

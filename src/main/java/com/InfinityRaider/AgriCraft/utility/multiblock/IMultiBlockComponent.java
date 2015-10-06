package com.InfinityRaider.AgriCraft.utility.multiblock;

import net.minecraftforge.common.util.ForgeDirection;

public interface IMultiBlockComponent<L extends MultiBlockLogic> {
    L getMultiBlockLogic();

    void setMultiBlockLogic(L logic);

    boolean hasNeighbour(ForgeDirection dir);

    boolean isValidComponent(IMultiBlockComponent component);
}

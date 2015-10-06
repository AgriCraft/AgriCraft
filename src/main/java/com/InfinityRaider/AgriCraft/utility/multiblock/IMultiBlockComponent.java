package com.InfinityRaider.AgriCraft.utility.multiblock;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public interface IMultiBlockComponent<L extends MultiBlockLogic> {
    TileEntity getTileEntity();

    L getMultiBlockLogic();

    void setMultiBlockLogic(L logic);

    boolean hasNeighbour(ForgeDirection dir);

    boolean isValidComponent(IMultiBlockComponent component);
}

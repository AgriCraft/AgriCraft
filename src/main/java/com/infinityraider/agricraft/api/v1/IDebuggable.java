package com.InfinityRaider.AgriCraft.api.v1;

import java.util.List;

/**
 * Implement this in TileEntity classes to be able to add debug info to a list when the TileEntity is right clicked by AgriCraft's debug item
 */
public interface IDebuggable {
    public void addDebugInfo(List<String> list);
}

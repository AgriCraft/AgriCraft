package com.infinityraider.agricraft.api.misc;

import java.util.List;

/**
 * Implement this in TileEntity classes to be able to add debug info to a list
 * when the TileEntity is right clicked by AgriCraft's debug item
 */
public interface IAgriDebuggable {

	public void addDebugInfo(List<String> list);

}

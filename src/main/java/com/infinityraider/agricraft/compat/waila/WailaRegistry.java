package com.infinityraider.agricraft.compat.waila;

import com.infinityraider.agricraft.blocks.BlockBase;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaRegistry {
    public static void initWaila(IWailaRegistrar registry) {
        //All blocks.
        IWailaDataProvider agriProvider = new AgriCraftDataProvider();
        registry.registerStackProvider(agriProvider, BlockBase.class);
        registry.registerBodyProvider(agriProvider, BlockBase.class);
    }
}

package com.infinityraider.agricraft.compat.waila;

import com.infinityraider.agricraft.blocks.BlockBase;
import com.infinityraider.agricraft.compat.AgriCompatModule;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class WailaHelper extends AgriCompatModule {
	
	public WailaHelper() {
		super("Waila");
	}
	
    @Override
    protected void init() {
        FMLInterModComms.sendMessage("Waila", "register", "com.infinityraider.agricraft.compat.waila.WailaHelper.initWaila");
    }
	
	public static void initWaila(IWailaRegistrar registry) {
		//All blocks.
		IWailaDataProvider agriProvider = new AgriWailaAdapter();
		registry.registerStackProvider(agriProvider, BlockBase.class);
		registry.registerBodyProvider(agriProvider, BlockBase.class);
	}

}

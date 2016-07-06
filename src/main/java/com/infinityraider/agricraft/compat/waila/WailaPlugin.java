package com.infinityraider.agricraft.compat.waila;

import com.infinityraider.agricraft.api.v1.AgriPlugin;
import com.infinityraider.agricraft.api.v1.IAgriPlugin;
import com.infinityraider.agricraft.blocks.BlockBase;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraftforge.fml.common.event.FMLInterModComms;

@AgriPlugin
public class WailaPlugin implements IAgriPlugin {

	@Override
	public boolean isEnabled() {
		return true;
	}
	
    @Override
    public void init() {
        FMLInterModComms.sendMessage("Waila", "register", this.getClass().getCanonicalName() + ".initWaila");
    }
	
	public static void initWaila(IWailaRegistrar registry) {
		//All blocks.
		IWailaDataProvider agriProvider = new AgriWailaAdapter();
		registry.registerStackProvider(agriProvider, BlockBase.class);
		registry.registerBodyProvider(agriProvider, BlockBase.class);
	}

}

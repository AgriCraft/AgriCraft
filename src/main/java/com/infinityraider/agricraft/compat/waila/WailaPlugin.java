package com.infinityraider.agricraft.compat.waila;

import com.infinityraider.agricraft.api.AgriPlugin;
import com.infinityraider.agricraft.api.IAgriPlugin;
import com.infinityraider.infinitylib.block.BlockBase;
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
    public void initPlugin() {
        FMLInterModComms.sendMessage("Waila", "register", this.getClass().getCanonicalName() + ".initWaila");
    }
	
	public static void initWaila(IWailaRegistrar registry) {
		//All blocks.
		IWailaDataProvider agriProvider = new AgriWailaAdapter();
		registry.registerStackProvider(agriProvider, BlockBase.class);
		registry.registerBodyProvider(agriProvider, BlockBase.class);
	}

}

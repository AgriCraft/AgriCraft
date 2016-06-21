package com.infinityraider.agricraft.compat.waila;

import com.infinityraider.agricraft.compat.ModHelper;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class WailaHelper extends ModHelper {
	
	public WailaHelper() {
		super("Waila");
	}
	
	
    @Override
    protected void init() {
        FMLInterModComms.sendMessage("Waila", "register", "com.infinityraider.agricraft.compatibility.waila.WailaRegistry.initWaila");
    }

}

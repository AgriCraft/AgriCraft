package com.infinityraider.agricraft.compatibility.waila;

import com.infinityraider.agricraft.compatibility.ModHelper;
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

package com.infinityraider.agricraft.compatibility.waila;

import com.infinityraider.agricraft.compatibility.ModHelper;
import com.infinityraider.agricraft.reference.AgriCraftMods;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class WailaHelper extends ModHelper {
	
	public WailaHelper() {
		super(AgriCraftMods.waila);
	}
	
	
    @Override
    protected void init() {
        FMLInterModComms.sendMessage(AgriCraftMods.waila, "register", "com.infinityraider.agricraft.compatibility.waila.WailaRegistry.initWaila");
    }

}

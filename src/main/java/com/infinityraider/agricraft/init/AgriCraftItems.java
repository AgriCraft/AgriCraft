package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.items.*;
import com.infinityraider.agricraft.utility.LogHelper;
import net.minecraft.item.Item;

public class AgriCraftItems {
    public static Item crops;
    public static Item journal;
    public static Item trowel;
    public static Item magnifyingGlass;
    public static Item debugItem;
    public static Item handRake;
    public static Item clipper;
	public static ItemClipping clipping;

    public static void init() {
		clipping = new ItemClipping();
        crops = new ItemCrop();
        journal = new ItemJournal();
        magnifyingGlass = new ItemMagnifyingGlass();
        debugItem = new ItemDebugger();
        if(ConfigurationHandler.enableTrowel) {
            trowel = new ItemTrowel();
        }
        if (ConfigurationHandler.enableHandRake) {
            handRake = new ItemHandRake();
        }
        if(ConfigurationHandler.enableClipper) {
            clipper = new ItemClipper();
        }
        LogHelper.debug("Items Registered");
    }
}

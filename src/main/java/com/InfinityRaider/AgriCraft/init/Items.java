package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.*;
import com.InfinityRaider.AgriCraft.utility.LogHelper;

public class Items {
    public static ItemCrop crops;
    public static ItemJournal journal;
    public static ItemTrowel trowel;
    public static ItemMagnifyingGlass magnifyingGlass;
    public static ItemDebugger debugItem;
    public static ItemHandRake handRake;
    public static ItemClipper clipper;
    public static ItemClipping clipping;

    public static void init() {
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
        clipping = new ItemClipping();
        LogHelper.debug("Items Registered");
    }
}

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
    public static ItemMutator mutator;

    public static void init() {
        crops = new ItemCrop();
        journal = new ItemJournal();
        trowel = new ItemTrowel();
        magnifyingGlass = new ItemMagnifyingGlass();
        debugItem = new ItemDebugger();
        mutator = new ItemMutator();
        
        if (ConfigurationHandler.enableHandRake) {
            handRake = new ItemHandRake();
        }
        LogHelper.debug("Items Registered");
    }
}

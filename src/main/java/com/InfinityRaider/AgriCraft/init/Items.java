package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.*;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;

public class Items {
    public static ItemCrop crops;
    public static ItemSprinkler sprinkler;
    public static ItemJournal journal;
    public static ItemTrowel trowel;
    public static ItemMagnifyingGlass magnifyingGlass;
    public static ItemDebugger debugItem;
    public static ItemHandRake handRake;

    public static void init() {
        crops = new ItemCrop();
        RegisterHelper.registerItem(crops, Names.Objects.crops+"Item");
        journal = new ItemJournal();
        RegisterHelper.registerItem(journal, Names.Objects.journal);
        trowel = new ItemTrowel();
        RegisterHelper.registerItem(trowel, Names.Objects.trowel);
        magnifyingGlass = new ItemMagnifyingGlass();
        RegisterHelper.registerItem(magnifyingGlass, Names.Objects.magnifyingGlass);
        debugItem = new ItemDebugger();
        RegisterHelper.registerItem(debugItem, "debugger");
        if (ConfigurationHandler.enableHandRake) {
            handRake = new ItemHandRake();
            RegisterHelper.registerItem(handRake, Names.Objects.handRake);
        }
        LogHelper.debug("Items Registered");
    }
}

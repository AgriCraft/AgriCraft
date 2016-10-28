package com.infinityraider.agricraft.items;

import java.util.ArrayList;
import java.util.List;

import com.infinityraider.agricraft.items.modes.*;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.infinitylib.handler.ConfigurationHandler;
import com.infinityraider.infinitylib.item.IItemWithModel;
import com.infinityraider.infinitylib.item.ItemDebuggerBase;
import com.infinityraider.infinitylib.utility.debug.DebugMode;

public class ItemDebugger extends ItemDebuggerBase implements IItemWithModel {

    public ItemDebugger() {
        super(true);
        this.setMaxStackSize(1);
        if (ConfigurationHandler.getInstance().debug) {
            this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
        }
    }

    @Override
    protected List<DebugMode> getDebugModes() {
        List<DebugMode> list = new ArrayList<>();
        list.add(new DebugModeCheckSoil());
        list.add(new DebugModeClearGrass());
        list.add(new DebugModeCoreInfo());
        return list;
    }
}

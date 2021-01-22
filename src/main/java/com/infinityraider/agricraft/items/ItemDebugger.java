package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.items.modes.*;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.infinitylib.item.IItemWithModel;
import com.infinityraider.infinitylib.item.ItemDebuggerBase;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import java.util.ArrayList;
import java.util.List;

public class ItemDebugger extends ItemDebuggerBase implements IItemWithModel {

    public ItemDebugger() {
        super(true);
        this.setMaxStackSize(1);
        if (AgriCraftConfig.debug) {
            this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
        }
    }

    @Override
    protected List<DebugMode> getDebugModes() {
        List<DebugMode> list = new ArrayList<>();
        list.add(new DebugModeCheckSoil());
        list.add(new DebugModeClearGrass());
        list.add(new DebugModeCoreInfo());
        list.add(new DebugModeTestBlockRange()); // Just for temporary testing.
        list.add(new DebugModeIGrowable());
        list.add(new DebugModeDiffLight());
        list.add(new DebugModeFillComponent());
        return list;
    }
}

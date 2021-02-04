package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.infinitylib.item.BlockItemBase;

public class ItemSeedAnalyzer extends BlockItemBase {
    public ItemSeedAnalyzer() {
        super(AgriCraft.instance.getModBlockRegistry().seed_analyzer, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
                .maxStackSize(1));
    }
}

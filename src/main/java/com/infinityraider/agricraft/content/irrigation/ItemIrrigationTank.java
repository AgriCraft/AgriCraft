package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;

public class ItemIrrigationTank extends BlockItemDynamicTexture {
    public ItemIrrigationTank() {
        super(AgriCraft.instance.getModBlockRegistry().tank, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
        );
    }
}

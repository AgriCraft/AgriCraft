package com.infinityraider.agricraft.content.decoration;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;

public class ItemGrate extends BlockItemDynamicTexture {
    public ItemGrate() {
        super(AgriCraft.instance.getModBlockRegistry().grate, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
        );
    }
}

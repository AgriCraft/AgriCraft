package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import net.minecraft.item.Item;

public class ItemIrrigationChannel extends BlockItemDynamicTexture {
    public ItemIrrigationChannel() {
        super(AgriCraft.instance.getModBlockRegistry().channel, new Item.Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
        );
    }
}

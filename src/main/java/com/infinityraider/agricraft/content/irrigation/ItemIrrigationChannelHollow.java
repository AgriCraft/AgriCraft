package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.content.core.ItemCustomWood;
import net.minecraft.world.item.Item;

public class ItemIrrigationChannelHollow extends ItemCustomWood {
    public ItemIrrigationChannelHollow() {
        super(AgriBlockRegistry.getInstance().irrigation_channel_hollow.get(), new Item.Properties()
                .tab(AgriTabs.TAB_AGRICRAFT)
        );
    }
}

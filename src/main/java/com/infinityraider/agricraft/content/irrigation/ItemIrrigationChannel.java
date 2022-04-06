package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.content.core.ItemCustomWood;
import net.minecraft.world.item.Item;

public class ItemIrrigationChannel extends ItemCustomWood {
    public ItemIrrigationChannel() {
        super(AgriBlockRegistry.CHANNEL, new Item.Properties()
                .tab(AgriTabs.TAB_AGRICRAFT)
        );
    }
}

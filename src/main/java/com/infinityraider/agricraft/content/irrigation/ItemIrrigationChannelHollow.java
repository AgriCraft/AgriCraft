package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.content.core.ItemCustomWood;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import net.minecraft.item.Item;

public class ItemIrrigationChannelHollow extends ItemCustomWood {
    public ItemIrrigationChannelHollow() {
        super(AgriCraft.instance.getModBlockRegistry().channel_hollow, new Item.Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
        );
    }
}

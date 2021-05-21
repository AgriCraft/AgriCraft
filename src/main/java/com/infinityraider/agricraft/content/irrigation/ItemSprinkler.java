package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.infinitylib.item.BlockItemBase;
import net.minecraft.item.Item;

public class ItemSprinkler extends BlockItemBase {
    public ItemSprinkler() {
        super(AgriCraft.instance.getModBlockRegistry().sprinkler, new Item.Properties()
                .group(AgriTabs.TAB_AGRICRAFT));
    }
}

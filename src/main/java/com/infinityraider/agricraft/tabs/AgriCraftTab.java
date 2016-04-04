package com.infinityraider.agricraft.tabs;

import com.infinityraider.agricraft.init.AgriCraftItems;
import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class AgriCraftTab {
    public static CreativeTabs agriCraftTab = new CreativeTabs(Reference.MOD_ID) {
        @Override
        public Item getTabIconItem() {
            return AgriCraftItems.debugItem;
        }
    };
}

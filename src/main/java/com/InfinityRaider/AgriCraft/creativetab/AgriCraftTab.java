package com.InfinityRaider.AgriCraft.creativetab;

import com.InfinityRaider.AgriCraft.reference.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class AgriCraftTab {
    public static CreativeTabs agriCraftTab = new CreativeTabs(Reference.MOD_ID.toLowerCase()) {
        @Override
        public Item getTabIconItem() {
            return Items.wheat;
        }
    };
}

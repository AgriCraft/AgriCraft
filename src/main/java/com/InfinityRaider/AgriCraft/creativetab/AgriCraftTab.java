package com.InfinityRaider.AgriCraft.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.reference.Reference;

public class AgriCraftTab {
    public static CreativeTabs agriCraftTab = new CreativeTabs(Reference.MOD_ID.toLowerCase()) {
        @Override
        public Item getTabIconItem() {
            return Items.debugItem;
        }
    };
}

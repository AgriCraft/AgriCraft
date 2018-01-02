package com.infinityraider.agricraft.items.tabs;

import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AgriTabs {

    public static final ItemStack TAB_AGRICRAFT_STACK = new ItemStack(AgriItems.getInstance().DEBUGGER);

    public static final ItemStack TAB_AGRICRAFT_SEED_STACK = new ItemStack(AgriItems.getInstance().AGRI_SEED);

    public static final CreativeTabs TAB_AGRICRAFT = new CreativeTabs(Reference.MOD_ID) {
        @Override
        public ItemStack getTabIconItem() {
            return TAB_AGRICRAFT_STACK;
        }
    };

    public static final CreativeTabs TAB_AGRICRAFT_SEED = new CreativeTabs(Reference.MOD_ID + "_seeds") {
        @Override
        public ItemStack getTabIconItem() {
            return TAB_AGRICRAFT_SEED_STACK;
        }
    };

}

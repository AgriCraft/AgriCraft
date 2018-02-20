package com.infinityraider.agricraft.items.tabs;

import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class AgriTabs {

    public static final CreativeTabs TAB_AGRICRAFT = new CreativeTabs(Reference.MOD_ID) {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(AgriItems.getInstance().DEBUGGER);
        }
    };

    public static final CreativeTabs TAB_AGRICRAFT_SEED = new CreativeTabs(Reference.MOD_ID + "_seeds") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(AgriItems.getInstance().AGRI_SEED);
        }
    };

}

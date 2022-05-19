package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AgriTabs {
    public static final CreativeModeTab TAB_AGRICRAFT = new CreativeModeTab(Reference.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(AgriApi.getAgriContent().getItems().getDebuggerItem());
        }
    };

    public static final CreativeModeTab TAB_AGRICRAFT_SEED = new CreativeModeTab(Reference.MOD_ID + "_seeds") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.WHEAT_SEEDS);
        }
    };

    public static final IAgriContent.Tabs TABS = new IAgriContent.Tabs() {
        @Override
        public CreativeModeTab getAgriCraftTab() {
            return TAB_AGRICRAFT;
        }

        @Override
        public CreativeModeTab getSeedsTab() {
            return TAB_AGRICRAFT_SEED;
        }
    };
}

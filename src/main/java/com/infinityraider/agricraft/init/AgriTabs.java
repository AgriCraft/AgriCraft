package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AgriTabs {
    private static final ItemStack TAB_ICON_MAIN = new ItemStack(AgriItemRegistry.getInstance().debugger);
    private static final ItemStack TAB_ICON_SEED = new ItemStack(Items.WHEAT_SEEDS);

    public static final ItemGroup TAB_AGRICRAFT = new ItemGroup(Reference.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return TAB_ICON_MAIN;
        }
    };

    public static final ItemGroup TAB_AGRICRAFT_SEED = new ItemGroup(Reference.MOD_ID + "_seeds") {
        @Override
        public ItemStack createIcon() {
            return TAB_ICON_SEED;
        }
    };
}

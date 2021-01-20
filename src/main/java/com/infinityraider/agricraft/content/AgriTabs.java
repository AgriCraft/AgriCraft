package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AgriTabs {
    public static final ItemGroup TAB_AGRICRAFT = new ItemGroup(Reference.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(AgriItemRegistry.getInstance().debugger);
        }
    };

    public static final ItemGroup TAB_AGRICRAFT_SEED = new ItemGroup(Reference.MOD_ID + "_seeds") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.WHEAT_SEEDS);
        }
    };
}

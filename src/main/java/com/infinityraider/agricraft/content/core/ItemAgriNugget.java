package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeHooks;

public class ItemAgriNugget extends ItemBase {
    public ItemAgriNugget(String name) {
        super(Names.Items.NUGGET + "_" + name, new Properties().group(ItemGroup.MATERIALS));
    }

    public static class Burnable extends ItemAgriNugget {
        private static final ItemStack REFERENCE = new ItemStack(Items.COAL, 1);
        public Burnable(String name) {
            super(name);
        }

        @Override
        public int getBurnTime(ItemStack itemStack) {
            return ForgeHooks.getBurnTime(REFERENCE) / 9;
        }
    }
}

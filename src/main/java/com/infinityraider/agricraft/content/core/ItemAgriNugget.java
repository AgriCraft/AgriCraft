package com.infinityraider.agricraft.content.core;

import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;

public class ItemAgriNugget extends ItemBase {
    public ItemAgriNugget(String name) {
        super(name, new Properties().tab(CreativeModeTab.TAB_MATERIALS));
    }

    public static class Burnable extends ItemAgriNugget {
        private static final ItemStack REFERENCE = new ItemStack(Items.COAL, 1);
        public Burnable(String name) {
            super(name);
        }

        @Override
        public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
            return ForgeHooks.getBurnTime(REFERENCE, recipeType) / 9;
        }
    }
}

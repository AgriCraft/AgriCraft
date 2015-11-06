package com.InfinityRaider.AgriCraft.items.crafting;


import com.InfinityRaider.AgriCraft.items.blocks.ItemBlockCustomWood;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RecipeShapelessCustomWood extends ShapelessRecipes {

    public RecipeShapelessCustomWood(ItemStack recipeOutput, List recipeItems) {
        super(recipeOutput, recipeItems);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        List recipeItems = new ArrayList(this.recipeItems);
        for(int column = 0; column < 3; ++column) {
            for(int row = 0; row < 3; ++row) {
                ItemStack itemStackToMatch = inventoryCrafting.getStackInRowAndColumn(row, column);
                if(itemStackToMatch != null) {
                    boolean match = false;
                    for (Object recipeItem : recipeItems) {
                        ItemStack itemStack = (ItemStack) recipeItem;
                        if (itemStackToMatch.getItem() == itemStack.getItem() && (itemStack.getItemDamage() == 32767 || itemStackToMatch.getItemDamage() == itemStack.getItemDamage())) {
                            if (itemStackToMatch.getItem() instanceof ItemBlockCustomWood) {
                                if (itemStackToMatch.stackTagCompound != null && itemStack.stackTagCompound != null && itemStackToMatch.stackTagCompound.equals(itemStack.stackTagCompound)) {
                                    match = true;
                                    recipeItems.remove(itemStack);
                                    break;
                                }
                            } else {
                                match = true;
                                recipeItems.remove(itemStack);
                                break;
                            }
                        }
                    }
                    if(!match) {
                        return false;
                    }
                }
            }
        }
        return recipeItems.isEmpty();
    }
}

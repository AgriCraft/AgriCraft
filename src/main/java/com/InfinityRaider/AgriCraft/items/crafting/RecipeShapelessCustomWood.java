package com.infinityraider.agricraft.items.crafting;


import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RecipeShapelessCustomWood extends ShapelessRecipes {

    public RecipeShapelessCustomWood(ItemStack recipeOutput, List<ItemStack> recipeItems) {
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
                                NBTTagCompound tag = itemStack.getTagCompound();
                                NBTTagCompound tagToMatch = itemStackToMatch.getTagCompound();
                                if (tagToMatch !=null && tag != null && tagToMatch.equals(tag)) {
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

package com.infinityraider.agricraft.crafting;

import com.agricraft.agricore.util.TypeHelper;
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
        // Copy recipe
        final List elements = new ArrayList(this.recipeItems);
        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 3; row++) {
                final ItemStack toMatch = inventoryCrafting.getStackInRowAndColumn(row, column);
                if (toMatch != null) {
                    boolean match = false;
                    for (Object o : elements) {
                        if (stacksMatch(toMatch, (ItemStack) o)) {
                            elements.remove(o);
                            match = true;
                            break;
                        }
                    }
                    if (!match) {
                        return false;
                    }
                }
            }
        }
        return elements.isEmpty();
    }

    private boolean stacksMatch(ItemStack itemStackToMatch, ItemStack itemStack) {
        if (itemStackToMatch.getItem() == itemStack.getItem() && metasMatch(itemStackToMatch, itemStack)) {
            if (itemStackToMatch.getItem() instanceof ItemBlockCustomWood) {
                NBTTagCompound tag = itemStack.getTagCompound();
                NBTTagCompound tagToMatch = itemStackToMatch.getTagCompound();
                return tagToMatch != null && tag != null && tagToMatch.equals(tag);
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean metasMatch(ItemStack itemStackToMatch, ItemStack itemStack) {
        return itemStack.getItemDamage() == 32767 || itemStackToMatch.getItemDamage() == itemStack.getItemDamage();
    }

}

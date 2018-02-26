package com.infinityraider.agricraft.crafting;

import com.infinityraider.agricraft.items.ItemJournal;
import com.infinityraider.agricraft.utility.StackHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeCopyJournal extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting invCrafting, World world) {
        boolean foundJournal = false;
        boolean foundBook = false;
        for (int i = 0; i < invCrafting.getSizeInventory(); i++) {
            ItemStack stackAtIndex = invCrafting.getStackInSlot(i);
            if (!stackAtIndex.isEmpty() && stackAtIndex.getItem() != null) {
                if (stackAtIndex.getItem() instanceof ItemJournal) {
                    if (!foundJournal) {
                        foundJournal = true;
                    } else {
                        // There can't be two journals!
                        // Scandalous!
                        return false;
                    }
                } else if (stackAtIndex.getItem() == Items.WRITABLE_BOOK) {
                    if (!foundBook) {
                        foundBook = true;
                    } else {
                        // There can only be one true king!
                        return false;
                    }
                }
            }
        }
        return foundJournal && foundBook;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting invCrafting) {
        for (int i = 0; i < invCrafting.getSizeInventory(); i++) {
            ItemStack stack = invCrafting.getStackInSlot(i);
            if (StackHelper.isValid(stack, ItemJournal.class)) {
                return invCrafting.getStackInSlot(i).copy();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

}

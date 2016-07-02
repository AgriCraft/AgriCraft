package com.infinityraider.agricraft.crafting;

import com.infinityraider.agricraft.items.ItemJournal;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeJournal implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting invCrafting, World world) {
		ItemStack journal = null;
		ItemStack book = null;
		for (int i = 0; i < invCrafting.getSizeInventory(); i++) {
			ItemStack stackAtIndex = invCrafting.getStackInSlot(i);
			if (stackAtIndex != null && stackAtIndex.getItem() != null) {
				if (stackAtIndex.getItem() instanceof ItemJournal) {
					if (journal == null) {
						journal = stackAtIndex.copy();
					} else {
						return false;
					}
				} else if (stackAtIndex.getItem() == Items.WRITABLE_BOOK) {
					if (book == null) {
						book = stackAtIndex;
					} else {
						return false;
					}
				}
			}
		}
		return journal != null && book != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting invCrafting) {
		for (int i = 0; i < invCrafting.getSizeInventory(); i++) {
			if (invCrafting.getStackInSlot(i) != null && invCrafting.getStackInSlot(i).getItem() instanceof ItemJournal) {
				return invCrafting.getStackInSlot(i).copy();
			}
		}
		return null;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		ItemStack remaining = getCraftingResult(inv);
		return new ItemStack[]{remaining};
	}

}

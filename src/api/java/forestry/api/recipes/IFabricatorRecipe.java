/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

public interface IFabricatorRecipe {

	/**
	 * @param plan The Fabricator plan, the item in the top right slot.
	 * @param resources The resources in the crafting grid.
	 * @return true if the plan and resources match this recipe.
	 */
	boolean matches(@Nullable ItemStack plan, ItemStack[][] resources);

	/**
	 * @return the molten liquid (and amount) required for this recipe.
	 */
	FluidStack getLiquid();

	/**
	 * @return the list of ingredients in the crafting grid to create this recipe.
	 */
	Object[] getIngredients();

	/**
	 * @return the width of ingredients in the crafting grid to create this recipe.
	 */
	int getWidth();

	/**
	 * @return the height of ingredients in the crafting grid to create this recipe.
	 */
	int getHeight();

	/**
	 * @return true if this recipe copies the NBT from input items to output items
	 */
	boolean preservesNbt();

	/**
	 * @return the plan for this recipe (the item in the top right slot).
	 */
	@Nullable
	ItemStack getPlan();

	/**
	 * Returns an Item that is the result of this recipe
	 */
	ItemStack getCraftingResult(IInventory craftingInventory);

	ItemStack getRecipeOutput();
}

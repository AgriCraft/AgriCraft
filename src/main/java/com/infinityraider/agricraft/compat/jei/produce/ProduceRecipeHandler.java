/*
 * AgriCraft JEI crop mutation recipe handler integration module.
 */
package com.infinityraider.agricraft.compat.jei.produce;

import com.infinityraider.agricraft.compat.jei.AgriCraftJEIPlugin;
import javax.annotation.Nonnull;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import com.infinityraider.agricraft.api.v3.IAgriCraftPlant;

/**
 *
 * @author RlonRyan
 */
public class ProduceRecipeHandler implements IRecipeHandler<IAgriCraftPlant> {

	@Nonnull
	@Override
	public Class<IAgriCraftPlant> getRecipeClass() {
		return IAgriCraftPlant.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return AgriCraftJEIPlugin.CATEGORY_PRODUCE;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull IAgriCraftPlant recipe) {
		return new ProduceRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(@Nonnull IAgriCraftPlant recipe) {
		return recipe.getAllFruits() != null;
	}

}

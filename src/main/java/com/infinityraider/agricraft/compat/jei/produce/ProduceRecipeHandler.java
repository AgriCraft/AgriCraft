/*
 * AgriCraft JEI crop mutation recipe handler integration module.
 */
package com.infinityraider.agricraft.compat.jei.produce;

import com.infinityraider.agricraft.compat.jei.AgriCraftJEIPlugin;
import javax.annotation.Nonnull;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import com.infinityraider.agricraft.api.plant.IAgriPlant;

/**
 *
 * 
 */
public class ProduceRecipeHandler implements IRecipeHandler<IAgriPlant> {

	@Nonnull
	@Override
	public Class<IAgriPlant> getRecipeClass() {
		return IAgriPlant.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return AgriCraftJEIPlugin.CATEGORY_PRODUCE;
	}
	
	@Override
	public String getRecipeCategoryUid(IAgriPlant recipe) {
		return AgriCraftJEIPlugin.CATEGORY_PRODUCE;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull IAgriPlant recipe) {
		return new ProduceRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(@Nonnull IAgriPlant recipe) {
		return recipe.getProducts() != null;
	}

}

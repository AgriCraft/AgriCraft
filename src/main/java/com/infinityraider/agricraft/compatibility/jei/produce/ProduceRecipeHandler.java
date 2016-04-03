/*
 * AgriCraft JEI crop mutation recipe handler integration module.
 */
package com.infinityraider.agricraft.compatibility.jei.produce;

import com.infinityraider.agricraft.api.v1.ICropPlant;
import com.infinityraider.agricraft.compatibility.jei.AgriCraftJEIPlugin;
import javax.annotation.Nonnull;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

/**
 *
 * @author RlonRyan
 */
public class ProduceRecipeHandler implements IRecipeHandler<ICropPlant> {

	@Nonnull
	@Override
	public Class<ICropPlant> getRecipeClass() {
		return ICropPlant.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return AgriCraftJEIPlugin.CATEGORY_PRODUCE;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull ICropPlant recipe) {
		return new ProduceRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(@Nonnull ICropPlant recipe) {
		return recipe.getAllFruits() != null;
	}

}

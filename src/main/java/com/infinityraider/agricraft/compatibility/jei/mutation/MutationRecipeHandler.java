/*
 * AgriCraft JEI crop mutation recipe handler integration module.
 */
package com.infinityraider.agricraft.compatibility.jei.mutation;

import com.infinityraider.agricraft.api.v1.IMutation;
import com.infinityraider.agricraft.compatibility.jei.AgriCraftJEIPlugin;
import javax.annotation.Nonnull;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

/**
 *
 * @author RlonRyan
 */
public class MutationRecipeHandler implements IRecipeHandler<IMutation> {

	@Nonnull
	@Override
	public Class<IMutation> getRecipeClass() {
		return IMutation.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return AgriCraftJEIPlugin.CATEGORY_MUTATION;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull IMutation recipe) {
		return new MutationRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(@Nonnull IMutation recipe) {
		return recipe.getParents().length > 0;
	}

}

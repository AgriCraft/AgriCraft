/*
 * AgriCraft JEI crop mutation recipe handler integration module.
 */
package com.infinityraider.agricraft.compatibility.jei.mutation;

import com.infinityraider.agricraft.farming.mutation.Mutation;
import javax.annotation.Nonnull;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

/**
 *
 * @author RlonRyan
 */
public class MutationRecipeHandler implements IRecipeHandler<Mutation> {

	@Nonnull
	@Override
	public Class<Mutation> getRecipeClass() {
		return Mutation.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return "agricraft.mutation";
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull Mutation recipe) {
		return new MutationRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(@Nonnull Mutation recipe) {
		return recipe.getParents().length <= 2;
	}

}

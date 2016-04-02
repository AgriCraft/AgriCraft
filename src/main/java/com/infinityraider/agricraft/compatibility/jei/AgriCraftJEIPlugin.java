package com.infinityraider.agricraft.compatibility.jei;

import com.infinityraider.agricraft.compatibility.jei.mutation.MutationRecipeCategory;
import com.infinityraider.agricraft.compatibility.jei.mutation.MutationRecipeHandler;
import com.infinityraider.agricraft.farming.mutation.MutationHandler;
import java.util.Arrays;
import mezz.jei.api.IItemRegistry;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class AgriCraftJEIPlugin implements IModPlugin {

	private IJeiHelpers jeiHelpers;

	@Override
	public void onJeiHelpersAvailable(IJeiHelpers jeiHelpers) {
		this.jeiHelpers = jeiHelpers;
	}

	@Override
	public void onItemRegistryAvailable(IItemRegistry itemRegistry) {
	}

	@Override
	public void register(IModRegistry registry) {
		
		registry.addRecipeCategories(
				new MutationRecipeCategory(jeiHelpers.getGuiHelper())
		);

		registry.addRecipeHandlers(
				new MutationRecipeHandler()
		);

		registry.addRecipes(Arrays.asList(MutationHandler.getMutations()));
	}

	@Override
	public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry) {
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
	}

}

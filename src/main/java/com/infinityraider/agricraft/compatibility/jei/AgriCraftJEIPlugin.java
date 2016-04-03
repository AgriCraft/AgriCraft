package com.infinityraider.agricraft.compatibility.jei;

import com.infinityraider.agricraft.api.v1.ICropPlant;
import com.infinityraider.agricraft.compatibility.jei.mutation.MutationRecipeCategory;
import com.infinityraider.agricraft.compatibility.jei.mutation.MutationRecipeHandler;
import com.infinityraider.agricraft.compatibility.jei.produce.ProduceRecipeCategory;
import com.infinityraider.agricraft.compatibility.jei.produce.ProduceRecipeHandler;
import com.infinityraider.agricraft.farming.CropPlantHandler;
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
	
	public static final String CATEGORY_MUTATION = "agricraft.mutation";
	public static final String CATEGORY_PRODUCE = "agricraft.produce";

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
				new MutationRecipeCategory(jeiHelpers.getGuiHelper()),
				new ProduceRecipeCategory(jeiHelpers.getGuiHelper())
		);

		registry.addRecipeHandlers(
				new MutationRecipeHandler(),
				new ProduceRecipeHandler()
		);

		registry.addRecipes(Arrays.asList(MutationHandler.getMutations()));
	}

	@Override
	public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry) {
		for(ICropPlant plant : CropPlantHandler.getPlants()) {
			recipeRegistry.addRecipe(plant);
		}
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
	}

}

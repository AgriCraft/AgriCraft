package com.infinityraider.agricraft.compatibility.jei;

import com.infinityraider.agricraft.compatibility.jei.mutation.MutationRecipeCategory;
import com.infinityraider.agricraft.compatibility.jei.mutation.MutationRecipeHandler;
import com.infinityraider.agricraft.compatibility.jei.produce.ProduceRecipeCategory;
import com.infinityraider.agricraft.compatibility.jei.produce.ProduceRecipeHandler;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.mutation.MutationHandler;
import java.util.Arrays;
import javax.annotation.Nonnull;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;

@JEIPlugin
public class AgriCraftJEIPlugin implements IModPlugin {
	
	public static final String CATEGORY_MUTATION = "agricraft.mutation";
	public static final String CATEGORY_PRODUCE = "agricraft.produce";

	@Override
	public void register(@Nonnull IModRegistry registry) {

		registry.addRecipeCategories(
				new MutationRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
				new ProduceRecipeCategory(registry.getJeiHelpers().getGuiHelper())
		);

		registry.addRecipeHandlers(
				new MutationRecipeHandler(),
				new ProduceRecipeHandler()
		);

		registry.addRecipes(Arrays.asList(MutationHandler.getMutations()));
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		for(IAgriCraftPlant plant : CropPlantHandler.getPlants()) {
			jeiRuntime.getRecipeRegistry().addRecipe(plant);
		}
	}

}

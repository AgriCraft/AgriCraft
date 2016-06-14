package com.infinityraider.agricraft.compat.jei;

import com.infinityraider.agricraft.compat.jei.mutation.MutationRecipeCategory;
import com.infinityraider.agricraft.compat.jei.mutation.MutationRecipeHandler;
import com.infinityraider.agricraft.compat.jei.produce.ProduceRecipeCategory;
import com.infinityraider.agricraft.compat.jei.produce.ProduceRecipeHandler;
import javax.annotation.Nonnull;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class AgriCraftJEIPlugin implements IModPlugin {

	public static final String CATEGORY_MUTATION = "agricraft.mutation";
	public static final String CATEGORY_PRODUCE = "agricraft.produce";

	private static final List<Object> toRegister = new ArrayList<>();
	private static IJeiRuntime jeiruntime;

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

	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		jeiruntime = jeiRuntime;
		for (Object o : toRegister) {
			jeiruntime.getRecipeRegistry().addRecipe(o);
		}
	}

	public static void registerRecipe(Object o) {
		if (!toRegister.contains(o)) {
			// Maintain a list to prevent duplicate registration.
			toRegister.add(o);
			if (jeiruntime != null) {
				jeiruntime.getRecipeRegistry().addRecipe(o);
			}
		}
	}

}

package com.infinityraider.agricraft.compat.jei;

import com.infinityraider.agricraft.compat.jei.mutation.MutationRecipeCategory;
import com.infinityraider.agricraft.compat.jei.mutation.MutationRecipeHandler;
import com.infinityraider.agricraft.compat.jei.produce.ProduceRecipeCategory;
import com.infinityraider.agricraft.compat.jei.produce.ProduceRecipeHandler;
import com.infinityraider.agricraft.init.AgriItems;
import javax.annotation.Nonnull;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mezz.jei.api.IJeiHelpers;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class AgriCraftJEIPlugin implements IModPlugin {

	public static final String CATEGORY_MUTATION = "agricraft.mutation";
	public static final String CATEGORY_PRODUCE = "agricraft.produce";

	private static final List<Object> toRegister = new ArrayList<>();
	private static final Map<Item, String[]> nbtIgnores = new HashMap<>();

	private static IJeiRuntime jeiRuntime;
	private static IJeiHelpers jeiHelpers;

	@Override
	public void register(@Nonnull IModRegistry registry) {

		jeiHelpers = registry.getJeiHelpers();

		registry.addRecipeCategories(
				new MutationRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
				new ProduceRecipeCategory(registry.getJeiHelpers().getGuiHelper())
		);

		registry.addRecipeHandlers(
				new MutationRecipeHandler(),
				new ProduceRecipeHandler()
		);
		
		registry.addRecipeCategoryCraftingItem(new ItemStack(AgriItems.CROPS), CATEGORY_MUTATION, CATEGORY_PRODUCE);

		for (Map.Entry<Item, String[]> nbt : nbtIgnores.entrySet()) {
			jeiHelpers.getNbtIgnoreList().ignoreNbtTagNames(nbt.getKey(), nbt.getValue());
		}

	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntimeInstance) {
		jeiRuntime = jeiRuntimeInstance;
		for (Object o : toRegister) {
			jeiRuntime.getRecipeRegistry().addRecipe(o);
		}
	}

	public static void registerRecipe(@Nonnull Object o) {
		if (!toRegister.contains(o)) {
			// Maintain a list to prevent duplicate registration.
			toRegister.add(o);
			if (jeiRuntime != null) {
				jeiRuntime.getRecipeRegistry().addRecipe(o);
			}
		}
	}

	// Todo: Determine what to do in case of duplicate entry.
	public static void registerNbtIgnore(@Nonnull Item item, @Nonnull List<String> tags) {
		if (!tags.isEmpty()) {
			registerNbtIgnore(item, tags.toArray(new String[tags.size()]));
		}
	}

	public static void registerNbtIgnore(@Nonnull Item item, @Nonnull String... tags) {
		nbtIgnores.put(item, tags);
		if (jeiHelpers != null) {
			jeiHelpers.getNbtIgnoreList().ignoreNbtTagNames(item, tags);
		}
	}

}

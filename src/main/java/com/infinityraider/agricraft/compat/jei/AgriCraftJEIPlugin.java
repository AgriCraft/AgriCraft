package com.infinityraider.agricraft.compat.jei;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.compat.jei.mutation.MutationRecipeCategory;
import com.infinityraider.agricraft.compat.jei.mutation.MutationRecipeHandler;
import com.infinityraider.agricraft.compat.jei.produce.ProduceRecipeCategory;
import com.infinityraider.agricraft.compat.jei.produce.ProduceRecipeHandler;
import com.infinityraider.agricraft.init.AgriItems;
import java.util.Optional;
import javax.annotation.Nonnull;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class AgriCraftJEIPlugin implements IModPlugin {

    public static final String CATEGORY_MUTATION = "agricraft.mutation";
    public static final String CATEGORY_PRODUCE = "agricraft.produce";

    private IJeiRuntime jeiRuntime = null;

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

        registry.addRecipeCategoryCraftingItem(new ItemStack(AgriItems.getInstance().CROPS), CATEGORY_MUTATION, CATEGORY_PRODUCE);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntimeInstance) {
        this.jeiRuntime = jeiRuntimeInstance;
        AgriApi.getPlantRegistry().all().forEach(this.jeiRuntime.getRecipeRegistry()::addRecipe);
        AgriApi.getMutationRegistry().all().forEach(this.jeiRuntime.getRecipeRegistry()::addRecipe);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.registerSubtypeInterpreter(AgriItems.getInstance().AGRI_SEED, (stack) -> {
            Optional<AgriSeed> seed = AgriApi.getSeedRegistry().valueOf(stack);
            return seed.map(s -> s.getPlant().getId()).orElse("generic");
        });
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {
        // Nothing to do here as far as I know...
        // I suppose mezz might know but oh well...
    }

}

package com.infinityraider.agricraft.plugins.jei;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AgriMutationRecipe {
    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "mutation");

    @Nullable
    public static IRecipeCategory<IAgriMutation> CATEGORY = null;

    public static void registerCategory(IRecipeCategoryRegistration registration) {
        if (CATEGORY == null) {
            CATEGORY = new AgriMutationRecipeCategory(registration.getJeiHelpers(), ID);
            registration.addRecipeCategories(CATEGORY);
        }
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(AgriApi.getMutationRegistry().all(), ID);
    }
}

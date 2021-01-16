package com.infinityraider.agricraft.plugins.jei;

import com.infinityraider.agricraft.AgriCraft;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@mezz.jei.api.JeiPlugin
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class JeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "compat_jei");

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        AgriPlantIngredient.register(registration);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        AgriMutationRecipe.registerCategory(registration);
        AgriProduceRecipe.registerCategory(registration);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        AgriMutationRecipe.registerRecipes(registration);
        AgriProduceRecipe.registerRecipes(registration);
    }
}

package com.infinityraider.agricraft.plugins.jei;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@mezz.jei.api.JeiPlugin
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class JeiPlugin implements IModPlugin {

    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "compat_jei");

    private static IJeiRuntime jei;

    public static IJeiRuntime getJei() {
        return jei;
    }

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        jei = jeiRuntime;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        AgriMutationRecipeCategory.registerRecipeCatalysts(registration);
        AgriProduceRecipeCategory.registerRecipeCatalysts(registration);
        AgriClippingRecipeCategory.registerRecipeCatalysts(registration);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        AgriPlantIngredient.register(registration);
        AgriSoilIngredient.register(registration);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AgriMutationRecipeCategory());
        registration.addRecipeCategories(new AgriProduceRecipeCategory());
        registration.addRecipeCategories(new AgriClippingRecipeCategory());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        AgriMutationRecipeCategory.registerRecipes(registration);
        AgriProduceRecipeCategory.registerRecipes(registration);
        AgriClippingRecipeCategory.registerRecipes(registration);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        // Register All The Seeds.
        registration.registerSubtypeInterpreter(AgriItemRegistry.getInstance().seed, (stack) -> {
            Optional<AgriSeed> seed = AgriApi.getSeedAdapterizer().valueOf(stack);
            return seed.map(s -> s.getPlant().getId()).orElse("generic");
        });
    }

    public static IAgriDrawable createAgriDrawable(ResourceLocation location, int u, int v, int w, int h, int textureWidth, int textureHeight) {
        return new IAgriDrawable() {
            @Override
            public int getWidth() {
                return w;
            }
    
            @Override
            public int getHeight() {
                return h;
            }
    
            @Override
            public void draw(MatrixStack transform, int x, int y) {
                this.bindTexture(location);
                Screen.blit(transform, x, y, u, v, getWidth(), getHeight(), textureWidth, textureHeight);
            }
        };
    }

}

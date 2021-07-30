package com.infinityraider.agricraft.plugins.jei;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.capability.CapabilityResearchedPlants;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@mezz.jei.api.JeiPlugin
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class JeiPlugin implements IModPlugin {

    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "compat_jei");

    private static IJeiRuntime jei;

    @Nullable
    public static IJeiRuntime getJei() {
        return jei;
    }

    public static void hideMutation(IAgriMutation mutation) {
        if(jei != null) {
            jei.getRecipeManager().hideRecipe(mutation, AgriRecipeCategoryMutation.ID);
        }
    }

    public static void unHideMutation(IAgriMutation mutation) {
        if(jei != null) {
            jei.getRecipeManager().unhideRecipe(mutation, AgriRecipeCategoryMutation.ID);
        }
    }

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        jei = jeiRuntime;
        if (AgriCraft.instance.getConfig().progressiveJEI()) {
            PlayerEntity player = AgriCraft.instance.getClientPlayer();
            AgriApi.getMutationRegistry()
                    .stream()
                    .forEach(mutation -> {
                        if (CapabilityResearchedPlants.getInstance().isMutationResearched(player, mutation)) {
                            unHideMutation(mutation);

                        } else {
                            hideMutation(mutation);
                        }
                    });
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        AgriRecipeCategoryMutation.registerRecipeCatalysts(registration);
        AgriRecipeCategoryProduce.registerRecipeCatalysts(registration);
        AgriRecipeCategoryClipping.registerRecipeCatalysts(registration);
        AgriRecipeCategoryGrowthRequirements.registerRecipeCatalysts(registration);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        AgriIngredientPlant.register(registration);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AgriRecipeCategoryMutation());
        registration.addRecipeCategories(new AgriRecipeCategoryProduce());
        registration.addRecipeCategories(new AgriRecipeCategoryClipping());
        registration.addRecipeCategories(new AgriRecipeCategoryGrowthRequirements(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        AgriRecipeCategoryMutation.registerRecipes(registration);
        AgriRecipeCategoryProduce.registerRecipes(registration);
        AgriRecipeCategoryClipping.registerRecipes(registration);
        AgriRecipeCategoryGrowthRequirements.registerRecipes(registration);
        AgriAnvilRecipes.registerRecipes(registration);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        // Register All The Seeds.
        registration.registerSubtypeInterpreter(AgriItemRegistry.getInstance().seed, (stack, context) -> {
            Optional<IAgriGenome> genome = AgriApi.getGenomeAdapterizer().valueOf(stack);
            return genome.map(s -> s.getPlant().getId()).orElse("generic");
        });
    }

    public static IAgriDrawable createAgriDrawable(ResourceLocation location, int u, int v, int w, int h, int textureWidth, int textureHeight) {
        return createAgriDrawable(location, u, v, w, h, w, h, textureWidth, textureHeight);
    }

    public static IAgriDrawable createAgriDrawable(ResourceLocation location, int u, int v, int w, int h, int uMax, int vMax, int textureWidth, int textureHeight) {
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
                Screen.blit(transform, x, y, getWidth(), getHeight(), u, v, uMax, vMax, textureWidth, textureHeight);
            }
        };
    }

}

package com.agricraft.agricraft.compat.jei;


import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.common.item.crafting.MagnifyingHelmetRecipe;
import com.agricraft.agricraft.common.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class AgriCraftJeiPlugin implements IModPlugin {

	public static final ResourceLocation ID = new ResourceLocation(AgriApi.MOD_ID, "compat_jei");

	@Override
	@NotNull
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		// Register all The Seeds.
		registration.registerSubtypeInterpreter(ModItems.SEED.get(), (stack, context) -> {
			AgriGenome genome = AgriGenome.fromNBT(stack.getTag());
			if (genome != null) {
				return genome.getSpeciesGene().getTrait();
			}
			return "agricraft:unknown";
		});
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		registration.getCraftingCategory().addCategoryExtension(MagnifyingHelmetRecipe.class, MagnifyingHelmetExtension::new);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new CropProduceCategory());
		registration.addRecipeCategories(new CropClippingCategory());
		registration.addRecipeCategories(new CropRequirementCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		AgriApi.getPlantRegistry().ifPresent(registry -> registration.addRecipes(CropProduceCategory.TYPE, registry.stream().toList()));
		AgriApi.getPlantRegistry().ifPresent(registry -> registration.addRecipes(CropClippingCategory.TYPE, registry.stream().filter(plant -> !plant.clipProducts().isEmpty()).toList()));
		AgriApi.getPlantRegistry().ifPresent(registry -> registration.addRecipes(CropRequirementCategory.TYPE, registry.stream().map(CropRequirementCategory.Recipe::new).toList()));
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registration) {
		AgriApi.getPlantRegistry().ifPresent(registry -> registration.register(PlantIngredient.TYPE, registry.stream().toList(), PlantIngredient.HELPER, PlantIngredient.RENDERER));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(ModItems.CLIPPER.get().getDefaultInstance(), CropClippingCategory.TYPE);
	}

	public static IDrawable createDrawable(ResourceLocation location, int uOffset, int vOffset, int width, int height, int textureWidth, int textureHeight) {
		return new IDrawable() {
			@Override
			public int getWidth() {
				return width;
			}

			@Override
			public int getHeight() {
				return height;
			}

			@Override
			public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
				guiGraphics.blit(location, xOffset, yOffset, uOffset, vOffset, getWidth(), getHeight(), textureWidth, textureHeight);
			}
		};
	}

}
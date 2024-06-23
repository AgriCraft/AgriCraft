package com.agricraft.agricraft.compat.emi;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.common.registry.ModItems;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;

public class AgriCraftEmiPlugin implements EmiPlugin {

	public static final EmiStack WOODEN_CROP_STICK = EmiStack.of(ModItems.WOODEN_CROP_STICKS.get());
	public static final EmiStack CLIPPER = EmiStack.of(ModItems.CLIPPER.get());
	public static final EmiStack FARMLAND = EmiStack.of(Items.FARMLAND);
	public static final EmiTexture TEXTURE = new EmiTexture(new ResourceLocation(AgriApi.MOD_ID, "textures/gui/jei/crop_mutation.png"), 0, 0, 128, 128, 128, 128, 128, 128);
	public static final EmiRecipeCategory MUTATION_CATEGORY = new EmiRecipeCategory(new ResourceLocation("agricraft", "mutation"), WOODEN_CROP_STICK);
	public static final EmiRecipeCategory PRODUCE_CATEGORY = new EmiRecipeCategory(new ResourceLocation("agricraft", "produce"), WOODEN_CROP_STICK);
	public static final EmiRecipeCategory CLIPPING_CATEGORY = new EmiRecipeCategory(new ResourceLocation("agricraft", "clipping"), CLIPPER);
	public static final EmiRecipeCategory REQUIREMENT_CATEGORY = new EmiRecipeCategory(new ResourceLocation("agricraft", "requirement"), FARMLAND);

	public static <T> ResourceLocation prefixedId(ResourceKey<T> key, String prefix) {
		return new ResourceLocation("agricraft", "/" + prefix + "/" + key.location().toString().replace(":", "/"));
	}

	@Override
	public void register(EmiRegistry registry) {
		registry.addCategory(MUTATION_CATEGORY);
		registry.addWorkstation(MUTATION_CATEGORY, WOODEN_CROP_STICK);
		registry.addWorkstation(MUTATION_CATEGORY, EmiStack.of(ModItems.IRON_CROP_STICKS.get()));
		registry.addWorkstation(MUTATION_CATEGORY, EmiStack.of(ModItems.OBSIDIAN_CROP_STICKS.get()));

		EmiStack normalSeed = EmiStack.of(ModItems.SEED.get()).comparison(Comparison.compareNbt());
		registry.removeEmiStacks(normalSeed);
		AgriApi.getMutationRegistry().ifPresent(mutations -> mutations.entrySet().forEach(entry -> registry.addRecipe(new CropMutationRecipe(prefixedId(entry.getKey(), "mutations"), entry.getValue()))));


		registry.addCategory(PRODUCE_CATEGORY);
		registry.addWorkstation(PRODUCE_CATEGORY, WOODEN_CROP_STICK);
		registry.addWorkstation(PRODUCE_CATEGORY, EmiStack.of(ModItems.IRON_CROP_STICKS.get()));
		registry.addWorkstation(PRODUCE_CATEGORY, EmiStack.of(ModItems.OBSIDIAN_CROP_STICKS.get()));
		AgriApi.getPlantRegistry().ifPresent(plants -> plants.entrySet().forEach(entry -> {
			ArrayList<ItemStack> l = new ArrayList<>();
			entry.getValue().getAllPossibleProducts(l::add);
			if (!l.isEmpty()) {
				registry.addRecipe(new CropProduceRecipe(prefixedId(entry.getKey(), "products"), entry.getValue()));
			}
		}));

		registry.addCategory(CLIPPING_CATEGORY);
		registry.addWorkstation(CLIPPING_CATEGORY, CLIPPER);
		AgriApi.getPlantRegistry().ifPresent(plants -> plants.entrySet().forEach(entry -> {
			ArrayList<ItemStack> l = new ArrayList<>();
			entry.getValue().getAllPossibleClipProducts(l::add);
			if (!l.isEmpty()) {
				registry.addRecipe(new CropClippingRecipe(prefixedId(entry.getKey(), "clippings"), entry.getValue()));
			}
		}));

		registry.addCategory(REQUIREMENT_CATEGORY);
		registry.addWorkstation(REQUIREMENT_CATEGORY, FARMLAND);
		AgriApi.getPlantRegistry().ifPresent(plants -> plants.entrySet().forEach(entry -> registry.addRecipe(new CropRequirementRecipe(prefixedId(entry.getKey(), "requirements"), entry.getValue()))));

	}

	@Override
	public void initialize(EmiInitRegistry registry) {
		EmiPlugin.super.initialize(registry);
	}

}

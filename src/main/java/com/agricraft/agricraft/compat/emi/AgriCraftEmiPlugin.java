package com.agricraft.agricraft.compat.emi;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.common.registry.AgriDataComponents;
import com.agricraft.agricraft.common.registry.AgriItems;
import dev.emi.emi.api.EmiEntrypoint;
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

@EmiEntrypoint
public class AgriCraftEmiPlugin implements EmiPlugin {
	public static final EmiStack WOODEN_CROP_STICK = EmiStack.of(AgriItems.WOODEN_CROP_STICKS.get());
	public static final EmiStack CLIPPER = EmiStack.of(AgriItems.CLIPPER.get());
	public static final EmiStack FARMLAND = EmiStack.of(Items.FARMLAND);
	public static final EmiTexture TEXTURE = new EmiTexture(ResourceLocation.fromNamespaceAndPath(AgriApi.MOD_ID, "textures/gui/jei/crop_mutation.png"), 0, 0, 128, 128, 128, 128, 128, 128);
	public static final EmiRecipeCategory MUTATION_CATEGORY = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath("agricraft", "mutation"), WOODEN_CROP_STICK);
	public static final EmiRecipeCategory PRODUCE_CATEGORY = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath("agricraft", "produce"), WOODEN_CROP_STICK);
	public static final EmiRecipeCategory CLIPPING_CATEGORY = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath("agricraft", "clipping"), CLIPPER);
	public static final EmiRecipeCategory REQUIREMENT_CATEGORY = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath("agricraft", "requirement"), FARMLAND);

	public static final Comparison COMPARE_SEEDS = Comparison.compareData(stack -> {
		var genome = stack.get(AgriDataComponents.GENOME.get());
		if (genome != null) {
			return genome.species().trait();
		} else {
			return "unknown";
		}
	});

	public static Comparison compareSeeds() {
		return COMPARE_SEEDS;
	}

	public static <T> ResourceLocation prefixedId(ResourceKey<T> key, String prefix) {
		return ResourceLocation.fromNamespaceAndPath("agricraft", "/" + prefix + "/" + key.location().toString().replace(":", "/"));
	}
	@Override
	public void register(EmiRegistry registry) {
		registry.addCategory(MUTATION_CATEGORY);
		registry.addWorkstation(MUTATION_CATEGORY, WOODEN_CROP_STICK);
		registry.addWorkstation(MUTATION_CATEGORY, EmiStack.of(AgriItems.IRON_CROP_STICKS.get()));
		registry.addWorkstation(MUTATION_CATEGORY, EmiStack.of(AgriItems.OBSIDIAN_CROP_STICKS.get()));

		EmiStack normalSeed = EmiStack.of(AgriItems.SEED.get()).comparison(Comparison.compareComponents());
		registry.removeEmiStacks(normalSeed);
		AgriApi.get().getMutationRegistry().ifPresent(mutations -> mutations.entrySet().forEach(entry -> registry.addRecipe(new CropMutationRecipe(prefixedId(entry.getKey(), "mutations"), entry.getValue()))));

		registry.addCategory(PRODUCE_CATEGORY);
		registry.addWorkstation(PRODUCE_CATEGORY, WOODEN_CROP_STICK);
		registry.addWorkstation(PRODUCE_CATEGORY, EmiStack.of(AgriItems.IRON_CROP_STICKS.get()));
		registry.addWorkstation(PRODUCE_CATEGORY, EmiStack.of(AgriItems.OBSIDIAN_CROP_STICKS.get()));
		AgriApi.get().getPlantRegistry().ifPresent(plants -> plants.entrySet().forEach(entry -> {
			ArrayList<ItemStack> l = new ArrayList<>();
			entry.getValue().getAllPossibleProducts(l::add);
			if (!l.isEmpty()) {
				registry.addRecipe(new CropProduceRecipe(prefixedId(entry.getKey(), "products"), entry.getValue()));
			}
		}));

		registry.addCategory(CLIPPING_CATEGORY);
		registry.addWorkstation(CLIPPING_CATEGORY, CLIPPER);
		AgriApi.get().getPlantRegistry().ifPresent(plants -> plants.entrySet().forEach(entry -> {
			ArrayList<ItemStack> l = new ArrayList<>();
			entry.getValue().getAllPossibleClipProducts(l::add);
			if (!l.isEmpty()) {
				registry.addRecipe(new CropClippingRecipe(prefixedId(entry.getKey(), "clippings"), entry.getValue()));
			}
		}));

		registry.addCategory(REQUIREMENT_CATEGORY);
		registry.addWorkstation(REQUIREMENT_CATEGORY, FARMLAND);
		AgriApi.get().getPlantRegistry().ifPresent(plants -> plants.entrySet().forEach(entry -> registry.addRecipe(new CropRequirementRecipe(prefixedId(entry.getKey(), "requirements"), entry.getValue()))));

	}

}

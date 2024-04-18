package com.agricraft.agricraft.compat.emi;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriMutation;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.common.item.AgriSeedItem;
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

public class AgriCraftEmiPlugin implements EmiPlugin {

	public static final EmiStack WOODEN_CROP_STICK = EmiStack.of(ModItems.WOODEN_CROP_STICKS.get());
	public static final EmiTexture TEXTURE = new EmiTexture(new ResourceLocation(AgriApi.MOD_ID, "textures/gui/jei/crop_mutation.png"), 0, 0, 128, 128, 128, 128, 128, 128);
	public static final EmiRecipeCategory MUTATION_CATEGORY = new EmiRecipeCategory(new ResourceLocation("agricraft", "mutation"), WOODEN_CROP_STICK);

	public static ResourceLocation toId(ResourceKey<AgriMutation> key) {
		return new ResourceLocation("agricraft", "/mutations/" + key.location().toString().replace(":", "/"));
	}

	@Override
	public void register(EmiRegistry registry) {
		registry.addCategory(MUTATION_CATEGORY);
		registry.addWorkstation(MUTATION_CATEGORY, WOODEN_CROP_STICK);
		registry.addWorkstation(MUTATION_CATEGORY, EmiStack.of(ModItems.IRON_CROP_STICKS.get()));
		registry.addWorkstation(MUTATION_CATEGORY, EmiStack.of(ModItems.OBSIDIAN_CROP_STICKS.get()));

//		Comparison seedComparison = Comparison.of((a, b) -> {
//			AgriGenome genomeA = AgriGenome.fromNBT(a.getNbt());
//			AgriGenome genomeB = AgriGenome.fromNBT(b.getNbt());
//			if (genomeA == null || genomeB == null) {
//				return false;
//			}
//			return genomeA.getSpeciesGene().getTrait().equals(genomeB.getSpeciesGene().getTrait());
//		});
		EmiStack normalSeed = EmiStack.of(ModItems.SEED.get()).comparison(Comparison.compareNbt());
//
//		AgriApi.getPlantRegistry().ifPresent(plants -> plants.forEach(plant -> registry.addEmiStackAfter(EmiStack.of(AgriSeedItem.toStack(plant)), normalSeed)));
//
//
		registry.removeEmiStacks(normalSeed);
//
//		registry.setDefaultComparison(normalSeed, seedComparison);

		AgriApi.getMutationRegistry().ifPresent(mutations -> mutations.entrySet().forEach(entry -> registry.addRecipe(new CropMutationRecipe(toId(entry.getKey()), entry.getValue()))));
	}

	@Override
	public void initialize(EmiInitRegistry registry) {
		EmiPlugin.super.initialize(registry);
	}

}

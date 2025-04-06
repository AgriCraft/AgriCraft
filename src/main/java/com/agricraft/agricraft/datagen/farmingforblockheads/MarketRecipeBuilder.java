package com.agricraft.agricraft.datagen.farmingforblockheads;

import net.blay09.mods.farmingforblockheads.recipe.MarketRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MarketRecipeBuilder implements RecipeBuilder {

	private final ItemStack result;
	private final ResourceLocation category;
	private final ResourceLocation preset;

	public MarketRecipeBuilder(ItemStack result, ResourceLocation category, ResourceLocation preset) {
		this.result = result;
		this.category = category;
		this.preset = preset;
	}

	@Override
	public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
		return this;
	}

	@Override
	public RecipeBuilder group(@Nullable String groupName) {
		return this;
	}

	@Override
	public Item getResult() {
		return result.getItem();
	}

	@Override
	public void save(RecipeOutput output, ResourceLocation id) {
		output.accept(id, new MarketRecipe(result, category, preset, Optional.empty()), null);
	}

}

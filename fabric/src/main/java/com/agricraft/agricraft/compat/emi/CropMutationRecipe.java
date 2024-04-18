package com.agricraft.agricraft.compat.emi;

import com.agricraft.agricraft.api.codecs.AgriMutation;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CropMutationRecipe implements EmiRecipe {

	private final ResourceLocation id;
	private final List<EmiIngredient> input;
	private final List<EmiStack> output;

	public CropMutationRecipe(ResourceLocation id, AgriMutation mutation) {
		this.id = id;
		input = List.of(EmiStack.of(AgriSeedItem.toStack(mutation.getParent1().orElse(AgriPlant.NO_PLANT))).comparison(Comparison.compareNbt()),
				EmiStack.of(AgriSeedItem.toStack(mutation.getParent2().orElse(AgriPlant.NO_PLANT))).comparison(Comparison.compareNbt()));
		output = List.of(EmiStack.of(AgriSeedItem.toStack(mutation.getChild().orElse(AgriPlant.NO_PLANT))).comparison(Comparison.compareNbt()));
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return AgriCraftEmiPlugin.MUTATION_CATEGORY;
	}

	@Override
	public @Nullable ResourceLocation getId() {
		return this.id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return input;
	}

	@Override
	public List<EmiStack> getOutputs() {
		return output;
	}

	@Override
	public int getDisplayWidth() {
		return 128;
	}

	@Override
	public int getDisplayHeight() {
		return 64;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addSlot(input.get(0), 16, 6);
		widgets.addSlot(input.get(1), 96, 6);
		widgets.addSlot(output.get(0), 56, 2).recipeContext(this);
	}

}

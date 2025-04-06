package com.agricraft.agricraft.compat.emi;

import com.agricraft.agricraft.api.codecs.AgriMutation;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class CropMutationRecipe implements EmiRecipe {

	private final ResourceLocation id;
	private final List<EmiIngredient> input;
	private final List<EmiStack> output;

	public CropMutationRecipe(ResourceLocation id, AgriMutation mutation) {
		this.id = id;
		input = List.of(EmiStack.of(AgriSeedItem.toStack(mutation.getParent1().orElse(AgriPlant.NO_PLANT))).comparison(AgriCraftEmiPlugin.compareSeeds()),
				EmiStack.of(AgriSeedItem.toStack(mutation.getParent2().orElse(AgriPlant.NO_PLANT))).comparison(AgriCraftEmiPlugin.compareSeeds()));
		output = List.of(EmiStack.of(AgriSeedItem.toStack(mutation.getChild().orElse(AgriPlant.NO_PLANT))).comparison(AgriCraftEmiPlugin.compareSeeds()));
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return AgriCraftEmiPlugin.MUTATION_CATEGORY;
	}

	@Override
	public ResourceLocation getId() {
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
		return 125;
	}

	@Override
	public int getDisplayHeight() {
		return 18;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addSlot(input.get(0), 0, 0);
		widgets.addSlot(input.get(1), 49, 0);
		widgets.addSlot(output.get(0), 107, 0).recipeContext(this);
		widgets.addTexture(EmiTexture.PLUS, 27, 3);
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
	}

}

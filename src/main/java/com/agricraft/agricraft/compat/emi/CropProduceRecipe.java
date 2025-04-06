package com.agricraft.agricraft.compat.emi;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.AgriClientApi;
import com.agricraft.agricraft.api.LangUtils;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CropProduceRecipe implements EmiRecipe {

	public static final EmiTexture BACKGROUND = new EmiTexture(ResourceLocation.fromNamespaceAndPath(AgriApi.MOD_ID, "textures/gui/jei/crop_produce.png"), 0, 0, 128, 128, 128, 128, 128, 128);

	private final ResourceLocation id;
	private final List<EmiIngredient> input;
	private final AgriPlant plant;
	private final List<EmiStack> output;

	public CropProduceRecipe(ResourceLocation id, AgriPlant plant) {
		this.id = id;
		input = List.of(EmiStack.of(AgriSeedItem.toStack(plant)).comparison(AgriCraftEmiPlugin.compareSeeds()));
		this.plant = plant;
		output = new ArrayList<>();
		plant.getAllPossibleProducts(product -> output.add(EmiStack.of(product).comparison(Comparison.compareComponents())));
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return AgriCraftEmiPlugin.PRODUCE_CATEGORY;
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
		return 128;
	}

	@Override
	public int getDisplayHeight() {
		return 128;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(BACKGROUND, 0, 0);
		widgets.addSlot(input.get(0), 15, 8);
		widgets.addDrawable(16, 49, 16, 16, (draw, mouseX, mouseY, delta) -> {
			Optional<ResourceLocation> optional = plant.getId();
			if (optional.isPresent()) {
				ResourceLocation plantId = optional.get();
				// get the model for the last growth stage and use the particle texture (that is also the crop texture) to render in emi
				draw.blit(0, 0, 0, 16, 16, AgriClientApi.get().getPlantModel(plantId.toString(), plant.getInitialGrowthStage().total() - 1).getParticleIcon());
			}
		});
		ArrayList<Component> list = new ArrayList<>();
		plant.getId().map(ResourceLocation::toString).ifPresent(id -> {
			list.add(LangUtils.plantName(id));
			Component desc = LangUtils.plantDescription(id);
			if (desc != null) {
				list.add(desc);
			}
		});
		widgets.addTooltipText(list, 16, 49, 16, 16);
		int index = 0;

		for (int y = 32; y < 83; y += 18) {
			if (index >= output.size()) {
				break;
			}
			for (int x = 74; x < 129; x += 18) {
				if (index < output.size()) {
					widgets.addSlot(output.get(index), x, y).drawBack(false).recipeContext(this);
					index++;
				} else {
					break;
				}
			}
		}
	}

}

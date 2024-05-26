package com.agricraft.agricraft.compat.jei;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriProduct;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;

public class CropProduceCategory implements IRecipeCategory<AgriPlant> {

	public static final ResourceLocation ID = new ResourceLocation(AgriApi.MOD_ID, "jei/produce");
	public static final RecipeType<AgriPlant> TYPE = new RecipeType<>(ID, AgriPlant.class);

	public static final IDrawable ICON = AgriCraftJeiPlugin.createDrawable(new ResourceLocation(AgriApi.MOD_ID, "textures/item/debugger.png"), 0, 0, 16, 16, 16, 16);
	public static final IDrawable BACKGROUND = AgriCraftJeiPlugin.createDrawable(new ResourceLocation(AgriApi.MOD_ID, "textures/gui/jei/crop_produce.png"), 0, 0, 128, 128, 128, 128);

	@Override
	public RecipeType<AgriPlant> getRecipeType() {
		return TYPE;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("agricraft.gui.jei.produce");
	}

	@Override
	public IDrawable getBackground() {
		return BACKGROUND;
	}

	@Override
	public IDrawable getIcon() {
		return ICON;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, AgriPlant plant, IFocusGroup focuses) {
		// Set shapeless
		builder.setShapeless();

		// inputs
		builder.addSlot(RecipeIngredientRole.INPUT, 16, 9)
				.setSlotName("input_seed")
				.addIngredient(VanillaTypes.ITEM_STACK, AgriSeedItem.toStack(plant));
		builder.addSlot(RecipeIngredientRole.INPUT, 16, 49)
				.setSlotName("input_plant")
				.setCustomRenderer(PlantIngredient.TYPE, PlantIngredient.RENDERER)
				.addIngredient(PlantIngredient.TYPE, plant);

		// outputs
		int index = 0;

		ArrayList<ItemStack> products = new ArrayList<>();
		plant.getAllPossibleProducts(products::add);
		for (int y = 33; y < 83; y += 18) {
			if (index >= products.size()) {
				break;
			}
			for (int x = 75; x < 129; x += 18) {
				if (index < products.size()) {
					ItemStack product = products.get(index);
					IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y).setSlotName("output_" + index);
					slotBuilder.addItemStack(product);
					index++;
				} else {
					break;
				}
			}
		}
	}

}

package com.agricraft.agricraft.plugin.minecraft;

import com.agricraft.agricraft.api.config.CoreConfig;
import com.agricraft.agricraft.common.registry.ModItems;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.ComposterBlock;

import java.util.stream.Stream;

public class MinecraftPlugin {

	public static void init() {
		// add agricraft seeds to the composter
		float compostValue = CoreConfig.seedCompostValue;
		if (compostValue > 0) {
			ComposterBlock.COMPOSTABLES.put(ModItems.SEED.get(), compostValue);
		}
		// add agricraft seeds to the chicken food items
		// (we're replacing the ingredient with a new one containing our seed)
		Stream.Builder<ItemStack> builder = Stream.builder();
		for (ItemStack stack : Chicken.FOOD_ITEMS.getItems()) {
			builder.accept(stack);
		}
		builder.accept(ModItems.SEED.get().getDefaultInstance());
		Chicken.FOOD_ITEMS = Ingredient.of(builder.build());
		MinecraftPlantModifiers.register();
	}

}

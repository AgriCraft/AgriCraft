package com.agricraft.agricraft.api.adapter;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizer;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

// TODO: @Ketheroth move to minecraft plugin
public class FertilizerAdapter implements AgriAdapter<AgriFertilizer> {

	@Override
	public boolean accepts(Object obj) {
		if (obj instanceof ItemLike itemLike) {
			return accepts(new ItemStack(itemLike));
		}
		if (obj instanceof ItemStack itemStack) {
			return match(itemStack);
		}
		return false;
	}

	@Override
	public Optional<AgriFertilizer> valueOf(Object obj) {
		if (obj instanceof ItemLike itemLike) {
			return valueOf(new ItemStack(itemLike));
		}
		if (obj instanceof ItemStack itemStack) {
			return convert(itemStack);
		}
		return Optional.empty();
	}

	public boolean match(ItemStack itemStack) {
		return AgriApi.getFertilizerRegistry().map(registry -> registry.stream()
				.flatMap(fertilizer -> fertilizer.variants().stream())
				.anyMatch(seed -> seed.isVariant(itemStack))).orElse(false);
	}

	public Optional<AgriFertilizer> convert(ItemStack itemStack) {
		return AgriApi.getFertilizerRegistry().flatMap(registry -> registry.stream()
				.filter(fertilizer -> fertilizer.variants().stream().anyMatch(variant -> variant.isVariant(itemStack)))
				.findFirst());

	}

}

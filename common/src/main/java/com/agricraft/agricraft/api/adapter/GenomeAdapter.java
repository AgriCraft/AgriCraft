package com.agricraft.agricraft.api.adapter;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

// TODO: @Ketheroth move to minecraft plugin
public class GenomeAdapter implements AgriAdapter<AgriGenome> {

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
	public Optional<AgriGenome> valueOf(Object obj) {
		if (obj instanceof ItemLike itemLike) {
			return valueOf(new ItemStack(itemLike));
		}
		if (obj instanceof ItemStack itemStack) {
			return convert(itemStack);
		}
		return Optional.empty();
	}

	public boolean match(ItemStack itemStack) {
		return AgriApi.getPlantRegistry().map(registry -> registry.stream().anyMatch(seed -> seed.isSeedItem(itemStack))).orElse(false);
	}

	public Optional<AgriGenome> convert(ItemStack itemStack) {
		return AgriApi.getPlantRegistry().flatMap(registry -> registry.stream()
				.filter(plant -> plant.isSeedItem(itemStack))
				.findFirst()
				.map(AgriGenome::new));

	}

}

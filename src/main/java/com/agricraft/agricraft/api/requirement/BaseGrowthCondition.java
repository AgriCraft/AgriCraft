package com.agricraft.agricraft.api.requirement;

import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.mojang.datafixers.util.Function3;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class BaseGrowthCondition<T> implements AgriGrowthCondition<T> {

	private final String id;
	private final Function3<AgriPlant, Integer, T, AgriGrowthResponse> response;
	private final BiFunction<Level, BlockPos, T> getter;

	public BaseGrowthCondition(String id, Function3<AgriPlant, Integer, T, AgriGrowthResponse> response, BiFunction<Level, BlockPos, T> getter) {
		this.id = id;
		this.response = response;
		this.getter = getter;
	}

	@Override
	public AgriGrowthResponse check(AgriCrop crop, Level level, BlockPos pos, int strength) {
		return this.response.apply(crop.getPlant(), strength, this.getter.apply(level, pos));
	}

	@Override
	public AgriGrowthResponse apply(AgriPlant plant, int strength, T value) {
		return this.response.apply(plant, strength, value);
	}

	@Override
	public void notMetDescription(Consumer<Component> consumer) {
		consumer.accept(Component.translatable("agricraft.tooltip.condition." + this.id));
	}

}

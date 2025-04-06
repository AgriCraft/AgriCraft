package com.agricraft.agricraft.api.requirement;

import com.agricraft.agricraft.api.codecs.AgriRequirement;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.codecs.AgriSoilCondition;
import com.agricraft.agricraft.api.codecs.AgriSoilValue;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.plant.AgriPlant;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;
import java.util.function.Function;

public class SoilGrowthCondition<T extends AgriSoilValue> implements AgriGrowthCondition<T> {

	private final String id;
	private final Function<AgriRequirement, AgriSoilCondition<T>> conditionGetter;
	private final Function<AgriSoil, T> valueGetter;

	public SoilGrowthCondition(String id, Function<AgriRequirement, AgriSoilCondition<T>> conditionGetter, Function<AgriSoil, T> valueGetter) {
		this.id = id;
		this.conditionGetter = conditionGetter;
		this.valueGetter = valueGetter;
	}

	private static <T extends AgriSoilValue> AgriGrowthResponse response(int strength, T value, AgriSoilCondition<?> condition) {
		int lower = condition.type().lowerLimit(condition.value().ordinal() - (int) (condition.toleranceFactor() * strength));
		int upper = condition.type().upperLimit(condition.value().ordinal() + (int) (condition.toleranceFactor() * strength));
		if (value.isValid() && lower <= value.ordinal() && value.ordinal() <= upper) {
			return AgriGrowthResponse.FERTILE;
		}
		return AgriGrowthResponse.INFERTILE;
	}

	@Override
	public AgriGrowthResponse check(AgriCrop crop, Level level1, BlockPos pos, int strength1) {
		AgriSoilCondition<T> condition = this.conditionGetter.apply(crop.getPlant().getGrowthRequirements());
		return crop.getSoil().map(valueGetter).map(value -> response(strength1, value, condition)).orElse(AgriGrowthResponse.INFERTILE);
	}

	@Override
	public AgriGrowthResponse apply(AgriPlant plant, int strength, AgriSoilValue value) {
		AgriSoilCondition<T> condition = this.conditionGetter.apply(plant.getGrowthRequirements());
		return response(strength, value, condition);
	}

	@Override
	public void notMetDescription(Consumer<Component> consumer) {
		consumer.accept(Component.translatable("agricraft.tooltip.condition." + this.id));
	}

}

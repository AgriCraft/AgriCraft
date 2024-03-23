package com.agricraft.agricraft.api.requirement;

import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.crop.AgriCrop;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface AgriGrowthCondition<T> {

	/**
	 * Checks this value at the given position in the given world.
	 *
	 * @param crop     the crop for which to check this value
	 * @param level    the level the crop is in
	 * @param pos      the position in the world
	 * @param strength the strength stat of the crop
	 * @return the response.
	 */
	AgriGrowthResponse check(AgriCrop crop, Level level, BlockPos pos, int strength);

	AgriGrowthResponse apply(AgriPlant crop, int strength, T value);

}

package com.agricraft.agricraft.compat.botania;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.plant.AgriPlantModifierFactoryRegistry;

import java.util.Optional;

public class BotaniaPlugin {

	public static void init() {
		AgriPlantModifierFactoryRegistry.register(ManaPlantModifier.ID, info -> {
			try {
				int n = Integer.parseInt(info.value());
				return Optional.of(new ManaPlantModifier(n));
			} catch (NumberFormatException e) {
				return Optional.empty();
			}
		});
		AgriApi.getGrowthConditionRegistry().add(new ManaGrowthCondition());
	}

}

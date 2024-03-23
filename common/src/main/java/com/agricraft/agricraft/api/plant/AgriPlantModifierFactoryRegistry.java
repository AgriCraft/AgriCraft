package com.agricraft.agricraft.api.plant;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AgriPlantModifierFactoryRegistry {

	private static final Map<String, AgriPlantModifierFactory> FACTORIES = new HashMap<>();

	public static boolean register(String id, AgriPlantModifierFactory modifier) {
		if (FACTORIES.containsKey(id)) {
			return false;
		}
		FACTORIES.put(id, modifier);
		return true;
	}

	public static Optional<AgriPlantModifierFactory> get(String id) {
		return Optional.ofNullable(FACTORIES.get(id));
	}
	public static Optional<AgriPlantModifierFactory> get(AgriPlantModifierInfo modifier) {
		return Optional.ofNullable(FACTORIES.get(modifier.id()));
	}

	public static Optional<IAgriPlantModifier> construct(AgriPlantModifierInfo info) {
		return get(info).flatMap(factory -> factory.construct(info));
	}
}

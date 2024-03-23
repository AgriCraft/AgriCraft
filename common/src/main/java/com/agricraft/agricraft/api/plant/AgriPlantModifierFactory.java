package com.agricraft.agricraft.api.plant;

import java.util.Optional;

public interface AgriPlantModifierFactory {
	Optional<IAgriPlantModifier> construct(AgriPlantModifierInfo info);
}

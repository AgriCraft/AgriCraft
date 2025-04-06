package com.agricraft.agricraft.common.registry;

import com.agricraft.agricraft.api.genetic.GeneSpecies;
import com.agricraft.agricraft.api.genetic.GeneStat;
import com.agricraft.agricraft.api.registries.AgriCraftGenes;
import com.agricraft.agricraft.api.registries.AgriCraftStats;
import org.jetbrains.annotations.ApiStatus;

import static com.agricraft.agricraft.common.registry.AgriRegistries.GENES;

public interface ModGenes {

	@ApiStatus.Internal
	static void register() {
		AgriCraftGenes.SPECIES = GENES.register("species", GeneSpecies::new);
		AgriCraftGenes.GAIN = GENES.register("gain", () -> new GeneStat(AgriCraftStats.GAIN));
		AgriCraftGenes.GROWTH = GENES.register("growth", () -> new GeneStat(AgriCraftStats.GROWTH));
		AgriCraftGenes.STRENGTH = GENES.register("strength", () -> new GeneStat(AgriCraftStats.STRENGTH));
		AgriCraftGenes.RESISTANCE = GENES.register("resistance", () -> new GeneStat(AgriCraftStats.RESISTANCE));
		AgriCraftGenes.FERTILITY = GENES.register("fertility", () -> new GeneStat(AgriCraftStats.FERTILITY));
		AgriCraftGenes.MUTATIVITY = GENES.register("mutativity", () -> new GeneStat(AgriCraftStats.MUTATIVITY));
	}

}

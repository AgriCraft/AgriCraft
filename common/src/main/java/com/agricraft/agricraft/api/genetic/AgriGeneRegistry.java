package com.agricraft.agricraft.api.genetic;

import com.agricraft.agricraft.api.AgriRegistry;
import com.agricraft.agricraft.api.stat.AgriStat;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;

import java.util.Optional;

public class AgriGeneRegistry extends AgriRegistry<AgriGene<?>> {

	private static AgriGeneRegistry INSTANCE;

	public static AgriGeneRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AgriGeneRegistry();
		}
		return INSTANCE;
	}

	private final GeneSpecies geneSpecies;

	private AgriGeneRegistry() {
		super();
		this.geneSpecies = new GeneSpecies();
		this.add(new GeneStat(AgriStatRegistry.getInstance().growthStat()));
		this.add(new GeneStat(AgriStatRegistry.getInstance().gainStat()));
		this.add(new GeneStat(AgriStatRegistry.getInstance().strengthStat()));
		this.add(new GeneStat(AgriStatRegistry.getInstance().fertilityStat()));
		this.add(new GeneStat(AgriStatRegistry.getInstance().resistanceStat()));
		this.add(new GeneStat(AgriStatRegistry.getInstance().mutativityStat()));
		this.add(geneSpecies);
	}

	public GeneSpecies getGeneSpecies() {
		return geneSpecies;
	}

	@SuppressWarnings("unchecked")
	public Optional<AgriGene<Integer>> getGeneStat(AgriStat stat) {
		return this.get(stat.getId()).map(gene -> ((AgriGene<Integer>) gene));
	}

}

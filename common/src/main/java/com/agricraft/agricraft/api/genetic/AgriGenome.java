package com.agricraft.agricraft.api.genetic;

import com.agricraft.agricraft.api.stat.AgriStat;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgriGenome {

	private final AgriGenePair<String> species;
	private final Map<String, AgriGenePair<Integer>> stats;

	public AgriGenome(AgriGenePair<String> species, List<AgriGenePair<Integer>> stats) {
		this.species = species;
		this.stats = new HashMap<>();
		for (AgriGenePair<Integer> gene : stats) {
			this.stats.put(gene.getGene().getId(), gene);
		}
	}

	public AgriGenome(AgriPlant plant) {
		GeneSpecies geneSpecies = AgriGeneRegistry.getInstance().getGeneSpecies();
		this.species = new AgriGenePair<>(geneSpecies, geneSpecies.defaultAllele(plant));
		this.stats = new HashMap<>();
		for (AgriStat stat : AgriStatRegistry.getInstance()) {
			AgriGeneRegistry.getInstance().getGeneStat(stat).ifPresent(gene -> this.stats.put(gene.getId(), new AgriGenePair<>(gene, gene.defaultAllele(plant))));
		}
	}

	public AgriGenePair<String> getSpeciesGene() {
		return this.species;
	}

	public AgriGenePair<Integer> getStatGene(AgriStat stat) {
		return this.stats.get(stat.getId());
	}

	public Collection<AgriGenePair<Integer>> getStatGenes() {
		return this.stats.values();
	}

	public void writeToNBT(CompoundTag tag) {
		CompoundTag genes = new CompoundTag();
		species.getGene().writeToNBT(genes, species.getDominant(), species.getRecessive());
		for (AgriGenePair<Integer> statPair : stats.values()) {
			statPair.getGene().writeToNBT(genes, statPair.getDominant(), statPair.getRecessive());
		}
		tag.put("genes", genes);
	}

	public static AgriGenome fromNBT(CompoundTag tag) {
		if (tag == null || !tag.contains("genes")) {
			return null;
		}
		CompoundTag genes = tag.getCompound("genes");
		AgriGenePair<String> species = AgriGeneRegistry.getInstance().getGeneSpecies().readFromNBT(genes);
		List<AgriGenePair<Integer>> stats = new ArrayList<>();
		for (AgriStat stat : AgriStatRegistry.getInstance()) {
			AgriGeneRegistry.getInstance().getGeneStat(stat).ifPresent(gene -> stats.add(gene.readFromNBT(genes)));
		}
		return new AgriGenome(species, stats);
	}

}

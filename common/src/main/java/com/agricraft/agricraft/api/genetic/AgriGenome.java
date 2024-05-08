package com.agricraft.agricraft.api.genetic;

import com.agricraft.agricraft.api.stat.AgriStat;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.agricraft.agricraft.api.plant.AgriPlant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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

	public static void removeFromNBT(CompoundTag tag) {
		tag.remove("genes");
	}

	public AgriGenePair<String> getSpeciesGene() {
		return this.species;
	}

	public AgriGenePair<Integer> getStatGene(AgriStat stat) {
		return this.stats.get(stat.getId());
	}

	public int getGain() {
		return this.getStatGene(AgriStatRegistry.getInstance().gainStat()).getTrait();
	}

	public int getGrowth() {
		return this.getStatGene(AgriStatRegistry.getInstance().growthStat()).getTrait();
	}

	public int getStrength() {
		return this.getStatGene(AgriStatRegistry.getInstance().strengthStat()).getTrait();
	}

	public int getFertility() {
		return this.getStatGene(AgriStatRegistry.getInstance().fertilityStat()).getTrait();
	}

	public int getResistance() {
		return this.getStatGene(AgriStatRegistry.getInstance().resistanceStat()).getTrait();
	}

	public int getMutativity() {
		return this.getStatGene(AgriStatRegistry.getInstance().mutativityStat()).getTrait();
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

	@Override
	public String toString() {
		CompoundTag compoundTag = new CompoundTag();
		this.writeToNBT(compoundTag);
		return compoundTag.toString();
	}

	public void appendHoverText(List<Component> tooltipComponents, TooltipFlag isAdvanced) {
		if (isAdvanced.isAdvanced()) {
			this.getSpeciesGene().getGene().addTooltip(tooltipComponents, this.getSpeciesGene().getTrait());
		}
		this.getStatGenes().stream()
				.sorted(Comparator.comparing(pair -> pair.getGene().getId()))
				.forEach(pair -> pair.getGene().addTooltip(tooltipComponents, pair.getTrait()));
	}
}

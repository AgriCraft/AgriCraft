package com.agricraft.agricraft.common.genetic;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.config.AgriCraftConfig;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.genetic.AgriCrossBreedEngine;
import com.agricraft.agricraft.api.genetic.AgriGene;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.genetic.Chromosome;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.registries.AgriCraftStats;
import net.minecraft.util.RandomSource;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * AgriCraft's cross breeding logic is executed by this class.
 *
 * It's active / default implementations can be obtained from the AgriCrossBreedEngine instance
 * Overriding implementations can be activated with the AgriCrossBreedEngine instance as well
 */
public class AgriCrossBreedEngineImpl implements AgriCrossBreedEngine {

	private final ParentSelector selector;
	private final CloneLogic cloner;
	private final CombineLogic combiner;

	public AgriCrossBreedEngineImpl() {
		this.selector = this::selectAndSortCandidates;
		this.cloner = (crop, parent, random) -> new AgriGenome(parent.chromosomes().stream().map(chromosome -> cloneChromosome(crop, chromosome, parent, random)).collect(Collectors.toList()));
		this.combiner = (crop, parent1, parent2, random) -> new AgriGenome(AgriApi.get().getGeneRegistry().stream().map(gene -> this.mutateChromosome(crop, gene, parent1, parent2, random)).collect(Collectors.toList()));
	}

	@Override
	public ParentSelector getSelector() {
		return this.selector;
	}

	@Override
	public CloneLogic getCloner() {
		return this.cloner;
	}

	@Override
	public CombineLogic getCombiner() {
		return this.combiner;
	}

	/**
	 * Handles a growth tick resulting in cross breeding, is only fired for cross crops.
	 * Any results from the success or failure of cross breeding, or clone, such as setting of the plant and genome must be fired from within this method as well.
	 * <p>
	 * The default {@link AgriCrossBreedEngineImpl} allows its behaviour to be overridden:
	 * <ul>
	 *  <li>The selector represented by {@link ParentSelector} filters out valid crops which can contribute to cross breeding / cloning, and rolls for their probability to contribute
	 *  <li>The clone logic represented by {@link CloneLogic} handles the cloning of a genome (offspring from one single parent)
	 *  <li>The combine logic represented by {@link CombineLogic} handles the combining of two parent genomes (offspring from two different parents)
	 * </ul>
	 *
	 * @param crop       the crop for which the cross breeding tick has been fired
	 * @param neighbours A stream of the crop's neighbouring crops (this includes all crops, regardless if these contain plants, weeds, are fertile, or mature)
	 * @param random     pseudo-random generator to take decisions
	 * @return true if the cross breeding / spread succeeded, false if it failed
	 */
	@Override
	public boolean handleCrossBreedTick(AgriCrop crop, Stream<AgriCrop> neighbours, RandomSource random) {
		// select candidate parents from the neighbours
		List<AgriCrop> candidates = this.getSelector().selectAndOrder(neighbours, random);
		// No candidates: do nothing
		if (candidates.isEmpty()) {
			return false;
		}
		// Only one candidate: clone
		if (candidates.size() == 1) {
			return this.doClone(crop, candidates.get(0), random);
		}
		// More than one candidate passed, pick the two parents with the highest fertility stat:
		return this.doCombine(crop, candidates.get(0), candidates.get(1), random);
	}

	protected List<AgriCrop> selectAndSortCandidates(Stream<AgriCrop> neighbours, RandomSource random) {
		return neighbours
				// Plant only
				.filter(AgriCrop::hasPlant)
				// Mature crops only
				.filter(AgriCrop::canBeHarvested)
				// Fertile crops only
				.filter(crop -> (!AgriCraftConfig.ONLY_FERTILE_CROPS_SPREAD.get() || crop.isFertile()))
				// Sort based on fertility stat
				.sorted(Comparator.comparingInt(this::sorter))
				// Roll for fertility stat
				.filter(neighbour -> this.rollFertility(neighbour, random))
				// Collect successful passes
				.collect(Collectors.toList());
	}

	protected boolean doClone(AgriCrop target, AgriCrop parent, RandomSource random) {
		AgriPlant plant = parent.getPlant();
		// Try spawning a clone if cloning is allowed
		if (plant.allowsCloning(parent.getGrowthStage())) {
			// roll for spread chance
			if (random.nextDouble() < parent.getPlant().getSpreadChance(parent.getGrowthStage())) {
				AgriGenome clone = this.getCloner().clone(target, parent.getGenome(), random);
				target.plantGenome(clone);
				target.getPlant().onSpawned(target);
				return true;
			}
		}
		// spreading failed
		return false;
	}

	protected boolean doCombine(AgriCrop target, AgriCrop a, AgriCrop b, RandomSource random) {
		// Determine the child's genome
		AgriGenome genome = this.getCombiner().combine(target, a.getGenome(), b.getGenome(), random);
		// Spawn the child
		target.plantGenome(genome);
		target.getPlant().onSpawned(target);
		return true;
	}

	protected int sorter(AgriCrop crop) {
		return AgriCraftStats.FERTILITY.get().getMax() - crop.getGenome().getFertility().trait();
	}

	protected boolean rollFertility(AgriCrop crop, RandomSource random) {
		return random.nextInt(AgriCraftStats.FERTILITY.get().getMax()) < crop.getGenome().getFertility().trait();
	}

	protected <T> Chromosome<T> mutateChromosome(AgriCrop crop, AgriGene<T> gene, AgriGenome parent1, AgriGenome parent2, RandomSource rand) {
		return gene.mutator().pickOrMutate(
				crop,
				gene,
				this.pickRandomAllele(parent1.getChromosome(gene), rand),
				this.pickRandomAllele(parent2.getChromosome(gene), rand),
				parent1, parent2,
				rand
		);
	}

	protected <T> Chromosome<T> cloneChromosome(AgriCrop crop, Chromosome<T> chromosome, AgriGenome parent, RandomSource rand) {
		if (AgriCraftConfig.CLONE_MUTATIONS.get()) {
			return chromosome.gene().mutator().pickOrMutate(
					crop,
					chromosome.gene(),
					chromosome.dominant(),
					chromosome.recessive(),
					parent, parent,
					rand
			);
		} else {
			return chromosome.copy();
		}
	}

	protected <T> T pickRandomAllele(Chromosome<T> pair, RandomSource random) {
		return random.nextBoolean() ? pair.dominant() : pair.recessive();
	}

}

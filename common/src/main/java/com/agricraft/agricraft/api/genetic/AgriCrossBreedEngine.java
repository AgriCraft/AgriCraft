package com.agricraft.agricraft.api.genetic;

import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.config.CoreConfig;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.stat.AgriStat;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
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
public class AgriCrossBreedEngine {

	private ParentSelector selector;
	private CloneLogic cloner;
	private CombineLogic combiner;

	public AgriCrossBreedEngine() {
		this.selector = this::selectAndSortCandidates;
//		this.cloner = (crop, parent, random) -> new AgriGenome(AgriGeneRegistry.getInstance().stream().map(gene -> this.cloneGene(crop, gene, parent, random)).collect(Collectors.toList()));
		this.cloner = (crop, parent, random) -> new AgriGenome(this.cloneGene(crop, parent.getSpeciesGene(), parent, random),
				parent.getStatGenes().stream().map(genePair -> this.cloneGene(crop, genePair, parent, random)).collect(Collectors.toList()));
//		this.combiner = (crop, parent1, parent2, random) -> new AgriGenome(AgriGeneRegistry.getInstance().stream().map(gene -> this.mutateGene(crop, gene, parent1, parent2, random)).collect(Collectors.toList()));
		this.combiner = (crop, parent1, parent2, random) -> new AgriGenome(this.mutateGene(crop, parent1.getSpeciesGene(), parent2.getSpeciesGene(), parent1, parent2, random),
				AgriStatRegistry.getInstance().stream().map(stat -> this.mutateGene(crop, parent1.getStatGene(stat), parent2.getStatGene(stat), parent1, parent2, random)).collect(Collectors.toList()));
	}

	protected ParentSelector getSelector() {
		return this.selector;
	}

	protected CloneLogic getCloner() {
		return this.cloner;
	}

	protected CombineLogic getCombiner() {
		return this.combiner;
	}

	/**
	 * Handles a growth tick resulting in cross breeding, is only fired for cross crops.
	 * Any results from the success or failure of cross breeding, or clone, such as setting of the plant and genome must be fired from within this method as well.
	 * <p>
	 * The default {@link AgriCrossBreedEngine} allows its behaviour to be overridden:
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
				.filter(crop -> (!CoreConfig.onlyFertileCropsSpread || crop.isFertile()))
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
		return true;
	}

	protected int sorter(AgriCrop crop) {
		AgriStat fertility = AgriStatRegistry.getInstance().fertilityStat();
		return fertility.getMax() - crop.getGenome().getStatGene(AgriStatRegistry.getInstance().fertilityStat()).getTrait();
	}

	protected boolean rollFertility(AgriCrop crop, RandomSource random) {
		AgriStat fertility = AgriStatRegistry.getInstance().fertilityStat();
		return random.nextInt(fertility.getMax()) < crop.getGenome().getStatGene(AgriStatRegistry.getInstance().fertilityStat()).getTrait();
	}

	protected <T> AgriGenePair<T> mutateGene(AgriCrop crop, AgriGenePair<T> genePair1, AgriGenePair<T> genePair2, AgriGenome parent1, AgriGenome parent2, RandomSource rand) {
		// we're using genePair1 gene, but it doesn't matter as genePair1 and genePair2 should have the same gene
		return genePair1.getGene().mutator().pickOrMutate(
				crop,
				genePair1.getGene(),
				this.pickRandomAllele(genePair1, rand),
				this.pickRandomAllele(genePair2, rand),
				parent1, parent2,
				rand
		);
	}

	protected <T> AgriGenePair<T> cloneGene(AgriCrop crop, AgriGenePair<T> genePair, AgriGenome parent, RandomSource rand) {
		if (CoreConfig.cloneMutations) {
			return genePair.getGene().mutator().pickOrMutate(
					crop,
					genePair.getGene(),
					genePair.getDominant(),
					genePair.getRecessive(),
					parent, parent,
					rand
			);
		} else {
			return genePair.copy();
		}
	}

	protected <T> AgriAllele<T> pickRandomAllele(AgriGenePair<T> pair, RandomSource random) {
		return random.nextBoolean() ? pair.getDominant() : pair.getRecessive();
	}

	/**
	 * Sets the selection logic to be used by the cross breeding engine
	 * <p>
	 * It is not obligatory to use this logic, in case you do not want your selection logic to be replaced.
	 * The default AgriCraft cross breeding engine does allow replacing of its selection logic
	 * <p>
	 * This method should always be safe to call
	 *
	 * @param selector the new selection logic
	 */
	void setSelectionLogic(ParentSelector selector) {
		this.selector = selector;
	}

	/**
	 * Sets the cloning logic to be used by the cross breeding engine
	 * <p>
	 * It is not obligatory to use this logic, in case you do not want your cloning logic to be replaced.
	 * The default AgriCraft cross breeding engine does allow replacing of its cloning logic
	 * <p>
	 * This method should always be safe to call
	 *
	 * @param cloneLogic the new cloning logic
	 */
	void setCloneLogic(CloneLogic cloneLogic) {
		this.cloner = cloneLogic;
	}

	/**
	 * Sets the combining logic to be used by the cross breeding engine
	 * <p>
	 * It is not obligatory to use this logic, in case you do not want your combining logic to be replaced.
	 * The default AgriCraft cross breeding engine does allow replacing of its combining logic
	 * <p>
	 * This method should always be safe to call
	 *
	 * @param combineLogic the new combining logic
	 */
	void setCombineLogic(CombineLogic combineLogic) {
		this.combiner = combineLogic;
	}

	/**
	 * Functional interface for selection logic
	 */
	@FunctionalInterface
	public interface ParentSelector {

		/**
		 * Selects potential parent candidates from all neighbouring crops.
		 * <p>
		 * In case an empty list is returned, nothing will happen.
		 * In case a list with 1 crop is returned, a cloning event will be attempted
		 * In case a list with 2 or more crops is returned, a combining event will be attempted with the first two items in the list
		 *
		 * @param candidates all candidates
		 * @param random     pseudo-random generator for decision making
		 * @return ordered list of remaining candidates
		 */
		List<AgriCrop> selectAndOrder(Stream<AgriCrop> candidates, RandomSource random);

	}

	/**
	 * Functional interface for cloning logic
	 */
	@FunctionalInterface
	public interface CloneLogic {

		/**
		 * Clones the parent genome to a new genome.
		 * <p>
		 * Note that in the default implementation this will be an exact copy.
		 * This, however, need not forcibly be the case, one could implement cloning logic which deteriorates the genes for instance
		 *
		 * @param crop   the crop onto which the plant is cloned
		 * @param parent the parent genome
		 * @param random pseudo-random generator for decision making
		 * @return the child genome
		 */
		AgriGenome clone(AgriCrop crop, AgriGenome parent, RandomSource random);

	}

	/**
	 * Functional interface for combining logic
	 */
	@FunctionalInterface
	public interface CombineLogic {

		/**
		 * Combines the genomes of two parents into a child genome
		 *
		 * @param crop    the crop onto which the plant is crossbred
		 * @param parent1 the genome of the first parent
		 * @param parent2 the genome of the second parent
		 * @param random  pseudo-random generator for decision making
		 * @return the child genome
		 */
		AgriGenome combine(AgriCrop crop, AgriGenome parent1, AgriGenome parent2, RandomSource random);

	}

}

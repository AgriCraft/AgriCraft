package com.infinityraider.agricraft.impl.v1.genetics;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.genetics.*;
import com.infinityraider.agricraft.api.v1.genetics.IAgriCrossBreedEngine;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.impl.v1.stats.AgriStatRegistry;
import net.minecraft.util.Tuple;

import java.util.Optional;
import java.util.Random;

/**
 * This class decides whether a plant is spreading or mutating and also calculates the new stats
 * (growth, gain, strength, fertility, resistance, etc.) of the new plant based on the 4 neighbours.
 */
public final class AgriMutationHandler implements IAgriMutationHandler {
    private static final AgriMutationHandler INSTANCE = new AgriMutationHandler();

    public static AgriMutationHandler getInstance() {
        return INSTANCE;
    }

    // Encapsulates the logic
    private final IAgriCrossBreedEngine defaultEngine;

    // Mutates individual genes
    private final IMutator<IAgriPlant> defaultPlantMutator;
    private final IMutator<Integer> defaultStatMutator;

    private IAgriCrossBreedEngine engine;
    private IMutator<IAgriPlant> plantMutator;
    private IMutator<Integer> statMutator;

    private AgriMutationHandler() {
        this.defaultEngine = new AgriCrossBreedEngine();
        this.defaultPlantMutator = new AgriPlantMutator();
        this.defaultStatMutator = new AgriStatMutator();
        this.setActiveCrossBreedEngine(this.getDefaultCrossBreedEngine())
                .setActivePlantMutator(this.getDefaultPlantMutator())
                .setActiveStatMutator(this.getDefaultStatMutator());
    }

    @Override
    public IAgriMutationHandler setActiveCrossBreedEngine(IAgriCrossBreedEngine engine) {
        this.engine = engine;
        return this;
    }

    @Override
    public IAgriCrossBreedEngine getActiveCrossBreedEngine() {
        return this.engine;
    }

    @Override
    public IAgriCrossBreedEngine getDefaultCrossBreedEngine() {
        return this.defaultEngine;
    }

    @Override
    public IAgriMutationHandler setActivePlantMutator(IMutator<IAgriPlant> mutator) {
        this.plantMutator = mutator;
        return this;
    }

    @Override
    public IMutator<IAgriPlant> getActivePlantMutator() {
        return this.plantMutator;
    }

    @Override
    public IMutator<IAgriPlant> getDefaultPlantMutator() {
        return this.defaultPlantMutator;
    }

    @Override
    public IAgriMutationHandler setActiveStatMutator(IMutator<Integer> mutator) {
        this.statMutator = mutator;
        return this;
    }

    @Override
    public IMutator<Integer> getActiveStatMutator() {
        return this.statMutator;
    }

    @Override
    public IMutator<Integer> getDefaultStatMutator() {
        return this.defaultStatMutator;
    }

    // Default AgriCraft plant mutation logic
    public static class AgriPlantMutator implements IMutator<IAgriPlant> {
        public AgriPlantMutator() {}

        @Override
        public IAgriGenePair<IAgriPlant> pickOrMutate(IAgriCrop crop, IAgriGene<IAgriPlant> gene, IAllele<IAgriPlant> first, IAllele<IAgriPlant> second,
                                                      Tuple<IAgriGenome, IAgriGenome> parents, Random random) {

            // Search for matching mutations
            // get trigger results
            // filter out any forbidden results
            // order them randomly
            // fetch the first
            return AgriMutationRegistry.getInstance().getMutationsFromParents(first.trait(), second.trait())
                    // get trigger results
                    .map(m -> new Tuple<>(m, this.evaluateTriggers(crop, m)))
                    // filter out any forbidden results
                    .filter(m -> m.getB() != IAgriMutation.ConditionResult.FORBID)
                    // Find the first result, sorted by trigger result, but shuffled by mutation
                    .min((a, b) -> this.sortAndShuffle(a, b, random))
                    // map it to its child, or to nothing based on the mutation success rate
                    .flatMap(m -> this.evaluate(m, random))
                    // map the result to a new gene pair with either of its parents as second gene
                    .map(plant -> gene.generateGenePair(gene.getAllele(plant), random.nextBoolean() ? first : second))
                    // if no mutation was found or if the mutation was unsuccessful, return a gene pair of the parents
                    .orElse(gene.generateGenePair(first, second));
        }

        protected IAgriMutation.ConditionResult evaluateTriggers(IAgriCrop crop, IAgriMutation mutation) {
            return mutation.getConditions().stream()
                    .map(trigger -> trigger.getResult(crop, mutation))
                    .sorted()
                    .findFirst()
                    .orElse(IAgriMutation.ConditionResult.PASS);
        }

        protected int sortAndShuffle(Tuple<IAgriMutation, IAgriMutation.ConditionResult> a, Tuple<IAgriMutation, IAgriMutation.ConditionResult> b, Random random) {
            if(a.getB() == b.getB()) {
                // in case the trigger result is identical, shuffle randomly
                return a.getA() == b.getA() ? 0 : random.nextBoolean() ? -1 : 1;
            }
            // otherwise trigger result defines priority
            return a.getB().compareTo(b.getB());
        }

        protected Optional<IAgriPlant> evaluate(Tuple<IAgriMutation, IAgriMutation.ConditionResult> mutation, Random random) {
            // If the mutation is forced, no need to evaluate a probability
            if(mutation.getB() == IAgriMutation.ConditionResult.FORCE) {
                return Optional.of(mutation.getA().getChild());
            }
            // Evaluate mutation probability
            if(mutation.getA().getChance() > random.nextDouble()) {
                return Optional.of(mutation.getA().getChild());
            }
            // mutation failed
            return Optional.empty();
        }
    }

    // Default AgriCraft stat mutation logic
    public static class AgriStatMutator implements IMutator<Integer> {
        public AgriStatMutator() {}

        @Override
        public IAgriGenePair<Integer> pickOrMutate(IAgriCrop crop, IAgriGene<Integer> gene, IAllele<Integer> first, IAllele<Integer> second,
                                                   Tuple<IAgriGenome, IAgriGenome> parents, Random random) {
            // return new gene pair with or without mutations, based on mutativity stat
            IAgriStat mutativity = AgriStatRegistry.getInstance().mutativityStat();
            return gene.generateGenePair(
                    this.rollAndExecuteMutation(gene, first, mutativity, parents.getA().getStats().getMutativity(), random),
                    this.rollAndExecuteMutation(gene, second, mutativity, parents.getB().getStats().getMutativity(), random)
            );
        }

        protected IAllele<Integer> rollAndExecuteMutation(IAgriGene<Integer> gene, IAllele<Integer> allel, IAgriStat mutativity, int statValue, Random random) {
            // Mutativity stat of 1 results in 25/50/25 probability of positive/no/negative mutation
            // Mutativity stat of 10 results in 100/0/0 probability of positive/no/negative mutation
            int max = mutativity.getMax();
            if(random.nextInt(max) < statValue) {
                int delta = random.nextInt(max) < (max + statValue)/2 ? 1 : -1;
                return gene.getAllele(allel.trait() + delta);
            } else {
                return allel;
            }
        }
    }
}

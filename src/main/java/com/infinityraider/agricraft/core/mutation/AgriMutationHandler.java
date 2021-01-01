package com.infinityraider.agricraft.core.mutation;

import com.infinityraider.agricraft.api.v1.genetics.*;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutationEngine;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.core.stats.AgriStatRegistry;
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

    private final IAgriMutationEngine defaultEngine;
    private final IMutator<IAgriPlant> defaultPlantMutator;
    private final IMutator<Byte> defaultStatMutator;

    private IAgriMutationEngine engine;
    private IMutator<IAgriPlant> plantMutator;
    private IMutator<Byte> statMutator;

    private AgriMutationHandler() {
        this.defaultEngine = new AgriMutationEngine();
        this.defaultPlantMutator = new AgriPlantMutator();
        this.defaultStatMutator = new AgriStatMutator();
        this.setActiveMutationEngine(this.getDefaultMutationEngine())
                .setActivePlantMutator(this.getDefaultPlantMutator())
                .setActiveStatMutator(this.getDefaultStatMutator());
    }

    public IAgriMutationEngine getEngine() {
        return this.engine;
    }

    public void setEngine(IAgriMutationEngine engine) {
        this.engine = engine;
    }

    @Override
    public IAgriMutationHandler setActiveMutationEngine(IAgriMutationEngine engine) {
        this.engine = engine;
        return this;
    }

    @Override
    public IAgriMutationEngine getActiveMutationEngine() {
        return this.engine;
    }

    @Override
    public IAgriMutationEngine getDefaultMutationEngine() {
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
    public IAgriMutationHandler setActiveStatMutator(IMutator<Byte> mutator) {
        this.statMutator = mutator;
        return this;
    }

    @Override
    public IMutator<Byte> getActiveStatMutator() {
        return this.statMutator;
    }

    @Override
    public IMutator<Byte> getDefaultStatMutator() {
        return this.defaultStatMutator;
    }

    public static class AgriPlantMutator implements IMutator<IAgriPlant> {
        public AgriPlantMutator() {}

        @Override
        public IAgriGenePair<IAgriPlant> pickOrMutate(IAgriGene<IAgriPlant> gene, IAllel<IAgriPlant> first, IAllel<IAgriPlant> second,
                                                      Tuple<IAgriGenome, IAgriGenome> parents, Random random) {

            return AgriMutationRegistry.getInstance().stream()
                    // scan the mutation registry
                    .filter(mut -> mut.hasParent(first.trait()) && mut.hasParent(second.trait()))
                    // find a matching mutation
                    .findAny()
                    // map it to its child, or to nothing based on the mutation success rate
                    .flatMap(mutation -> Optional.ofNullable(mutation.getChance() > random.nextDouble() ? mutation.getChild() : null))
                    // map the result to a new gene pair with either of its parents as second gene
                    .map(plant -> gene.generateGenePair(gene.getAllel(plant), random.nextBoolean() ? first : second))
                    // if no mutation was found or if the mutation was unsuccessful, return a gene pair of the parents
                    .orElse(gene.generateGenePair(first, second));
        }
    }

    public static class AgriStatMutator implements IMutator<Byte> {

        public AgriStatMutator() {}

        @Override
        public IAgriGenePair<Byte> pickOrMutate(IAgriGene<Byte> gene, IAllel<Byte> first, IAllel<Byte> second,
                                                Tuple<IAgriGenome, IAgriGenome> parents, Random random) {

            // intermediate variables for mutating
            IAllel<Byte> a = first;
            IAllel<Byte> b = second;

            // 50/50 chance to do a mutation (TODO: add a mutativity stat to influence these odds)
            if(random.nextBoolean()) {
                // TODO: base this on mutativity stat
                int delta = random.nextBoolean() ? 1 : -1;
                // randomly pick one of the alleles to mutate
                if(random.nextBoolean()) {
                    a = gene.getAllel((byte) Math.min(AgriStatRegistry.MAX, Math.max(AgriStatRegistry.MIN, (a.trait() + delta))));
                } else {
                    b = gene.getAllel((byte) Math.min(AgriStatRegistry.MAX, Math.max(AgriStatRegistry.MIN, (b.trait() + delta))));
                }
            }

            // return new gene pair
            return gene.generateGenePair(a, b);
        }
    }
}

package com.infinityraider.agricraft.core.genetics;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.genetics.*;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationEngine;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.core.stats.AgriStatRegistry;
import net.minecraft.util.Tuple;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AgriMutationEngine implements IAgriMutationEngine {
    public AgriMutationEngine() {}

    @Override
    public Optional<IAgriPlant> handleMutationTick(IAgriCrop crop, Stream<IAgriCrop> neighbours, Random random) {
        // select candidate parents from the neighbours
        List<IAgriCrop> candidates = neighbours
                // Valid crops only
                .filter(IAgriCrop::isValid)
                // Mature crops only
                .filter(IAgriCrop::isMature)
                // Fertile crops only
                .filter(IAgriCrop::isFertile)
                // Sort based on fertility stat
                .sorted(Comparator.comparingInt(this::sorter))
                // Roll for fertility stat
                .filter(neighbour -> this.rollFertility(neighbour, random))
                // Collect successful passes
                .collect(Collectors.toList());
        // No candidates: do nothing
        if(candidates.size() <= 0) {
            return Optional.empty();
        }
        // Only one candidate: clone
        if(candidates.size() == 1) {
            IAgriCrop parent = candidates.get(0);
            return parent.getPlant().flatMap(plant -> {
                // Spawn a clone if cloning is allowed
                if (plant.allowsCloning(parent.getGrowthStage())) {
                    return Optional.of(this.spawnChild(crop, plant, parent.getGenome().clone()));
                } else {
                    return Optional.empty();
                }
            });
        }
        // More than one candidate passed, pick the two parents with the highest fertility stat:
        IAgriCrop a = candidates.get(0);
        IAgriCrop b = candidates.get(1);
        Tuple<IAgriGenome, IAgriGenome> parents = new Tuple<>(a.getGenome(), b.getGenome());
        // Determine the child's genome
        IAgriGenome genome = AgriApi.getAgriGenomeBuilder().populate(gene -> this.mutateGene(gene, parents, random)).build();
        // Fetch the child's species from the genome
        IAgriPlant plant = genome.getGenePair(AgriGeneRegistry.getInstance().gene_species).getTrait();
        // Spawn the child
        return Optional.of(this.spawnChild(crop, plant, genome));
    }

    protected int sorter(IAgriCrop crop) {
        return AgriStatRegistry.MAX - crop.getStats().getValue(AgriStatRegistry.getInstance().fertilityStat());
    }

    protected boolean rollFertility(IAgriCrop crop, Random random) {
        int fertility = crop.getStats().getValue(AgriStatRegistry.getInstance().fertilityStat());
        return random.nextInt(AgriStatRegistry.MAX) < fertility;
    }

    protected IAgriPlant spawnChild(IAgriCrop target, IAgriPlant plant, IAgriGenome genome) {
        target.setCrossCrop(false);
        target.setPlant(plant);
        target.setGenome(genome);
        return plant;
    }

    protected <T> IAgriGenePair<T> mutateGene(IAgriGene<T> gene, Tuple<IAgriGenome, IAgriGenome> parents, Random rand) {
        return gene.mutator().pickOrMutate(
                gene,
                this.pickRandomAllel(parents.getA().getGenePair(gene), rand),
                this.pickRandomAllel(parents.getB().getGenePair(gene), rand),
                parents, rand);
    }

    protected  <T> IAllel<T> pickRandomAllel(IAgriGenePair<T> pair, Random random) {
        return random.nextBoolean() ? pair.getDominant() : pair.getRecessive();
    }
}

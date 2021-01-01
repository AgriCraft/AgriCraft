package com.infinityraider.agricraft.core.mutation;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.genetics.*;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutationEngine;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.core.genetics.AgriGeneRegistry;
import net.minecraft.util.Tuple;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class AgriMutationEngine implements IAgriMutationEngine {
    public AgriMutationEngine() {}

    @Override
    public void handleMutationTick(IAgriCrop crop, List<IAgriCrop> neighbours, Random random) {
        // prune neighbours for mature and fertile only
        List<IAgriCrop> candidates = neighbours.stream()
                .filter(IAgriCrop::isValid)
                .filter(IAgriCrop::isMature)
                .filter(IAgriCrop::isFertile)
                .collect(Collectors.toList());
        // No candidates: do nothing
        if(candidates.size() <= 0) {
            return;
        }
        // Only one candidate: clone
        if(neighbours.size() == 1) {
            IAgriCrop parent = neighbours.get(0);
            parent.getPlant().ifPresent(plant -> {
                // Spawn a clone if cloning is allowed
                if(plant.allowsCloning(parent.getGrowthStage())) {
                    this.spawnChild(crop, plant, parent.getGenome().clone());
                }
            });
            return;
        }
        // More than one candidate, pick two random parents:
        IAgriCrop a = candidates.remove(random.nextInt(candidates.size()) - 1);
        IAgriCrop b = candidates.remove(random.nextInt(candidates.size()) - 1);
        Tuple<IAgriGenome, IAgriGenome> parents = new Tuple<>(a.getGenome(), b.getGenome());
        // Determine the child's genome
        IAgriGenome genome = AgriApi.getAgriGenomeBuilder().populate(gene -> this.mutateGene(gene, parents, random)).build();
        // Fetch the child's species from the genome
        IAgriPlant plant = genome.getGenePair(AgriGeneRegistry.getInstance().gene_species).getTrait();
        // Spawn the child
        this.spawnChild(crop, plant, genome);
    }

    protected void spawnChild(IAgriCrop target, IAgriPlant plant, IAgriGenome genome) {
        target.setCrossCrop(false);
        target.setPlant(plant);
        target.setGenome(genome);
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

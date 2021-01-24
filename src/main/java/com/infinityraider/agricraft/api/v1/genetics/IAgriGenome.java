package com.infinityraider.agricraft.api.v1.genetics;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatProvider;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

/**
 * The genome of an AgriCraft plant / seed / clipping
 *
 * The implementation of this follows Mendelian genetics and is not designed to be overridden
 *
 * The behaviour of the implementation of the genome, and its propagation through generations occurs according to the following rules:
 *  - All genes in a genome consist of pairs of alleles which give rise to its apparent trait
 *  - One of the alleles in a gene pair is dominant, while the other is recessive
 *  - By default, the apparent trait is decided by the dominant allel, but this could differ based on the gene)
 *  - Each of the parents gives, for each of its genes, one of its alleles to a child, these alleles, one for each parent,
 *    form a new gene pair in the child's genome
 *  - When a new gene pair is formed, it is possible for one of the alleles to mutate,
 *    resulting in properties which differ slightly from its parents
 */
public interface IAgriGenome extends IAgriStatProvider {
    /**
     * @return the plant species for this genome
     */
    default IAgriPlant getPlant() {
        return this.getGenePair(AgriApi.getGeneRegistry().getPlantGene()).getTrait();
    }

    /**
     * Fetches the gene pair for a given gene id (as registered in the IAgriGeneRegistry)
     * @param geneId the id of the gene
     * @param <T> the type of the gene
     * @return an optional holding the gene pair, or empty in case no such gene is registered
     */
    @SuppressWarnings("unchecked")
    default <T> Optional<IAgriGenePair<T>> getGenePair(String geneId) {
        return AgriApi.getGeneRegistry().get(geneId).map(gene -> (IAgriGenePair<T>) this.getGenePair(gene));
    }

    /**
     * Fetches the gene pair for a gene
     * @param gene the gene
     * @param <T> the type of the gene
     * @return the gene pair in this genome for the given gene
     */
    <T> IAgriGenePair<T> getGenePair(IAgriGene<T> gene);

    /**
     * Fetches the apparent trait for a gene
     * @param gene the gene
     * @param <T> the type of the gene
     * @return the trait this genome results in for the given gene
     */
    default <T> T getTrait(IAgriGene<T> gene) {
        return this.getGenePair(gene).getTrait();
    }

    /**
     * Clones the genome
     * @return an identical copy of this genome
     */
    IAgriGenome clone();

    /**
     * Writes the genome to CompoundNBT.
     *
     * @param tag The tag to serialize to.
     * @return if the transcription was successful.
     */
    boolean writeToNBT(@Nonnull CompoundNBT tag);

    /**
     * Reads the genome from CompoundNBT.
     *
     * @param tag The tag to serialize to.
     * @return if the transcription was successful.
     */
    boolean readFromNBT(@Nonnull CompoundNBT tag);

    /**
     * Checks if another genome is equal to this one (meaning it is a clone)
     * @param other the other genome
     * @return true if the other genome is a clone of this one
     */
    default boolean equalGenome(IAgriGenome other) {
        if(this == other) {
            return true;
        }
        for(IAgriGene<?> gene : AgriApi.getGeneRegistry().all()) {
            if(!this.getGenePair(gene).equals(other.getGenePair(gene))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Default builder to create IAgriGenome objects, obtain via AgriApi.getAgriGenomeBuilder(),
     */
    interface Builder {
        /**
         * Builds the IAgriGenome, for any genes, not explicitly defined, the genome will be populated with their default alleles
         * @return a new IAgriGenome
         */
        IAgriGenome build();

        /**
         * Defines a gene pair
         * @param pair the pair
         * @param <T> the type of the pair
         * @return this
         */
        <T> Builder put(IAgriGenePair<T> pair);

        /**
         * Defines multiple gene pairs
         * @param pairs the pairs
         * @return this
         */
        default Builder put(IAgriGenePair<?>... pairs) {
            return this.put(Arrays.asList(pairs));
        }

        /**
         * Defines multiple gene pairs
         * @param pairs the pairs
         * @return this
         */
        default Builder put(Collection<IAgriGenePair<?>> pairs) {
            return this.consumeStream(pairs.stream());
        }

        /**
         * Fully populates the genome definition based on a single mapping function.
         * Uses the mapping function to define gene pairs for all genes registered in the IAgriGeneRegistry.
         * @param mapper the mapping function
         * @return this
         */
        default Builder populate(Function<IAgriGene<?>, IAgriGenePair<?>> mapper) {
            return this.consumeStream(AgriApi.getGeneRegistry().stream().map(mapper));
        }

        /**
         * Defines multiple gene pairs
         * @param stream a stream of gene pairs
         * @return this
         */
        default Builder consumeStream(Stream<IAgriGenePair<?>> stream) {
            stream.forEach(this::put);
            return this;
        }

        /**
         * Clones the genes from a different genome
         * @param genome the genome to clone from
         * @return this
         */
        default Builder cloneFrom(IAgriGenome genome) {
            return this.populate(gene -> genome.getGenePair(gene).clone());
        }

        /**
         * Populates the genome with random stats
         *
         * @param random pseudo-random generator
         * @return this
         */
        default Builder randomStats(Random random) {
            return this.randomStats(IAgriStat::getMax, random);
        }

        /**
         * Populates the genome with random stats, based on allowed maximum values
         *
         * @param maxFunc Function to limit the maximum value of the stats that can appear in the genome (inclusive)
         * @param random pseudo-random generator
         * @return this
         */
        default Builder randomStats(ToIntFunction<IAgriStat> maxFunc, Random random) {
            return this.randomStats(stat -> random.nextInt(maxFunc.applyAsInt(stat)) + 1);
        }

        /**
         * Populates the genome with random stats, using a predefined function to randomly generate the stat values
         *
         * @param randomizer pre-defined function to randomly generate values for stats
         * @return this
         */
        default Builder randomStats(ToIntFunction<IAgriStat> randomizer) {
            return this.consumeStream(AgriApi.getStatRegistry().stream()
                    .map(stat -> AgriApi.getGeneRegistry().get(stat).map(gene -> {
                        IAllel<Integer> first = gene.getAllel(randomizer.applyAsInt(stat));
                        IAllel<Integer> second = gene.getAllel(randomizer.applyAsInt(stat));
                        return gene.generateGenePair(first, second);}))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
            );
        }
    }
}

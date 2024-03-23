package com.agricraft.agricraft.api.genetic;

import com.agricraft.agricraft.api.AgriRegistrable;
import com.agricraft.agricraft.api.plant.AgriPlant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Defines a gene from AgriCraft's genome for plants and seeds
 * Must be registered in the IAgriGeneRegistry to function correctly.
 *
 * @param <T> the type of the trait governed by this gene
 */
public interface AgriGene<T> extends AgriRegistrable {

	/**
	 * Gets the default fallback trait for the gene, used when genomes are constructed without explicitly assigning alleles
	 *
	 * @param plant the plant for which to fetch the default allele
	 * @return the default allele for this gene
	 */
	AgriAllele<T> defaultAllele(AgriPlant plant);

	/**
	 * Maps a value of the gene to an allele, it is possible that this value falls out of the set of acceptable values,
	 * in which case a different allele must be returned (e.g. the default, or the closest one)
	 *
	 * @param value the value
	 * @return the allele for a value
	 */
	AgriAllele<T> getAllele(T value);

	/**
	 * @return The mutator object which controls mutations for this gene
	 */
	AgriGeneMutator<T> mutator();

	/**
	 * Used when serializing genomes, write the alleles to the tag
	 *
	 * @param genes     the tag to write to
	 * @param dominant  the dominant allele
	 * @param recessive the recessive allele
	 */
	void writeToNBT(CompoundTag genes, AgriAllele<T> dominant, AgriAllele<T> recessive);

	/**
	 * Used when deserializing genomes, reads the alleles from the tag.
	 *
	 * @param tag the tag to read from
	 * @return the gene pair
	 */
	AgriGenePair<T> readFromNBT(CompoundTag tag);

	/**
	 * Add components to the item tooltip
	 *
	 * @param tooltipComponents the list to add the components to
	 * @param trait             the value of the gene
	 */
	void addTooltip(List<Component> tooltipComponents, T trait);

	/**
	 * @return the dominant color as an argb int
	 */
	int getDominantColor();

	/**
	 * @return the recessive color as an argb int
	 */
	int getRecessiveColor();

}

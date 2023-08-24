package com.agricraft.agricraft.api.genetic;

import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.common.util.PlatformUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneSpecies implements AgriGene<String> {

	public static final String ID = "species";
	public static final Map<String, StringAllele> alleles = new HashMap<>();

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public AgriAllele<String> defaultAllele(AgriPlant object) {
		String id = PlatformUtils.getIdFromPlant(object);
		StringAllele allele = alleles.get(id);
		if (allele != null) {
			return allele;
		}
		allele = new StringAllele(id, this);
		alleles.put(id, allele);
		return allele;
	}

	@Override
	public AgriAllele<String> getAllele(String value) {
		StringAllele allele = alleles.get(value);
		if (allele != null) {
			return allele;
		}
		allele = new StringAllele(value, this);
		alleles.put(value, allele);
		return allele;
	}

	@Override
	public void writeToNBT(CompoundTag genes, AgriAllele<String> dominant, AgriAllele<String> recessive) {
		CompoundTag species = new CompoundTag();
		species.putString("dom", dominant.trait());
		species.putString("rec", recessive.trait());
		genes.put(ID, species);
	}

	public AgriGenePair<String> readFromNBT(CompoundTag genes) {
		CompoundTag species = genes.getCompound(ID);
		return new AgriGenePair<>(this, this.getAllele(species.getString("dom")),
				this.getAllele(species.getString("rec")));
	}

	@Override
	public void addTooltip(List<Component> tooltipComponents, String trait) {
		tooltipComponents.add(Component.translatable("agricraft.gene.species").append(": " + trait).withStyle(ChatFormatting.DARK_GRAY));
	}

	public static class StringAllele implements AgriAllele<String> {

		private final String trait;
		private final AgriGene<String> gene;

		public StringAllele(String trait, AgriGene<String> gene) {
			this.trait = trait;
			this.gene = gene;
		}

		@Override
		public String trait() {
			return this.trait;
		}

		@Override
		public boolean isDominant(AgriAllele<String> other) {
			// TODO: @Kether fix this later with species complexity
			return this.trait.compareTo(other.trait()) >= 0;
		}

		@Override
		public AgriGene<String> gene() {
			return this.gene;
		}

	}

}

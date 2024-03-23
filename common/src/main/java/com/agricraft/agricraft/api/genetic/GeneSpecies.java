package com.agricraft.agricraft.api.genetic;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.plant.AgriPlant;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

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
		String id = AgriApi.getPlantId(object).map(ResourceLocation::toString).orElse("");
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
	public AgriGeneMutator<String> mutator() {
		return AgriMutationHandler.getInstance().getActivePlantMutator();
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
			if (this.equals(other)) {
				return true;
			}
			// Fetch complexity of both plants
			int a = AgriApi.getMutationHandler().complexity(this.trait());
			int b = AgriApi.getMutationHandler().complexity(other.trait());
			if(a == b) {
				// Equal complexity, therefore we use an arbitrary definition for dominance, which we will base on the plant id
				return this.trait().compareTo(other.trait()) < 0;
			}
			// Having more difficult obtain plants be dominant will be more challenging to deal with than having them recessive
			return a > b;
		}

		@Override
		public AgriGene<String> gene() {
			return this.gene;
		}

	}

	@Override
	public int getDominantColor() {
		return 0xffbf007f;
	}

	@Override
	public int getRecessiveColor() {
		return 0xff7f00bf;
	}

}

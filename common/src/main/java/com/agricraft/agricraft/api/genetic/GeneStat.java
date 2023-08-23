package com.agricraft.agricraft.api.genetic;

import com.agricraft.agricraft.api.stat.AgriStat;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;

public class GeneStat implements AgriGene<Integer> {

	private final AgriStat stat;
	private final HashMap<Integer, IntAllele> alleles;

	public GeneStat(AgriStat stat) {
		this.stat = stat;
		alleles = new HashMap<>();
		for (int i = stat.getMin(); i <= stat.getMax(); ++i) {
			alleles.put(i, new IntAllele(i, this));
		}
	}

	@Override
	public String getId() {
		return stat.getId();
	}

	@Override
	public AgriAllele<Integer> defaultAllele(AgriPlant object) {
		return alleles.get(stat.getMin());
	}

	@Override
	public AgriAllele<Integer> getAllele(Integer value) {
		return alleles.get(value);
	}

	@Override
	public void writeToNBT(CompoundTag genes, AgriAllele<Integer> dominant, AgriAllele<Integer> recessive) {
		CompoundTag stat = new CompoundTag();
		stat.putInt("dom", dominant.trait());
		stat.putInt("rec", recessive.trait());
		genes.put(this.getId(), stat);
	}

	@Override
	public AgriGenePair<Integer> fromNBT(CompoundTag genes) {
		CompoundTag stat = genes.getCompound(this.getId());
		return new AgriGenePair<>(this, this.getAllele(stat.getInt("dom")), this.getAllele(stat.getInt("rec")));
	}

	@Override
	public void addTooltip(List<Component> tooltipComponents, Integer trait) {
		tooltipComponents.add(Component.translatable("agricraft.stat." + this.getId()).append(": " + trait).withStyle(ChatFormatting.DARK_GRAY));
	}

	public static class IntAllele implements AgriAllele<Integer> {

		private final int trait;
		private final AgriGene<Integer> gene;

		public IntAllele(int trait, AgriGene<Integer> gene) {
			this.trait = trait;
			this.gene = gene;
		}

		@Override
		public Integer trait() {
			return this.trait;
		}

		@Override
		public boolean isDominant(AgriAllele<Integer> other) {
			return this.trait >= other.trait();
		}

		@Override
		public AgriGene<Integer> gene() {
			return this.gene;
		}

	}

}

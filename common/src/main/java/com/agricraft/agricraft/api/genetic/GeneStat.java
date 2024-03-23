package com.agricraft.agricraft.api.genetic;

import com.agricraft.agricraft.api.stat.AgriStat;
import com.agricraft.agricraft.api.plant.AgriPlant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

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
		int val = Mth.clamp(value, this.stat.getMin(), this.stat.getMax());
		return alleles.get(val);
	}

	@Override
	public AgriGeneMutator<Integer> mutator() {
		return AgriMutationHandler.getInstance().getActiveStatMutator();
	}

	@Override
	public void writeToNBT(CompoundTag genes, AgriAllele<Integer> dominant, AgriAllele<Integer> recessive) {
		CompoundTag stat = new CompoundTag();
		stat.putInt("dom", dominant.trait());
		stat.putInt("rec", recessive.trait());
		genes.put(this.getId(), stat);
	}

	@Override
	public AgriGenePair<Integer> readFromNBT(CompoundTag genes) {
		CompoundTag stat = genes.getCompound(this.getId());
		return new AgriGenePair<>(this, this.getAllele(stat.getInt("dom")), this.getAllele(stat.getInt("rec")));
	}

	@Override
	public void addTooltip(List<Component> tooltipComponents, Integer trait) {
		this.stat.addTooltip(tooltipComponents::add, trait);
	}

	@Override
	public int getDominantColor() {
		return stat.getColor();
	}

	@Override
	public int getRecessiveColor() {
		int col = this.stat.getColor();
		int r = (int) ((col >> 16 & 255) * 0.6f);
		int g = (int) ((col >> 8 & 255) * 0.6f);
		int b = (int) ((col & 255) * 0.6f);
		return 0xFF << 24 | r << 16 | g << 8 | b & 0xFF;
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

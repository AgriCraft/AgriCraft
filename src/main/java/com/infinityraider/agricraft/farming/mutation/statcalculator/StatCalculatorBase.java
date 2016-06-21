package com.infinityraider.agricraft.farming.mutation.statcalculator;

import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.farming.mutation.MutationHandler;
import com.infinityraider.agricraft.config.AgriCraftConfig;

import java.util.ArrayList;
import java.util.List;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutation;

public abstract class StatCalculatorBase extends StatCalculator {

	@Override
	public IAgriStat calculateStats(IAgriPlant child, List<? extends IAgriCrop> input, boolean mutation) {
		IAgriCrop[] parents = filterParents(input);
		int nrParents = parents.length;
		int nrValidParents = 0;
		int[] growth = new int[nrParents];
		int[] gain = new int[nrParents];
		int[] strength = new int[nrParents];
		for (int i = 0; i < nrParents; i++) {
			boolean canInherit = canInheritStats(child, parents[i].getPlant());
			if (canInherit) {
				nrValidParents = nrValidParents + 1;
			}
			//multiplier is the difficulty
			//-1: if neighbour is a non-parent crops and non parent crops do not affect stats, multiplier is -1 (negative values get filtered)
			//0 : if neighbour is a non-parent crop, and non parent crops affect stat gain negatively, multiplier is 0 (0 will reduce the average)
			//1 : if neighbour is parent crop, multiplier is 1
			int multiplier = canInherit ? 1 : (AgriCraftConfig.otherCropsAffectStatsNegatively ? 0 : -1);
			growth[i] = multiplier * parents[i].getStat().getGrowth();
			gain[i] = multiplier * parents[i].getStat().getGain();
			strength[i] = multiplier * parents[i].getStat().getStrength();
		}
		int meanGrowth = getMeanIgnoringNegativeValues(growth);
		int meanGain = getMeanIgnoringNegativeValues(gain);
		int meanStrength = getMeanIgnoringNegativeValues(strength);
		int divisor = mutation ? AgriCraftConfig.cropStatDivisor : 1;
		return new PlantStats(
				calculateStats(meanGrowth, nrValidParents, divisor),
				calculateStats(meanGain, nrValidParents, divisor),
				calculateStats(meanStrength, nrValidParents, divisor)
		);
	}

	//gets an array of all the possible parents from the array containing all the neighbouring crops
	protected IAgriCrop[] filterParents(List<? extends IAgriCrop> input) {
		ArrayList<IAgriCrop> list = new ArrayList<>();
		for (IAgriCrop crop : input) {
			if (crop != null && crop.isMature()) {
				list.add(crop);
			}
		}
		return list.toArray(new IAgriCrop[list.size()]);
	}

	// TODO: Investigate Config Setting.
	protected boolean canInheritStats(IAgriPlant child, IAgriPlant parent) {
		int validParentId = AgriCraftConfig.validParents;
		//1: any crop
		//2: only parent crops and identical crops
		//3: only identical crops
		if (validParentId == 1) {
			return true;
		} else if (child.equals(parent)) {
			return validParentId == 3;
		}
		for (IAgriMutation mutation : MutationHandler.getMutationsFromChild(child)) {
			for (IAgriPlant p : mutation.getParents()) {
				if (parent.equals(p)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * returns the mean value of an int array, this ignores negative values in
	 * the array
	 */
	private int getMeanIgnoringNegativeValues(int[] input) {
		int sum = 0;
		int total = input.length;
		int mean = 0;
		if (total > 0) {
			for (int nr : input) {
				if (nr >= 0) {
					sum = sum + nr;
				} else {
					total--;
				}
			}
			if (total > 0) {
				mean = Math.round(((float) sum) / ((float) total));
			}
		}
		return mean;
	}

	/**
	 * calculates the new stats based on an input stat, the nr of neighbours and
	 * a divisor
	 */
	protected abstract int calculateStats(int input, int neighbours, int divisor);
}

package com.infinityraider.agricraft.farming.mutation;

import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.farming.mutation.statcalculator.StatCalculator;
import com.infinityraider.agricraft.api.mutation.IAgriMutation;
import com.infinityraider.agricraft.apiimpl.MutationRegistry;
import java.util.List;
import java.util.stream.Collectors;

public class MutateStrategy extends BaseStrategy {

	public MutateStrategy(MutationEngine mutationEngine) {
		super(mutationEngine);
	}

	@Override
	public CrossOverResult executeStrategy() {
		List<IAgriMutation> crossOvers = MutationRegistry.getInstance().getPossibleMutations(
				this.engine.getCrop().getMatureNeighbours().stream()
				.map(IAgriCrop::getPlant)
				.filter(TypeHelper::isNonNull)
				.collect(Collectors.toList())
		);
		if (!crossOvers.isEmpty()) {
			int index = engine.getRandom().nextInt(crossOvers.size());
			CrossOverResult result = CrossOverResult.fromMutation(crossOvers.get(index));
			StatCalculator.setResultStats(result, engine.getCrop().getMatureNeighbours(), true);
			return result;
		}
		return null;
	}
}

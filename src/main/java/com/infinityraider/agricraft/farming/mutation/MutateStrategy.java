package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.farming.mutation.statcalculator.StatCalculator;
import java.util.ArrayList;

public class MutateStrategy extends BaseStrategy {
    public MutateStrategy(MutationEngine mutationEngine) {
        super(mutationEngine);
    }

    @Override
    public CrossOverResult executeStrategy() {
		// TODO: UN-HACK
        Mutation[] crossOvers = MutationHandler.getCrossOvers(new ArrayList<>(engine.getCrop().getMatureNeighbours()));
        if (crossOvers.length > 0) {
            int index = engine.getRandom().nextInt(crossOvers.length);
            if (crossOvers[index].getChild().getSeed().getItem() != null) {
                CrossOverResult result = CrossOverResult.fromMutation(crossOvers[index]);
                StatCalculator.setResultStats(result, engine.getCrop().getMatureNeighbours(), true);
                return result;
            }
        }
        return null;
    }
}

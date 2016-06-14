package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.api.v1.IMutation;
import com.infinityraider.agricraft.farming.mutation.statcalculator.StatCalculator;

public class MutateStrategy extends BaseStrategy {
    public MutateStrategy(MutationEngine mutationEngine) {
        super(mutationEngine);
    }

    @Override
    public CrossOverResult executeStrategy() {
        IMutation[] crossOvers = MutationHandler.getCrossOvers(engine.getCrop().getMatureNeighbours());
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

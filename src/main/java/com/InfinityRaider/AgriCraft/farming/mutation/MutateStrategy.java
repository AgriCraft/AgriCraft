package com.InfinityRaider.AgriCraft.farming.mutation;


public class MutateStrategy extends BaseStrategy {

    public MutateStrategy(MutationEngine mutationEngine) {
        super(mutationEngine);
    }

    @Override
    public StrategyResult executeStrategy() {
        Mutation[] crossOvers = MutationHandler.getCrossOvers(engine.getCrop().getMatureNeighbours());
        if (crossOvers != null && crossOvers.length > 0) {
            int index = engine.getRandom().nextInt(crossOvers.length);
            if (crossOvers[index].result.getItem() != null) {
                return StrategyResult.fromMutation(crossOvers[index]);
            }
        }
        return null;
    }
}

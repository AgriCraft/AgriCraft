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
                StrategyResult result = StrategyResult.fromMutation(crossOvers[index]);
                int[] stats = MutationHandler.getStats(engine.getCrop().getMatureNeighbours(), true);
                result.setStats(stats[0], stats[1], stats[2]);
                return result;
            }
        }
        return null;
    }
}

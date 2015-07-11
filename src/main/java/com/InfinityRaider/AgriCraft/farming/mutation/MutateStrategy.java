package com.InfinityRaider.AgriCraft.farming.mutation;


public class MutateStrategy extends BaseStrategy {

    public MutateStrategy(MutationEngine mutationEngine) {
        super(mutationEngine);
    }

    @Override
    public CrossOverResult executeStrategy() {
        Mutation[] crossOvers = MutationHandler.getCrossOvers(engine.getCrop().getMatureNeighbours());
        if (crossOvers != null && crossOvers.length > 0) {
            int index = engine.getRandom().nextInt(crossOvers.length);
            if (crossOvers[index].getResult().getItem() != null) {
                CrossOverResult result = CrossOverResult.fromMutation(crossOvers[index]);
                MutationHandler.setResultStats(result, engine.getCrop().getMatureNeighbours(), true);
                return result;
            }
        }
        return null;
    }
}

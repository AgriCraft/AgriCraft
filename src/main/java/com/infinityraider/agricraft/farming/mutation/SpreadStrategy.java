package com.infinityraider.agricraft.farming.mutation;


import com.infinityraider.agricraft.api.v1.ICrop;
import com.infinityraider.agricraft.farming.mutation.statcalculator.StatCalculator;

import java.util.List;

public class SpreadStrategy extends BaseStrategy {
    public SpreadStrategy(MutationEngine mutationEngine) {
        super(mutationEngine);
    }

    @Override
    public CrossOverResult executeStrategy() {
        List<ICrop> matureNeighbours = engine.getCrop().getMatureNeighbours();
        if (matureNeighbours.isEmpty()) {
            return null;
        }

        int index = engine.getRandom().nextInt(matureNeighbours.size());
        ICrop neighbour = matureNeighbours.get(index);
        CrossOverResult result = CrossOverResult.fromTileEntityCrop(neighbour);
        StatCalculator.setResultStats(result, matureNeighbours, false);
        return result;
    }
}

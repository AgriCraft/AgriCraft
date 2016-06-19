package com.infinityraider.agricraft.farming.mutation;


import com.infinityraider.agricraft.farming.mutation.statcalculator.StatCalculator;

import java.util.List;
import com.infinityraider.agricraft.api.v3.crop.IAgriCrop;

public class SpreadStrategy extends BaseStrategy {
    public SpreadStrategy(MutationEngine mutationEngine) {
        super(mutationEngine);
    }

    @Override
    public CrossOverResult executeStrategy() {
        List<IAgriCrop> matureNeighbours = engine.getCrop().getMatureNeighbours();
        if (matureNeighbours.isEmpty()) {
            return null;
        }

        int index = engine.getRandom().nextInt(matureNeighbours.size());
        IAgriCrop neighbour = matureNeighbours.get(index);
        CrossOverResult result = CrossOverResult.fromTileEntityCrop(neighbour);
        StatCalculator.setResultStats(result, matureNeighbours, false);
        return result;
    }
}

package com.infinityraider.agricraft.farming.mutation;


import com.infinityraider.agricraft.farming.mutation.statcalculator.StatCalculator;
import com.infinityraider.agricraft.tiles.TileEntityCrop;

import java.util.List;

public class SpreadStrategy extends BaseStrategy {
    public SpreadStrategy(MutationEngine mutationEngine) {
        super(mutationEngine);
    }

    @Override
    public CrossOverResult executeStrategy() {
        List<TileEntityCrop> matureNeighbours = engine.getCrop().getMatureNeighbours();
        if (matureNeighbours.isEmpty()) {
            return null;
        }

        int index = engine.getRandom().nextInt(matureNeighbours.size());
        TileEntityCrop neighbour = matureNeighbours.get(index);
        CrossOverResult result = CrossOverResult.fromTileEntityCrop(neighbour);
        StatCalculator.setResultStats(result, matureNeighbours, false);
        return result;
    }
}

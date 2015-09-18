package com.InfinityRaider.AgriCraft.farming.mutation;


import java.util.List;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

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
        MutationHandler.setResultStats(result, matureNeighbours, false);
        return result;
    }
}

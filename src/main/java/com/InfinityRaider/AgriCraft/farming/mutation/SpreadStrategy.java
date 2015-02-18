package com.InfinityRaider.AgriCraft.farming.mutation;


import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

import java.util.List;

public class SpreadStrategy extends BaseStrategy {

    public SpreadStrategy(MutationEngine mutationEngine) {
        super(mutationEngine);
    }

    @Override
    public StrategyResult executeStrategy() {
        List<TileEntityCrop> matureNeighbours = engine.getCrop().getMatureNeighbours();
        if (matureNeighbours.isEmpty()) {
            return null;
        }

        int index = engine.getRandom().nextInt(matureNeighbours.size());
        TileEntityCrop neighbour = matureNeighbours.get(index);
        StrategyResult result = StrategyResult.fromTileEntityCrop(neighbour);
        int[] stats = MutationHandler.getStats(matureNeighbours, false);
        result.setStats(stats[0], stats[1], stats[2]);
        return result;
    }
}

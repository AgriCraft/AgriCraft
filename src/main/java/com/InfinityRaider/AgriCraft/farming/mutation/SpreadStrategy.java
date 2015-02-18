package com.InfinityRaider.AgriCraft.farming.mutation;


import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

import java.util.List;
import java.util.Random;

public class SpreadStrategy implements INewSeedStrategy {

    private static final Random random = new Random();

    @Override
    public StrategyResult executeStrategy(TileEntityCrop crop) {
        List<TileEntityCrop> matureNeighbours = crop.getMatureNeighbours();
        if (matureNeighbours.isEmpty()) {
            return null;
        }

        int index = random.nextInt(matureNeighbours.size());
        TileEntityCrop neighbour = matureNeighbours.get(index);
        return StrategyResult.fromTileEntityCrop(neighbour);
    }
}

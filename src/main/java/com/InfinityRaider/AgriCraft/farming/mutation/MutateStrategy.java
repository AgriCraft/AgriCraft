package com.InfinityRaider.AgriCraft.farming.mutation;


import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

import java.util.Random;

public class MutateStrategy implements INewSeedStrategy {

    private static final Random random = new Random();

    @Override
    public StrategyResult executeStrategy(TileEntityCrop crop) {
        Mutation[] crossOvers = MutationHandler.getCrossOvers(crop.getMatureNeighbours());
        if (crossOvers != null && crossOvers.length > 0) {
            int index = random.nextInt(crossOvers.length);
            if (crossOvers[index].result.getItem() != null) {
                return StrategyResult.fromMutation(crossOvers[index]);
            }
        }
        return null;
    }
}

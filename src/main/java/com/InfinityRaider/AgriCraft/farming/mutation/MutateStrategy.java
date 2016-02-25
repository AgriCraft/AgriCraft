package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.api.v1.IMutation;
import com.InfinityRaider.AgriCraft.api.v3.ICrop;
import com.InfinityRaider.AgriCraft.api.v3.ICrossOverResult;
import com.InfinityRaider.AgriCraft.api.v3.IMutationEngine;
import net.minecraft.item.Item;

import java.util.List;

public class MutateStrategy extends BaseStrategy {
    public MutateStrategy(IMutationEngine mutationEngine) {
        super(mutationEngine);
    }

    public ICrossOverResult executeStrategy() {
        List<ICrop> neighbours = engine.getCrop().getMatureNeighbours();
        IMutation[] crossOvers = MutationHandler.getInstance().getCrossOvers(neighbours);
        if (crossOvers.length > 0) {
            int index = engine.getRandom().nextInt(crossOvers.length);
            if (crossOvers[index].getResult().getItem() != null) {
                return fromMutation(crossOvers[index], neighbours);
            }
        }
        return null;
    }

    /** Creates a new instanced based off the result of the given mutation. Does not validate the mutation object */
    private CrossOverResult fromMutation(IMutation mutation, List<ICrop> neighbours) {
        Item seed = mutation.getResult().getItem();
        int meta = mutation.getResult().getItemDamage();

        return new CrossOverResult(seed, meta, mutation.getChance(), engine.getStatCalculator().calculateStats(seed, meta, neighbours, true));
    }
}

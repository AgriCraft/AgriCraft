package com.InfinityRaider.AgriCraft.farming.mutation;


import com.InfinityRaider.AgriCraft.api.v3.ICrop;
import com.InfinityRaider.AgriCraft.api.v3.IMutationEngine;
import com.InfinityRaider.AgriCraft.farming.mutation.statcalculator.StatCalculator;
import net.minecraft.item.Item;

import java.util.List;

public class SpreadStrategy extends BaseStrategy {
    public SpreadStrategy(IMutationEngine mutationEngine) {
        super(mutationEngine);
    }

    public CrossOverResult executeStrategy() {
        List<ICrop> matureNeighbours = engine.getCrop().getMatureNeighbours();
        if (matureNeighbours.isEmpty()) {
            return null;
        }
        int index = engine.getRandom().nextInt(matureNeighbours.size());
        ICrop neighbour = matureNeighbours.get(index);
        return fromTileEntityCrop(neighbour, matureNeighbours);
    }

    /** Creates a new instance based on the planted seed of the given TE. Does not validate the TE */
    private CrossOverResult fromTileEntityCrop(ICrop crop, List<ICrop> neighbours) {
        Item seed = crop.getSeedStack().getItem();
        int meta = crop.getSeedStack().getItemDamage();
        double chance = ((double) crop.getPlant().getSpreadChance())/100.0;

        return new CrossOverResult(seed, meta, chance, engine.getStatCalculator().calculateStats(seed, meta, neighbours, false));
    }
}

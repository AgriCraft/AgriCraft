package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

/**
 * Base interface for different spread or mutation strategies.
 */
public interface INewSeedStrategy {

    StrategyResult executeStrategy(TileEntityCrop crop);
}

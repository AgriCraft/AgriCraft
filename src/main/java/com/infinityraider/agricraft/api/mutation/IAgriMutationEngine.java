/*
 */
package com.infinityraider.agricraft.api.mutation;

import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;
import java.util.Random;

/**
 *
 * @author Ryan
 */
public interface IAgriMutationEngine {

    /*
     * Applies one of the 2 strategies and notifies the TE if it should update
     */
    void executeCrossOver(TileEntityCrop crop, Random rand);

    IAgriCrossStrategy rollStrategy(Random rand);
	
}

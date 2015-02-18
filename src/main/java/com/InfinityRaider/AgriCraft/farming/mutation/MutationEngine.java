package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class decides whether a plant is spreading or mutating and also
 * calculates the new stats (growth, gain, strength) of the new plant based on
 * the 4 neighbours.
 */
public class MutationEngine {

    private final TileEntityCrop crop;
    private final Random random;

    public MutationEngine(TileEntityCrop crop) {
        this(crop, new Random());
    }

    public MutationEngine(TileEntityCrop crop, Random random) {
        this.crop = crop;
        this.random = random;
    }

    public INewSeedStrategy rollStrategy() {
        boolean spreading = random.nextDouble() > ConfigurationHandler.mutationChance;
        return spreading ? new SpreadStrategy() : new MutateStrategy();
    }

    /**
     * @returns a list with all neighbours of type <code>TileEntityCrop</code> in the
     *          NORTH, SOUTH, EAST and WEST direction
     */
    public List<TileEntityCrop> getNeighbours() {
        List<TileEntityCrop> neighbours = new ArrayList<TileEntityCrop>();
        addNeighbour(neighbours, ForgeDirection.NORTH);
        addNeighbour(neighbours, ForgeDirection.SOUTH);
        addNeighbour(neighbours, ForgeDirection.EAST);
        addNeighbour(neighbours, ForgeDirection.WEST);
        return neighbours;
    }

    private void addNeighbour(List<TileEntityCrop> neighbours, ForgeDirection direction) {
        TileEntity te = crop.getWorldObj().getTileEntity(crop.xCoord + direction.offsetX, crop.yCoord + direction.offsetY, crop.zCoord + direction.offsetZ);
        if (te == null || !(te instanceof TileEntityCrop)) {
            return;
        }

        neighbours.add((TileEntityCrop) te);
    }
}

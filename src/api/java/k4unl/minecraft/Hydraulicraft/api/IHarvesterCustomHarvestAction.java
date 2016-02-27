package k4unl.minecraft.Hydraulicraft.api;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * @author Koen Beckers (K-4U)
 * Implement this interface if you want the harvester not to break the crop
 */
public interface IHarvesterCustomHarvestAction {

    /**
     * The custom action that the harvester wil execute, instead of breaking the crop.
     * @param world The world we want to harvest in.
     * @param x, y, z The position on which we'll harvest.
     * @return A list of Itemstacks, containing all the stuff that should be added to the inventory of the harvester.
     */
    ArrayList<ItemStack> doHarvest(World world, int x, int y, int z);
}

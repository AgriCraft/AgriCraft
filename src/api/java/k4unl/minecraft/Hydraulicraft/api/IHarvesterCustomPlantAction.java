package k4unl.minecraft.Hydraulicraft.api;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author Koen Beckers (K-4U)
 * Implement this interface if you want the harvester to do a custom action on plant.
 */
public interface IHarvesterCustomPlantAction {

    /**
     * Custom action for the harvester to do on planting.
     * @param world The world in which we'll be planting.
     * @param x, y, z The position that we want to plant.
     * @param seed Contains one seed that is to be planted (has already been removed from inventory).
     */
    void doPlant(World world, int x, int y, int  z, ItemStack seed);
}

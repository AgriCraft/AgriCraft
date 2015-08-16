package net.shadowmage.ancientwarfare.api;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Interface used for compatibility with Ancient Warfare crop farms for crops which don't follow the standard MinecraftForge crop code
 * This interface has to be implemented in the Item class of your crops (e.g. in a custom seed class or ItemBlock class)
 */
public interface IAncientWarfarePlantable {
    /**
     * Checks if the stack can be planted
     * @param stack: the stack
     * @return true if this stack can be planted by the crop farm
     */
    boolean isPlantable(ItemStack stack);

    /**
     * Called when the crop farm tries to plant this crop
     * @param world
     * @param x
     * @param y
     * @param z
     * @param stack a copy of the stack which the farm is trying to plant
     * @return true if planting is allowed, false if not
     */
    boolean tryPlant(World world, int x, int y, int z, ItemStack stack);
}

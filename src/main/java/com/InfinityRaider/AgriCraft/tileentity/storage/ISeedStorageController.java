package com.InfinityRaider.AgriCraft.tileentity.storage;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorageController;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public interface ISeedStorageController {
    /**
     * Adds a controllable to this controller
     */
    public void addControllable(ISeedStorageControllable controllable);

    /**
     * Checks if this controller is active
     */
    public boolean isControlling();

    /**
     * Returns the coordinates of all the controllables under this controller
     */
    public ArrayList<int[]> getControlledCoordinates();

    /**
     * Returns the coordinates of the controller
     */
    public int[] getCoordinates();

    /**
     * Gets the id of the controllable in this controller
     */
    public int getControllableID(ISeedStorageControllable controllable);

    /**
     * Returns the controllable with this active stack
     */
    public ISeedStorageControllable getControllable(ItemStack stack);
}

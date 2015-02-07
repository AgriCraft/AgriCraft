package com.InfinityRaider.AgriCraft.tileentity.storage;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorageController;
import com.InfinityRaider.AgriCraft.container.SlotSeedStorage;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ISeedStorageController {
    /**
     * Gets a Hashmap containing SlotSeedStorage slots for all the controllables this controller is controlling
     */
    public Map<ItemSeeds, Map<Integer, List<SlotSeedStorage>>> getInventoryMap(ContainerSeedStorageController container);

    /**
     * Sets the inventories of all the controllables from a hashmap
     */
    public void setControlledInventories(Map<ItemSeeds, Map<Integer, List<SlotSeedStorage>>> map);

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

package com.InfinityRaider.AgriCraft.tileentity.storage;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ISeedStorageController {
    /**
     * Tries to add a stack to the inventory, returns true on success
     */
    public boolean addStackToInventory(ItemStack stack);

    /**
     * Returns a list of all the kinds of seeds
     */
    public List<ItemStack> getControlledSeeds();

    /**
     * Returns a list of all the slots in the inventory corresponding to that seed
     */
    public List<SeedStorageSlot> getSlots(Item seed, int meta);

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

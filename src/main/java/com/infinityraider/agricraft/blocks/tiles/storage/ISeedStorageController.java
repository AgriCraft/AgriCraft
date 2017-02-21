package com.infinityraider.agricraft.blocks.tiles.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Optional;

public interface ISeedStorageController {
    /**
     * Tries to add a stack to the inventory, returns true on success
     */
    boolean addStackToInventory(ItemStack stack);

    /**
     * Returns a list of all the kinds of seeds
     */
    List<ItemStack> getControlledSeeds();

    /**
     * Returns a list of all the slots in the inventory corresponding to that seed
     */
    List<SeedStorageSlot> getSlots(Item seed, int meta);

    /**
     * Adds a controllable to this controller
     */
    void addControllable(ISeedStorageControllable controllable);

    /**
     * Checks if this controller is active
     */
    boolean isControlling();

    /**
     * Returns the coordinates of all the controllables under this controller
     */
    List<int[]> getControlledCoordinates();

    /**
     * Returns the coordinates of the controller
     */
    int[] getCoordinates();

    /**
     * Gets the id of the controllable in this controller
     */
    int getControllableID(ISeedStorageControllable controllable);

    /**
     * Returns the controllable with this active stack
     */
    Optional<ISeedStorageControllable> getControllable(ItemStack stack);
}

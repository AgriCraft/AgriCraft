package com.infinityraider.agricraft.blocks.tiles.storage;

import com.infinityraider.agricraft.api.seed.AgriSeed;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Optional;

public interface ISeedStorageControllable {
    /**
     * Tries to add a stack to the inventory, returns true on success
     */
    boolean addStackToInventory(ItemStack stack);

    /**
     * Sets the ItemStack in a slot to a given ItemStack
     */
    void setSlotContents(int realSlotId, ItemStack inputStack);

    /**
     * Gets stack for a slot Id
     */
    ItemStack getStackForSlotId(int slotId);

    /**
     * Decreases the stacksize of the stack in the slot with the given ID by an amount
     */
    ItemStack decreaseStackSizeInSlot(int slotId, int amount);

    /**
     * Returns a list of all the stacks in this inventory, all the slots in this inventory must have the same type of seed
     */
    List<ItemStack> getInventory();

    /**
     * Returns a list of all the slots in the inventory corresponding to that seed
     */
    List<SeedStorageSlot> getSlots();

    /**
     * Returns the coordinates of the controller controlling this controllable
     */
    int[] getControllerCoords();

    /**
     * Returns the coordinates of this controllable
     */
    int[] getCoords();

    /**
     * Returns the controller controlling this controllable
     */
    ISeedStorageController getController();

    /**
     * Returns true if this is being controlled
     */
    boolean hasController();

    /**
     * Returns true if this has a locked seed
     */
    boolean hasLockedSeed();

    /**
     * Sets the locked seed
     */
    boolean setLockedSeed(AgriSeed seed);

    /**
     * Clears the locked seed
     */
    void clearLockedSeed();

    /**
     * Returns the type of seed stored in this controllable
     */
    Optional<AgriSeed> getLockedSeed();

    /**
     * Returns the id of this controllable in the controller, returns -1 if this doesn't have a controller
     */
    int getControllableID();
}

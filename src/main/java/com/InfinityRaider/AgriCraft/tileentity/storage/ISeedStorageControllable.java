package com.InfinityRaider.AgriCraft.tileentity.storage;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorageController;
import com.InfinityRaider.AgriCraft.container.SlotSeedStorage;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public interface ISeedStorageControllable extends IInventory{
    /**
     * Returns a list of all the slots in this inventory, all the slots in this inventory must have the same type of seed
     */
    public ArrayList<SeedStorageSlot> getInventorySlots();

    /**
     * Returns a list of all the stacks in this inventory, all the slots in this inventory must have the same type of seed
     */
    public ArrayList<ItemStack> getInventory();

    /**
     * Sets the inventory based off a lit of slots, pass null in the arguments to clear the inventory
     */
    public void setInventory(ArrayList<SlotSeedStorage> list);

    /**
     * Returns a list of all the slots in this inventory, all the slots in this inventory must have the same type of seed
     */
    public ArrayList<SlotSeedStorage> getInventorySlots(ContainerSeedStorageController container);

    /**
     * Returns the coordinates of the controller controlling this controllable
     */
    public int[] getControllerCoords();

    /**
     * Returns the coordinates of this controllable
     */
    public int[] getCoords();

    /**
     * Returns the controller controlling this controllable
     */
    public ISeedStorageController getController();

    /**
     * Returns true if this is being controlled
     */
    public boolean hasController();

    /**
     * Returns true if this has a locked seed
     */
    public boolean hasLockedSeed();

    /**
     * Sets the locked seed
     */
    public void setLockedSeed(ItemSeeds seed, int meta);

    /**
     * Returns the type of seed stored in this controllable
     */
    public ItemStack getLockedSeed();

    /**
     * Returns the id of this controllable in the controller, returns -1 if this doesn't have a controller
     */
    public int getControllableID();
}

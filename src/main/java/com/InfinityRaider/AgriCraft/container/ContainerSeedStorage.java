package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class ContainerSeedStorage extends ContainerAgricraft {
    public HashMap<ItemSeeds, HashMap<Integer, ArrayList<Slot>>> entries;
    public TileEntitySeedStorage te;
    private int lastSlotId;

    public ContainerSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(inventory, 82, 94);
        this.lastSlotId = 35;
        this.te = te;
        this.entries = new HashMap<ItemSeeds, HashMap<Integer, ArrayList<Slot>>>();
        for(ItemStack seedStack:te.getInventory()) {
            this.addSeed(seedStack);
        }
    }

    public void addSeed(ItemStack stack) {
        if(stack!=null && stack.getItem()!=null && stack.getItem() instanceof ItemSeeds) {
            HashMap<Integer, ArrayList<Slot>> itemEntry = entries.get(stack.getItem());
            //there is an entry for this item
            if(itemEntry !=null) {
                ArrayList<Slot> seedEntries = itemEntry.get(stack.getItemDamage());
                //there is an entry for this item and meta
                if(seedEntries!=null) {
                    boolean seedAdded = false;
                    for(Slot slot:seedEntries) {
                        //there is an entry with equal NBT
                        ItemStack seedStack = slot.getStack();
                        if(ItemStack.areItemStackTagsEqual(seedStack, stack)) {
                            //if we don't go trough the method to set/get stacksize we can get stacks over 64 stuffed away
                            seedStack.stackSize = seedStack.stackSize + stack.stackSize;
                            seedAdded = true;
                        }
                    }
                    //there is no entry with equal NBT
                    if(!seedAdded) {
                        seedEntries.add(addNewSlot(stack.copy()));
                    }
                }
                //there is not yet an entry for this  meta
                else {
                    ArrayList<Slot> newList = new ArrayList<Slot>();
                    newList.add(addNewSlot(stack.copy()));
                    itemEntry.put(stack.getItemDamage(), newList);
                }
            }
            //there is no entry for this item yet
            else {
                ArrayList<Slot> newList = new ArrayList<Slot>();
                newList.add(addNewSlot(stack.copy()));
                HashMap<Integer, ArrayList<Slot>> newEntry = new HashMap<Integer, ArrayList<Slot>>();
                newEntry.put(stack.getItemDamage(), newList);
                entries.put((ItemSeeds) stack.getItem(), newEntry);
            }
        }
    }

    private Slot addNewSlot(ItemStack stack) {
        Slot newSlot = new Slot(this.te, this.lastSlotId+1, 0, 0);
        this.addSlotToContainer(newSlot);
        this.lastSlotId++;
        newSlot.putStack(stack.copy());
        return newSlot;
    }

    @Override
    public void putStackInSlot(int Slot, ItemStack stack) {
        this.addSeed(stack);
    }

    //this gets called when a player shift clicks a stack into the inventory
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int clickedSlot) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(clickedSlot);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            //try to move item from the container into the player's inventory
            if (clickedSlot>35) {
                if (!this.mergeItemStack(itemstack1, 0, inventorySlots.size(), false)) {
                    return null;
                }
            }
            else {
                //try to move item from the player's inventory into the container
                if(itemstack1.getItem()!=null) {
                    if(itemstack1.getItem() instanceof ItemSeeds) {
                        if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                            return null;
                        }
                    }
                }
            }
            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            }
            else {
                slot.onSlotChanged();
            }
            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(player, itemstack1);
        }
        return itemstack;
    }
}

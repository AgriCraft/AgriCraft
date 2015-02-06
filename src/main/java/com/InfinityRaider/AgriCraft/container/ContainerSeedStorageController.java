package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.gui.GuiSeedStorageController;
import com.InfinityRaider.AgriCraft.network.MessageContainerSeedStorage;
import com.InfinityRaider.AgriCraft.network.NetworkWrapperAgriCraft;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorageController;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContainerSeedStorageController extends ContainerAgricraft {
    //one hash map to quickly find the correct slot based on a stack
    public Map<ItemSeeds, Map<Integer, List<SlotSeedStorage>>> entries;
    //another map based on the slot id
    public HashMap<Integer, SlotSeedStorage> seedSlots;
    public TileEntitySeedStorageController te;
    private int lastSlotId;

    public ContainerSeedStorageController(InventoryPlayer inventory, TileEntitySeedStorageController te) {
        super(inventory, 82, 94);
        this.lastSlotId = this.PLAYER_INVENTORY_SIZE -1;
        this.te = te;
        this.entries = te.getInventoryMap(this);
        this.initSeedSlots();
    }

    private void initSeedSlots() {
        if(this.entries!=null) {
            this.seedSlots = new HashMap<Integer, SlotSeedStorage>();
            for(Map.Entry<ItemSeeds, Map<Integer, List<SlotSeedStorage>>> seedEntry:entries.entrySet()) {
                if(seedEntry!=null && seedEntry.getKey()!=null && seedEntry.getValue()!=null) {
                    for(Map.Entry<Integer, List<SlotSeedStorage>> metaEntry:seedEntry.getValue().entrySet()) {
                        if(metaEntry!=null && metaEntry.getKey()!=null && metaEntry.getValue()!=null) {
                            for(SlotSeedStorage slot:metaEntry.getValue()) {
                                this.seedSlots.put(slot.index, slot);
                            }
                        }
                    }
                }
            }
        }
    }

    //tries to add a stack to the storage, return true on success
    public boolean addSeedToStorage(ItemStack stack) {
        boolean success = false;
        if(stack!=null && stack.getItem()!=null) {
            ItemSeeds seed = (ItemSeeds) stack.getItem();
            //There is a value for this seed
            if (this.entries.get(seed) != null) {
                Map<Integer, List<SlotSeedStorage>> metaMap = this.entries.get(seed);
                //There is a value for this meta
                if (metaMap.get(stack.getItemDamage()) != null && metaMap.get(stack.getItemDamage()).size() > 0) {
                    List<SlotSeedStorage> list = metaMap.get(stack.getItemDamage());
                    for (SlotSeedStorage slot : list) {
                        if (slot != null && slot.getStack() != null) {
                            ItemStack stackInSlot = slot.getStack();
                            if (stackInSlot != null && stackInSlot.getItem() != null && stack.hasTagCompound() && ItemStack.areItemStackTagsEqual(stackInSlot, stack)) {
                                slot.putStack(stack);
                                success = true;
                                break;
                            }
                        }
                    }
                    if(!success) {
                        //there is not yet a slot with this NBT tag
                        list.add(this.getNewSeedSlot(stack));
                        ItemStack activeSeed = this.getActiveSeed();
                        if(activeSeed!=null && stack.getItem()==activeSeed.getItem() && stack.getItemDamage()==activeSeed.getItemDamage()) {
                            this.resetActiveEntries(activeSeed, 0);
                        }
                        success = true;
                    }
                }
                //There is no value for this meta yet
                else {
                    //create new array list for this seed & meta
                    ArrayList<SlotSeedStorage> newList = new ArrayList<SlotSeedStorage>();
                    newList.add(this.getNewSeedSlot(stack));
                    metaMap.put(stack.getItemDamage(), newList);
                    success = true;
                }
            }
            //There is not yet a value for this seed
            else {
                //create new array list for this seed & meta
                ArrayList<SlotSeedStorage> newList = new ArrayList<SlotSeedStorage>();
                newList.add(this.getNewSeedSlot(stack));
                //create new hash map for this seed
                Map<Integer, List<SlotSeedStorage>> newMetaMap = new HashMap<Integer, List<SlotSeedStorage>>();
                newMetaMap.put(stack.getItemDamage(), newList);
                this.entries.put(seed, newMetaMap);
                success = true;
            }
        }
        return success;
    }

    public SlotSeedStorage getNewSeedSlot(ItemStack stack) {
        this.lastSlotId = this.lastSlotId+1;
        IInventory inventory = this.te.getControllable(stack);
        SlotSeedStorage newSlot = new SlotSeedStorage(inventory, this.lastSlotId, stack);
        newSlot.addActiveContainer(this);
        this.seedSlots.put(lastSlotId, newSlot);
        return newSlot;
    }

    public ArrayList<SlotSeedStorage> getEntries() {
        ArrayList<SlotSeedStorage> slots = new ArrayList<SlotSeedStorage>();
        slots.addAll(this.seedSlots.values());
        return slots;
    }

    /**
     * returns a list if itemStacks, for each slot.
     */
    @Override
    public List getInventory() {
        ArrayList arraylist = new ArrayList();
        for(int i = 0; i < this.inventorySlots.size(); ++i) {
            arraylist.add(((Slot)this.inventorySlots.get(i)).getStack());
        }
        for(SlotSeedStorage slot:this.seedSlots.values())
        arraylist.add(slot.getStack());
        return arraylist;
    }

    @Override
    public Slot getSlotFromInventory(IInventory inventory, int slotIndex) {
        Slot slot = this.seedSlots.get(slotIndex);
        if(slot!=null && slot instanceof SlotSeedStorage) {
            if (slot.isSlotInInventory(inventory, slotIndex)) {
                return slot;
            }
        }
        return super.getSlotFromInventory(inventory, slotIndex);
    }

    @Override
    public Slot getSlot(int id) {
        if(id<this.PLAYER_INVENTORY_SIZE) {
            return (Slot) this.inventorySlots.get(id);
        }
        else {
            return this.seedSlots.get(id);
        }
    }

    /**
     * places item stacks in the first x slots, x being itemstack.length
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void putStacksInSlots(ItemStack[] stackArray) {
        for (int i=0;i<Math.min(stackArray.length,this.PLAYER_INVENTORY_SIZE);++i) {
            this.getSlot(i).putStack(stackArray[i]);
        }
        for(int i=this.PLAYER_INVENTORY_SIZE;i<stackArray.length;i++) {
            this.addSeedToStorage(stackArray[i]);
        }
    }

    public ItemStack getActiveSeed() {
        ItemStack seed = null;
        if(this.inventorySlots.size()>36) {
            seed = ((SlotSeedStorage) this.inventorySlots.get(36)).getStack().copy();
            seed.stackSize = 1;
        }
        return seed;
    }

    public void resetActiveEntries() {
        this.resetActiveEntries(this.getActiveSeed(), 0);
    }

    public void resetActiveEntries(ItemStack stack, int offset) {
        this.clearActiveEntries();
        this.setActiveEntries(stack, offset);
        if(FMLCommonHandler.instance().getEffectiveSide()==Side.CLIENT) {
            NetworkWrapperAgriCraft.wrapper.sendToServer(new MessageContainerSeedStorage(Minecraft.getMinecraft().thePlayer, stack.getItem(), stack.getItemDamage(), offset));
        }
    }

    public void setActiveEntries(ItemStack stack, int offset) {
        if(stack!=null && stack.getItem()!=null) {
            ItemSeeds seed = (ItemSeeds) stack.getItem();
            int seedMeta = stack.getItemDamage();
            Map<Integer, List<SlotSeedStorage>> map = this.entries.get(seed);
            if(map!=null) {
                List<SlotSeedStorage> activeEntries =map.get(seedMeta);
                if (activeEntries != null) {
                    int xOffset = 82;
                    int yOffset = 8;
                    int stopIndex = Math.min(activeEntries.size(), offset + GuiSeedStorageController.maxNrHorizontalSeeds);
                    for (int i = offset; i < stopIndex; i++) {
                        SlotSeedStorage slot = activeEntries.get(i);
                        slot.set(xOffset + 16 * i, yOffset, this.PLAYER_INVENTORY_SIZE + i);
                        this.inventorySlots.add(slot);
                        this.inventoryItemStacks.add(slot.getStack());
                    }
                }
            }
        }
    }

    public void clearActiveEntries() {
        for(int i=this.inventoryItemStacks.size()-1;i>=this.PLAYER_INVENTORY_SIZE;i--) {
            ((SlotSeedStorage) this.inventorySlots.get(i)).reset();
            this.inventorySlots.remove(i);
            this.inventoryItemStacks.remove(i);
        }
    }

    //checks if the player can drag a stack over this slot to split it
    public boolean canDragIntoSlot(Slot slot) {
        return !(slot instanceof SlotSeedStorage);
    }

    /**
     * Handles shift clicking in the inventory, return the stack that was transferred
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int clickedSlot) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(clickedSlot);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            //try to move item from the container into the player's inventory
            if (slot instanceof SlotSeedStorage) {
                if (!this.mergeItemStack(itemstack1, 0, 36, false)) {
                    return null;
                }
            }
            else {
                //try to move item from the player's inventory into the container
                if(itemstack1.getItem()!=null) {
                    if(itemstack1.getItem() instanceof ItemSeeds && itemstack1.hasTagCompound() && itemstack1.stackTagCompound.hasKey(Names.NBT.analyzed) && itemstack1.stackTagCompound.getBoolean(Names.NBT.analyzed)) {
                        if (this.addSeedToStorage(itemstack1)) {
                            itemstack1.stackSize=0;
                        }
                        else {
                            return null;
                        }
                    }
                }
            }
            if (itemstack1.stackSize == 0) {
                if(slot instanceof SlotSeedStorage) {
                    ((SlotSeedStorage) slot).clearSlot();
                }
                else {
                    slot.putStack(null);
                }
            }
            else {
                slot.onSlotChanged();
            }
            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }
            //it's possible that the slot gets set to null if the complete stack is taken out
            if(slot!=null) {
                slot.onPickupFromSlot(player, itemstack1);
            }
        }
        return itemstack;
    }

    /**
     * Tries to merge an itemstack into a range of slots, return true if the stack was (partly) merged
     */
    @Override
    protected boolean mergeItemStack(ItemStack stack, int startSlot, int endSlot, boolean iterateBackwards) {
        boolean flag = false;
        int k = iterateBackwards?endSlot - 1:startSlot;
        Slot currentSlot;
        ItemStack currentStack;
        //look for identical stacks to merge with
        while (stack.stackSize > 0 && (!iterateBackwards && k < endSlot || iterateBackwards && k >= startSlot)) {
            currentSlot = (Slot)this.inventorySlots.get(k);
            currentStack = currentSlot.getStack();
            if (currentStack != null && currentStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == currentStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, currentStack)) {
                int l = currentStack.stackSize + stack.stackSize;
                //total stacksize is smaller than the limit: merge entire stack into this stack
                if (l <= stack.getMaxStackSize()) {
                    stack.stackSize = 0;
                    currentStack.stackSize = l;
                    currentSlot.onSlotChanged();
                    flag = true;
                }
                //total stacksize exceeds the limit: merge part of the stack into this stack
                else if (currentStack.stackSize < stack.getMaxStackSize()) {
                    stack.stackSize -= stack.getMaxStackSize() - currentStack.stackSize;
                    currentStack.stackSize = stack.getMaxStackSize();
                    currentSlot.onSlotChanged();
                    flag = true;
                }
            }
            k = iterateBackwards?k-1:k+1;
        }
        //couldn't completely merge stack with an existing slot, find the first empty slot to put the rest of the stack in
        if (stack.stackSize > 0) {
            k = iterateBackwards?endSlot-1:startSlot;
            while (!iterateBackwards && k < endSlot || iterateBackwards && k >= startSlot) {
                currentSlot = (Slot)this.inventorySlots.get(k);
                currentStack = currentSlot.getStack();
                if (currentStack == null) {
                    currentSlot.putStack(stack.copy());
                    currentSlot.onSlotChanged();
                    stack.stackSize = 0;
                    flag = true;
                    break;
                }
                k = iterateBackwards?k-1:k+1;
            }
        }
        return flag;
    }

    //par1: slotIndex
    //par2: 0 = LMB, 1 = RMB, 2 = MMB
    //par3: 1 = shift, 3 = MMB
    @Override
    public ItemStack slotClick(int slotIndex, int mouseButton, int shiftHeld, EntityPlayer player) {
        LogHelper.debug("Slot CLicked: par1 = "+slotIndex+", par2 = "+mouseButton+", par3 = "+shiftHeld);
        if(slotIndex>=this.PLAYER_INVENTORY_SIZE) {
            SlotSeedStorage slot = (SlotSeedStorage) this.getSlot(slotIndex);
        }
        return super.slotClick(slotIndex, mouseButton, shiftHeld, player);
    }
/*
    @Override
    public void onContainerClosed(EntityPlayer player) {
        if(FMLCommonHandler.instance().getEffectiveSide()==Side.SERVER) {
            this.te.setControlledInventories(this.entries);
        }
        InventoryPlayer inventoryplayer = player.inventory;
        if (inventoryplayer.getItemStack() != null) {
            player.dropPlayerItemWithRandomChoice(inventoryplayer.getItemStack(), false);
            inventoryplayer.setItemStack(null);
        }
    }
*/
}

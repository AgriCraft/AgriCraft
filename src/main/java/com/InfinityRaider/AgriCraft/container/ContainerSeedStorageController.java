package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorageController;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContainerSeedStorageController extends ContainerSeedStorageDummy {
    //one hash map to quickly find the correct slot based on a stack
    public Map<ItemSeeds, Map<Integer, List<SlotSeedStorage>>> entries;
    //another map based on the slot id
    public HashMap<Integer, SlotSeedStorage> seedSlots;
    public TileEntitySeedStorageController te;
    private int lastSlotId;

    public ContainerSeedStorageController(InventoryPlayer inventory, TileEntitySeedStorageController te) {
        super(inventory, 82, 94, 10, 6);
        this.lastSlotId = this.PLAYER_INVENTORY_SIZE -1;
        this.te = te;
        this.entries = te.getInventoryMap(this);
        this.initSeedSlots();
        this.activeSlotOffsetX = 82;
        this.activeSlotOffsetY = 8;
    }

    @Override
    protected void initSeedSlots() {
        if(this.entries!=null) {
            this.seedSlots = new HashMap<Integer, SlotSeedStorage>();
            for(Map.Entry<ItemSeeds, Map<Integer, List<SlotSeedStorage>>> seedEntry:entries.entrySet()) {
                if(seedEntry!=null && seedEntry.getKey()!=null && seedEntry.getValue()!=null) {
                    for(Map.Entry<Integer, List<SlotSeedStorage>> metaEntry:seedEntry.getValue().entrySet()) {
                        if(metaEntry!=null && metaEntry.getKey()!=null && metaEntry.getValue()!=null) {
                            for(SlotSeedStorage slot:metaEntry.getValue()) {
                                this.seedSlots.put(slot.index, slot);
                                slot.addActiveContainer(this);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected List<SlotSeedStorage> getAllSlots() {
        return new ArrayList<SlotSeedStorage>(seedSlots.values());
    }

    /**
     * tries to add a stack to the storage, return true on success
     */
    @Override
    public boolean addSeedToStorage(ItemStack stack) {
        boolean success = false;
        if(SeedHelper.isAnalyzedSeed(stack)) {
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

    @Override
    public void setActiveEntries(ItemStack stack, int offset) {
        if(stack!=null && stack.getItem()!=null) {
            ItemSeeds seed = (ItemSeeds) stack.getItem();
            int seedMeta = stack.getItemDamage();
            Map<Integer, List<SlotSeedStorage>> map = this.entries.get(seed);
            if(map!=null) {
                List<SlotSeedStorage> activeEntries = map.get(seedMeta);
                if (activeEntries != null) {
                    int xOffset = 82;
                    int yOffset = 8;
                    int stopIndex = Math.min(activeEntries.size(), offset + this.maxNrHorizontalSeeds);
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

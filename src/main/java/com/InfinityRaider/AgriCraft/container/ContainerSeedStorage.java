package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedStorage;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class ContainerSeedStorage extends ContainerAgricraft {
    public HashMap<ItemSeeds, HashMap<Integer, ArrayList<ItemStack>>> entries;
    public TileEntitySeedStorage te;

    public ContainerSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(inventory, 82, 94);
        this.te = te;
        this.initEntries();
    }

    private void initEntries() {
        if(this.te!=null) {
            entries = te.getContents();
        }
        else {
            this.entries = new HashMap<ItemSeeds, HashMap<Integer, ArrayList<ItemStack>>>();
        }
    }

    public void addSeed(ItemStack stack) {
        addSeedToEntries(this.entries, stack);
    }

    public static void addSeedToEntries(HashMap<ItemSeeds, HashMap<Integer, ArrayList<ItemStack>>> entries, ItemStack stack) {
        if(stack!=null && stack.getItem()!=null && stack.getItem() instanceof ItemSeeds) {
            HashMap<Integer, ArrayList<ItemStack>> itemEntry = entries.get(stack.getItem());
            //there is an entry for this item
            if(itemEntry !=null) {
                ArrayList<ItemStack> seedEntries = itemEntry.get(stack.getItemDamage());
                //there is an entry for this item and meta
                if(seedEntries!=null) {
                    boolean seedAdded = false;
                    for(ItemStack seedStack:seedEntries) {
                        //there is an entry with equal NBT
                        if(ItemStack.areItemStackTagsEqual(seedStack, stack)) {
                            //if we don't go trough the method to set/get stacksize we can get stacks over 64 stuffed away
                            seedStack.stackSize = seedStack.stackSize + stack.stackSize;
                            seedAdded = true;
                        }
                    }
                    //there is no entry with equal NBT
                    if(!seedAdded) {
                        seedEntries.add(stack.copy());
                    }
                }
                //there is not yet an entry for this  meta
                else {
                    ArrayList<ItemStack> newList = new ArrayList<ItemStack>();
                    newList.add(stack.copy());
                    itemEntry.put(stack.getItemDamage(), newList);
                }
            }
            //there is no entry for this item yet
            else {
                ArrayList<ItemStack> newList = new ArrayList<ItemStack>();
                newList.add(stack.copy());
                HashMap<Integer, ArrayList<ItemStack>> newEntry = new HashMap<Integer, ArrayList<ItemStack>>();
                newEntry.put(stack.getItemDamage(), newList);
                entries.put((ItemSeeds) stack.getItem(), newEntry);
            }
        }
    }

    @Override
    public void putStackInSlot(int Slot, ItemStack stack) {
        this.addSeed(stack);
    }
}

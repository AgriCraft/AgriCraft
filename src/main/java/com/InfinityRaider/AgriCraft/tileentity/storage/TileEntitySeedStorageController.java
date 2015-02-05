package com.InfinityRaider.AgriCraft.tileentity.storage;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorageController;
import com.InfinityRaider.AgriCraft.container.SlotSeedStorage;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class TileEntitySeedStorageController extends TileEntityCustomWood implements  ISeedStorageController{
    private ArrayList<ISeedStorageControllable> controllables = new ArrayList<ISeedStorageControllable>();
    public boolean isControlling;

    @Override
    public HashMap<ItemSeeds, HashMap<Integer, ArrayList<SlotSeedStorage>>> getInventoryMap(ContainerSeedStorageController container) {
        HashMap<ItemSeeds, HashMap<Integer, ArrayList<SlotSeedStorage>>> map = new HashMap<ItemSeeds, HashMap<Integer, ArrayList<SlotSeedStorage>>>();
        for(ISeedStorageControllable controllable:controllables) {
            if (controllable.hasLockedSeed()) {
                ArrayList<SlotSeedStorage> list = controllable.getInventorySlots(container);
                ItemSeeds seed = (ItemSeeds) controllable.getLockedSeed().getItem();
                int seedMeta = controllable.getLockedSeed().getItemDamage();
                if (map.get(seed) == null) {
                    HashMap<Integer, ArrayList<SlotSeedStorage>> subMap = new HashMap<Integer, ArrayList<SlotSeedStorage>>();
                    subMap.put(seedMeta, list);
                    map.put(seed, subMap);
                } else {
                    HashMap<Integer, ArrayList<SlotSeedStorage>> subMap = map.get(seed);
                    if (subMap == null) {
                        subMap = new HashMap<Integer, ArrayList<SlotSeedStorage>>();
                    }
                    subMap.put(seedMeta, list);
                }
            }
        }
        return map;
    }

    @Override
    public void setControlledInventories(HashMap<ItemSeeds, HashMap<Integer, ArrayList<SlotSeedStorage>>> map) {
        for(ISeedStorageControllable controllable:this.controllables) {
            if (controllable.hasLockedSeed()) {
                ItemSeeds seed = (ItemSeeds) controllable.getLockedSeed().getItem();
                int meta = controllable.getLockedSeed().getItemDamage();
                HashMap<Integer, ArrayList<SlotSeedStorage>> metaMap = map.get(seed);
                if(metaMap!=null) {
                    controllable.setInventory(metaMap.get(meta));
                }
                else {
                    controllable.setInventory(null);
                }
            }
        }
    }

    @Override
    public void addControllable(ISeedStorageControllable controllable) {
        if(!controllable.hasController()) {
            this.controllables.add(controllable);
        }
    }

    @Override
    public boolean isControlling() {
        return this.isControlling;
    }

    @Override
    public ArrayList<int[]> getControlledCoordinates() {
        ArrayList<int[]> coords = new ArrayList<int[]>();
        for(ISeedStorageControllable controllable:this.controllables) {
            coords.add(controllable.getCoords());
        }
        return coords;
    }

    @Override
    public int[] getCoordinates() {
        return new int[] {this.xCoord, this.yCoord, this.zCoord};
    }

    @Override
    public int getControllableID(ISeedStorageControllable controllable) {
        int id=-1;
        for(int i=0;i<this.controllables.size() && id<0;i++) {
            ISeedStorageControllable currentControllable = this.controllables.get(i);
            if(currentControllable==controllable) {
                id = i;
            }
        }
        return id;
    }

    @Override
    public ISeedStorageControllable getControllable(ItemStack stack) {
        return null;
    }


}

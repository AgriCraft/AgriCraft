package com.InfinityRaider.AgriCraft.tileentity.storage;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class TileEntitySeedStorageController extends TileEntityCustomWood implements  ISeedStorageController{
    private ArrayList<ISeedStorageControllable> controllables = new ArrayList<ISeedStorageControllable>();
    public boolean isControlling;

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

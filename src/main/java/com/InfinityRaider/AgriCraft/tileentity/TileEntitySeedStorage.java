package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorage;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.HashMap;

public class TileEntitySeedStorage extends TileEntityCustomWood {
    public ForgeDirection direction;
    private ArrayList<ItemStack> inventory;

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if(this.direction!=null) {
            tag.setByte("direction", (byte) this.direction.ordinal());
        }
        if(this.inventory!=null && this.inventory.size()>0) {
            //TODO: write ArrayList<ItemStack> to NBT
        }
        super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if(tag.hasKey("direction")) {
            this.setDirection(tag.getByte("direction"));
        }
        super.readFromNBT(tag);
    }

    public HashMap<ItemSeeds, HashMap<Integer, ArrayList<ItemStack>>> getContents() {
        HashMap<ItemSeeds, HashMap<Integer, ArrayList<ItemStack>>> contents = new HashMap<ItemSeeds, HashMap<Integer, ArrayList<ItemStack>>>();
        for(ItemStack stack:inventory) {
            ContainerSeedStorage.addSeedToEntries(contents, stack);
        }
        return contents;
    }

    //sets the direction based on an int
    public void setDirection(int direction) {
        this.direction = ForgeDirection.getOrientation(direction);
    }
}

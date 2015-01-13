package com.InfinityRaider.AgriCraft.tileentity;


import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.interfaces.IDebuggable;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class TileEntityValve extends TileEntityChannel implements IDebuggable{
    private boolean powered = false;

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(this.powered) {
            tag.setBoolean(Names.NBT.power, true);
        }
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.powered = tag.hasKey(Names.NBT.power);
        super.readFromNBT(tag);
    }

    @Override
    public void updateEntity() {
        if(!this.worldObj.isRemote) {
            if(!this.powered) {
                super.updateEntity();
            }
        }
    }

    public boolean isPowered() {return powered;}

    public void setPowered(boolean powered) {this.powered = powered;}

    @Override
    public void addDebugInfo(List<String> list) {
        list.add("VALVE");
        list.add("  - State: "+(this.isPowered()?"closed":"open"));
        list.add("  - FluidLevel: " + this.getFluidLevel() + "/" + Constants.mB / 2);
        list.add("  - FluidHeight: " + this.getFluidHeight());
        list.add("this material is: " + Item.itemRegistry.getNameForObject(this.getMaterial().getItem()) + ":" + this.getMaterial().getItemDamage());
    }
}

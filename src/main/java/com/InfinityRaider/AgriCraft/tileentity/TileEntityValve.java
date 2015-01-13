package com.InfinityRaider.AgriCraft.tileentity;


import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.utility.interfaces.IDebuggable;
import net.minecraft.item.Item;

import java.util.List;

public class TileEntityValve extends TileEntityChannel implements IDebuggable{

    private boolean powered = false;

    @Override
    public void updateEntity() {
        // TODO: This is very inefficient, better only check when neighboring blocks change or find another wa
        this.powered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

        if(!this.worldObj.isRemote) {
            if(!this.powered) {
                super.updateEntity();
            }
        }
    }

    public boolean isPowered() {
        return powered;
    }
    @Override
    public void addDebugInfo(List<String> list) {
        list.add("VALVE");
        list.add("  - State: "+(this.isPowered()?"closed":"open"));
        list.add("  - FluidLevel: " + this.getFluidLevel() + "/" + Constants.mB / 2);
        list.add("  - FluidHeight: " + this.getFluidHeight());
        list.add("this material is: " + Item.itemRegistry.getNameForObject(this.getMaterial().getItem()) + ":" + this.getMaterial().getItemDamage());
    }
}

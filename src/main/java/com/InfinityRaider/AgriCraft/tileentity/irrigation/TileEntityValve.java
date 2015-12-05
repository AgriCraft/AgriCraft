package com.InfinityRaider.AgriCraft.tileentity.irrigation;


import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import java.util.List;

public class TileEntityValve extends TileEntityChannel implements IDebuggable{
	
    private boolean powered = false;

    public TileEntityValve() {
        super();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean(Names.NBT.power, powered);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.powered = tag.getBoolean(Names.NBT.power);
        super.readFromNBT(tag);
    }

    @Override
    public void updateEntity() {
        if(!this.worldObj.isRemote) {
            if(!this.powered) {
                super.updateEntity();
            } else {
                updateNeighbours();
            }
        }
    }

    public void updatePowerStatus() {
        boolean wasPowered = powered;
        powered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
        if (powered != wasPowered) {
            markForUpdate();
        }
    }

    public boolean isPowered() {
    	return powered;
    }
    
    public boolean canAccept() {
    	return super.canAccept() && !powered;
    }

    @Override
    public void addDebugInfo(List<String> list) {
        list.add("VALVE");
        list.add("  - State: "+(this.isPowered()?"closed":"open"));
        list.add("  - FluidLevel: " + this.getFluidLevel() + "/" + Constants.BUCKET_mB / 2);
        list.add("  - FluidHeight: " + this.getFluidHeight());
        list.add("  - Material: " + this.getMaterialName() + ":" + this.getMaterialMeta()); //Much Nicer.
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addWailaInformation(List information) {
    	//Required super call
    	super.addWailaInformation(information);
    	//show status
        String status = StatCollector.translateToLocal(powered?"agricraft_tooltip.closed":"agricraft_tooltip.open");
        information.add(StatCollector.translateToLocal("agricraft_tooltip.state")+": "+status);
    }
}

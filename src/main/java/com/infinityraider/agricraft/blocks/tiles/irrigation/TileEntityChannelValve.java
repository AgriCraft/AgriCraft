package com.infinityraider.agricraft.blocks.tiles.irrigation;


import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;

import com.agricraft.agricore.core.AgriCore;

public class TileEntityChannelValve extends TileEntityChannel implements IDebuggable {

    private boolean powered = false;

    @Override
    protected final void writeChannelNBT(NBTTagCompound tag) {
        tag.setBoolean(AgriNBT.POWER, powered);
    }

    //this loads the saved data for the tile entity
    @Override
    protected final void readChannelNBT(NBTTagCompound tag) {
        this.powered = tag.getBoolean(AgriNBT.POWER);
    }

    @Override
    public void update() {
        if(!this.worldObj.isRemote) {
            if(!this.powered) {
                super.update();
            } else {
                updateNeighbours();
            }
        }
    }

    public void updatePowerStatus() {
        final boolean wasPowered = powered;
        powered = worldObj.isBlockIndirectlyGettingPowered(getPos()) > 0;
        if (powered != wasPowered) {
            markForUpdate();
        }
    }

    public boolean isPowered() {
        return powered;
    }

    @Override
    public boolean canAcceptFluid(int y, int amount, boolean partial) {
        return !powered && super.canAcceptFluid(y, amount, partial);
    }

    @Override
    public void addServerDebugInfo(List<String> list) {
        list.add("VALVE");
        list.add("  - State: "+(this.isPowered()?"closed":"open"));
        list.add("  - FluidLevel: " + this.getFluidAmount(0) + "/" + Constants.BUCKET_mB / 2);
        list.add("  - FluidHeight: " + this.getFluidHeight());
        list.add("  - Material: " + this.getMaterialBlock().getRegistryName() + ":" + this.getMaterialMeta()); //Much Nicer.
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addDisplayInfo(List information) {
        //Required super call
        super.addDisplayInfo(information);
        //show status
        String status = AgriCore.getTranslator().translate(powered?"agricraft_tooltip.closed":"agricraft_tooltip.open");
        information.add(AgriCore.getTranslator().translate("agricraft_tooltip.state")+": "+status);
    }
}

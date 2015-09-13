package com.InfinityRaider.AgriCraft.tileentity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * The root class for all AgriCraft TileEntities.
 */
public abstract class TileEntityAgricraft extends TileEntity {
	
    /**
     * The orientation of the block.
     * Defaults to ForgeDirection.UNKNOWN;
     */
    private ForgeDirection orientation = ForgeDirection.UNKNOWN;

    /**
     * Saves the tile entity to an NBTTag.
     * 
     * Overriding subclasses should <em>always</em> make a call to Super().
     */
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(this.orientation!=null) {
            tag.setByte("direction", (byte) this.orientation.ordinal());
        }
    }

    /**
     * Reads the tile entity from an NBTTag.
     * 
     * Overriding subclasses should <em>always</em> make a call to Super().
     */
    @Override
      public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if(tag.hasKey("direction")) {
            this.setOrientation(tag.getByte("direction"));
        }
    }

    /**
     * Determines if the block may be rotated.
     * 
     * @return if the block rotates.
     */
    public abstract boolean isRotatable();

    /**
     * Retrieves the block's orientation as a ForgeDirection.
     * 
     * @return the block's orientation.
     */
    public final ForgeDirection getOrientation() {
        return orientation;
    }

    /**
     * Sets the block's orientation.
     * 
     * @param orientation the new orientation of the block.
     */
    public final void setOrientation(ForgeDirection orientation) {
        if(this.isRotatable() && orientation!=ForgeDirection.UNKNOWN) {
            this.orientation = orientation;
            if(this.worldObj!=null) {
                this.markForUpdate();
            }
        }
    }

    /**
     * Sets the block's orientation from an integer.
     * This is not the recommended method, and is only included for serialization purposes.
     * @param orientation
     */
    private final void setOrientation(int orientation) {
       this.setOrientation(ForgeDirection.getOrientation(orientation));
    }

    @Override
    public Packet getDescriptionPacket(){
        NBTTagCompound nbtTag = new NBTTagCompound();
        writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, this.blockMetadata, nbtTag);
    }

    //read data from packet
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt){
        readFromNBT(pkt.func_148857_g());
        if(this.worldObj.isRemote) {
            //cause a block update on the client to re-render the block
            this.markForUpdate();
        }
    }

    /**
     * Marks the tile entity for an update.
     */
    public final void markForUpdate() {
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    /**
     * Add the waila information to a list.
     * I reccomend a call to the super method where applicable.
     * 
     * @param information the list to add to.
     */
    public abstract void addWailaInformation(List information);
}

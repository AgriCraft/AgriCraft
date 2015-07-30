package com.InfinityRaider.AgriCraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityAgricraft extends TileEntity {
    private ForgeDirection orientation;

    protected TileEntityAgricraft() {
        super();
        this.orientation = ForgeDirection.UNKNOWN;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(this.orientation!=null) {
            tag.setByte("direction", (byte) this.orientation.ordinal());
        }
    }

    @Override
      public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if(tag.hasKey("direction")) {
            this.setOrientation(tag.getByte("direction"));
        }
    }

    public abstract boolean isRotatable();

    public ForgeDirection getOrientation() {
        return orientation;
    }

    public void setOrientation(ForgeDirection orientation) {
        if(this.isRotatable() && orientation!=ForgeDirection.UNKNOWN) {
            this.orientation = orientation;
            if(this.worldObj!=null) {
                this.markForUpdate();
            }
        }
    }

    public void setOrientation(int orientation) {
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

    public void markForUpdate() {
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}

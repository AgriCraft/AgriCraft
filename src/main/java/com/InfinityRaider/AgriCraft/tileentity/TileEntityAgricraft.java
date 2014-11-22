package com.InfinityRaider.AgriCraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityAgricraft extends TileEntity {
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
    }

    //this gets called when the tile entity should get updated on the client
    @Override
    public void markDirty() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);
        super.markDirty();
    }
}

package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.handler.PacketHandler;
import com.InfinityRaider.AgriCraft.network.MessageTileEntityChannel;
import com.InfinityRaider.AgriCraft.reference.Constants;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

public class TileEntityChannel extends TileEntity {
    private int lvl;
    public static final int maxlvl = Constants.mB;

    public int getFluidLevel() {return this.lvl;}

    public void setFluidLevel(int lvl) {this.lvl=lvl;}

    //uses the packet handler to create a packet with the data contained in the tile entity
    @Override
    public Packet getDescriptionPacket() {
        return PacketHandler.instance.getPacketFrom(new MessageTileEntityChannel(this));
    }
}

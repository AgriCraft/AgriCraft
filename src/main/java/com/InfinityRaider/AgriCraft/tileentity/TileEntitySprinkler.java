package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.blocks.BlockWaterChannel;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class TileEntitySprinkler extends TileEntityAgricraft{

    //checks if the sprinkler is connected to an irrigation channel
    public boolean isConnected() {
        return this.worldObj.getBlock(this.xCoord, this.yCoord+1, this.zCoord) instanceof BlockWaterChannel;
    }

    public IIcon getChannelIcon() {
        if(this.isConnected()) {
            TileEntityChannel channel = (TileEntityChannel) this.worldObj.getTileEntity(this.xCoord, this.yCoord+1, this.zCoord);
            return channel.getIcon();
        }
        return Blocks.planks.getIcon(0, 0);
    }

    //tries to sprinkle by taking water from the channel above
    public boolean sprinkle() {
        if(this.isConnected()) {
            TileEntityChannel channel = (TileEntityChannel) this.worldObj.getTileEntity(this.xCoord, this.yCoord+1, this.zCoord);
            if(channel.getFluidLevel()>= ConfigurationHandler.sprinklerConsumption) {
                channel.setFluidLevel(channel.getFluidLevel()- ConfigurationHandler.sprinklerConsumption);
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateEntity() {
        if(!worldObj.isRemote) {
            if(this.sprinkle()) {
                for(int yOffset=-1;yOffset>-4;yOffset--) {
                    for(int xOffset=yOffset;xOffset<=-yOffset;xOffset++) {
                        for(int zOffset=yOffset;zOffset<=-zOffset;zOffset++) {
                            this.irrigate(xOffset, yOffset, zOffset);
                        }
                    }
                }
            }
        }
    }

    private void irrigate(int xOffset, int yOffset, int zOffset) {
        Block block = this.worldObj.getBlock(this.xCoord+xOffset, this.yCoord+yOffset, this.zCoord+zOffset);
        if(block!=null && block instanceof BlockFarmland) {
            BlockFarmland farmland = (BlockFarmland) block;
        }
    }
}

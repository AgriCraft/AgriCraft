package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.blocks.BlockWaterChannel;
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

    @Override
    public void updateEntity() {
        if(!worldObj.isRemote) {
            for(int yOffset=-4;yOffset<0;yOffset++) {
                for(int xOffset=-3;xOffset<=3;xOffset++) {
                    for(int zOffset=-3;zOffset<=-3;zOffset++) {
                        this.irrigate(xOffset, yOffset, zOffset);
                    }
                }
            }

        }
    }

    private void irrigate(int xOffset, int yOffset, int zOffset) {
        Block block = this.worldObj.getBlock(this.xCoord+xOffset, this.yCoord+yOffset, this.zCoord+zOffset);
        if(block!=null && block instanceof BlockFarmland && this.worldObj.getBlockMetadata(this.xCoord+xOffset, this.yCoord+yOffset, this.zCoord+zOffset)<7) {
            if(this.isConnected()) {
                TileEntityChannel channel = (TileEntityChannel) this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord);
                int amount = 7 - this.worldObj.getBlockMetadata(this.xCoord+xOffset, this.yCoord+yOffset, this.zCoord+zOffset);
                if (channel.getFluidLevel() >= amount) {
                    channel.setFluidLevel(channel.getFluidLevel() - amount);
                    this.worldObj.setBlockMetadataWithNotify(this.xCoord+xOffset, this.yCoord+yOffset, this.zCoord+zOffset, 7, 2);
                }
            }
        }
    }

}

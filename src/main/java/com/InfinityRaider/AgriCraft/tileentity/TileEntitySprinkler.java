package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.blocks.BlockWaterChannel;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFarmland;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import java.util.Random;

public class TileEntitySprinkler extends TileEntityAgricraft{
    private int counter = 0;

    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(this.counter>0) {
            tag.setInteger(Names.level, this.counter);
        }
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if(tag.hasKey(Names.level)) {
            this.counter = tag.getInteger(Names.level);
        }
        else {
            this.counter=0;
        }
    }

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
            for(int yOffset=1;yOffset<5;yOffset++) {
                for(int xOffset=-3;xOffset<=3;xOffset++) {
                    for(int zOffset=-3;zOffset<=3;zOffset++) {
                        if(this.sprinkle())
                        this.irrigate(this.xCoord+xOffset, this.yCoord-yOffset, this.zCoord+zOffset);
                    }
                }
            }

        }
    }

    private boolean sprinkle() {
        if(this.isConnected()) {
            TileEntityChannel channel = (TileEntityChannel) this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord);
            if(channel.getFluidLevel()>0) {
                counter = (counter+1)%10;
                if(this.counter==0) {
                    channel.setFluidLevel(channel.getFluidLevel() - 1);
                }
                return true;
            }
        }
        return false;
    }

    private void irrigate(int x, int y, int z) {
        Block block = this.worldObj.getBlock(x, y, z);
        if(block!=null) {
            if(block instanceof BlockFarmland && this.worldObj.getBlockMetadata(x, y, z) < 7) {
                //irrigate farmland
                this.worldObj.setBlockMetadataWithNotify(x, y, z, 7, 2);
            } else if(block instanceof BlockBush) {
                //10% chance to force growth tick on plant
                if(Math.random()<0.1) {
                    block.updateTick(this.worldObj, x, y, z, new Random());
                }
            }
        }

    }

}

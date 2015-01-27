package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.blocks.BlockWaterChannel;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.particles.LiquidSprayFX;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFarmland;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;

import java.util.Random;

public class TileEntitySprinkler extends TileEntityAgricraft{

    private static final double GROWTH_CHANCE = 0.2;

    private int counter = 0;
    public float angle = 0.0F;
    private boolean isSprinkled = false;

    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(this.counter>0) {
            tag.setInteger(Names.NBT.level, this.counter);
        }
        tag.setBoolean(Names.NBT.isSprinkled, isSprinkled);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if(tag.hasKey(Names.NBT.level)) {
            this.counter = tag.getInteger(Names.NBT.level);
        }
        else {
            this.counter=0;
        }
        
        if(tag.hasKey(Names.NBT.isSprinkled)) {
             this.isSprinkled = tag.getBoolean(Names.NBT.isSprinkled);
         }
         else {
             this.isSprinkled = false;
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
        if (!worldObj.isRemote) {
            if (this.sprinkle()) {
                counter = ++counter % 60;
                drainWaterFromChannel();

                for (int yOffset = 1; yOffset < 5; yOffset++) {
                    for (int xOffset = -3; xOffset <= 3; xOffset++) {
                        for (int zOffset = -3; zOffset <= 3; zOffset++) {
                            this.irrigate(this.xCoord + xOffset, this.yCoord - yOffset, this.zCoord + zOffset);
                        }
                    }
                }
            }
        }
        else {
            if(this.isSprinkled) {
            	this.renderLiquidSpray();
            }
        }
    }

    public boolean canSprinkle() {
        return this.isConnected() && ((TileEntityChannel) this.worldObj.getTileEntity(this.xCoord, this.yCoord+1, this.zCoord)).getFluidLevel() > 0;
    }

    private boolean sprinkle() {
    	boolean newState  = this.canSprinkle();
        if(newState != this.isSprinkled) {
        	this.isSprinkled = newState;
        	this.markDirtyAndMarkForUpdate();
        }
        return this.isSprinkled;
    }

    /** Depending on the block type either irrigates farmland or forces plant growth (based on chance) */
    private void irrigate(int x, int y, int z) {
        Block block = this.worldObj.getBlock(x, y, z);
        if (block != null) {
            if (block instanceof BlockFarmland && this.worldObj.getBlockMetadata(x, y, z) < 7) {
                // irrigate farmland
                this.worldObj.setBlockMetadataWithNotify(x, y, z, 7, 2);
            } else if (block instanceof BlockBush) {
                // 20% chance to force growth tick on plant, every 60 ticks
                if (counter == 0 && Constants.rand.nextDouble() <= GROWTH_CHANCE) {
                    block.updateTick(this.worldObj, x, y, z, Constants.rand);
                }
            }
        }
    }

    /** Called once per tick, drains water out of the WaterChannel one y-level above */
    private void drainWaterFromChannel() {
        if (counter % 10 == 0) {
            TileEntityChannel channel = (TileEntityChannel) this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord);
            channel.drainFluid(ConfigurationHandler.sprinklerRatePerHalfSecond);
        }
    }

    @SideOnly(Side.CLIENT)
    private void renderLiquidSpray() {
        this.angle = (this.angle+5F)%360;
        for(int i=0;i<4;i++) {
            float alpha = (this.angle+90*i)*((float)Math.PI)/180;
            double xOffset = (4*Constants.unit)*Math.cos(alpha);
            double zOffset = (4*Constants.unit)*Math.sin(alpha);
            float radius = 0.3F;
            for(int j=0;j<=4;j++) {
                float beta = -j*((float)Math.PI)/(8.0F);
                Vec3 vector = Vec3.createVectorHelper(radius*Math.cos(alpha), radius*Math.sin(beta), radius*Math.sin(alpha));
                this.spawnLiquidSpray(xOffset*(4-j)/4, zOffset*(4-j)/4, vector);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnLiquidSpray(double xOffset, double zOffset, Vec3 vector) {
        LiquidSprayFX liquidSpray = new LiquidSprayFX(this.worldObj, this.xCoord+0.5F+xOffset, this.yCoord+5* Constants.unit, this.zCoord+0.5F+zOffset, 0.3F, 0.7F, vector);
        Minecraft.getMinecraft().effectRenderer.addEffect(liquidSpray);
    }

}

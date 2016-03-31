package com.infinityraider.agricraft.tileentity.irrigation;

import com.infinityraider.agricraft.blocks.BlockWaterChannel;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.renderers.particles.LiquidSprayFX;
import com.infinityraider.agricraft.tileentity.TileEntityBase;
import com.infinityraider.agricraft.utility.icon.BaseIcons;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntitySprinkler extends TileEntityBase implements ITickable {
	
    private int counter = 0;
    public float angle = 0.0F;
    private boolean isSprinkled = false;

    //this saves the data on the tile entity
    @Override
    public void writeTileNBT(NBTTagCompound tag) {
        if(this.counter>0) {
            tag.setInteger(AgriCraftNBT.LEVEL, this.counter);
        }
        tag.setBoolean(AgriCraftNBT.IS_SPRINKLED, isSprinkled);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readTileNBT(NBTTagCompound tag) {
        if(tag.hasKey(AgriCraftNBT.LEVEL)) {
            this.counter = tag.getInteger(AgriCraftNBT.LEVEL);
        }
        else {
            this.counter=0;
        }
        
        if(tag.hasKey(AgriCraftNBT.IS_SPRINKLED)) {
             this.isSprinkled = tag.getBoolean(AgriCraftNBT.IS_SPRINKLED);
         }
         else {
             this.isSprinkled = false;
         }
    }

    //checks if the sprinkler is CONNECTED to an irrigation channel
    public boolean isConnected() {
        return this.worldObj!=null && this.worldObj.getBlockState(getPos().add(0, 1, 0)).getBlock() instanceof BlockWaterChannel;
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            if (this.sprinkle()) {
                counter = ++counter % AgriCraftConfig.sprinklerGrowthIntervalTicks;
                drainWaterFromChannel();

                for (int yOffset = 1; yOffset < 6; yOffset++) {
                    for (int xOffset = -3; xOffset <= 3; xOffset++) {
                        for (int zOffset = -3; zOffset <= 3; zOffset++) {
                            this.irrigate(this.xCoord() + xOffset, this.yCoord() - yOffset, this.zCoord() + zOffset, yOffset>=5);
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
        return this.isConnected() && ((TileEntityChannel) this.worldObj.getTileEntity(getPos().add(0, 1, 0))).getFluidLevel()
                > AgriCraftConfig.sprinklerRatePerHalfSecond;
    }

    private boolean sprinkle() {
    	boolean newState  = this.canSprinkle();
        if(newState != this.isSprinkled) {
        	this.isSprinkled = newState;
        	this.markForUpdate();
        }
        return this.isSprinkled;
    }

    /** Depending on the block type either irrigates farmland or forces plant GROWTH (based on chance) */
    private void irrigate(int x, int y, int z, boolean farmlandOnly) {
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = this.getWorld().getBlockState(pos);
        Block block = state.getBlock();
        if (block != null) {
            if (block instanceof BlockFarmland && block.getMetaFromState(state) < 7) {
                // irrigate farmland
                int flag = counter==0?2:6;
                worldObj.setBlockState(pos, block.getStateFromMeta(7), flag);
            } else if (((block instanceof IPlantable) || (block instanceof IGrowable)) && !farmlandOnly) {
                // X1 chance to force GROWTH tick on plant every Y1 ticks
                if (counter == 0 && worldObj.rand.nextDouble() <= AgriCraftConfig.sprinklerGrowthChancePercent) {
                    block.updateTick(this.getWorld(), pos, state, worldObj.rand);
                }
            }
        }
    }

    /** Called once per tick, drains water out of the WaterChannel one Y1-LEVEL above */
    private void drainWaterFromChannel() {
        if (counter % 10 == 0) {
            TileEntityChannel channel = (TileEntityChannel) this.worldObj.getTileEntity(getPos().add(0, 1, 0));
            channel.pullFluid(AgriCraftConfig.sprinklerRatePerHalfSecond);
        }
    }

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getChannelIcon() {
		
        if(this.getWorld() == null) {
            return BaseIcons.OAK_PLANKS.getIcon();
        }
		
		TileEntity te = getWorld().getTileEntity(getPos().add(0, 1, 0));
		
		if (te instanceof TileEntityChannel) {
            //TODO: get channel icon
			return null;
		}
		
		return BaseIcons.OAK_PLANKS.getIcon();
		
    }

    @SideOnly(Side.CLIENT)
    private void renderLiquidSpray() {
        if(AgriCraftConfig.disableParticles) {
            return;
        }
        this.angle = (this.angle+5F)%360;
        int particleSetting = Minecraft.getMinecraft().gameSettings.particleSetting;    //0 = all, 1 = decreased; 2 = minimal;
        counter = (counter+1)%(particleSetting+1);
        if(counter==0) {
            for (int i = 0; i < 4; i++) {
                float alpha = -(this.angle + 90 * i) * ((float) Math.PI) / 180;
                double xOffset = (4 * Constants.UNIT) * Math.cos(alpha);
                double zOffset = (4 * Constants.UNIT) * Math.sin(alpha);
                float radius = 0.3F;
                for (int j = 0; j <= 4; j++) {
                    float beta = -j * ((float) Math.PI) / (8.0F);
                    Vec3d vector = new Vec3d(radius * Math.cos(alpha), radius * Math.sin(beta), radius * Math.sin(alpha));
                    this.spawnLiquidSpray(xOffset * (4 - j) / 4, zOffset * (4 - j) / 4, vector);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnLiquidSpray(double xOffset, double zOffset, Vec3d vector) {
        LiquidSprayFX liquidSpray = new LiquidSprayFX(this.worldObj, this.xCoord()+0.5F+xOffset, this.yCoord()+8*Constants.UNIT, this.zCoord()+0.5F+zOffset, 0.3F, 0.7F, vector);
        Minecraft.getMinecraft().effectRenderer.addEffect(liquidSpray);
    }

    @Override
    public boolean isRotatable() {
        return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addWailaInformation(List information) {
    	//Nothing to add here. Move along!
    }
}

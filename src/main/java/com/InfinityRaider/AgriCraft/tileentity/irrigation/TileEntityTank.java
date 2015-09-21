package com.InfinityRaider.AgriCraft.tileentity.irrigation;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.network.MessageSyncFluidLevel;
import com.InfinityRaider.AgriCraft.network.NetworkWrapperAgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityMultiBlock;
import com.InfinityRaider.AgriCraft.utility.LogHelper;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.List;

public class TileEntityTank extends TileEntityMultiBlock implements IFluidHandler, IIrrigationComponent, IDebuggable {

    protected static final int DISCRETE_MAX = 32;
    /**
     * Don't call this directly, use getFluidLevel() and setFluidLevel(int amount) because only the tank at position (0, 0, 0)
     * in the multiblock holds the liquid
     */
    private int fluidLevel=0;
    private int lastDiscreteLvl=0;

    public TileEntityTank() {
        super();
    }
    
    //OVERRIDES
    //---------
    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(this.fluidLevel>0) {
            tag.setInteger(Names.NBT.level, this.fluidLevel);
        }
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if(tag.hasKey(Names.NBT.level)) {
            if(getXPosition()==0 && getYPosition()==0 && getZPosition()==0) {
                this.fluidLevel = tag.getInteger(Names.NBT.level);
            }
        }
        else {
            this.fluidLevel=0;
        }
    }

    //updates the tile entity every tick
    @Override
    public void updateEntity() {
    	super.updateEntity();
        if(!this.worldObj.isRemote) {
            if(this.worldObj.canBlockSeeTheSky(this.xCoord, this.yCoord, this.zCoord) && this.worldObj.isRaining()) {
                if(getYPosition()+1==getYSize()) {
                    BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(this.xCoord, this.zCoord);
                    if(biome!=BiomeGenBase.desert && biome!=BiomeGenBase.desertHills) {
                       this.setFluidLevel(this.getFluidLevel() + 1);
                    }
                }
            }
            if(ConfigurationHandler.fillFromFlowingWater && (this.worldObj.getBlock(this.xCoord, this.yCoord+1, this.zCoord)==Blocks.water || this.worldObj.getBlock(this.xCoord, this.yCoord+1, this.zCoord)==Blocks.flowing_water)) {
                this.setFluidLevel(this.getFluidLevel()+5);
            }
        }
    }

    public void syncFluidLevel() {
        int newDiscreteLvl = getDiscreteFluidLevel();
        if(newDiscreteLvl != lastDiscreteLvl) {
            lastDiscreteLvl = newDiscreteLvl;
            IMessage msg = new MessageSyncFluidLevel(this.fluidLevel, this.xCoord, this.yCoord, this.zCoord);
            NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 64);
            NetworkWrapperAgriCraft.wrapper.sendToAllAround(msg, point);
        }
    }

    public boolean isConnectedToChannel(char axis, int direction) {
        if(this.worldObj == null) {
            return false;
        }
        boolean x = axis=='x';
        TileEntity tile = this.getWorldObj().getTileEntity(this.xCoord+(x?direction:0), this.yCoord, this.zCoord+(x?0:direction));
        if(tile == null) {
            return false;
        }
        if(tile instanceof TileEntityChannel) {
            return ((TileEntityChannel) tile).isSameMaterial(this);
        } else {
            return false;
        }
    }
    
    @Override
    public boolean canJoinMultiBlock(TileEntity tileEntity) {
    	return tileEntity instanceof TileEntityTank && ((TileEntityTank)tileEntity).isSameMaterial(this);
    }

    //multiblockify
    @Override
    public boolean checkForMultiBlock() {
        if(super.checkForMultiBlock() && !this.worldObj.isRemote) {
            //find dimensions
            int xPos = this.findArrayXPosition();
            int yPos = this.findArrayYPosition();
            int zPos = this.findArrayZPosition();
            int xSizeNew = this.findArrayXSize();
            int ySizeNew = this.findArrayYSize();
            int zSizeNew = this.findArrayZSize();
            if (xSizeNew == 1 && ySizeNew == 1 && zSizeNew == 1) {
                return false;
            }
            //iterate trough the x-, y- and z-directions if all blocks are tanks
            for (int x = this.xCoord - xPos; x < this.xCoord - xPos + xSizeNew; x++) {
                for (int y = this.yCoord - yPos; y < this.yCoord - yPos + ySizeNew; y++) {
                    for (int z = this.zCoord - zPos; z < this.zCoord - zPos + zSizeNew; z++) {
                        if (this.isMultiBlockPartner(this.worldObj.getTileEntity(x, y, z))) {
                            TileEntityTank tank = (TileEntityTank) this.worldObj.getTileEntity(x, y, z);
                            int[] tankSize = tank.findArrayDimensions();
                            if(!(xSizeNew==tankSize[0] && ySizeNew==tankSize[1] && zSizeNew==tankSize[2])) {
                                return false;
                            }
                        }
                        else {
                            return false;
                        }
                    }
                }
            }
            //calculate the total fluid level
            int lvl = 0;
            for (int x = this.xCoord - xPos; x < this.xCoord - xPos + xSizeNew; x++) {
                for (int y = this.yCoord - yPos; y < this.yCoord - yPos + ySizeNew; y++) {
                    for (int z = this.zCoord - zPos; z < this.zCoord - zPos + zSizeNew; z++) {
                        TileEntityTank tank = ((TileEntityTank) this.worldObj.getTileEntity(x, y, z));
                        lvl = tank.fluidLevel+lvl;
                        tank.fluidLevel = 0;
                    }
                }
            }
            this.setFluidLevel(lvl);
            return true;
        }
        return false;
    }

    //breaks up the multiblock and divides the fluid among the tanks
    @Override
    public void breakMultiBlock(int lvl) {
        super.breakMultiBlock(lvl);
    	//The loss is a new feature of breaking tanks.
        int amount = this.getFluidLevel() / this.getCapacity();
        LogHelper.debug("Amount to distribute per tank: " + amount);
        TileEntity te;
        for(int x=0;x<getXSize();x++) {
            for(int y=0;y<getYSize();y++) {
                for(int z=0;z<getZSize();z++) {
                	te = this.worldObj.getTileEntity(this.xCoord - getXPosition() + x, this.yCoord - getYPosition() + y, this.zCoord - getZPosition() + z);
                    if (te instanceof TileEntityTank) {
                    	((TileEntityTank) te).fluidLevel = amount;
                    } else {
                    	LogHelper.debug("Multiblock hole issue detected.");
                    }
                    
                }
            }
        }
    }

    //TANK METHODS
    //------------
    public FluidStack getContents() {
        return new FluidStack(FluidRegistry.WATER, this.getFluidLevel());
    }

    public int getFluidLevel() {
        TileEntity te = worldObj.getTileEntity(xCoord - getXPosition(), yCoord - getYPosition(), zCoord - getZPosition());
        if(te==null || !(te instanceof TileEntityTank)) {
            return this.fluidLevel;
        }
        TileEntityTank tank = (TileEntityTank) te;
        return tank.fluidLevel;
    }

    /** Maps the current fluid level into the interval [0, 32] */
    public int getDiscreteFluidLevel() {
        float discreteFactor = (float) DISCRETE_MAX / (float) getSingleCapacity();
        int discreteFluidLevel = Math.round(discreteFactor * getFluidLevel());
        if (discreteFluidLevel == 0 && getFluidLevel() > 0) {
            discreteFluidLevel = 1;
        }
        return discreteFluidLevel;
    }

    public int getScaledDiscreteFluidLevel() {
        if(this.worldObj == null) {
            return 0;
        }
        float scaleFactor = (float) getSingleCapacity() / (float) DISCRETE_MAX;
        int discreteFluidLevel = getDiscreteFluidLevel();
        return Math.round(scaleFactor * discreteFluidLevel);
    }

    public float getScaledDiscreteFluidY() {
        return getFluidY(getScaledDiscreteFluidLevel());
    }

    public float getFluidY() {
        return this.getFluidY(this.getFluidLevel());
    }
    
    @Override
    public float getFluidHeight() {
    	return this.getFluidY(this.getFluidLevel());
    }

    public float getFluidY(int volume) {
        return getFluidY(volume, getYSize(), getTotalCapacity());
    }

    public static float getFluidY(int volume, int ySize, int capacity) {
        int totalHeight = 16*ySize-2;     //total height in 1/16th's of a block
        return totalHeight*((float) volume)/((float) capacity)+2;
    }
    
    /*@Override
    public int pushFluid(int amount) {
        if(!worldObj.isRemote) {
        	int room = this.getTotalCapacity() - this.getFluidLevel();
        	if (room >= amount) {
        		this.setFluidLevel(this.getFluidLevel()+amount);
        		amount = 0;
        	} else if (room > 0) {
        		this.setFluidLevel(this.getTotalCapacity());
        		amount = amount - room;
        	}
        }
        return amount;
    }
    
    @Override
    public int pullFluid(int amount) {
    	if(!worldObj.isRemote) {
        	if (amount <= this.getFluidLevel()) {
        		this.setFluidLevel(this.getFluidLevel()-amount);
        	} else {
        		amount = this.getFluidLevel();
        		this.setFluidLevel(0);
        	}
        }
        return amount;
    }*/

    @Override
    public void setFluidLevel(int lvl) {
        if(lvl!=this.getFluidLevel()) {
            lvl = lvl > this.getTotalCapacity() ? this.getTotalCapacity() : lvl;
            if(!(worldObj.getTileEntity(xCoord - getXPosition(), yCoord - getYPosition(), zCoord - getZPosition()) instanceof TileEntityTank)){
                return; 
            }
            TileEntityTank tank = (TileEntityTank) worldObj.getTileEntity(xCoord - getXPosition(), yCoord - getYPosition(), zCoord - getZPosition());
            if(tank != null) {
                tank.fluidLevel = lvl;
                if(!tank.worldObj.isRemote) {
                    tank.syncFluidLevel();
                }
            }
        }
    }

    @Override
    public boolean canConnectTo(IIrrigationComponent component) {
        return false;
    }

    public int getSingleCapacity() {
        return (this.getBlockMetadata()+1)*8*Constants.BUCKET_mB;
    }

    public int getTotalCapacity() {
        return this.getSingleCapacity()*this.getConnectedBlocks();
    }
    
    @Override
    public int getCapacity() {
    	return getTotalCapacity();
    }

    public boolean isFull() {
        return this.getFluidLevel()==this.getTotalCapacity();
    }

    public boolean isEmpty() {
        return this.getFluidLevel()==0;
    }

    //try to fill the tank
    public int fill(ForgeDirection from, int amount, boolean doFill) {
        return this.fill(from, new FluidStack(FluidRegistry.WATER, amount), doFill);
    }

    //try to fill the tank
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if(resource==null || !this.canFill(from, resource.getFluid())) {
            return 0;
        }
        int filled = Math.min(resource.amount, this.getTotalCapacity() - this.getFluidLevel());
        if(doFill && !worldObj.isRemote) {
            this.setFluidLevel(this.getFluidLevel()+filled);
        }
        return filled;
    }

    //try to drain from the tank
    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if(resource==null || !this.canDrain(from, resource.getFluid())) {
           return null;
        }
        int drained = Math.min(resource.amount, this.getFluidLevel());
        if(doDrain && !worldObj.isRemote) {
            this.setFluidLevel(this.getFluidLevel()-drained);
        }
        return new FluidStack(FluidRegistry.WATER, drained);
    }

    //try to drain from the tank
    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return this.drain(from, new FluidStack(FluidRegistry.WATER, maxDrain), doDrain);
    }

    //check if the tank can be filled
    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid==FluidRegistry.WATER && !this.isFull();
    }

    //check if the tank can be drained
    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return fluid==FluidRegistry.WATER && !this.isEmpty();
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        FluidTankInfo[] info = new FluidTankInfo[1];
        info[0] = new FluidTankInfo(this.getContents(), this.getTotalCapacity());
        return info;
    }

    //debug info
    @Override
    public void addDebugInfo(List<String> list) {
    	super.addDebugInfo(list);
        list.add("TANK:");
        list.add("Tank: " + (this.isWood() ? "wood" : "iron") + " (single capacity: " + this.getSingleCapacity() + ")");
        list.add("  - FluidLevel: " + this.getFluidLevel() + "/" + this.getTotalCapacity());
        list.add("  - FluidHeight: " + this.getFluidY());
        list.add("Water level is on layer " + (int) Math.floor(((float) this.getFluidLevel() - 0.1F) / ((float) (this.getSingleCapacity() * this.getXSize() * this.getZSize()))) + ".");
        list.add("this clicked is on  layer " + this.getYPosition() + ".");
        list.add("Water height is " + this.getFluidY());
    }
    
    @Override
    public void addWailaInformation(List information) {
    	super.addWailaInformation(information);
    	information.add(StatCollector.translateToLocal("agricraft_tooltip.waterLevel")+": "+this.getFluidLevel()+"/"+this.getTotalCapacity());
    }
}

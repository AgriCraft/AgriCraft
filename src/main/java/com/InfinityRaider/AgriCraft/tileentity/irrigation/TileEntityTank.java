package com.InfinityRaider.AgriCraft.tileentity.irrigation;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.network.MessageSyncFluidLevel;
import com.InfinityRaider.AgriCraft.network.NetworkWrapperAgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;

import com.InfinityRaider.AgriCraft.utility.multiblock.IMultiBlockComponent;
import com.InfinityRaider.AgriCraft.utility.multiblock.MultiBlockLogicTank;
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

public class TileEntityTank extends TileEntityCustomWood implements IFluidHandler, IIrrigationComponent, IMultiBlockComponent<MultiBlockLogicTank>, IDebuggable {
	public static final int SYNC_DELTA = Constants.HALF_BUCKET_mB;

    public static final int DISCRETE_MAX = Constants.WHOLE;
    
    public static final int SINGLE_CAPACITY = 8 * Constants.BUCKET_mB;
    
    /**
     * Don't call this directly, use getFluidLevel() and setFluidLevel(int amount) because only the tank at position (0, 0, 0)
     * in the multiblock holds the liquid.
     * <p>
     * Represents the amount of fluid the tank is holding.
     * </p>
     */
    private MultiBlockLogicTank multiBlockLogic;
    private int fluidLevel=0;
    private int lastDiscreteLvl=0;
    
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(getMultiBLockLogic().isRootComponent(this)) {
            tag.setBoolean(Names.NBT.tag, true);
            multiBlockLogic.writeToNBT(tag);
            if (this.fluidLevel > 0) {
                tag.setInteger(Names.NBT.level, this.fluidLevel);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        //TODO: make it read NBT correctly from old version
        super.readFromNBT(tag);
        if(tag.hasKey(Names.NBT.tag)) {
            multiBlockLogic = new MultiBlockLogicTank(this);
            multiBlockLogic.readFromNBT(tag);
        }
        if(getMultiBLockLogic().isRootComponent(this) && tag.hasKey(Names.NBT.level)) {
        	this.fluidLevel = tag.getInteger(Names.NBT.level);
        }
    }

    //updates the tile entity every tick
    @Override
    public void updateEntity() {
    	super.updateEntity();
        if(!this.worldObj.isRemote) {
            if(this.worldObj.canBlockSeeTheSky(this.xCoord, this.yCoord, this.zCoord) && this.worldObj.isRaining()) {
                if(!this.hasNeighbour(ForgeDirection.UP)) {
                    BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(this.xCoord, this.zCoord);
                    if(biome!=BiomeGenBase.desert && biome!=BiomeGenBase.desertHills) {
                       this.setFluidLevel(this.getFluidLevel() + 1);
                    }
                }
            }
            if(ConfigurationHandler.fillFromFlowingWater && (this.worldObj.getBlock(this.xCoord, this.yCoord+1, this.zCoord)==Blocks.water || this.worldObj.getBlock(this.xCoord, this.yCoord+1, this.zCoord)==Blocks.flowing_water)) {
                this.setFluidLevel(this.getFluidLevel() + 5);
            }
        }
    }

    @Override
	public void syncFluidLevel() {
        int newDiscreteLvl = getDiscreteFluidLevel();
        if(newDiscreteLvl != lastDiscreteLvl) {
            lastDiscreteLvl = newDiscreteLvl;
            IMessage msg = new MessageSyncFluidLevel(this.fluidLevel, this.xCoord, this.yCoord, this.zCoord);
            NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 64);
            NetworkWrapperAgriCraft.wrapper.sendToAllAround(msg, point);
        }
    }

    public boolean isConnectedToChannel(ForgeDirection direction) {
        if((this.worldObj != null) && (direction != ForgeDirection.UNKNOWN) && (direction.offsetY == 0)) {
        	TileEntity tile = this.getWorldObj().getTileEntity(this.xCoord+direction.offsetX, this.yCoord+direction.offsetY, this.zCoord+direction.offsetZ);
            if(tile instanceof TileEntityChannel) {
                return ((TileEntityChannel) tile).isSameMaterial(this);
            }
        }
        return false;
    }

    //TANK METHODS
    //------------
    public FluidStack getContents() {
        return new FluidStack(FluidRegistry.WATER, this.getFluidLevel());
    }

	@Override
	public int getFluidLevel() {
        return getMultiBLockLogic().isRootComponent(this)?fluidLevel:getMultiBLockLogic().getRootComponent().getFluidLevel();
	}

    public int getYPosition() {
        TileEntityTank tank = getMultiBLockLogic().getRootComponent();
        return this.yCoord - tank.yCoord;
    }

    /**
     * Maps the current fluid level into the interval [0, {@value #DISCRETE_MAX}]
     */
    public int getDiscreteFluidLevel() {
        float discreteFactor = DISCRETE_MAX / ((float) SINGLE_CAPACITY * getMultiBLockLogic().sizeX() * getMultiBLockLogic().sizeZ());
        int discreteFluidLevel = Math.round(discreteFactor * getFluidLevel());
        // This is so the fluid shows up over the bottom...
        if (discreteFluidLevel < 2 && getFluidLevel() > 0) {
            discreteFluidLevel = 2;
        }
        return discreteFluidLevel;
    }
    
    @Override
	public float getFluidHeight() {
    	return this.getDiscreteFluidLevel();
    }
    
    @Override
	public int pushFluid(int amount) {
        if(!worldObj.isRemote && this.canAccept() && amount >= 0) {
        	int room = this.getCapacity() - this.getFluidLevel();
        	if (room >= amount) {
        		this.setFluidLevel(this.getFluidLevel()+amount);
        		amount = 0;
        	} else if (room > 0) {
        		this.setFluidLevel(this.getCapacity());
        		amount = amount - room;
        	}
        }
        return amount;
    }
    
    @Override
	public int pullFluid(int amount) {
    	if(!worldObj.isRemote && this.canProvide() && amount >= 0) {
        	if (amount <= this.getFluidLevel()) {
        		this.setFluidLevel(this.getFluidLevel() - amount);
        	} else {
        		amount = this.getFluidLevel();
        		this.setFluidLevel(0);
        	}
        }
        return amount;
    }

    @Override
	public void setFluidLevel(int lvl) {
        if(lvl!=this.getFluidLevel()) {
            if(!getMultiBLockLogic().isRootComponent(this)) {
                getMultiBLockLogic().getRootComponent().setFluidLevel(lvl);
            } else {
                lvl = lvl > this.getCapacity() ? this.getCapacity() : lvl;
                this.fluidLevel = lvl;
                if (!this.worldObj.isRemote) {
                    this.syncFluidLevel();
                }

            }
        }
    }

    @Override
    public boolean canConnectTo(IIrrigationComponent component) {
        return false;
    }
    
    @Override
    public int getCapacity() {
    	return SINGLE_CAPACITY*getMultiBLockLogic().getMultiBlockCount();
    }

    @Override
	public boolean canAccept() {
        return this.getFluidLevel() < this.getCapacity();
    }
    
    @Override
	public boolean canProvide() {
    	return this.getFluidLevel() > 0;
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
        int filled = Math.min(resource.amount, this.getCapacity() - this.getFluidLevel());
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
        return fluid==FluidRegistry.WATER && this.canAccept();
    }

    //check if the tank can be drained
    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return fluid==FluidRegistry.WATER && this.canProvide();
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        FluidTankInfo[] info = new FluidTankInfo[1];
        info[0] = new FluidTankInfo(this.getContents(), this.getCapacity());
        return info;
    }

    //debug info
    @Override
    public void addDebugInfo(List<String> list) {
    	super.addDebugInfo(list);
        list.add("TANK:");
        list.add("Tank: (single capacity: " + SINGLE_CAPACITY + ")");
        list.add("  - FluidLevel: " + this.getFluidLevel() + "/" + this.getCapacity());
        list.add("  - Water level is on layer " + (int) Math.floor((this.getFluidLevel() - 0.1F) / (this.getCapacity() * getMultiBLockLogic().sizeX() * getMultiBLockLogic().sizeZ())) + ".");
        list.add("  - Water height is " + this.getFluidHeight());
        StringBuilder neighbours = new StringBuilder();
        for(ForgeDirection dir:ForgeDirection.values()) {
            if(this.hasNeighbour(dir)) {
                neighbours.append(dir.name()).append(", ");
            }
        }
        list.add("  - Neighbours: " + neighbours.toString());
        list.add("  - MultiBlock Size: "+ getMultiBLockLogic().sizeX()+"x"+getMultiBLockLogic().sizeY()+"x"+getMultiBLockLogic().sizeZ());
    }
    
    @Override
    public void addWailaInformation(List information) {
    	super.addWailaInformation(information);
    	information.add(StatCollector.translateToLocal("agricraft_tooltip.waterLevel")+": "+this.getFluidLevel()+"/"+this.getCapacity());
    }

    @Override
    public MultiBlockLogicTank getMultiBLockLogic() {
        if(this.multiBlockLogic == null) {
            this.multiBlockLogic = new MultiBlockLogicTank(this);
            this.multiBlockLogic.checkToUpdateExistingMultiBlock();
        }
        return multiBlockLogic;
    }

    @Override
    public void setMultiBlockLogic(MultiBlockLogicTank logic) {
        this.multiBlockLogic = logic;
        if (!multiBlockLogic.isRootComponent(this)) {
            fluidLevel = 0;
        }
        this.markForUpdate();
    }

    @Override
    public boolean hasNeighbour(ForgeDirection dir) {
        return worldObj!=null && getMultiBLockLogic().isPartOfMultiBlock(worldObj, xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
    }

    @Override
    public boolean isValidComponent(IMultiBlockComponent component) {
        return component instanceof TileEntityTank && this.isSameMaterial((TileEntityTank) component);
    }
}

package com.InfinityRaider.AgriCraft.tileentity.irrigation;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.network.MessageSyncFluidLevel;
import com.InfinityRaider.AgriCraft.network.NetworkWrapperAgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;

import com.InfinityRaider.AgriCraft.utility.multiblock.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.List;

public class TileEntityTank extends TileEntityCustomWood implements IFluidHandler, IIrrigationComponent, IMultiBlockComponent<MultiBlockManager, MultiBlockPartData>, IDebuggable {
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
    private int fluidLevel = 0;
    private int lastFluidLevel = 0;
    private int lastDiscreteFluidLevel =0;
    private MultiBlockPartData multiBlockData;
    /** Main component cache is only used in the server thread because it's accessed there very often */
    private TileEntityTank mainComponent;
    
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (this.fluidLevel > 0) {
            tag.setInteger(Names.NBT.level, this.fluidLevel);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.fluidLevel = tag.hasKey(Names.NBT.level) ? tag.getInteger(Names.NBT.level) : 0;
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
        if(needsSync()) {
            IMessage msg = new MessageSyncFluidLevel(this.fluidLevel, this.xCoord, this.yCoord, this.zCoord);
            NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 64);
            NetworkWrapperAgriCraft.wrapper.sendToAllAround(msg, point);
        }
    }

    private boolean needsSync() {
        int newDiscreteLvl = getDiscreteFluidLevel();
        //sync when the discrete fluid level has changed
        if(newDiscreteLvl != lastDiscreteFluidLevel) {
            lastDiscreteFluidLevel = newDiscreteLvl;
            lastFluidLevel = fluidLevel;
            return true;
        }
        //sync when the fluid level ahs changed too much
        if(SYNC_DELTA<=Math.abs(lastFluidLevel-fluidLevel)) {
            lastDiscreteFluidLevel = newDiscreteLvl;
            lastFluidLevel = fluidLevel;
            return true;
        }
        return false;
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
        if(this.getMainComponent() == this) {
            return this.fluidLevel;
        }
        TileEntityTank mainComponent = this.getMainComponent();
        return mainComponent!=null ? mainComponent.getFluidLevel() : 0;
	}

    public int getYPosition() {
        return getMultiBlockData().posY();
    }

    /**
     * Maps the current fluid level into the interval [0, {@value #DISCRETE_MAX}]
     */
    public int getDiscreteFluidLevel() {
        IMultiBlockPartData data = getMultiBlockData();
        float discreteFactor = DISCRETE_MAX / ((float) SINGLE_CAPACITY * data.sizeX() * data.sizeZ());
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
            TileEntityTank tank = this.getMainComponent();
            if (tank == null) {
                //This is a crappy fix, but I don't have time or incentive to find out why the tank is null
                tank = this;
            }
            lvl = lvl > tank.getCapacity() ? tank.getCapacity() : lvl;
            tank.fluidLevel = lvl;
            if (!tank.worldObj.isRemote) {
                tank.syncFluidLevel();
            }
        }
    }

    @Override
    public boolean canConnectTo(IIrrigationComponent component) {
        return false;
    }
    
    @Override
    public int getCapacity() {
    	return SINGLE_CAPACITY * getMultiBlockData().size();
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

    @Override
    public TileEntityTank getMainComponent() {
        if(worldObj.isRemote) {
            IMultiBlockPartData data = this.getMultiBlockData();
            return (TileEntityTank) worldObj.getTileEntity(xCoord - data.posX(), yCoord - data.posY(), zCoord - data.posZ());
        }
        if(this.mainComponent == null) {
            IMultiBlockPartData data = this.getMultiBlockData();
            this.mainComponent =  (TileEntityTank) worldObj.getTileEntity(xCoord - data.posX(), yCoord - data.posY(), zCoord - data.posZ());
        }
        return mainComponent;
    }

    @Override
    public MultiBlockManager getMultiBlockManager() {
        return MultiBlockManager.getInstance();
    }

    @Override
    public void setMultiBlockPartData(MultiBlockPartData data) {
        this.multiBlockData = data;
        this.mainComponent = null;
        this.markForUpdate();
    }

    @Override
    public MultiBlockPartData getMultiBlockData() {
        if(this.multiBlockData == null) {
            this.multiBlockData = new MultiBlockPartData(0, 0, 0, 1, 1, 1);
        }
        return this.multiBlockData;
    }

    @Override
    public boolean hasNeighbour(ForgeDirection dir) {
        IMultiBlockPartData data= this.getMultiBlockData();
        int x = data.posX()+dir.offsetX;
        int y = data.posY()+dir.offsetY;
        int z = data.posZ()+dir.offsetZ;
        return (x>=0 && x<data.sizeX()) && (y>=0 && y<data.sizeY()) && (z>=0 && z<data.sizeZ());
    }

    @Override
    public boolean isValidComponent(IMultiBlockComponent component) {
        return component instanceof TileEntityTank && this.isSameMaterial((TileEntityTank) component);
    }

    @Override
    public void preMultiBlockCreation(int sizeX, int sizeY, int sizeZ) {
        int lvl = 0;
        for(int x=0;x<sizeX;x++) {
            for(int y=0;y<sizeY;y++) {
                for(int z=0;z<sizeZ;z++) {
                    TileEntityTank tank = (TileEntityTank) worldObj.getTileEntity(x+xCoord, y+yCoord, z+zCoord);
                    if(tank == null) {
                        continue;
                    }
                    lvl = lvl + tank.fluidLevel;
                    tank.fluidLevel = 0;
                }
            }
        }
        this.fluidLevel = lvl;
    }

    @Override
    public void postMultiBlockCreation() {
        this.mainComponent = null;
    }

    @Override
    public void preMultiBlockBreak() {
        MultiBlockPartData data = this.getMultiBlockData();
        int[] fluidLevelByLayer = new int[data.sizeY()];
        int area = data.sizeX()*data.sizeZ();
        int fluidContentByLayer = area*SINGLE_CAPACITY;
        int layer = 0;
        while(fluidLevel>0 && layer<fluidLevelByLayer.length) {
            fluidLevelByLayer[layer] = (fluidLevel>=fluidContentByLayer) ? (fluidContentByLayer/area) : (fluidLevel/area);
            fluidLevel = (fluidLevel>=fluidContentByLayer) ? (fluidLevel-fluidContentByLayer) : 0;
            layer++;
        }
        for(int x=0;x<data.sizeX();x++) {
            for(int y=0;y<fluidLevelByLayer.length;y++) {
                for(int z=0;z<data.sizeZ();z++) {
                    TileEntityTank tank = (TileEntityTank) worldObj.getTileEntity(x+xCoord, y+yCoord, z+zCoord);
                    if(tank != null) {
                        tank.fluidLevel = fluidLevelByLayer[y];
                    }
                }
            }
        }
    }

    @Override
    public void postMultiBlockBreak() {
        this.mainComponent = null;
        this.syncFluidLevel();
    }

    //debug info
    @Override
    public void addDebugInfo(List<String> list) {
        super.addDebugInfo(list);
        IMultiBlockPartData data = this.getMultiBlockData();
        TileEntityTank root = getMainComponent();
        list.add("TANK:");
        list.add("coordinates: ("+xCoord+", "+yCoord+", "+zCoord+")");
        list.add("root coords: ("+root.xCoord+", "+root.yCoord+", "+root.zCoord+")");
        list.add("Tank: (single capacity: " + SINGLE_CAPACITY + ")");
        list.add("  - FluidLevel: " + this.getFluidLevel() + "/" + this.getCapacity());
        list.add("  - Water level is on layer " + (int) Math.floor((this.getFluidLevel() - 0.1F) / (this.getCapacity() * data.sizeX() * data.sizeZ())) + ".");
        list.add("  - Water height is " + this.getFluidHeight());
        StringBuilder neighbours = new StringBuilder();
        for(ForgeDirection dir:ForgeDirection.values()) {
            if(dir==ForgeDirection.UNKNOWN) {
                continue;
            }
            if(this.hasNeighbour(dir)) {
                neighbours.append(dir.name()).append(", ");
            }
        }
        list.add("  - Neighbours: " + neighbours.toString());
        list.add("  - MultiBlock data: " + data.toString());
        list.add("  - MultiBlock Size: "+ data.sizeX()+"x"+ data.sizeY()+"x"+data.sizeZ());
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addWailaInformation(List information) {
        super.addWailaInformation(information);
        information.add(StatCollector.translateToLocal("agricraft_tooltip.waterLevel") + ": " + this.getFluidLevel() + "/" + this.getCapacity());
    }
}

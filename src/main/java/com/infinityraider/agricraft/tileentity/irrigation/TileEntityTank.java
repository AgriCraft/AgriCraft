package com.infinityraider.agricraft.tileentity.irrigation;

import com.infinityraider.agricraft.api.v1.IDebuggable;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.network.MessageSyncFluidLevel;
import com.infinityraider.agricraft.network.NetworkWrapperAgriCraft;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.tileentity.TileEntityCustomWood;

import com.infinityraider.agricraft.utility.AgriForgeDirection;
import com.infinityraider.agricraft.utility.multiblock.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class TileEntityTank extends TileEntityCustomWood implements ITickable, IFluidHandler, IIrrigationComponent, IMultiBlockComponent<MultiBlockManager, MultiBlockPartData>, IDebuggable {
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
                tag.setInteger(AgriCraftNBT.LEVEL, this.fluidLevel);
            }
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            super.readFromNBT(tag);
            this.fluidLevel = tag.hasKey(AgriCraftNBT.LEVEL) ? tag.getInteger(AgriCraftNBT.LEVEL) : 0;
        }

        //updates the tile entity every tick
        @Override
        public void tick() {
            if(!this.worldObj.isRemote) {
                if(this.worldObj.canBlockSeeSky(getPos()) && this.worldObj.isRaining()) {
                    if(!this.hasNeighbour(AgriForgeDirection.UP)) {
                        BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(getPos());
                        if(biome!=BiomeGenBase.desert && biome!=BiomeGenBase.desertHills) {
                            this.setFluidLevel(this.getFluidLevel() + 1);
                        }
                    }
                }
                Block block = this.worldObj.getBlockState(pos.add(0, 1, 0)).getBlock();
                if(ConfigurationHandler.fillFromFlowingWater && (block==Blocks.water || block==Blocks.flowing_water)) {
                    this.setFluidLevel(this.getFluidLevel() + 5);
                }
            }
        }

        @Override
        public void syncFluidLevel() {
            if(needsSync()) {
                IMessage msg = new MessageSyncFluidLevel(this.fluidLevel, this.getPos());
                NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(this.worldObj.provider.getDimensionId(), this.xCoord(), this.yCoord(), this.zCoord(), 64);
                NetworkWrapperAgriCraft.wrapper.sendToAllAround(msg, point);
            }
        }

    private boolean needsSync() {
        int newDiscreteLvl = getDiscreteFluidLevel();
        //sync when the discrete fluid LEVEL has changed
        if(newDiscreteLvl != lastDiscreteFluidLevel) {
            lastDiscreteFluidLevel = newDiscreteLvl;
            lastFluidLevel = fluidLevel;
            return true;
        }
        //sync when the fluid LEVEL ahs changed too much
        if(SYNC_DELTA<=Math.abs(lastFluidLevel-fluidLevel)) {
            lastDiscreteFluidLevel = newDiscreteLvl;
            lastFluidLevel = fluidLevel;
            return true;
        }
        return false;
    }

    public boolean isConnectedToChannel(AgriForgeDirection direction) {
        if((this.worldObj != null) && (direction != AgriForgeDirection.UNKNOWN) && (direction.offsetY == 0)) {
            TileEntity tile = this.getWorld().getTileEntity(direction.offset(getPos()));
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

	/**
	 * TEMPORARY: Fix to correct build.
	 * @param lvl
	 * @return 
	 */
	@Override
	public float getFluidHeight(int lvl) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

    public int getYPosition() {
        return getMultiBlockData().posY();
    }

    /**
     * Maps the current fluid LEVEL into the interval [0, {@value #DISCRETE_MAX}]
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


    /** IFluidHandler methods */
    /** --------------------- */

    //try to fill the tank
    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
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
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
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
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return this.drain(from, new FluidStack(FluidRegistry.WATER, maxDrain), doDrain);
    }

    //check if the tank can be filled
    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return fluid==FluidRegistry.WATER && this.canAccept();
    }

    //check if the tank can be drained
    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return fluid==FluidRegistry.WATER && this.canProvide();
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        FluidTankInfo[] info = new FluidTankInfo[1];
        info[0] = new FluidTankInfo(this.getContents(), this.getCapacity());
        return info;
    }

    /** MultiBlock methods */
    /** ------------------ */

    @Override
    public TileEntityTank getMainComponent() {
        if(worldObj.isRemote) {
            IMultiBlockPartData data = this.getMultiBlockData();
            return (TileEntityTank) worldObj.getTileEntity(getPos().add(-data.posX(), -data.posY(), -data.posZ()));
        }
        if(this.mainComponent == null) {
            IMultiBlockPartData data = this.getMultiBlockData();
            this.mainComponent =  (TileEntityTank) worldObj.getTileEntity(getPos().add(-data.posX(), -data.posY(), -data.posZ()));
        }
        return mainComponent;
    }

	// This is kinda an odd choice.
    @Override
    public MultiBlockManager getMultiBlockManager() {
        return MultiBlockManager.INSTANCE;
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
    public boolean hasNeighbour(AgriForgeDirection dir) {
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
                    TileEntityTank tank = (TileEntityTank) worldObj.getTileEntity(getPos().add(xCoord(), yCoord(), zCoord()));
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
                    TileEntityTank tank = (TileEntityTank) worldObj.getTileEntity(getPos().add(xCoord(), yCoord(), zCoord()));
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


    /** IDebuggable methods */
    /** ------------------- */

    //debug info
    @Override
    public void addDebugInfo(List<String> list) {
        super.addDebugInfo(list);
        IMultiBlockPartData data = this.getMultiBlockData();
        TileEntityTank root = getMainComponent();
        list.add("TANK:");
        list.add("coordinates: ("+xCoord()+", "+yCoord()+", "+zCoord()+")");
        list.add("root coords: ("+root.xCoord()+", "+root.yCoord()+", "+root.zCoord()+")");
        list.add("Tank: (single capacity: " + SINGLE_CAPACITY + ")");
        list.add("  - FluidLevel: " + this.getFluidLevel() + "/" + this.getCapacity());
        list.add("  - Water level is on layer " + (int) Math.floor((this.getFluidLevel() - 0.1F) / (this.getCapacity() * data.sizeX() * data.sizeZ())) + ".");
        list.add("  - Water height is " + this.getFluidHeight());
        StringBuilder neighbours = new StringBuilder();
        for(AgriForgeDirection dir:AgriForgeDirection.values()) {
            if(dir==AgriForgeDirection.UNKNOWN) {
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

    /** Waila methods */
    /** ------------- */

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addWailaInformation(List information) {
        super.addWailaInformation(information);
        information.add(StatCollector.translateToLocal("agricraft_tooltip.waterLevel") + ": " + this.getFluidLevel() + "/" + this.getCapacity());
    }
}

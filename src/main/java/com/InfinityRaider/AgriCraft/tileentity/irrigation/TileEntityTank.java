package com.InfinityRaider.AgriCraft.tileentity.irrigation;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.List;

public class TileEntityTank extends TileEntityCustomWood implements IFluidHandler, IDebuggable {

    protected static final int DISCRETE_MAX = 32;
    /**
     * Don't call this directly, use getFluidLevel() and setFluidLevel(int amount) because only the tank at position (0, 0, 0)
     * in the multiblock holds the liquid
     */
    private int fluidLevel=0;
    protected int lastDiscreteFluidLevel = 0;

    private int xPosition=0;
    private int yPosition=0;
    private int zPosition=0;
    private int xSize=1;
    private int ySize=1;
    private int zSize=1;

    //boolean to convert pre-1.4 tanks
    private boolean oldVersion = false;
    
    //OVERRIDES
    //---------
    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(this.fluidLevel>0) {
            tag.setInteger(Names.NBT.level, this.fluidLevel);
        }
        tag.setInteger("xPosition", xPosition);
        tag.setInteger("yPosition", yPosition);
        tag.setInteger("zPosition", zPosition);
        tag.setInteger("xSize", xSize);
        tag.setInteger("ySize", ySize);
        tag.setInteger("zSize", zSize);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if(tag.hasKey(Names.NBT.connected)) {
            oldVersion = true;
        } else {
            xPosition = tag.getInteger("xPosition");
            yPosition = tag.getInteger("yPosition");
            zPosition = tag.getInteger("zPosition");
            xSize = tag.getInteger("xSize");
            ySize = tag.getInteger("ySize");
            zSize = tag.getInteger("zSize");
        }
        if(tag.hasKey(Names.NBT.level)) {
            if(xPosition==0 && yPosition==0 && zPosition==0) {
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
        if(!this.worldObj.isRemote) {
            if(oldVersion) {
                this.checkForMultiBlock();
            }
            if(this.worldObj.canBlockSeeTheSky(this.xCoord, this.yCoord, this.zCoord) && this.worldObj.isRaining()) {
                if(this.yPosition+1==this.ySize) {
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

    public void syncToClient(boolean forceUpdate) {
        boolean change = forceUpdate || this.getDiscreteFluidLevel()!=lastDiscreteFluidLevel;
        if(change) {
            this.lastDiscreteFluidLevel = this.getDiscreteFluidLevel();
            this.markForUpdate();
        }
    }


    //MULTIBLOCK METHODS
    //------------------
    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public int getZPosition() {
        return zPosition;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public int getZSize() {
        return zSize;
    }

    //checks if this is made of wood
    public boolean isWood() {return this.getBlockMetadata()==0;}

    //checks if this is part of a multiblock
    public boolean isMultiBlock() {return this.getConnectedTanks()>1;}

    //returns the number of blocks in the multiblock
    public int getConnectedTanks() {return xSize*ySize*zSize;}

    //check if a tile entity is instance of TileEntityTank and is the same material
    public boolean isSameTank(TileEntity tileEntity) {
        if(tileEntity!=null && tileEntity instanceof TileEntityTank) {
            TileEntityTank tank = (TileEntityTank) tileEntity;
            return this.isSameMaterial(tank);
        }
        return false;
    }
    //check if a tile entity is part of this multiblock
    public boolean isMultiBlockPartner(TileEntity tileEntity) {
        return this.getConnectedTanks()>1 && (this.isSameTank(tileEntity)) && (this.getConnectedTanks() == ((TileEntityTank) tileEntity).getConnectedTanks());
    }

    //updates the multiblock, returns true if something has changed
    public boolean updateMultiBlock() {
        return this.checkForMultiBlock();
    }

    //multiblockify
    public boolean checkForMultiBlock() {
        if(!this.worldObj.isRemote) {
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
                        if (this.isSameTank(this.worldObj.getTileEntity(x, y, z))) {
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
                    }
                }
            }
            //turn all the blocks into one multiblock
            this.setDimensions(xSizeNew, ySizeNew, zSizeNew);
            for (int x = this.xCoord - xPos; x < this.xCoord - xPos + xSizeNew; x++) {
                for (int y = this.yCoord - yPos; y < this.yCoord - yPos + ySizeNew; y++) {
                    for (int z = this.zCoord - zPos; z < this.zCoord - zPos + zSizeNew; z++) {
                        TileEntityTank tank = ((TileEntityTank) this.worldObj.getTileEntity(x, y, z));
                        tank.setDimensions(xSizeNew, ySizeNew, zSizeNew);
                        tank.syncToClient(true);
                    }
                }
            }
            this.setFluidLevel(lvl);
            return true;
        }
        return false;
    }

    private void setDimensions(int xSize, int ySize, int zSize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        this.findPositionInMultiBlock();
    }

    private void findPositionInMultiBlock() {
        this.xPosition = findArrayXPosition();
        this.yPosition = findArrayYPosition();
        this.zPosition = findArrayZPosition();
    }

    private void resetTank() {
        this.xSize = 1;
        this.ySize = 1;
        this.zSize = 1;
        this.xPosition = 0;
        this.yPosition = 0;
        this.zPosition = 0;
    }

    //breaks up the multiblock and divides the fluid among the tanks
    public void breakMultiBlock(boolean sync, int lvl) {
        int[] levels = new int[ySize];
        int area = xSize*zSize;
        for(int i=0;i<levels.length;i++) {
            levels[i] = (lvl/area>=this.getSingleCapacity())?this.getSingleCapacity():lvl/area;
            lvl = (lvl - levels[i]*area)<0?0:(lvl - levels[i]*area);
        }
        for(int x=0;x<xSize;x++) {
            for(int y=0;y<ySize;y++) {
                for(int z=0;z<zSize;z++) {
                    if(!(this.worldObj.getTileEntity(this.xCoord-xPosition+x, this.yCoord-yPosition+y, this.zCoord-zPosition+z) instanceof TileEntityTank)){
                        continue;
                    } 
                    TileEntityTank tank = (TileEntityTank) this.worldObj.getTileEntity(this.xCoord-xPosition+x, this.yCoord-yPosition+y, this.zCoord-zPosition+z);
                    if(tank==null) {
                        continue;
                    }
                    tank.resetTank();
                    tank.fluidLevel = levels[y];
                    oldVersion = false;
                    if(sync) {
                        tank.syncToClient(true);
                    }
                }
            }
        }
    }

    //returns the xPosition of this block in along a row of these blocks along the X-axis
    private int findArrayXPosition() {
        if(this.isSameTank(this.worldObj.getTileEntity(this.xCoord-1, this.yCoord, this.zCoord))) {
            return (((TileEntityTank) this.worldObj.getTileEntity(this.xCoord-1, this.yCoord, this.zCoord)).findArrayXPosition() + 1);
        }
        else {
            return 0;
        }
    }

    //returns the yPosition of this block in along a row of these blocks along the Y-axis
    private int findArrayYPosition() {
        if(this.isSameTank(this.worldObj.getTileEntity(this.xCoord, this.yCoord-1, this.zCoord))) {
            return (((TileEntityTank) this.worldObj.getTileEntity(this.xCoord, this.yCoord-1, this.zCoord)).findArrayYPosition() + 1);
        }
        else {
            return 0;
        }
    }

    //returns the zPosition of this block in along a row of these blocks along the Z-axis
    private int findArrayZPosition() {
        if(this.isSameTank(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord-1))) {
            return (((TileEntityTank) this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord-1)).findArrayZPosition() + 1);
        }
        else {
            return 0;
        }
    }

    //returns the x size of an array of these blocks this block is in along the X-axis
    private int findArrayXSize() {
        if(this.isSameTank(this.worldObj.getTileEntity(this.xCoord+1, this.yCoord, this.zCoord))) {
            return ((TileEntityTank) this.worldObj.getTileEntity(this.xCoord+1, this.yCoord, this.zCoord)).findArrayXSize();
        }
        else {
            return this.findArrayXPosition()+1;
        }
    }

    //returns the y size of an array of these blocks this block is in along the Y-axis
    private int findArrayYSize() {
        if(this.isSameTank(this.worldObj.getTileEntity(this.xCoord, this.yCoord+1, this.zCoord))) {
            return ((TileEntityTank) this.worldObj.getTileEntity(this.xCoord, this.yCoord+1, this.zCoord)).findArrayYSize();
        }
        else {
            return this.findArrayYPosition()+1;
        }
    }

    //returns the z size of an array of these blocks this block is in along the Z-axis
    private int findArrayZSize() {
        if(this.isSameTank(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord+1))) {
            return ((TileEntityTank) this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord+1)).findArrayZSize();
        }
        else {
            return this.findArrayZPosition()+1;
        }
    }

    //returns the x, y and z sizes of 3 arrays of these blocks along the cardinal directions
    private int[] findArrayDimensions() {
        int[] size = new int[3];
        size[0] = this.findArrayXSize();
        size[1] = this.findArrayYSize();
        size[2] = this.findArrayZSize();
        return size;
    }

    //returns the xPosition of this block in the multiblock
    private int calculateXPosition() {
        if(this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord))) {
            return (((TileEntityTank) this.worldObj.getTileEntity(this.xCoord-1, this.yCoord, this.zCoord)).calculateXPosition() + 1);
        }
        else {
            return 0;
        }
    }

    //returns the yPosition of this block in the multiblock
    private int calculateYPosition() {
        if(this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord))) {
            return (((TileEntityTank) this.worldObj.getTileEntity(this.xCoord, this.yCoord-1, this.zCoord)).calculateYPosition() + 1);
        }
        else {
            return 0;
        }
    }

    //returns the zPosition of this block in the multiblock
    private int calculateZPosition() {
        if(this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1))) {
            return (((TileEntityTank) this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord-1)).calculateZPosition() + 1);
        }
        else {
            return 0;
        }
    }

    //returns the x size of the multiblock
    private int calculateXSize() {
        if(this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord))) {
            return ((TileEntityTank) this.worldObj.getTileEntity(this.xCoord+1, this.yCoord, this.zCoord)).calculateXSize();
        }
        else {
            return this.calculateXPosition()+1;
        }
    }

    //returns the y size of an array of these blocks this block is in along the Y-axis
    private int calculateYSize() {
        if(this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord))) {
            return ((TileEntityTank) this.worldObj.getTileEntity(this.xCoord, this.yCoord+1, this.zCoord)).calculateYSize();
        }
        else {
            return this.calculateYPosition()+1;
        }
    }

    //returns the z size of an array of these blocks this block is in along the Z-axis
    private int calculateZSize() {
        if(this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1))) {
            return ((TileEntityTank) this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord+1)).calculateZSize();
        }
        else {
            return this.calculateZPosition()+1;
        }
    }

    //returns the x, y and z sizes of the multiblock
    public int[] calculateDimensions() {
        int[] size = new int[3];
        size[0] = this.calculateXSize();
        size[1] = this.calculateYSize();
        size[2] = this.calculateZSize();
        return size;
    }

    //TANK METHODS
    //------------
    public FluidStack getContents() {
        return new FluidStack(FluidRegistry.WATER, this.getFluidLevel());
    }

    public int getFluidLevel() {
        TileEntity te = worldObj.getTileEntity(xCoord - xPosition, yCoord - yPosition, zCoord - zPosition);
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

    public float getFluidY(int volume) {
        return getFluidY(volume, getYSize(), getTotalCapacity());
    }

    public static float getFluidY(int volume, int ySize, int capacity) {
        int totalHeight = 16*ySize-2;     //total height in 1/16th's of a block
        return totalHeight*((float) volume)/((float) capacity)+2;
    }

    public void setFluidLevel(int lvl) {
        if(lvl!=this.getFluidLevel()) {
            lvl = lvl > this.getTotalCapacity() ? this.getTotalCapacity() : lvl;
            if(!(worldObj.getTileEntity(xCoord - xPosition, yCoord - yPosition, zCoord - zPosition) instanceof TileEntityTank)){
                return; 
            }
            TileEntityTank tank = (TileEntityTank) worldObj.getTileEntity(xCoord - xPosition, yCoord - yPosition, zCoord - zPosition);
            if(tank != null) {
                tank.fluidLevel = lvl;
                tank.syncToClient(true);
            }
        }
    }

    public int getSingleCapacity() {
        return (this.getBlockMetadata()+1)*8*Constants.mB;
    }

    public int getTotalCapacity() {
        return this.getSingleCapacity()*this.getConnectedTanks();
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
        if(doFill) {
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
        if(doDrain) {
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
        list.add("TANK:");
        list.add("Tank: " + (this.isWood() ? "wood" : "iron") + " (single capacity: " + this.getSingleCapacity() + ")");
        super.addDebugInfo(list);
        list.add("  - MultiBlock: " + this.isMultiBlock());
        list.add("  - Connected tanks: " + this.getConnectedTanks());
        int[] size = this.calculateDimensions();
        list.add("  - MultiBlock Size: " + size[0] + "x" + size[1] + "x" + size[2]);
        list.add("  - FluidLevel: " + this.getFluidLevel() + "/" + this.getTotalCapacity());
        list.add("  - FluidHeight: " + this.getFluidY());
        boolean left = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord));
        boolean right = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord));
        boolean back = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1));
        boolean front = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1));
        boolean top = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord));
        boolean below = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord));
        list.add("  - Found multiblock partners on: " + (left ? "left, " : "") + (right ? "right, " : "") + (back ? "back, " : "") + (front ? "front, " : "") + (top ? "top, " : "") + (below ? "below" : ""));
        list.add("Water level is on layer " + (int) Math.floor(((float) this.getFluidLevel() - 0.1F) / ((float) (this.getSingleCapacity() * this.getXSize() * this.getZSize()))) + ".");
        list.add("this clicked is on  layer " + this.getYPosition() + ".");
        list.add("Water height is " + this.getFluidY());
    }
}

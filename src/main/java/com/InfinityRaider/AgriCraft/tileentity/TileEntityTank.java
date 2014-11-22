package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.handler.PacketHandler;
import com.InfinityRaider.AgriCraft.network.MessageTileEntityTank;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

public class TileEntityTank extends TileEntity implements IFluidHandler {
    protected int connectedTanks=1;
    protected int fluidLevel;
    protected String materialName;
    protected int materialMeta;

    //OVERRIDES
    //---------
    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger(Names.connected, this.connectedTanks);
        tag.setInteger(Names.level, this.fluidLevel);
        if(this.materialName!=null && !this.materialName.equals("")) {
            tag.setString(Names.material, this.materialName);
            tag.setInteger(Names.materialMeta, this.materialMeta);
        }
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.connectedTanks = tag.getInteger(Names.connected);
        this.fluidLevel = tag.getInteger(Names.level);
        String mat = tag.getString(Names.material);
        if(mat!=null && !mat.equals("")) {
            this.materialName = mat;
            this.materialMeta = tag.getInteger(Names.materialMeta);
        }
    }

    //updates the tile entity every tick
    @Override
    public void updateEntity() {
        if(!this.worldObj.isRemote) {
            boolean change = this.updateMultiBlock();
            if(this.worldObj.isRaining()) {
                if(this.getYPosition()+1==this.getYSize()) {
                    this.setFluidLevel(this.fluidLevel+1);
                    change = true;
                }
            }
            if(change) {
                this.markDirty();
            }
        }
    }

    //uses the packet handler to create a packet with the data contained in the tile entity
    @Override
    public Packet getDescriptionPacket() {
        return PacketHandler.instance.getPacketFrom(new MessageTileEntityTank(this));
    }

    //called on client event
    @Override
    public boolean receiveClientEvent(int id, int value) {
        if(worldObj.isRemote && id == 1) {
            this.worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
            this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);
            Minecraft.getMinecraft().renderGlobal.markBlockForUpdate(xCoord, yCoord, zCoord);
            super.receiveClientEvent(id, value);
        }
        return true;
    }

    //this gets called when the tile entity should get updated on the client (sort of)
    @Override
    public void markDirty() {
        PacketHandler.instance.sendToAllAround(new MessageTileEntityTank(this),new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId,(double) this.xCoord,(double) this.yCoord, (double) this.zCoord, 128d));
        this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);
        super.markDirty();
    }

    //RENDERING METHODS
    //-----------------
    //set the wood material
    public void setMaterial(NBTTagCompound tag) {
        if(tag!=null && tag.hasKey(Names.material) && tag.hasKey(Names.materialMeta)) {
            this.materialName = tag.getString(Names.material);
            this.materialMeta = tag.getInteger(Names.materialMeta);
        }
    }

    public void setMaterial(ItemStack stack) {
        this.materialName = Block.blockRegistry.getNameForObject(stack.getItem());
        this.materialMeta = stack.getItemDamage();
    }

    public void setMaterial(String name, int meta) {
        LogHelper.debug("setting material to " + name + ":" + meta);
        if(name!=null && Block.blockRegistry.getObject(name)!=null) {
            this.materialName = name;
            this.materialMeta = meta;
        }
    }

    //get material information
    public String getMaterialName() {
        return this.materialName;
    }

    public int getMaterialMeta() {
        return this.materialMeta;
    }

    public ItemStack getMaterial() {
        ItemStack stack = new ItemStack(Blocks.planks, 1, 0);
        if(this.materialName !=null && !this.materialName.equals("")) {
            stack = new ItemStack((Block) Block.blockRegistry.getObject(this.materialName), 1, this.materialMeta);
        }
        return stack;
    }

    public IIcon getIcon() {
        if(this.materialName !=null && !this.materialName.equals("")) {
            Block material = (Block) Block.blockRegistry.getObject(this.materialName);
            return material.getIcon(0, this.materialMeta);
        }
        else {
            return this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord).getIcon(0, this.getBlockMetadata());
        }
    }

    //MULTIBLOCK METHODS
    //------------------
    //checks if this is made of wood
    public boolean isWood() {return this.getBlockMetadata()==0;}

    //checks if this is part of a multiblock
    public boolean isMultiBlock() {return this.connectedTanks>1;}

    //returns the number of blocks in the multiblock
    public int getConnectedTanks() {return this.connectedTanks;}

    //sets the number of connected tanks
    public void setConnectedTanks(int nr) {this.connectedTanks = nr;}

    //check if a tile entity is instance of TileEntityTank and is the same material
    public boolean isSameTank(TileEntity tileEntity) {
        if(tileEntity!=null && tileEntity instanceof TileEntityTank) {
            TileEntityTank tank = (TileEntityTank) tileEntity;
            return ItemStack.areItemStacksEqual(this.getMaterial(), tank.getMaterial());
        }
        return false;
    }
    //check if a tile entity is part of this multiblock
    public boolean isMultiBlockPartner(TileEntity tileEntity) {
        return this.connectedTanks>1 && (this.isSameTank(tileEntity)) && (this.connectedTanks == ((TileEntityTank) tileEntity).connectedTanks);
    }

    //updates the multiblock, returns true if something has changed
    public boolean updateMultiBlock() {
        boolean change = false;
        if(!this.isMultiBlock()) {
            change = this.checkForMultiBlock();
        }
        return change;
    }

    //multiblockify
    public boolean checkForMultiBlock() {
        if(!this.worldObj.isRemote) {
            //find dimensions
            int xPos = this.findArrayXPosition();
            int yPos = this.findArrayYPosition();
            int zPos = this.findArrayZPosition();
            int xSize = this.findArrayXSize();
            int ySize = this.findArrayYSize();
            int zSize = this.findArrayZSize();
            //iterate trough the x-, y- and z-directions if all blocks are tanks
            for (int x = this.xCoord - xPos; x < this.xCoord - xPos + xSize; x++) {
                for (int y = this.yCoord - yPos; y < this.yCoord - yPos + ySize; y++) {
                    for (int z = this.zCoord - zPos; z < this.zCoord - zPos + zSize; z++) {
                        if (this.isSameTank(this.worldObj.getTileEntity(x, y, z))) {
                            TileEntityTank tank = (TileEntityTank) this.worldObj.getTileEntity(x, y, z);
                            int[] tankSize = tank.findArrayDimensions();
                            if(!(xSize==tankSize[0] && ySize==tankSize[1] && zSize==tankSize[2])) {
                                return false;
                            }
                        }
                        else {
                            return false;
                        }
                    }
                }
            }
            //turn all blocks that are in a multiblock in single blocks again to calculate the total fluid level
            for (int x = this.xCoord - xPos; x < this.xCoord - xPos + xSize; x++) {
                for (int y = this.yCoord - yPos; y < this.yCoord - yPos + ySize; y++) {
                    for (int z = this.zCoord - zPos; z < this.zCoord - zPos + zSize; z++) {
                        TileEntityTank tank = ((TileEntityTank) this.worldObj.getTileEntity(x, y, z));
                        if(tank.isMultiBlock()) {
                            tank.breakMultiBlock();
                        }
                    }
                }
            }
            //calculate the total fluid level
            int lvl = 0;
            for (int x = this.xCoord - xPos; x < this.xCoord - xPos + xSize; x++) {
                for (int y = this.yCoord - yPos; y < this.yCoord - yPos + ySize; y++) {
                    for (int z = this.zCoord - zPos; z < this.zCoord - zPos + zSize; z++) {
                        TileEntityTank tank = ((TileEntityTank) this.worldObj.getTileEntity(x, y, z));
                        lvl = tank.fluidLevel+lvl;
                    }
                }
            }
            //turn all the blocks into one multiblock
            this.connectedTanks = xSize * ySize * zSize;
            for (int x = this.xCoord - xPos; x < this.xCoord - xPos + xSize; x++) {
                for (int y = this.yCoord - yPos; y < this.yCoord - yPos + ySize; y++) {
                    for (int z = this.zCoord - zPos; z < this.zCoord - zPos + zSize; z++) {
                        TileEntityTank tank = ((TileEntityTank) this.worldObj.getTileEntity(x, y, z));
                        tank.connectedTanks = xSize * ySize * zSize;
                        tank.setFluidLevel(lvl);
                        tank.markDirty();
                    }
                }
            }

            return true;
        }
        return false;
    }

    //breaks up the multiblock and divides the fluid among the tanks
    public void breakMultiBlock() {
        int lvl = this.fluidLevel;
        //get dimensions of the multiblock
        int xPos = this.getXPosition();
        int yPos = this.getYPosition();
        int zPos = this.getZPosition();
        int xSize = this.getXSize();
        int ySize = this.getYSize();
        int zSize = this.getZSize();
        int[] levels = new int[ySize];
        int area = xSize*zSize;
        for(int i=0;i<levels.length;i++) {
            levels[i] = (lvl/area>=this.getSingleCapacity())?this.getSingleCapacity():lvl/area;
            lvl = (lvl - levels[i]*area)<0?0:(lvl - levels[i]*area);
        }
        for(int x=0;x<xSize;x++) {
            for(int y=0;y<ySize;y++) {
                for(int z=0;z<zSize;z++) {
                    TileEntityTank tank = (TileEntityTank) this.worldObj.getTileEntity(this.xCoord-xPos+x, this.yCoord-yPos+y, this.zCoord-zPos+z);
                    tank.connectedTanks = 1;
                    tank.fluidLevel = levels[y];
                    tank.markDirty();
                }
            }
        }
    }

    //syncs this fluid level to all tanks in the multiblock
    public void syncFluidLevels() {
        int lvl = this.fluidLevel;
        //get dimensions of the multiblock
        int xPos = this.getXPosition();
        int yPos = this.getYPosition();
        int zPos = this.getZPosition();
        int xSize = this.getXSize();
        int ySize = this.getYSize();
        int zSize = this.getZSize();
        //iterate trough the x-, y- and z-directions if all blocks are tanks
        for (int x = this.xCoord - xPos; x < this.xCoord - xPos + xSize; x++) {
            for (int y = this.yCoord - yPos; y < this.yCoord - yPos + ySize; y++) {
                for (int z = this.zCoord - zPos; z < this.zCoord - zPos + zSize; z++) {
                    if(this.worldObj.getTileEntity(x, y ,z)!=null && this.worldObj.getTileEntity(x, y ,z) instanceof TileEntityTank) {
                        ((TileEntityTank) this.worldObj.getTileEntity(x, y, z)).fluidLevel = lvl;
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
    public int getXPosition() {
        if(this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord))) {
            return (((TileEntityTank) this.worldObj.getTileEntity(this.xCoord-1, this.yCoord, this.zCoord)).getXPosition() + 1);
        }
        else {
            return 0;
        }
    }

    //returns the yPosition of this block in the multiblock
    public int getYPosition() {
        if(this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord))) {
            return (((TileEntityTank) this.worldObj.getTileEntity(this.xCoord, this.yCoord-1, this.zCoord)).getYPosition() + 1);
        }
        else {
            return 0;
        }
    }

    //returns the zPosition of this block in the multiblock
    public int getZPosition() {
        if(this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1))) {
            return (((TileEntityTank) this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord-1)).getZPosition() + 1);
        }
        else {
            return 0;
        }
    }

    //returns the x size of the multiblock
    public int getXSize() {
        if(this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord))) {
            return ((TileEntityTank) this.worldObj.getTileEntity(this.xCoord+1, this.yCoord, this.zCoord)).getXSize();
        }
        else {
            return this.getXPosition()+1;
        }
    }

    //returns the y size of an array of these blocks this block is in along the Y-axis
    public int getYSize() {
        if(this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord))) {
            return ((TileEntityTank) this.worldObj.getTileEntity(this.xCoord, this.yCoord+1, this.zCoord)).getYSize();
        }
        else {
            return this.getYPosition()+1;
        }
    }

    //returns the z size of an array of these blocks this block is in along the Z-axis
    public int getZSize() {
        if(this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1))) {
            return ((TileEntityTank) this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord+1)).getZSize();
        }
        else {
            return this.getZPosition()+1;
        }
    }

    //returns the x, y and z sizes of the multiblock
    public int[] getDimensions() {
        int[] size = new int[3];
        size[0] = this.getXSize();
        size[1] = this.getYSize();
        size[2] = this.getZSize();
        return size;
    }

    //TANK METHODS
    //------------
    public FluidStack getContents() {
        return new FluidStack(FluidRegistry.WATER, this.fluidLevel);
    }

    public int getFluidLevel() {
        return this.fluidLevel;
    }

    public void setFluidLevel(int lvl) {
        this.fluidLevel = lvl>this.getTotalCapacity()?this.getTotalCapacity():lvl;
        this.syncFluidLevels();
    }

    public int getSingleCapacity() {
        return (this.getBlockMetadata()+1)*8*Constants.mB;
    }

    public int getTotalCapacity() {
        return this.getSingleCapacity()*this.connectedTanks;
    }

    public boolean isFull() {
        return this.fluidLevel==this.getTotalCapacity();
    }

    public boolean isEmpty() {
        return this.fluidLevel==0;
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
           this.setFluidLevel(this.fluidLevel-drained);
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
        return this.getYPosition()==0 && fluid==FluidRegistry.WATER && !this.isEmpty();
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        FluidTankInfo[] info = new FluidTankInfo[1];
        info[0] = new FluidTankInfo(this.getContents(), this.getTotalCapacity());
        return info;
    }
}

package com.InfinityRaider.AgriCraft.utility.multiblock;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityTank;
import com.InfinityRaider.AgriCraft.utility.CoordinateIterator;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MultiBlockLogicTank extends MultiBlockLogic {
    private int sizeX = 1;
    private int sizeY = 1;
    private int sizeZ = 1;

    public MultiBlockLogicTank(TileEntityTank tank) {
        super(tank);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.sizeX = tag.getInteger(Names.NBT.x);
        this.sizeY = tag.getInteger(Names.NBT.y);
        this.sizeZ = tag.getInteger(Names.NBT.z);
        createMultiBLock();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger(Names.NBT.x, sizeX);
        tag.setInteger(Names.NBT.y, sizeY);
        tag.setInteger(Names.NBT.z, sizeZ);
    }

    @Override
    public TileEntityTank getRootComponent() {
        return (TileEntityTank) rootComponent;
    }

    public int sizeX() {
        return sizeX;
    }

    public int sizeY() {
        return sizeY;
    }

    public int sizeZ() {
        return sizeZ;
    }

    @Override
    public int getMultiBlockCount() {
        return sizeX*sizeY*sizeZ;
    }

    private boolean setRootComponent(TileEntityTank tank) {
        if(!areCoordinatesSmallerThanCurrentRoot(tank)) {
            return false;
        }
        TileEntityTank oldRoot = getRootComponent();
        this.rootComponent = tank;
        if(!checkMultiBlock()) {
            this.rootComponent = oldRoot;
            return false;
        }
        return true;
    }

    private boolean areCoordinatesSmallerThanCurrentRoot(TileEntityTank tank) {
        TileEntityTank root = getRootComponent();
        return tank.xCoord<=root.xCoord && tank.yCoord<=root.yCoord && tank.zCoord<=root.zCoord;
    }

    private boolean isValidComponent(TileEntity tile) {
        return !(tile == null || !(tile instanceof TileEntityTank)) && isValidComponent((IMultiBlockComponent) tile);
    }

    @Override
    public boolean isPartOfMultiBlock(World world, int x, int y, int z) {
        TileEntityTank root = getRootComponent();
        if(root.getWorldObj() != world) {
            return false;
        }
        if(root.xCoord>x || root.xCoord+sizeX<=x) {
            return false;
        }
        if(root.yCoord>y || root.yCoord+sizeY<=y) {
            return false;
        }
        if(root.zCoord>z || root.zCoord+sizeZ<=z) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkMultiBlock() {
        TileEntityTank root = getRootComponent();
        CoordinateIterator iterator = new CoordinateIterator();
        //check if a new root component has to be set
        int newX = findNewRootCoordinate(iterator.setX());
        int newY = findNewRootCoordinate(iterator.setY());
        int newZ = findNewRootCoordinate(iterator.setZ());
        if(newX * newY * newZ != 0) {
            if(setRootComponent((TileEntityTank) root.getWorldObj().getTileEntity(root.xCoord - newX, root.yCoord - newY, root.zCoord - newZ))) {
                return true;
            }
        }
        //calculate the new multiblock size
        int sizeX = calculateSize(iterator.setX());
        int sizeY = calculateSize(iterator.setY());
        int sizeZ = calculateSize(iterator.setZ());
        if(!areAllBlocksInRangeValidComponents(sizeX, sizeY, sizeZ)) {
            return false;
        }
        if(sizeX==this.sizeX && sizeY==this.sizeY && sizeZ==this.sizeZ) {
            return true;
        }
        breakMultiBlock();
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        createMultiBLock();
        return true;
    }

    private int findNewRootCoordinate(CoordinateIterator it) {
        TileEntityTank root = getRootComponent();
        if(!it.isActive()) {
            LogHelper.debug("ERROR WHEN ITERATING COORDINATES: ITERATOR NOT ACTIVE");
            return 0;
        }
        while(true) {
            it.increment();
            if (!isValidComponent(root.getWorldObj().getTileEntity(root.xCoord - it.x(), root.yCoord - it.y(), root.zCoord - it.z()))) {
                break;
            }
        }
        return it.getOffset()-1;
    }

    private int calculateSize(CoordinateIterator it) {
        TileEntityTank root = getRootComponent();
        if(!it.isActive()) {
            LogHelper.debug("ERROR WHEN ITERATING COORDINATES: ITERATOR NOT ACTIVE");
            return 0;
        }
        while(true) {
            it.increment();
            if (!isValidComponent(root.getWorldObj().getTileEntity(root.xCoord + it.x(), root.yCoord + it.y(), root.zCoord + it.z()))) {
                break;
            }
        }
        return it.getOffset();
    }

    private boolean areAllBlocksInRangeValidComponents(int sizeX, int sizeY, int sizeZ) {
        TileEntityTank root = getRootComponent();
        World world = root.getWorldObj();
        for(int x = root.xCoord;x<root.xCoord+sizeX;x++) {
            for(int y = root.yCoord;y<root.yCoord+sizeY;y++) {
                for(int z = root.zCoord;z<root.zCoord+sizeZ;z++) {
                    if(!isValidComponent(world.getTileEntity(x, y, z))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void createMultiBLock() {
        int fluidLevel = 0;
        TileEntityTank root = getRootComponent();
        World world = root.getWorldObj();
        breakMultiBlock();
        for(int x = root.xCoord;x<root.xCoord+sizeX;x++) {
            for(int y = root.yCoord;y<root.yCoord+sizeY;y++) {
                for(int z = root.zCoord;z<root.zCoord+sizeZ;z++) {
                    TileEntityTank tank = (TileEntityTank) world.getTileEntity(x, y, z);
                    fluidLevel = fluidLevel + tank.getFluidLevel();
                    tank.setMultiBlockLogic(this, true);
                }
            }
        }
        this.getRootComponent().setFluidLevel(fluidLevel);
    }

    @Override
    public void breakMultiBlock() {
        //calculate fluid levels
        int[] fluidLevelByLayer = new int[this.sizeY()];
        int fluidLevel = getRootComponent().getFluidLevel();
        int area = this.sizeX()*this.sizeZ();
        int fluidContentByLayer = area*TileEntityTank.SINGLE_CAPACITY;
        int layer = 0;
        while(fluidLevel>0) {
            fluidLevelByLayer[layer] = fluidLevel>fluidContentByLayer?fluidContentByLayer/area:fluidLevel/8;
            fluidLevel = fluidLevel>fluidContentByLayer?fluidLevel - fluidContentByLayer:0;
        }
        //apply fluid levels
        TileEntityTank root = getRootComponent();
        for(int x = root.xCoord;x<root.xCoord+sizeX;x++) {
            for(int y = root.yCoord;y<root.yCoord+sizeY;y++) {
                for(int z = root.zCoord;z<root.zCoord+sizeZ;z++) {
                    TileEntityTank tank = (TileEntityTank) root.getWorldObj().getTileEntity(x, y, z);
                    tank.setMultiBlockLogic(new MultiBlockLogicTank(tank), false);
                    tank.setFluidLevel(fluidLevelByLayer[y-root.yCoord]);
                }
            }
        }
    }
}

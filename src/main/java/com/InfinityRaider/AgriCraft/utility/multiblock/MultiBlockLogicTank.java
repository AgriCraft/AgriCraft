package com.InfinityRaider.AgriCraft.utility.multiblock;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityTank;
import com.InfinityRaider.AgriCraft.utility.CoordinateIterator;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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

    /** Only try to form multiblocks with existing multiblocks, if none are found, form new multiblock */
    @Override
    public boolean checkMultiBlockOnPlace() {
        TileEntityTank newTank = this.getRootComponent();
        boolean onlySingleNeighbours = true;
        for (ForgeDirection dir : ForgeDirection.values()) {
            if (dir == ForgeDirection.UNKNOWN) {
                continue;
            }
            TileEntity te = newTank.getWorldObj().getTileEntity(newTank.xCoord + dir.offsetX, newTank.yCoord + dir.offsetY, newTank.zCoord + dir.offsetZ);
            if (te == null || !(te instanceof TileEntityTank)) {
                continue;
            }
            TileEntityTank tankAt = (TileEntityTank) te;
            if (tankAt.getMultiBLockLogic().checkToUpdateExistingMultiBlock()) {
                //A new multiblock has been made with an existing multiblock
                return true;
            } else {
                //keep track of a single neighbour blocks
                if (tankAt.getMultiBLockLogic().getMultiBlockCount() > 1) {
                    onlySingleNeighbours = false;
                }
            }
        }
        //there are only a single neighbours next to this block
        return onlySingleNeighbours && formMultiBlockWithNeighbours();
    }

    @Override
    public boolean checkToUpdateExistingMultiBlock() {
        return this.getMultiBlockCount() > 1 && formMultiBlockWithNeighbours();
    }

    private boolean formMultiBlockWithNeighbours() {CoordinateIterator iterator = new CoordinateIterator();
        TileEntityTank oldRoot = getRootComponent();
        TileEntityTank newRoot = findNewRoot(iterator);
        //check if a new root component has to be set
        if(newRoot != oldRoot) {
            this.rootComponent = newRoot;
        }
        //calculate the new multiblock size
        int xMax = calculateSize(iterator.setX());
        int yMax = calculateSize(iterator.setY());
        int zMax = calculateSize(iterator.setZ());
        //if not all blocks for new root are correct, turn back to the old root and return
        if(!areAllBlocksInRangeValidComponents(xMax, yMax, zMax)) {
            this.rootComponent = oldRoot;
            return false;
        }
        //if dimensions and root are the same the multiblock hasn't changed and nothing has to happen
        if(oldRoot==newRoot && xMax==this.sizeX && yMax==this.sizeY && zMax==this.sizeZ) {
            return false;
        }
        //new multiblock dimensions are required, update the multiblock
        breakMultiBlock();
        this.sizeX = xMax;
        this.sizeY = yMax;
        this.sizeZ = zMax;
        createMultiBLock();
        return true;
    }

    private TileEntityTank findNewRoot(CoordinateIterator iterator) {
        TileEntityTank oldRoot = getRootComponent();
        int xMin = findNewRootCoordinateOffset(iterator.setX());
        int yMin = findNewRootCoordinateOffset(iterator.setY());
        int zMin = findNewRootCoordinateOffset(iterator.setZ());
        return (TileEntityTank) oldRoot.getWorldObj().getTileEntity(oldRoot.xCoord - xMin, oldRoot.yCoord - yMin, oldRoot.zCoord - zMin);
    }

    private int findNewRootCoordinateOffset(CoordinateIterator it) {
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
                    tank.setMultiBlockLogic(this);
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
                    if(tank.getMultiBLockLogic() != this) {
                        tank.getMultiBLockLogic().breakMultiBlock();
                    }
                    tank.setMultiBlockLogic(new MultiBlockLogicTank(tank));
                    tank.setFluidLevel(fluidLevelByLayer[y-root.yCoord]);
                }
            }
        }
    }
}

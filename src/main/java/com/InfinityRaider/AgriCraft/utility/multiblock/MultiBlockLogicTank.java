package com.InfinityRaider.AgriCraft.utility.multiblock;

import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityTank;
import com.InfinityRaider.AgriCraft.utility.CoordinateIterator;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class MultiBlockLogicTank extends MultiBlockLogic {
    private boolean placeCheck = false;

    public MultiBlockLogicTank(TileEntityTank tank) {
        super(tank);
    }

    @Override
    public void setRootComponent(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te !=null && te instanceof TileEntityTank) {
            this.rootComponent = (TileEntityTank) te;
        }
    }

    @Override
    public TileEntityTank getRootComponent() {
        return (TileEntityTank) rootComponent;
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
        if(root.xCoord>x || root.xCoord+sizeX()<=x) {
            return false;
        }
        if(root.yCoord>y || root.yCoord+sizeY()<=y) {
            return false;
        }
        if(root.zCoord>z || root.zCoord+sizeZ()<=z) {
            return false;
        }
        return true;
    }

    @Override
    public void onBlockPlaced(World world, int x, int y, int z) {
        if(placeCheck) {
            return;
        }
        placeCheck = true;
        boolean flag = false;
        for(ForgeDirection dir:ForgeDirection.values()) {
            if(dir == ForgeDirection.UNKNOWN) {
                continue;
            }
            TileEntity te = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            if(te!=null && te instanceof TileEntityTank) {
                TileEntityTank tank = (TileEntityTank) te;
                if(tank.getMultiBlockLogic().canCheckForMultiBlock()) {
                    if(tank.getMultiBlockLogic().checkForMultiBlock()) {
                        return;
                    }
                    flag = true;
                }
            }
        }
        if(!flag) {
            this.checkForMultiBlock();
        }
    }

    private boolean canCheckForMultiBlock() {
        return getMultiBlockCount()>1;
    }

    @Override
    public boolean checkForMultiBlock() {
        CoordinateIterator iterator = new CoordinateIterator();
        TileEntityTank oldRoot = getRootComponent();
        int xMin = calculateDimensionOffsetBackwards(iterator.setX());
        int yMin = calculateDimensionOffsetBackwards(iterator.setY());
        int zMin = calculateDimensionOffsetBackwards(iterator.setZ());
        //calculate the new multiblock size
        int xMax = calculateDimensionOffsetForwards(iterator.setX());
        int yMax = calculateDimensionOffsetForwards(iterator.setY());
        int zMax = calculateDimensionOffsetForwards(iterator.setZ());
        //if not all blocks for new root are correct, do nothing
        if(!areAllBlocksInRangeValidComponents(xMin, yMin, zMin, xMax, yMax, zMax)) {
            return false;
        }
        TileEntityTank newRoot = (TileEntityTank) oldRoot.getWorldObj().getTileEntity(oldRoot.xCoord - xMin, oldRoot.yCoord - yMin, oldRoot.zCoord - zMin);
        int xSizeNew = xMax + xMin;
        int ySizeNew = yMax + yMin;
        int zSizeNew = zMax + zMin;
        //if dimensions and root are the same the multiblock hasn't changed and nothing has to happen
        if(oldRoot==newRoot && xSizeNew==this.sizeX() && ySizeNew==this.sizeY() && zSizeNew==this.sizeZ()) {
            return false;
        }
        //new multiblock dimensions are required, update the multiblock
        breakAllMultiBlocksInRange(xMin, yMin, zMin, xMax, yMax, zMax);
        int fluidLevel = calculateTotalFluidLevelForBlocksInRange(xMin, yMin, zMin, xMax, yMax, zMax);
        this.setDimensions(xSizeNew, ySizeNew, zSizeNew);
        this.rootComponent = newRoot;
        createMultiBlock();
        newRoot.setFluidLevel(fluidLevel);
        return true;
    }

    private int calculateDimensionOffsetBackwards(CoordinateIterator it) {
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

    private int calculateDimensionOffsetForwards(CoordinateIterator it) {
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

    private boolean areAllBlocksInRangeValidComponents(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        TileEntityTank root = getRootComponent();
        World world = root.getWorldObj();
        for(int x = root.xCoord-xMin;x<root.xCoord+xMax;x++) {
            for(int y = root.yCoord-yMin;y<root.yCoord+yMax;y++) {
                for(int z = root.zCoord-zMin;z<root.zCoord+zMax;z++) {
                    if(!isValidComponent(world.getTileEntity(x, y, z))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void breakAllMultiBlocksInRange(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        TileEntityTank root = getRootComponent();
        World world = root.getWorldObj();
        for(int x = root.xCoord-xMin;x<root.xCoord+xMax;x++) {
            for(int y = root.yCoord-yMin;y<root.yCoord+yMax;y++) {
                for(int z = root.zCoord-zMin;z<root.zCoord+zMax;z++) {
                    TileEntity te = world.getTileEntity(x, y, z);
                    if(te != null && te instanceof TileEntityTank) {
                        ((TileEntityTank) te).getMultiBlockLogic().breakMultiBlock();
                    }
                }
            }
        }
    }

    private int calculateTotalFluidLevelForBlocksInRange(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        int lvl = 0;
        TileEntityTank root = getRootComponent();
        World world = root.getWorldObj();
        for(int x = root.xCoord-xMin;x<root.xCoord+xMax;x++) {
            for(int y = root.yCoord-yMin;y<root.yCoord+yMax;y++) {
                for(int z = root.zCoord-zMin;z<root.zCoord+zMax;z++) {
                    TileEntity te = world.getTileEntity(x, y, z);
                    if(te != null && te instanceof TileEntityTank) {
                        lvl = lvl + ((TileEntityTank) te).getFluidLevel();
                    }
                }
            }
        }
        return lvl;
    }

    @Override
    public void createMultiBlock() {
        TileEntityTank root = getRootComponent();
        World world = root.getWorldObj();
        for(int x = root.xCoord;x<root.xCoord+sizeX();x++) {
            for(int y = root.yCoord;y<root.yCoord+sizeY();y++) {
                for(int z = root.zCoord;z<root.zCoord+sizeZ();z++) {
                    TileEntityTank tank = (TileEntityTank) world.getTileEntity(x, y, z);
                    tank.setMultiBlockLogic(this);
                }
            }
        }
        if(!world.isRemote) {
            root.syncMultiBlockToClient();
        }
    }

    @Override
    public void breakMultiBlock() {
        //if this is not a multiblock, do nothing
        if(this.getMultiBlockCount()<=1) {
            return;
        }
        //calculate fluid levels
        int[] fluidLevelByLayer = new int[this.sizeY()];
        int fluidLevel = getRootComponent().getFluidLevel();
        int area = this.sizeX()*this.sizeZ();
        int fluidContentByLayer = area*TileEntityTank.SINGLE_CAPACITY;
        int layer = 0;
        while(fluidLevel>0) {
            fluidLevelByLayer[layer] = fluidLevel>fluidContentByLayer?fluidContentByLayer/area:fluidLevel/area;
            fluidLevel = fluidLevel>fluidContentByLayer?fluidLevel - fluidContentByLayer:0;
            layer++;
        }
        //apply fluid levels
        TileEntityTank root = getRootComponent();
        for(int x = root.xCoord;x<root.xCoord+sizeX();x++) {
            for(int y = root.yCoord;y<root.yCoord+sizeY();y++) {
                for(int z = root.zCoord;z<root.zCoord+sizeZ();z++) {
                    TileEntityTank tank = (TileEntityTank) root.getWorldObj().getTileEntity(x, y, z);
                    if(tank == null) {
                        continue;
                    }
                    tank.setMultiBlockLogic(new MultiBlockLogicTank(tank));
                    tank.syncMultiBlockToClient();
                    tank.setFluidLevel(fluidLevelByLayer[y-root.yCoord]);
                }
            }
        }
    }
}

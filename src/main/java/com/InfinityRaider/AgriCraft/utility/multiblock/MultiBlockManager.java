package com.InfinityRaider.AgriCraft.utility.multiblock;

import com.InfinityRaider.AgriCraft.utility.CoordinateIterator;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class MultiBlockManager implements IMultiBlockManager<MultiBlockPartData> {
    private static MultiBlockManager INSTANCE;

    private MultiBlockManager() {
    }

    public static MultiBlockManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MultiBlockManager();
        }
        return INSTANCE;
    }

    @Override
    public void onBlockPlaced(World world, int x, int y, int z, IMultiBlockComponent component) {
        boolean flag = false;
        for (ForgeDirection dir : ForgeDirection.values()) {
            if (dir == ForgeDirection.UNKNOWN) {
                continue;
            }
            TileEntity te = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            if (te != null && te instanceof IMultiBlockComponent && component.isValidComponent((IMultiBlockComponent) te)) {
                IMultiBlockComponent componentAt = (IMultiBlockComponent) te;
                if (canCheckForMultiBlock(componentAt)) {
                    if (checkForMultiBlock(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, componentAt)) {
                        return;
                    }
                    flag = true;
                }
            }
        }
        if (!flag) {
            checkForMultiBlock(world, x, y, z, component);
        }
    }

    @Override
    public void onBlockBroken(World world, int x, int y, int z, IMultiBlockComponent<? extends IMultiBlockManager<MultiBlockPartData>, MultiBlockPartData> component) {
        component.getMainComponent().preMultiBlockBreak();
        IMultiBlockPartData data = component.getMultiBlockData();
        breakAllMultiBlocksInRange(world, x - data.posX(), y - data.posY(), z - data.posZ(), x + data.sizeX(), y + data.sizeY(), z + data.sizeZ());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void createMultiBlock(World world, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        int sizeX = xMax-xMin;
        int sizeY = yMax-yMin;
        int sizeZ = zMax-zMin;
        for (int x = xMin; x < xMax; x++) {
            for (int y = yMin; y < yMax; y++) {
                for (int z = zMin; z < zMax; z++) {
                    IMultiBlockComponent component = (IMultiBlockComponent) world.getTileEntity(x, y, z);
                    if(x == xMin && y == yMin && z == zMin) {
                        component.preMultiBlockCreation(xMax-xMin, yMax-yMin, zMax-zMin);
                    }
                    component.setMultiBlockPartData(new MultiBlockPartData(x-xMin, y-yMin, z-zMin, sizeX, sizeY, sizeZ));
                }
            }
        }
        if (world.isRemote) {
            world.markBlockRangeForRenderUpdate(xMin, yMin, zMin, xMax, yMax, zMax);
        }
        ((IMultiBlockComponent) world.getTileEntity(xMin, yMin, zMin)).postMultiBlockCreation();
    }

    private boolean canCheckForMultiBlock(IMultiBlockComponent component) {
        return component.getMultiBlockData().size() > 1;
    }

    private boolean checkForMultiBlock(World world, int x, int y, int z, IMultiBlockComponent component) {
        IMultiBlockComponent rootComponent = component.getMainComponent();
        if (rootComponent != component) {
            IMultiBlockPartData data = component.getMultiBlockData();
            return checkForMultiBlock(world, x - data.posX(), y - data.posY(), z - data.posZ(), rootComponent);
        }
        CoordinateIterator iterator = new CoordinateIterator();
        int xOffsetMin = calculateDimensionOffsetBackwards(world, x, y, z, component, iterator.setX());
        int yOffsetMin = calculateDimensionOffsetBackwards(world, x, y, z, component, iterator.setY());
        int zOffsetMin = calculateDimensionOffsetBackwards(world, x, y, z, component, iterator.setZ());
        int xOffsetPlus = calculateDimensionOffsetForwards(world, x, y, z, component, iterator.setX());
        int yOffsetPlus = calculateDimensionOffsetForwards(world, x, y, z, component, iterator.setY());
        int zOffsetPlus = calculateDimensionOffsetForwards(world, x, y, z, component, iterator.setZ());
        //if not all blocks for new root are correct, do nothing
        if (!areAllBlocksInRangeValidComponents(world, x - xOffsetMin, y - yOffsetMin, z - zOffsetMin, x + xOffsetPlus, y + yOffsetPlus, z + zOffsetPlus, component)) {
            return false;
        }
        IMultiBlockComponent newRoot = (IMultiBlockComponent) world.getTileEntity(x - xOffsetMin, y - yOffsetMin, z - zOffsetMin);
        int xSizeNew = xOffsetPlus + xOffsetMin;
        int ySizeNew = yOffsetPlus + yOffsetMin;
        int zSizeNew = zOffsetPlus + zOffsetMin;
        IMultiBlockPartData data = component.getMultiBlockData();
        //if dimensions and root are the same the multiblock hasn't changed and nothing has to happen
        if (component == newRoot && xSizeNew == data.sizeX() && ySizeNew == data.sizeY() && zSizeNew == data.sizeZ()) {
            return false;
        }
        //new multiblock dimensions are required, update the multiblock
        createMultiBlock(world, x - xOffsetMin, y - yOffsetMin, z - zOffsetMin, x + xOffsetPlus, y + yOffsetPlus, z + zOffsetPlus);
        return true;
    }

    private int calculateDimensionOffsetBackwards(World world, int x, int y, int z, IMultiBlockComponent component, CoordinateIterator it) {
        if (!it.isActive()) {
            LogHelper.debug("ERROR WHEN ITERATING COORDINATES: ITERATOR NOT ACTIVE");
            return 0;
        }
        IMultiBlockPartData data = component.getMultiBlockData();
        x = x - data.posX();
        y = y - data.posY();
        z = z - data.posZ();
        while (true) {
            it.increment();
            if (!isValidComponent(world, x - it.x(), y - it.y(), z - it.z(), component)) {
                break;
            }
        }
        return it.getOffset() - 1;
    }

    private int calculateDimensionOffsetForwards(World world, int x, int y, int z, IMultiBlockComponent component, CoordinateIterator it) {
        if (!it.isActive()) {
            LogHelper.debug("ERROR WHEN ITERATING COORDINATES: ITERATOR NOT ACTIVE");
            return 0;
        }
        IMultiBlockPartData data = component.getMultiBlockData();
        x = x - data.posX();
        y = y - data.posY();
        z = z - data.posZ();
        while (true) {
            it.increment();
            if (!isValidComponent(world, x + it.x(), y + it.y(), z + it.z(), component)) {
                break;
            }
        }
        return it.getOffset();
    }

    private boolean areAllBlocksInRangeValidComponents(World world, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, IMultiBlockComponent component) {
        for (int x = xMin; x < xMax; x++) {
            for (int y = yMin; y < yMax; y++) {
                for (int z = zMin; z < zMax; z++) {
                    if (!isValidComponent(world, x, y, z, component)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isValidComponent(World world, int x, int y, int z, IMultiBlockComponent component) {
        TileEntity te = world.getTileEntity(x, y, z);
        return (te != null) && (te instanceof IMultiBlockComponent) && (component.isValidComponent((IMultiBlockComponent) te));
    }

    @SuppressWarnings("unchecked")
    private void breakAllMultiBlocksInRange(World world, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        for (int x=xMin; x<xMax;x++) {
            for (int y=yMin;y<yMax;y++) {
                for (int z=zMin;z<zMax;z++) {
                    TileEntity te = world.getTileEntity(x, y, z);
                    if((te == null) || !(te instanceof IMultiBlockComponent)) {
                        continue;
                    }
                    IMultiBlockComponent component = (IMultiBlockComponent) te;
                    component.setMultiBlockPartData(new MultiBlockPartData(0, 0, 0, 1, 1, 1));
                    component.postMultiBlockBreak();
                }
            }
        }
    }
}

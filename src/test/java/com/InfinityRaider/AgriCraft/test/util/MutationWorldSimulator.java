package com.InfinityRaider.AgriCraft.test.util;


import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MutationWorldSimulator {

    private final World world;
    private final TileEntityCrop targetCrop;

    private final int x;
    private final int y;
    private final int z;

    public MutationWorldSimulator(int x, int y, int z) {
        world = mock(World.class);
        this.x = x;
        this.y = y;
        this.z = z;

        targetCrop = new TileEntityCrop();
        targetCrop.setWorldObj(world);
        targetCrop.xCoord = x;
        targetCrop.yCoord = y;
        targetCrop.zCoord = z;
    }

    public TileEntityCrop getTargetCrop() {
        return targetCrop;
    }

    public void addNeighbour(ForgeDirection direction, IPlantable plant, int meta, int growth, int gain, int strength) {
        TileEntityCrop crop = new TileEntityCrop();
        crop.setWorldObj(world);
        crop.seed = plant;
        crop.seedMeta = meta;
        crop.growth = growth;
        crop.gain = gain;
        crop.strength = strength;

        when(world.getTileEntity(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ)).thenReturn(crop);
    }
}

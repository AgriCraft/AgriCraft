package com.InfinityRaider.AgriCraft.test.util;


import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.item.ItemSeeds;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MutationWorldSimulator {

    private final World world;
    private final TileEntityCrop targetCrop;

    public MutationWorldSimulator(int x, int y, int z) {
        world = mock(World.class);
        world.isRemote = false;

        targetCrop = new TileEntityCrop();
        targetCrop.setWorldObj(world);
        targetCrop.xCoord = x;
        targetCrop.yCoord = y;
        targetCrop.zCoord = z;
    }

    public TileEntityCrop getTargetCrop() {
        return targetCrop;
    }

    public void addNeighbour(ForgeDirection direction, CropPlant plant, int meta, int growth, int gain, int strength) {
        TileEntityCrop crop = new TileEntityCrop();
        crop.setWorldObj(world);
        crop.plant = plant;
        crop.seedMeta = meta;
        crop.growth = growth;
        crop.gain = gain;
        crop.strength = strength;

        crop.xCoord = targetCrop.xCoord + direction.offsetX;
        crop.yCoord = targetCrop.yCoord + direction.offsetY;
        crop.zCoord = targetCrop.zCoord + direction.offsetZ;

        when(world.getTileEntity(crop.xCoord, crop.yCoord, crop.zCoord)).thenReturn(crop);
        when(world.getBlock(crop.xCoord, crop.yCoord, crop.zCoord)).thenReturn(new BlockCrop());
        when(world.getBlockMetadata(crop.xCoord, crop.yCoord, crop.zCoord)).thenReturn(meta);
    }
}

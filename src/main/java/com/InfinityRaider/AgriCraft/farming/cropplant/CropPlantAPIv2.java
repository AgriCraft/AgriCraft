package com.InfinityRaider.AgriCraft.farming.cropplant;

import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v2.IAdditionalCropData;
import com.InfinityRaider.AgriCraft.api.v2.ICrop;
import com.InfinityRaider.AgriCraft.api.v2.ICropPlant;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class CropPlantAPIv2 extends CropPlantAPIv1 {
    public CropPlantAPIv2(ICropPlant plant) {
        super(plant);
        this.setGrowthRequirement(plant.getGrowthRequirement());
    }

    @Override
    public IAdditionalCropData getInitialCropData(World world, int x, int y, int z, ICrop crop) {
        return ((ICropPlant) plant).getInitialCropData(world, x, y, z, crop);
    }

    @Override
    public IAdditionalCropData readCropDataFromNBT(NBTTagCompound tag) {
        return ((ICropPlant) plant).readCropDataFromNBT(tag);}

    @Override
    public IGrowthRequirement initGrowthRequirement() {
        return plant == null ? null : ((ICropPlant) plant).getGrowthRequirement();
    }

    @Override
    public void onValidate(World world, int x, int y, int z, ICrop crop) {
        ((ICropPlant) plant).onValidate(world, x, y, z, crop);
    }

    @Override
    public void onInvalidate(World world, int x, int y, int z, ICrop crop) {
        ((ICropPlant) plant).onInvalidate(world, x, y, z, crop);}

    @Override
    public void onChunkUnload(World world, int x, int y, int z, ICrop crop) {
        ((ICropPlant) plant).onChunkUnload(world, x, y, z, crop);}
}

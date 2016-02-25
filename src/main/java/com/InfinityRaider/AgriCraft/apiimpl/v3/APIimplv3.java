package com.InfinityRaider.AgriCraft.apiimpl.v3;

import com.InfinityRaider.AgriCraft.api.APIStatus;
import com.InfinityRaider.AgriCraft.api.v3.*;
import com.InfinityRaider.AgriCraft.apiimpl.v2.APIimplv2;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantAPIv3;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationHandler;
import com.InfinityRaider.AgriCraft.farming.mutation.statcalculator.StatCalculator;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class APIimplv3 extends APIimplv2 implements APIv3 {
    public APIimplv3(int version, APIStatus status) {
        super(version, status);
    }

    @Override
    public ICropPlant getCropPlant(ItemStack seed) {
        return CropPlantHandler.getPlantFromStack(seed);
    }

    @Override
    public void registerCropPlant(ICropPlant plant) {
        CropPlantHandler.addCropToRegister(new CropPlantAPIv3(plant));
    }


    @Override
    public ICropPlant getCropPlant(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te==null || !(te instanceof TileEntityCrop)) {
            return null;
        }
        return ((TileEntityCrop) te).getPlant();
    }

    @Override
    public ICrop getCrop(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te instanceof com.InfinityRaider.AgriCraft.api.v2.ICrop) {
            return (ICrop) te;
        }
        return null;
    }

    @Override
    public void setStatCalculator(IStatCalculator calculator) {
        StatCalculator.setStatCalculator(calculator);
    }

    @Override
    public IMutationHandler getMutationHandler() {
        return MutationHandler.getInstance();
    }
}

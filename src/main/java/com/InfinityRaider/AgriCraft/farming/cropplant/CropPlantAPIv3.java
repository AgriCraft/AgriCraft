package com.InfinityRaider.AgriCraft.farming.cropplant;

import com.InfinityRaider.AgriCraft.api.v3.ICrop;
import com.InfinityRaider.AgriCraft.api.v3.ICropPlant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class CropPlantAPIv3 extends CropPlantAPIv2 implements ICropPlant {
    public CropPlantAPIv3(ICropPlant plant) {
        super(plant);
    }

    @Override
    public boolean onHarvest(World world, int x, int y, int z, ICrop crop, EntityPlayer player) {
        return ((ICropPlant) plant).onHarvest(world, x, y, z, crop, player);
    }

    @Override
    public void onSeedPlanted(World world, int x, int y, int z, ICrop crop) {
        ((ICropPlant) plant).onSeedPlanted(world, x, y, z, crop);
    }

    @Override
    public void onPlantRemoved(World world, int x, int y, int z, ICrop crop) {
        ((ICropPlant) plant).onPlantRemoved(world, x, y, z, crop);
    }

    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage, ICrop crop) {
        return ((ICropPlant) plant).onAllowedGrowthTick(world, x, y, z, oldGrowthStage, crop);
    }
}

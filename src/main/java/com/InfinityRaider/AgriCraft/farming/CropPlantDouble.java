package com.InfinityRaider.AgriCraft.farming;

import net.minecraft.world.World;

public abstract class CropPlantDouble extends CropPlant {
    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return false;
    }
}

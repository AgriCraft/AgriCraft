package com.InfinityRaider.AgriCraft.compatibility.magicalcrops;

import java.util.Random;

import net.minecraft.world.World;

import com.InfinityRaider.AgriCraft.api.v1.IFertiliser;

public final class MagicalCropsFertiliser implements IFertiliser {
    @Override
    public boolean isFertiliserAllowed(int tier) {
        return true;
    }

    @Override
    public boolean canTriggerMutation() {
        return true;
    }

    @Override
    public boolean hasSpecialBehaviour() {
        return false;
    }

    @Override
    public void onFertiliserApplied(World world, int x, int y, int z, Random random) {

    }

    @Override
    public void performClientAnimations(int meta, World world, int x, int y, int z) {

    }
}

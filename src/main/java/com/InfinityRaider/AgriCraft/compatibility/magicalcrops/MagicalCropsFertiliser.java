package com.InfinityRaider.AgriCraft.compatibility.magicalcrops;

import com.InfinityRaider.AgriCraft.api.v1.IFertiliser;
import net.minecraft.world.World;

import java.util.Random;

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

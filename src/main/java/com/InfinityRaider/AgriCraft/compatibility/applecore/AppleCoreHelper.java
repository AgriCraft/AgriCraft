package com.InfinityRaider.AgriCraft.compatibility.applecore;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import squeek.applecore.api.AppleCoreAPI;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.Event;

public class AppleCoreHelper {
    public static final String MODID = "AppleCore";
    public static final boolean isAppleCoreLoaded = Loader.isModLoaded(AppleCoreHelper.MODID);
    public static boolean hasDispatcher;
    static {
        try {
            hasDispatcher = isAppleCoreLoaded && Class.forName("squeek.applecore.api.IAppleCoreDispatcher") != null;
        } catch (ClassNotFoundException e) {
            hasDispatcher = false;
        }
    }

    @Optional.Method(modid = AppleCoreHelper.MODID)
    private static Event.Result validateAppleCoreGrowthTick(Block block, World world, int x, int y, int z, Random random) {
        return AppleCoreAPI.dispatcher.validatePlantGrowth(block, world, x, y, z, random);
    }

    @Optional.Method(modid = AppleCoreHelper.MODID)
    private static void announceAppleCoreGrowthTick(Block block, World world, int x, int y, int z) {
        AppleCoreAPI.dispatcher.announcePlantGrowth(block, world, x, y, z);
    }

    public static Event.Result validateGrowthTick(Block block, World world, int x, int y, int z, Random random) {
        if (hasDispatcher)
            return validateAppleCoreGrowthTick(block, world, x, y, z, random);
        else
            return Event.Result.DEFAULT;
    }

    public static void announceGrowthTick(Block block, World world, int x, int y, int z) {
        if (hasDispatcher) {
            announceAppleCoreGrowthTick(block, world, x, y, z);
        }
    }
}

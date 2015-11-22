package com.InfinityRaider.AgriCraft.compatibility.applecore;


import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import squeek.applecore.api.AppleCoreAPI;

import java.util.Random;

public final class AppleCoreHelper extends ModHelper {
    public static final String MODID = "AppleCore";
    public static boolean isAppleCoreLoaded;
    public static boolean hasDispatcher;

    @Override
    protected void onInit() {
        isAppleCoreLoaded = Loader.isModLoaded(AppleCoreHelper.MODID);
        try {
            hasDispatcher = isAppleCoreLoaded && Class.forName("squeek.applecore.api.IAppleCoreDispatcher") != null;
        } catch(ClassNotFoundException e) {
            hasDispatcher = false;
        }
    }

    @Override
    protected String modId() {
        return MODID;
    }

    @Optional.Method(modid = AppleCoreHelper.MODID)
    private static Event.Result validateAppleCoreGrowthTick(Block block, World world, int x, int y, int z, Random random) {
        Event.Result result = Event.Result.DEFAULT;
        if(AppleCoreAPI.dispatcher!=null) {
            result = AppleCoreAPI.dispatcher.validatePlantGrowth(block, world, x, y, z, random);
        }
        return result;
    }

    @Optional.Method(modid = AppleCoreHelper.MODID)
    private static void announceAppleCoreGrowthTick(Block block, World world, int x, int y, int z) {
        if(AppleCoreAPI.dispatcher!=null) {
            AppleCoreAPI.dispatcher.announcePlantGrowth(block, world, x, y, z);
        }
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

package com.InfinityRaider.AgriCraft.compatibility.applecore;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Random;

public class AppleCoreHelper extends ModHelper {
    @Override
    protected String modId() {
        return Names.Mods.appleCore;
    }

    public static Event.Result validateGrowthTick(BlockCrop crop, World world, BlockPos pos, Random rnd) {
        return Event.Result.ALLOW;
    }

    public static void announceGrowthTick(World world, BlockPos pos, IBlockState state) {
    }
}

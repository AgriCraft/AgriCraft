package com.InfinityRaider.AgriCraft.compatibility.applecore;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class AppleCoreHelper extends ModHelper {
    protected AppleCoreHelper() {
        super(Names.Mods.appleCore);
    }

    protected boolean handleGrowthTick() {
        return true;
    }

    @Override
    protected void announceGrowthTick(World world, BlockPos pos, IBlockState state) {}

    @Override
    protected boolean allowGrowthTick(World world, BlockPos pos, BlockCrop block, TileEntityCrop crop, Random rnd) {
        return true;
    }
}

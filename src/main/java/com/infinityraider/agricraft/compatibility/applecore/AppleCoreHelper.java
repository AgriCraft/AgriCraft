package com.infinityraider.agricraft.compatibility.applecore;

import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.compatibility.ModHelper;
import com.infinityraider.agricraft.reference.AgriCraftMods;
import com.infinityraider.agricraft.tileentity.TileEntityCrop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class AppleCoreHelper extends ModHelper {
    protected AppleCoreHelper() {
        super(AgriCraftMods.appleCore);
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

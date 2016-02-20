package com.infinityraider.agricraft.api.v1;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public interface IFertiliser {
    /** return true if this fertiliser is allowed to speed up growth of a crop of this tier */
    public boolean isFertiliserAllowed(int tier);

    /** wether or not this mod can be used on a cross crop to trigger a mutation (does not override configuration option) */
    public boolean canTriggerMutation();

    /** return true here if you want a custom behaviour when this fertiliser is used on a crop, else it will just mimic bonemeal behaviour */
    public boolean hasSpecialBehaviour();

    /** this is called when the fertiliser is used on a crop, this only is called if true is returned from hasSpecialBehaviour */
    public void onFertiliserApplied(World world, BlockPos pos, Random random);

    /** this is called on the client when the fertiliser is applied, can be used for particles or other visual effects */
    @SideOnly(Side.CLIENT)
    public void performClientAnimations(int meta, World world, BlockPos pos);
}

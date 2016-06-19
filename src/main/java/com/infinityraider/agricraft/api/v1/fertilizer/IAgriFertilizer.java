package com.infinityraider.agricraft.api.v1.fertilizer;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public interface IAgriFertilizer {
    /** return true if this fertilizer is allowed to speed up growth of a crop of this tier */
    boolean isFertilizerAllowed(int tier);

    /** wether or not this mod can be used on a cross crop to trigger a mutation (does not override configuration option) */
    boolean canTriggerMutation();

    /** return true here if you want a custom behaviour when this fertilizer is used on a crop, else it will just mimic bonemeal behaviour */
    boolean hasSpecialBehaviour();

    /** this is called when the fertilizer is used on a crop, this only is called if true is returned from hasSpecialBehaviour */
    void onFertilizerApplied(World world, BlockPos pos, Random random);

    /** this is called on the client when the fertilizer is applied, can be used for particles or other visual effects */
    @SideOnly(Side.CLIENT)
    void performClientAnimations(int meta, World world, BlockPos pos);
}

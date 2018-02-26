package com.infinityraider.agricraft.api.v1.fertilizer;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IAgriFertilizer {

    /**
     * Whether or not this mod can be used on a cross crop to trigger a mutation (does not override
     * configuration option).
     *
     * @return If the fertilizer can trigger a mutation event.
     */
    boolean canTriggerMutation();

    /**
     * This is called when the fertilizer is used on a crop, this only is called if true is returned
     * from hasSpecialBehaviour.
     *
     * @param player The player that applied the fertilizer.
     * @param world The world that the fertilizer was applied in.
     * @param pos The location that the fertilizer was applied.
     * @param target The fertilizable object to which the fertilizer was applied.
     * @param stack The stack that the player was holding that triggered the fertilizer to be
     * applied.
     * @param random A random for use in generating probabilities.
     *
     * @return
     */
    boolean applyFertilizer(EntityPlayer player, World world, BlockPos pos, IAgriFertilizable target, ItemStack stack, Random random);

    /**
     * Called on the client when the fertilizer is applied, can be used for particles or other
     * visual effects.
     *
     * @param meta UNKNOWN!
     * @param world The world that the fertilizer was applied in.
     * @param pos The location at which the fertilizer was applied in the world.
     */
    @SideOnly(Side.CLIENT)
    void performClientAnimations(int meta, World world, BlockPos pos);

}

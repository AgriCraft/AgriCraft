/*
 */
package com.infinityraider.agricraft.compat.vanilla;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizable;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.util.MethodResult;
import java.util.Optional;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 *
 *
 */
public class BonemealWrapper implements IAgriFertilizer, IAgriAdapter<IAgriFertilizer> {

    public static final BonemealWrapper INSTANCE = new BonemealWrapper();

    private static final ItemStack BONEMEAL = new ItemStack(Items.DYE, 1, 15);

    private BonemealWrapper() {
    }

    @Override
    public boolean canTriggerMutation() {
        return true;
    }

    @Override
    public boolean applyFertilizer(EntityPlayer player, World world, BlockPos pos, IAgriFertilizable target, ItemStack stack, Random random) {
        if (target.acceptsFertilizer(this) && target.onApplyFertilizer(this, random) == MethodResult.SUCCESS) {
            world.playEvent(2005, pos, 1); // Bonemeal particle effect. Last parameter (data) is num of particles.
            if (player == null || !player.capabilities.isCreativeMode) {
                stack.setCount(stack.getCount() - 1);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void performClientAnimations(int meta, World world, BlockPos pos) {
        // TODO!
    }

    @Override
    public boolean accepts(Object obj) {
        return obj instanceof ItemStack && BONEMEAL.isItemEqual((ItemStack) obj);
    }

    @Override
    public Optional<IAgriFertilizer> valueOf(Object obj) {
        if (obj instanceof ItemStack && BONEMEAL.isItemEqual((ItemStack) obj)) {
            return Optional.of(this);
        } else {
            return Optional.empty();
        }
    }

}

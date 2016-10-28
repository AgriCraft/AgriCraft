/*
 */
package com.infinityraider.agricraft.compat.vanilla;

import java.util.Optional;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.infinityraider.agricraft.api.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizable;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizer;

/**
 *
 *
 */
public class BonemealWrapper implements IAgriFertilizer, IAgriAdapter<IAgriFertilizer> {

    private static final ItemStack BONEMEAL = new ItemStack(Items.DYE, 1, 15);

    @Override
    public boolean isFertilizerAllowed(int tier) {
        return tier < 3;
    }

    @Override
    public boolean canTriggerMutation() {
        return true;
    }

    @Override
    public boolean applyFertilizer(EntityPlayer player, World world, BlockPos pos, IAgriFertilizable target, ItemStack stack, Random random) {
        if (target.acceptsFertilizer(this) && target.onApplyFertilizer(this, random)) {
            if (player == null || !player.capabilities.isCreativeMode) {
                stack.stackSize = stack.stackSize - 1;
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

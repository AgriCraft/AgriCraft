package com.infinityraider.agricraft.plugins.cyclic;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.content.core.BlockCropBase;
import com.lothrazar.cyclic.api.IHarvesterOverride;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class AgriHarvesterOverride implements IHarvesterOverride {
    @Override
    public boolean appliesTo(BlockState blockState, World world, BlockPos blockPos) {
        return blockState.getBlock() instanceof BlockCropBase;
    }

    @Override
    public boolean attemptHarvest(BlockState blockState, World world, BlockPos blockPos, Consumer<ItemStack> consumer) {
        return AgriApi.getCrop(world, blockPos)
                .map(crop -> crop.harvest(stack -> {
                        if (!world.isRemote) {
                            world.addEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack));
                        }
                    }, null).isSuccess())
                .orElse(false);
    }
}

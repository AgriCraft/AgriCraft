package com.infinityraider.agricraft.plugins.cyclic;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.content.core.BlockCrop;
import com.lothrazar.cyclic.api.IHarvesterOverride;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class AgriHarvesterOverride implements IHarvesterOverride {
    @Override
    public boolean appliesTo(BlockState blockState, Level world, BlockPos blockPos) {
        return blockState.getBlock() instanceof BlockCrop;
    }

    @Override
    public boolean attemptHarvest(BlockState blockState, Level world, BlockPos blockPos, Consumer<ItemStack> consumer) {
        return AgriApi.getCrop(world, blockPos)
                .map(crop -> crop.harvest(stack -> {
                    if (!world.isClientSide()) {
                        world.addFreshEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack));
                    }
                }, null) == InteractionResult.SUCCESS)
                .orElse(false);
    }
}

package com.infinityraider.agricraft.plugins.bloodmagic;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import wayoftime.bloodmagic.ritual.harvest.IHarvestHandler;

import java.util.List;

public class AgriHarvestHandler implements IHarvestHandler {
    protected AgriHarvestHandler() {}

    @Override
    public boolean harvest(World world, BlockPos blockPos, BlockState blockState, List<ItemStack> list) {
        return AgriApi.getCrop(world, blockPos)
                .map(crop -> crop.harvest(list::add, null))
                .map(ActionResultType::isSuccess)
                .orElse(false);
    }

    @Override
    public boolean test(World world, BlockPos blockPos, BlockState blockState) {
        return AgriApi.getCrop(world, blockPos)
                .map(crop -> crop.hasPlant() && crop.isMature())
                .orElse(false);
    }
}

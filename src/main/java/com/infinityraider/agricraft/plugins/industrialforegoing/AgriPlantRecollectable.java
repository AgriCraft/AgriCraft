package com.infinityraider.agricraft.plugins.industrialforegoing;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.google.common.collect.Lists;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class AgriPlantRecollectable extends PlantRecollectable {
    protected AgriPlantRecollectable() {
        super(AgriCraft.instance.getModId());
    }

    @Override
    public boolean canBeHarvested(Level world, BlockPos blockPos, BlockState blockState) {
        return AgriApi.getCrop(world, blockPos).map(crop -> crop.hasPlant() && crop.isMature()).orElse(false);
    }

    @Override
    public List<ItemStack> doHarvestOperation(Level world, BlockPos blockPos, BlockState blockState) {
        List<ItemStack> drops = Lists.newArrayList();
        AgriApi.getCrop(world, blockPos).ifPresent(crop -> crop.harvest(drops::add, null));
        return drops;
    }

    @Override
    public boolean shouldCheckNextPlant(Level world, BlockPos blockPos, BlockState blockState) {
        return true;
    }
}

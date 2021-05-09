package com.infinityraider.agricraft.plugins.industrialforegoing;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.google.common.collect.Lists;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class AgriPlantRecollectable extends PlantRecollectable {
    protected AgriPlantRecollectable() {
        super(AgriCraft.instance.getModId());
    }

    @Override
    public boolean canBeHarvested(World world, BlockPos blockPos, BlockState blockState) {
        return AgriApi.getCrop(world, blockPos).map(crop -> crop.hasPlant() && crop.isMature()).orElse(false);
    }

    @Override
    public List<ItemStack> doHarvestOperation(World world, BlockPos blockPos, BlockState blockState) {
        List<ItemStack> drops = Lists.newArrayList();
        AgriApi.getCrop(world, blockPos).ifPresent(crop -> crop.harvest(drops::add, null));
        return drops;
    }

    @Override
    public boolean shouldCheckNextPlant(World world, BlockPos blockPos, BlockState blockState) {
        return true;
    }
}

package com.infinityraider.agricraft.plugins.botania;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.api.item.IHornHarvestable;

public class AgriHornHarvestable implements IHornHarvestable {
    public static final AgriHornHarvestable INSTANCE = new AgriHornHarvestable();

    private AgriHornHarvestable() {}

    @Override
    public boolean canHornHarvest(World world, BlockPos pos, ItemStack stack, EnumHornType type) {
        return type == EnumHornType.WILD && AgriApi.getCrop(world, pos).map(IAgriCrop::isMature).orElse(false);
    }

    @Override
    public boolean hasSpecialHornHarvest(World world, BlockPos pos, ItemStack stack, EnumHornType type) {
        // this is always called after canHornHarvest returns true, so we are certain this is for an agricraft crop
        return true;
    }

    @Override
    public void harvestByHorn(World world, BlockPos pos, ItemStack stack, EnumHornType type) {
        if(type == EnumHornType.WILD) {
            AgriApi.getCrop(world, pos).ifPresent(crop -> crop.harvest(drop -> this.spawnEntity(world, pos, drop), null));
        }
    }

    private void spawnEntity(World world, BlockPos pos, ItemStack stack) {
        double x = pos.getX() + 0.5 + 0.25*world.getRandom().nextDouble();
        double y = pos.getY() + 0.5 + 0.25*world.getRandom().nextDouble();
        double z = pos.getZ() + 0.5 + 0.25*world.getRandom().nextDouble();
        world.addEntity(new ItemEntity(world, x, y, z, stack));
    }
}

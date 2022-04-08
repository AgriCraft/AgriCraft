package com.infinityraider.agricraft.plugins.botania;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.botania.api.block.IHornHarvestable;

public class AgriHornHarvestable implements IHornHarvestable {
    public static final AgriHornHarvestable INSTANCE = new AgriHornHarvestable();

    private AgriHornHarvestable() {}

    @Override
    public boolean canHornHarvest(Level world, BlockPos pos, ItemStack stack, EnumHornType type) {
        return type == EnumHornType.WILD && AgriApi.getCrop(world, pos).map(IAgriCrop::isMature).orElse(false);
    }

    @Override
    public boolean hasSpecialHornHarvest(Level world, BlockPos pos, ItemStack stack, EnumHornType type) {
        // this is always called after canHornHarvest returns true, so we are certain this is for an agricraft crop
        return true;
    }

    @Override
    public void harvestByHorn(Level world, BlockPos pos, ItemStack stack, EnumHornType type) {
        if(type == EnumHornType.WILD) {
            AgriApi.getCrop(world, pos).ifPresent(crop -> crop.harvest(drop -> this.spawnEntity(world, pos, drop), null));
        }
    }

    private void spawnEntity(Level world, BlockPos pos, ItemStack stack) {
        double x = pos.getX() + 0.5 + 0.25*world.getRandom().nextDouble();
        double y = pos.getY() + 0.5 + 0.25*world.getRandom().nextDouble();
        double z = pos.getZ() + 0.5 + 0.25*world.getRandom().nextDouble();
        world.addFreshEntity(new ItemEntity(world, x, y, z, stack));
    }
}

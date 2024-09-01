package com.agricraft.agricraft.mixin.compat.botania;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.common.block.CropBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.botania.api.block.HornHarvestable;

@Mixin(CropBlock.class)
public class CropBlockMixin implements HornHarvestable {
    @Override
    public boolean canHornHarvest(Level level, BlockPos pos, ItemStack stack, EnumHornType hornType, @Nullable LivingEntity user) {
        return hornType == EnumHornType.WILD && AgriApi.getCrop(level, pos).map(AgriCrop::canBeHarvested).orElse(false);
    }

    @Override
    public boolean hasSpecialHornHarvest(Level level, BlockPos pos, ItemStack stack, EnumHornType hornType, @Nullable LivingEntity user) {
        return true;
    }

    @Override
    public void harvestByHorn(Level level, BlockPos pos, ItemStack stack, EnumHornType hornType, @Nullable LivingEntity user) {
        if (hornType == EnumHornType.WILD) {
            AgriApi.getCrop(level, pos).ifPresent(crop -> {
                crop.getHarvestProducts(itemStack -> CropBlock.spawnItem(level, pos, itemStack));
                crop.setGrowthStage(crop.getPlant().getGrowthStageAfterHarvest());
                crop.getPlant().onHarvest(crop, user);
            });
        }
    }
}

package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.content.core.BlockCropSticks;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlantCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JsonPlantCallBackBrightness extends JsonPlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "brightness";
    private static final JsonPlantCallback INSTANCE = new JsonPlantCallBackBrightness();

    public static JsonPlantCallback getInstance() {
        return INSTANCE;
    }

    private JsonPlantCallBackBrightness() {
        super(ID);
    }

    @Override
    public void onPlanted(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {
        this.handleBrightness(crop.world(), crop.getPosition(), crop.getBlockState(), crop.getGrowthStage());
    }

    @Override
    public void onSpawned(@Nonnull IAgriCrop crop) {
        this.handleBrightness(crop.world(), crop.getPosition(), crop.getBlockState(), crop.getGrowthStage());}

    @Override
    public void onGrowth(@Nonnull IAgriCrop crop) {
        this.handleBrightness(crop.world(), crop.getPosition(), crop.getBlockState(), crop.getGrowthStage());
    }

    @Override
    public void onHarvest(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {
        this.handleBrightness(crop.world(), crop.getPosition(), crop.getBlockState(), crop.getGrowthStage());
    }

    protected void handleBrightness(World world, BlockPos pos, BlockState state, IAgriGrowthStage stage) {
        if(world != null && !world.isRemote() && state.getBlock() instanceof BlockCropSticks) {
            world.setBlockState(pos, BlockCropSticks.LIGHT.apply(state, (int) (16*stage.growthPercentage())));
        }
    }
}

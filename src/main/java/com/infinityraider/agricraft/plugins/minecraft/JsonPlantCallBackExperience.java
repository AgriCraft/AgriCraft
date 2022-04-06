package com.infinityraider.agricraft.plugins.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JsonPlantCallBackExperience implements IJsonPlantCallback {
    public static final int XP = 4;

    public static final String ID = AgriCraft.instance.getModId() + ":" + "experience";

    private static final IJsonPlantCallback INSTANCE = new JsonPlantCallBackExperience();

    private static final IJsonPlantCallback.Factory FACTORY = new IJsonPlantCallback.Factory() {
        @Override
        public String getId() {
            return ID;
        }

        @Override
        public IJsonPlantCallback makeCallBack(JsonElement json) throws JsonParseException {
            return INSTANCE;
        }
    };

    public static IJsonPlantCallback.Factory getFactory() {
        return FACTORY;
    }

    private JsonPlantCallBackExperience() {}

    @Override
    public void onHarvest(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {
        Level world = crop.world();
        if(world != null && !world.isClientSide()) {
            for (int i = 0; i < crop.getStats().getGain(); i++) {
                if(i == 0 || world.getRandom().nextDouble() < 0.5) {
                    this.spawnExperience(world, crop.getPosition());
                }
            }
        }
    }

    protected void spawnExperience(Level world, BlockPos pos) {
        world.addFreshEntity(
                new ExperienceOrb(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, XP)
        );
    }
}

package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlantCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JsonPlantCallBackExperience extends JsonPlantCallback {
    public static final int XP = 4;
    public static final String ID = AgriCraft.instance.getModId() + ":" + "experience";
    private static final JsonPlantCallback INSTANCE = new JsonPlantCallBackExperience();

    public static JsonPlantCallback getInstance() {
        return INSTANCE;
    }

    private JsonPlantCallBackExperience() {
        super(ID);
    }

    @Override
    public void onHarvest(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {
        World world = crop.world();
        if(world != null && !world.isRemote()) {
            for (int i = 0; i < crop.getStats().getGain(); i++) {
                if(i == 0 || world.getRandom().nextDouble() < 0.5) {
                    this.spawnExperience(world, crop.getPosition());
                }
            }
        }
    }

    protected void spawnExperience(World world, BlockPos pos) {
        world.addEntity(
                new ExperienceOrbEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, XP)
        );
    }
}

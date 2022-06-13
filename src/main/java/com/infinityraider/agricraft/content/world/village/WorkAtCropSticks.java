package com.infinityraider.agricraft.content.world.village;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.WorkAtPoi;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;

import javax.annotation.Nonnull;
import java.util.Optional;

public class WorkAtCropSticks extends WorkAtPoi {
    @Override
    protected void useWorkstation(@Nonnull ServerLevel world, Villager villager) {
        Optional<GlobalPos> optional = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (optional.isPresent()) {
            GlobalPos pos = optional.get();
            AgriApi.getCrop(world, pos.pos()).ifPresent(crop -> {
                if(crop.hasWeeds()) {
                    crop.removeWeed();
                }
            });
        }
    }
}

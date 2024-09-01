package com.agricraft.agricraft.plugin.create;

import com.agricraft.agricraft.api.AgriApi;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class AgriHarvesterMovementBehaviour extends HarvesterMovementBehaviour {
    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        Level level = context.world;
        if (!level.isClientSide()) {
            AgriApi.getCrop(level, pos).ifPresentOrElse(crop -> {
                if (crop.canBeHarvested()) {
                    crop.getHarvestProducts(itemStack -> this.dropItem(context, itemStack));
                    crop.setGrowthStage(crop.getPlant().getGrowthStageAfterHarvest());
                    crop.getPlant().onHarvest(crop, null);
                }
            }, () -> super.visitNewPosition(context, pos));
        }
    }
}

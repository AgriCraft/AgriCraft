package com.infinityraider.agricraft.plugins.create;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class AgriHarvesterMovementBehaviour extends HarvesterMovementBehaviour {

    protected AgriHarvesterMovementBehaviour() {}

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        Level world = context.world;
        if (!world.isClientSide()) {
            AgriApi.getCrop(world, pos).map(crop -> {
                if(crop.hasPlant() && crop.isMature()) {
                    crop.harvest(stack -> this.dropItem(context, stack), null);
                }
                return true;
            }).orElseGet(() -> {
                super.visitNewPosition(context, pos);
                return false;
            });
        }
    }
}

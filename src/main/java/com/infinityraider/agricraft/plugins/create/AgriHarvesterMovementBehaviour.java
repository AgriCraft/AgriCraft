package com.infinityraider.agricraft.plugins.create;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.simibubi.create.content.contraptions.components.actors.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AgriHarvesterMovementBehaviour extends HarvesterMovementBehaviour {

    protected AgriHarvesterMovementBehaviour() {}

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        World world = context.world;
        if (!world.isRemote()) {
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

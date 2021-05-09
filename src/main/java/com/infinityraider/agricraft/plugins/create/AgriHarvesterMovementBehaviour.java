package com.infinityraider.agricraft.plugins.create;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.simibubi.create.content.contraptions.components.actors.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AgriHarvesterMovementBehaviour extends HarvesterMovementBehaviour {

    protected AgriHarvesterMovementBehaviour() {}

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        World world = context.world;
        if (!world.isRemote()) {
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof IAgriCrop) {
                IAgriCrop crop = (IAgriCrop) tile;
                if(crop.isMature()) {
                    crop.harvest(stack -> this.dropItem(context, stack), null);
                }
            } else {
                super.visitNewPosition(context, pos);
            }
        }
    }
}

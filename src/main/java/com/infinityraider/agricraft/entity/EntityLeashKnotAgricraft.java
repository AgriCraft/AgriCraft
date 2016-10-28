package com.infinityraider.agricraft.entity;

import java.util.List;

import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.infinityraider.agricraft.blocks.decoration.BlockFence;

public class EntityLeashKnotAgricraft extends EntityLeashKnot {

    @SuppressWarnings("unused")
    public EntityLeashKnotAgricraft(World world) {
        super(world);
    }

    public EntityLeashKnotAgricraft(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public boolean onValidSurface() {
        return this.worldObj.getBlockState(this.hangingPosition).getBlock() instanceof BlockFence;
    }

    public static EntityLeashKnotAgricraft getKnotForPosition(World world, BlockPos pos) {
        pos = pos.toImmutable();
        AxisAlignedBB searchArea = new AxisAlignedBB(pos.add(-1, -1, -1), pos.add(1, 1, 1));
        List list = world.getEntitiesWithinAABB(EntityLeashKnotAgricraft.class, searchArea);
        for (Object obj : list) {
            EntityLeashKnotAgricraft entityleashknot = (EntityLeashKnotAgricraft) obj;
            if (entityleashknot.getHangingPosition().equals(pos)) {
                return entityleashknot;
            }
        }
        return null;
    }

    public static EntityLeashKnotAgricraft createKnot(World world, BlockPos pos) {
        EntityLeashKnotAgricraft entityleashknot = new EntityLeashKnotAgricraft(world, pos);
        entityleashknot.forceSpawn = true;
        world.spawnEntityInWorld(entityleashknot);
        return entityleashknot;
    }
}

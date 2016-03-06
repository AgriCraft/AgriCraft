package com.infinityraider.agricraft.entity;

import com.infinityraider.agricraft.blocks.BlockFence;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;

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
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        List list = world.getEntitiesWithinAABB(EntityLeashKnotAgricraft.class, new AxisAlignedBB((double)i - 1.0D, (double)j - 1.0D, (double)k - 1.0D, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D));
        if (list != null) {
            for (Object obj : list) {
                EntityLeashKnotAgricraft entityleashknot = (EntityLeashKnotAgricraft) obj;
                if (entityleashknot.getHangingPosition().equals(pos)) {
                    return entityleashknot;
                }
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
